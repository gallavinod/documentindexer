/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.irf14.analysis.Trie;
import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author jvallabh, saket, nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	private static final String placeFile = "place";
	private static final String categoryFile = "category";
	private static final String authorFile = "author";
	private static final String termFile = "term"; 
	private static final String dictionaryFile = "dictionary";
	private static final String sizeFile = "size";
	private static final String trieFile = "trie";
	private static final String trieMapFile = "trieMap";
	
	private String indexDir;
	private Trie trie;
	private HashMap<Integer, String> trieMap;
	
	private ArrayList<String> fileNameDictionary;
	private ArrayList<Double> fileSizeDictionary;
	private HashMap<Integer, HashMap<Integer, Integer>> placeIndexMap;
	private HashMap<Integer, HashMap<Integer, Integer>> categoryIndexMap; 
	private HashMap<Integer, HashMap<Integer, Integer>> authorIndexMap; 
	private HashMap<Integer, HashMap<Integer, Integer>> termIndexMap;
	
	private AnalyzerFactory factory = AnalyzerFactory.getInstance();
	private Tokenizer tokenizer;
	private int index;
	private double wordCount;

	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		tokenizer = new Tokenizer();
		trie = new Trie();
		fileNameDictionary = new ArrayList<String>();
		fileSizeDictionary = new ArrayList<Double>();
		placeIndexMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		categoryIndexMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		authorIndexMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		termIndexMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		trieMap = new HashMap<Integer, String>();
		index = -1;
		this.indexDir = indexDir;
	}
	
	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		//TODO : YOU MUST IMPLEMENT THIS
		try {
			TokenStream tokenStream = null;
			String[] temp;
			if (fileNameDictionary.contains(d.getField(FieldNames.FILEID)[0])) {
				int fIndex = fileNameDictionary.indexOf(d.getField(FieldNames.FILEID)[0]);
				temp = d.getField(FieldNames.CATEGORY);
				if(temp != null)
					tokenStream = categoryProcessor(temp[0]);
				indexBuilder(tokenStream, categoryIndexMap, fIndex);
				return;
			}
			wordCount = 0;
			index++;
			fileNameDictionary.add(index, d.getField(FieldNames.FILEID)[0]);
			temp = d.getField(FieldNames.PLACE);
			if(temp != null)
				tokenStream = placeProcessor(temp[0]);
			indexBuilder(tokenStream, placeIndexMap);
			
			temp = d.getField(FieldNames.CATEGORY);
			if(temp != null)
				tokenStream = categoryProcessor(temp[0]);
			indexBuilder(tokenStream, categoryIndexMap);
			
			tokenStream = authorProcessor(d.getField(FieldNames.AUTHOR));
			if (tokenStream != null) 
				indexBuilder(tokenStream, authorIndexMap);
			
			temp = d.getFields(FieldNames.TITLE, FieldNames.CONTENT);
			if(temp != null)
				tokenStream = termProcessor(temp);
			indexBuilder(tokenStream, termIndexMap);
			fileSizeDictionary.add(index, wordCount);
		} catch(Exception ex) {
			//System.out.println(fileNameDictionary.get(index));
		}
	}
	
	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		//TODO
		ObjectOutputStream placeIndex = null;
		ObjectOutputStream categoryIndex = null;
		ObjectOutputStream authorIndex = null;
		ObjectOutputStream termIndex = null;
		ObjectOutputStream dictionary = null;
		ObjectOutputStream size = null;
		ObjectOutputStream trie = null;
		ObjectOutputStream trieMap = null;
		try {
			placeIndex = generateObjectStream(placeFile);
			categoryIndex = generateObjectStream(categoryFile);
			authorIndex = generateObjectStream(authorFile);
			termIndex = generateObjectStream(termFile);
			dictionary = generateObjectStream(dictionaryFile);
			size = generateObjectStream(sizeFile);
			trie = generateObjectStream(trieFile);
			trieMap = generateObjectStream(trieMapFile);
			
			writeToDisk(placeIndex, placeIndexMap);
			writeToDisk(categoryIndex, categoryIndexMap);
			writeToDisk(authorIndex, authorIndexMap);
			writeToDisk(termIndex, termIndexMap);
			writeToDisk(dictionary, fileNameDictionary);
			setDocumentByAvgSize();
			writeToDisk(size, fileSizeDictionary);
			writeToDisk(trie, this.trie);
			writeTrieMap(trieMap, this.trieMap);
		} catch(IOException ex) {
			try {
				placeIndex.close();
				categoryIndex.close();
				authorIndex.close();
				termIndex.close();
				dictionary.close();
				trie.close();
				trieMap.close();
			} catch(Exception e) {

			}
			throw new IndexerException();
		}
		
	}

	private void setDocumentByAvgSize() {
		Double avgDictionarySize = getAvgDictionarySize();
		for (int i=0; i<fileSizeDictionary.size(); i++) {
			Double val = fileSizeDictionary.remove(i);
			fileSizeDictionary.add(i, val/avgDictionarySize);
		}
	}
	
	public Double getAvgDictionarySize() {
		double sum = 0;
		for (Double fileSize: fileSizeDictionary) {
			sum += fileSize;
		}
		return sum/((double)fileSizeDictionary.size()) ;
	}
	
	private TokenStream termProcessor(String[] strings) throws TokenizerException {
		String str;
		if(strings.length == 2){
			str = strings[0] + " " + strings[1];
		} else {
			str = strings[0];
		}
		TokenStream tokenStream = tokenizer.consumeWD(str);
		Analyzer analyzer = factory.getAnalyzerForField(FieldNames.CONTENT, tokenStream);
		while (analyzer.increment()) {
		}
		tokenStream.reset();
		wordCount += tokenStream.getSize();
		return tokenStream;
	}
	
	private TokenStream placeProcessor(String str) throws TokenizerException {
		if (str == null)
			return null;
		TokenStream tokenStream = new TokenStream();
		String[] strs = str.split(",");
		for(String s: strs) {
			tokenStream.add(new Token(s.trim()));
		}
		Analyzer analyzer = factory.getAnalyzerForField(FieldNames.PLACE, tokenStream);
		while (analyzer.increment()) {
		}
		tokenStream.reset();
		wordCount += tokenStream.getSize();
		return tokenStream;
	}
	
	private TokenStream authorProcessor(String[] authors) throws TokenizerException {
		if (authors == null)
			return null;
		TokenStream tokenStream = new TokenStream();
		for(String author: authors) {
			String[] strs = author.split(" ");
			for (String str: strs) {
				tokenStream.add(new Token(str));
			}
		}
		Analyzer analyzer = factory.getAnalyzerForField(FieldNames.AUTHOR, tokenStream);
		while (analyzer.increment()) {
		}
		tokenStream.reset();
		wordCount += tokenStream.getSize();
		return tokenStream;
	}
	
	private TokenStream categoryProcessor(String str) {
		if(str == null)
			return null;
		TokenStream tokenStream = new TokenStream();
		tokenStream.add(new Token(str));
		tokenStream.reset();
		wordCount += tokenStream.getSize();
		return tokenStream;
	}
	
	private void indexBuilder(TokenStream tokenStream, HashMap<Integer, HashMap<Integer, Integer>> map) {
		indexBuilder(tokenStream, map, index);
	}
	
	private void indexBuilder(TokenStream tokenStream, HashMap<Integer, HashMap<Integer, Integer>> map, int fIndex) {
		HashMap<Integer, Integer> posting;
		String text;
		int count = 0;
		int termid;
		if(tokenStream != null) {
			while(tokenStream.hasNext()) {
				text = tokenStream.next().toString();
				termid = trie.insert(text);
				trieMap.put(termid, text);
				posting = map.get(termid);
				if(posting == null) {
					posting = new HashMap<Integer, Integer>();
					posting.put(-1, 0);
				} 
				if (posting.containsKey(fIndex)) {
					count = posting.get(fIndex);
				} else {
					count = 0;
				}
				posting.put(-1, posting.get(-1)+1);
				posting.put(fIndex, ++count);
				map.put(termid, posting);
			}
		}
	}

	private ObjectOutputStream generateObjectStream(String fileName) throws FileNotFoundException, IOException {
		return new ObjectOutputStream(new FileOutputStream(new File(indexDir+File.separatorChar+fileName)));
	}
	
	@SuppressWarnings("unchecked")
	private void writeToDisk(ObjectOutputStream objectOutputStream, Object map) throws IOException {
		if(map instanceof HashMap) {
			if(((HashMap<Integer, HashMap<Integer, Integer>>)map).size() != 0) {
				objectOutputStream.writeObject(map);
			}
		} else if(map instanceof ArrayList){
			if(((ArrayList<String>)map).size() != 0) {
				objectOutputStream.writeObject(map);
			}
		} else {
			objectOutputStream.writeObject(map);
		}
		objectOutputStream.close();
	}
	
	@SuppressWarnings("unchecked")
	private void writeTrieMap(ObjectOutputStream objectOutputStream, Object map) throws IOException {
		if(((HashMap<Integer, String>)map).size() != 0) {
			objectOutputStream.writeObject(map);
		}
		objectOutputStream.close();
	}
}
