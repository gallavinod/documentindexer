package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.analysis.Trie;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.AutoCorrect;
import edu.buffalo.cse.irf14.query.Index;
import edu.buffalo.cse.irf14.query.OperandQuery;
import edu.buffalo.cse.irf14.query.Operator;
import edu.buffalo.cse.irf14.query.OperatorQuery;
import edu.buffalo.cse.irf14.query.Pair;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.QueryParserException;
import edu.buffalo.cse.irf14.query.ScoredDocument;
import edu.buffalo.cse.irf14.query.WildCard;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author jvallabh, saketadu, nikhillo
 *
 */
public class SearchRunner {
	private String indexDir;
	private String corpusDir;
	private char mode;
	private PrintStream stream;
	private HashMap<Index, IndexReader> readerMap;
	private HashMap<Index, Double> numDocsMap;
	//private double docSize;
	private Trie trie;
	private WildCard wildCard;
	private HashMap<Integer, String> trieMap;
	private Query query;
	private File mainDir;
	BufferedReader reader = null;
	private AutoCorrect autoCorrect;
	
	public enum ScoringModel {TFIDF, OKAPI, CUSTOM};
	
	public SearchRunner(){
	}
	
	public SearchRunner(String query){
		try {
			this.query = QueryParser.parse(query, "OR");
		} catch(Exception ex) {
			
		}
	}
	
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		//TODO: IMPLEMENT THIS METHOD
		this.indexDir = indexDir;
		this.corpusDir = corpusDir;
		mainDir = new File(corpusDir);
		this.mode = mode;
		this.stream = stream;
		flattenCorpus();
		buildReaderMap();
		buildNumDocsMap();
		//this.docSize = readerMap.get(Index.TERM).getTotalValueTerms();
		this.wildCard = new WildCard(trie);
		this.autoCorrect = new AutoCorrect();
		this.stream = stream;
	}
	
	private void flattenCorpus() {
		//IndexWriter writer = new IndexWriter(indexDir);
		String[] subDirs = mainDir.list();
		String[] files;
		File dir;
		File file;
		//Document d = null;
		try {
			for (String subDir : subDirs) {
				dir = new File(corpusDir+ File.separator+ subDir);
				files = dir.list();
				if (files == null)
					continue;
				for (String f : files) {
					//try {
						//d = Parser.parse(dir.getAbsolutePath() + File.separator +f);
						//writer.addDocument(d);
						file = new File(dir.getAbsolutePath() + File.separator + f);
						if(!file.renameTo(new File(mainDir, file.getName()))) {
							file.delete();
						}
					/*} catch (ParserException e) {
						file = new File(dir.getAbsolutePath() + File.separator + f);
						file.delete();
					}*/
				}
				dir.delete();
			}
			//writer.close();
		} catch(Exception e) {
			
		}
	}

	private void buildReaderMap() {
		readerMap = new HashMap<Index, IndexReader>();
		readerMap.put(Index.AUTHOR, new IndexReader(indexDir, IndexType.AUTHOR));
		readerMap.put(Index.CATEGORY, new IndexReader(indexDir, IndexType.CATEGORY));
		readerMap.put(Index.PLACE, new IndexReader(indexDir, IndexType.PLACE));
		readerMap.put(Index.TERM, new IndexReader(indexDir, IndexType.TERM));
		trie = readerMap.get(Index.AUTHOR).getTrie();
		trieMap = readerMap.get(Index.CATEGORY).getTrieMap();
	}
	
	private void buildNumDocsMap() {
		numDocsMap = new HashMap<Index, Double>();
		numDocsMap.put(Index.AUTHOR, (double)readerMap.get(Index.AUTHOR).getTotalValueTerms());
		numDocsMap.put(Index.CATEGORY, (double)readerMap.get(Index.CATEGORY).getTotalValueTerms());
		numDocsMap.put(Index.PLACE, (double)readerMap.get(Index.PLACE).getTotalValueTerms());
		numDocsMap.put(Index.TERM, (double)readerMap.get(Index.TERM).getTotalValueTerms());
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		//TODO: IMPLEMENT THIS METHOD
		try {
			long start = System.currentTimeMillis();
			stream.println("\n\nQuery: '" + userQuery + "'");
			query = QueryParser.parse(userQuery, "OR");
			Set<Integer> docIds = getDocs(query);
			if (docIds == null || docIds.isEmpty()) return;
			if (docIds.contains(-1)) {
				docIds.remove(-1);
			}
			List<OperandQuery> terms = new ArrayList<OperandQuery>();
			getQueryTerms(query, terms);
			List<ScoredDocument> sDocs = getTopDocs(terms, docIds, model);
			if(sDocs.size() == 0) return;
			double maxScore = sDocs.get(sDocs.size()-1).getScore();
			long end = System.currentTimeMillis();
			stream.println("Query Time: " + (end-start) + "\n");
			int size = sDocs.size();
			for (int i=size-1; i>=0; i--) {
				ScoredDocument sDoc = sDocs.get(i);
				sDoc.setScore(sDoc.getScore()/maxScore);
				DecimalFormat df = new DecimalFormat("0.00000");
				String fileName = sDoc.getDocName();
				stream.println("Result Rank: " + (size-i));
				stream.println("Document Name: " + fileName);// + " | Score: " + df.format(sDoc.getScore()));
				generateSnippet(fileName);
				stream.println("Relevancy: " + df.format(sDoc.getScore()) + "\n");
			}
		} catch (QueryParserException e) {
			e.printStackTrace();
		}
	}

	private List<ScoredDocument> getTopDocs(List<OperandQuery> terms, Set<Integer> docIds, ScoringModel model) {
		PriorityQueue<ScoredDocument> pq = new PriorityQueue<ScoredDocument>();
		int maxSize = 10;
		double score;
		for (Integer docId: docIds) {
			if (model == ScoringModel.TFIDF) {
				score = getTfIdfScore(terms, docId);
			} else if (model == ScoringModel.OKAPI) {
				score = getOkapiScore(terms, docId);
			} else {
				score = getCustomScore(terms, docId);
			}
			String docName = readerMap.get(Index.TERM).getDocument(docId);
			ScoredDocument doc = new ScoredDocument(docName, score);
			if (pq.size() < maxSize) {
				pq.add(doc);
			} else {
				if (pq.peek().compareTo(doc) < 0) {
					pq.poll();
					pq.add(doc);
				}
			}
		}
		List<ScoredDocument> retList = new ArrayList<ScoredDocument>();
		ScoredDocument sDoc;
		while (!pq.isEmpty()) {
			sDoc = pq.poll();
			retList.add(sDoc);
		}
		return retList;
	}
	
	private double getTfIdfScore(List<OperandQuery> terms, Integer docId) {
		double score = 0;
		for (OperandQuery term: terms) {
			Map<Integer, Integer> posting = readerMap.get(term.index).getIntegerPostings(term.value);
			if(posting == null) continue; 
			if (posting.containsKey(docId)) {
				double tf = Math.log(1 + posting.get(docId));
				double df = posting.size()-1;
				double idf = Math.log10(numDocsMap.get(term.index)/df);
				score += tf*idf; 
			}
		}
		return score;
	}

	private double getOkapiScore(List<OperandQuery> terms, Integer docId) {
		double score = 0;
		for (OperandQuery term: terms) {
			Map<Integer, Integer> posting = readerMap.get(term.index).getIntegerPostings(term.value);
			if (posting == null) continue;
			if (posting.containsKey(docId)) {
				double tf = Math.log(1 + posting.get(docId));
				double df = posting.size()-1;
				double idf = Math.log10((numDocsMap.get(term.index)-df+0.5)/(df+0.5));
				//double idf = Math.log10(docSize/df);
				double dlm = readerMap.get(term.index).getDocumentByAvgSize(docId);
				final double b = 0.75;
				final double k = 1.2;
				score += idf*((tf*(k+1))/(tf+k*(1-b+b*dlm))); 
			}
		}
		return score;
	}

	private double getCustomScore(List<OperandQuery> terms, Integer docId) {
		double score = 0;
		double delta;
		double bonus = 1;
		for (OperandQuery term: terms) {
			Map<Integer, Integer> posting = readerMap.get(term.index).getIntegerPostings(term.value);
			if (posting == null) continue;
			if (posting.containsKey(docId)) {
				double tf = Math.log(1 + posting.get(docId));
				double df = posting.size()-1;
				double idf = Math.log10((numDocsMap.get(term.index)-df+0.5)/(df+0.5));
				double dlm = readerMap.get(term.index).getDocumentByAvgSize(docId);
				final double b = 0.75;
				final double k = 1.2;
				if (term.index == Index.AUTHOR) {
					delta = 5;
				} else if (term.index == Index.CATEGORY) {
					delta = 3;
				} else if (term.index == Index.PLACE) {
					delta = 2;
				} else {
					delta = 0;
				} 
				score += idf*(delta + ((tf*(k+1))/(tf+k*(1-b+b*dlm))));
				bonus = bonus + 0.1;
			}
		}
		int size = terms.size();
		size -= size/5;
		if (bonus >= size) {
			bonus += 0.3;
		}
		score = score*bonus;
		return score;
	}
	
	private boolean isPresent(String fileName, String phrase) {
		String absFileName = mainDir.getAbsolutePath() + File.separator + fileName;
		try {
			reader = new BufferedReader(new FileReader(absFileName));
			String line = getLine(reader);
			StringBuffer content = new StringBuffer();
	    	while(line != null) {
	    		content.append(line.trim().toLowerCase());
	    		content.append(" ");
	    		line = getLine(reader);
	    	}
	    	if (content.toString().contains(phrase)) {
    			return true;
    		}
	    	return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void generateSnippet(String fileName) {
		String absFileName = mainDir.getAbsolutePath() + File.separator + fileName;
		try {
			String temp, temp2;
			List<OperandQuery> terms = new ArrayList<OperandQuery>();
			getSnippetQueryTerms(query, terms);
			reader = new BufferedReader(new FileReader(absFileName));
			stream.println("Result Title: " + boldSearchWords(getTitle(reader).toUpperCase(), terms));
			String line1 = getLine(reader);
			temp = line1;
			temp2 = getLine(reader);
			line1 = temp;
			while(line1 !=null) {
				line1 = boldSearchWords(line1, terms);
				if(line1.contains("<b>")) break;
				line1 = getLine(reader);
			}
			if(line1 == null) {
				line1 = temp;
			}
			//if (line1.length() > 60) line1 = line1.substring(0, 57) + "...";
			String line2 = getLine(reader);
			if(line2 == null) line2 = temp2;
			line2 = boldSearchWords(line2, terms);
			//if (line2.length() > 60) line2 = line2.substring(0, 57) + "...";
			stream.println("Result Snippet:\n" + line1 + "\n" + line2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getTitle(BufferedReader reader) throws IOException{
		return getLine(reader);
	}
	
	private static String getLine(BufferedReader reader) throws IOException{
		String line = null;
		while ((line = reader.readLine()) == null || line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")) {
			if(line == null) {
				return line;
			}
		}
		return line.trim();
	}
	
	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		//TODO: IMPLEMENT THIS METHOD
		try {
			ArrayList<Pair> pairs = parseQueryFile(queryFile);
			HashMap<String, List<ScoredDocument>> scoreMap = new HashMap<String, List<ScoredDocument>>();
			for(Pair pair: pairs) {
				Set<Integer> docIds = getDocs(pair.getQuery());
				docIds.remove(-1);
				List<OperandQuery> terms = new ArrayList<OperandQuery>();
				getQueryTerms(pair.getQuery(), terms);
				List<ScoredDocument> sDocs = getTopDocs(terms, docIds, ScoringModel.CUSTOM);
				if(sDocs.size() > 0) {
					scoreMap.put(pair.getQueryID(), sDocs);
				}
			}
			stream.println("numResults=" + scoreMap.size());
			for(Entry<String, List<ScoredDocument>> entry: scoreMap.entrySet()) {
				stream.print(entry.getKey()+":{");
				List<ScoredDocument> sDocs = entry.getValue();
				double maxScore = sDocs.get(sDocs.size()-1).getScore();
				int size = sDocs.size();
				for (int i=size-1; i>=0; i--) {
					ScoredDocument sDoc = sDocs.get(i);
					sDoc.setScore(sDoc.getScore()/maxScore);
					DecimalFormat df = new DecimalFormat("0.00000");
					String fileName = sDoc.getDocName();
					stream.print(fileName + "#" + df.format(sDoc.getScore()));
					if(i != 0) {
						stream.print(", ");
					}
				}
				stream.println("}");
			}
		} catch(Exception e) {
			
		}
	}
	
	private Set<Integer> getDocs(Query q) {
		if (q instanceof OperandQuery) {
			OperandQuery oq = (OperandQuery)q;
			//TODO
			if (!oq.hasQuotes) {
				if (oq.value.contains("?") || oq.value.contains("*")) {
					ArrayList<String> terms = getTerms(wildCard.getWords(oq.value));
					if (terms == null || terms.size() == 0) return null;
					Set<Integer> lset = readerMap.get(oq.index).getPostingsSet(terms.get(0), false);
					Set<Integer> rset;
					for (int i=1; i<terms.size(); i++) {
						rset = readerMap.get(oq.index).getPostingsSet(terms.get(i), false);
						lset = getOrSet(lset, rset);
					}
					return (oq.isNot)? readerMap.get(oq.index).getNotSet(lset):lset;
				} else {
					return (oq.isNot)? readerMap.get(oq.index).getPostingsSet(oq.value, true):
				          readerMap.get(oq.index).getPostingsSet(oq.value, false);
				}
			} else {
				String[] strs = oq.value.split(" ");
				Set<Integer> lset = readerMap.get(oq.index).getPostingsSet(strs[0], false);
				Set<Integer> rset;
				for (int i=1; i<strs.length; i++) {
					rset = readerMap.get(oq.index).getPostingsSet(strs[i], false);
					lset = getAndSet(lset, rset);
				}
				if (lset.contains(-1)) lset.remove(-1);
				Set<Integer> retSet = new HashSet<Integer>();
				for (Integer docId: lset) {
					if (isPresent(readerMap.get(oq.index).getDocument(docId), oq.value)) {
						retSet.add(docId);
					}
				}
				return (oq.isNot)? readerMap.get(oq.index).getNotSet(retSet):retSet;
			}
		} else {
			OperatorQuery oq = (OperatorQuery)q;
			Set<Integer> lSet = getDocs(oq.left);
			Set<Integer> rSet = getDocs(oq.right);
			return (oq.op.equals(Operator.OR))? getOrSet(lSet, rSet):getAndSet(lSet, rSet);
		}
	}
	
	private Set<Integer> getOrSet(Set<Integer> lSet, Set<Integer> rSet) {
		Set<Integer> retSet = new HashSet<Integer>(rSet);
		retSet.addAll(lSet);
		return retSet;
	}

	private Set<Integer> getAndSet(Set<Integer> lSet, Set<Integer> rSet) {
		Set<Integer> retSet = new HashSet<Integer>(rSet);
		retSet.retainAll(lSet);
		return retSet;
	}
	
	private void getQueryTerms(Query q, List<OperandQuery> terms) {
		if (q instanceof OperandQuery) {
			OperandQuery oq = (OperandQuery)q;
			if (!oq.isNot) {
				if (oq.hasQuotes) {
					String[] strs = oq.value.split(" ");
					for (String str: strs) {
						terms.add(new OperandQuery(str));
					}
				} else {
					if (oq.value.contains("?") || oq.value.contains("*")) {
						ArrayList<String> strs = getTerms(wildCard.getWords(oq.value));
						if (strs == null || strs.size() == 0) return;
						for (String str:strs) {
							terms.add(new OperandQuery(str));
						}
					} else {
						terms.add(oq);
					}
				}
			}
		} else {
			getQueryTerms(((OperatorQuery)q).left, terms);
			getQueryTerms(((OperatorQuery)q).right, terms);
		}
	}
	
	private void getSnippetQueryTerms(Query q, List<OperandQuery> terms) {
		if (q instanceof OperandQuery) {
			OperandQuery oq = (OperandQuery)q;
			if (!oq.isNot) {
				if (oq.value.contains("?") || oq.value.contains("*")) {
					ArrayList<String> strs = getTerms(wildCard.getWords(oq.value));
					if (strs == null || strs.size() == 0) return;
					for (String str:strs) {
						terms.add(new OperandQuery(str));
					}
				} else {
					terms.add(oq);
				}
			}
		} else {
			getSnippetQueryTerms(((OperatorQuery)q).left, terms);
			getSnippetQueryTerms(((OperatorQuery)q).right, terms);
		}
	}
	
	private void getTermsForWildCards(Query q, List<OperandQuery> terms) {
		if (q instanceof OperandQuery) {
			OperandQuery oq = (OperandQuery)q;
			if (!oq.isNot) {
				if (oq.hasQuotes) {
					String[] strs = oq.value.split(" ");
					for (String str: strs) {
						terms.add(new OperandQuery(str));
					}
				} else {
					terms.add(oq);
				}
			}
		} else {
			getQueryTerms(((OperatorQuery)q).left, terms);
			getQueryTerms(((OperatorQuery)q).right, terms);
		}
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
	}
	
	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return true;
	}
	
	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		HashMap<String, List<String>> queryTermsMap = new HashMap<String, List<String>>();
		ArrayList<OperandQuery> operandList = new ArrayList<OperandQuery>();
		getTermsForWildCards(query, operandList);
		for(OperandQuery op: operandList) {
			if(op.value.contains("?") || op.value.contains("*")) {
				queryTermsMap.put(op.value, getTerms(wildCard.getWords(op.value)));
			} else {
				queryTermsMap.put(op.value, null);
			}
		}
		return queryTermsMap;
	}
	
	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}
	
	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		List<String> strings = new ArrayList<String>();
		ArrayList<OperandQuery> operandList = new ArrayList<OperandQuery>();
		getQueryTerms(query, operandList);
		for(OperandQuery op: operandList) {
			List<String> arrayList = autoCorrect.getCorrectedWord(op.value, new ArrayList<String>(trieMap.values()));
			if(arrayList != null) {
				strings.addAll(arrayList);
			}
		}
		if(strings.size() == 0)
			return null;
		else
			return strings;
	}
	
	public ArrayList<Pair> parseQueryFile(File file) throws IOException, QueryParserException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayList<Pair> arrayList = new ArrayList<Pair>();
		String currentLine = br.readLine();;
		int count = Integer.parseInt(currentLine.substring(currentLine.lastIndexOf("=")+1));
		while(count > 0) {
			currentLine = br.readLine();
			String queryID = currentLine.substring(0, currentLine.indexOf(":"));
			String query = currentLine.substring(currentLine.indexOf("{")+1, currentLine.indexOf("}"));
			Pair pair = new Pair(queryID, QueryParser.parse(query, "OR"));
			arrayList.add(pair);
			count--;
		}
		br.close();
		return arrayList;
	}
	
	private ArrayList<String> getTerms(List<Integer> termIds) {
		ArrayList<String> terms = new ArrayList<String>();
		for(int termid: termIds) {
			terms.add(trieMap.get(termid));
		}
		return terms;
	}
	
	private String boldSearchWords(String word, List<OperandQuery> operandQueries) {
		Matcher matcher;
		boolean found;
		for(OperandQuery term: operandQueries){
			matcher = Pattern.compile(Pattern.quote(" "+term.absValue+" "), Pattern.CASE_INSENSITIVE).matcher(word);
			found = matcher.find();
			if(found) {
				word = matcher.replaceAll(" <b>" + matcher.group(0).trim() + "</b> ");
			}
			if(term.value.contains(" ")) {
				matcher = Pattern.compile(Pattern.quote(" "+term.absValue.replaceAll(" ", "-")+" "), Pattern.CASE_INSENSITIVE).matcher(word);
				found = matcher.find();
				if(found) {
					word = matcher.replaceAll(" <b>" + matcher.group(0).trim() + "</b> ");
				}
			}
		}
		return word;
	}
}
