/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import edu.buffalo.cse.irf14.analysis.Trie;

/**
 * @author jvallabh, saket, nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	private static final String placeFile = "place";
	private static final String categoryFile = "category";
	private static final String authorFile = "author";
	private static final String termFile = "term"; 
	private static final String dictionaryFile = "dictionary";
	private static final String sizeFile = "size";
	private static final String trieFile = "trie";
	private static final String trieMapFile = "trieMap";
	
	private String indexDir;
	private HashMap<Integer, HashMap<Integer, Integer>> map;
	private ArrayList<String> fileNameDictionary;
	private ArrayList<Double> fileSizeDictionary;
	private Trie trie;
	private HashMap<Integer, String> trieMap;
	private double avgDictionarySize = -1;
	
	public IndexReader(String indexDir, IndexType type) {
		//TODO
		this.indexDir = indexDir;
		this.map = getMap(type);
		this.fileNameDictionary = getFileDictionary();
		this.fileSizeDictionary = getSizeDictionary();
		this.trie = getTrie();
		this.trieMap = getTrieMap();
	}
	
	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		//TODO : YOU MUST IMPLEMENT THIS
		return map.size();
	}
	
	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		//TODO: YOU MUST IMPLEMENT THIS
		HashSet<Integer> udocs = new HashSet<Integer>();
		for (Map.Entry<Integer, HashMap<Integer, Integer>> entry: map.entrySet()) {
			HashMap<Integer, Integer> posting = entry.getValue();
			for (Integer key: posting.keySet()) {
				udocs.add(key);
			}
		}
		return (udocs.size() == 0)? 0 : udocs.size()-1;
		//return IndexWriter.fileNameDictionary.size();
	}
	
	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		//TODO:YOU MUST IMPLEMENT THIS
		HashMap<Integer, Integer> posting;
		HashMap<String, Integer> rMap = new HashMap<String, Integer>();
		int termid = trie.getId(term);
		posting = map.get(termid);
		if (posting == null) {
			return null;
		}
		for (Entry<Integer, Integer> entry: posting.entrySet()) {
			if (entry.getKey() != -1) { 
				rMap.put(fileNameDictionary.get(entry.getKey()), entry.getValue());
			}
		}
		return rMap;
	}
	
	public Map<Integer, Integer> getIntegerPostings(String term) {
		return map.get(trie.getId(term));
	}
	
	public Set<Integer> getPostingsSet(String term, boolean isNot) {
		HashMap<Integer, Integer> posting;
		int termid = trie.getId(term);
		posting = map.get(termid);
		if (posting == null) {
			return new HashSet<Integer>();
		}
		Set<Integer> pSet = posting.keySet(); 
		if (!isNot) {
			return pSet;
		} else {
			Set<Integer> rSet = new HashSet<Integer>();
			for (int i=0; i<fileNameDictionary.size(); i++) {
				if (!pSet.contains(i)) {
					rSet.add(i);
				}
			}
			return rSet;
		}
	}
	
	public Set<Integer> getNotSet(Set<Integer> pSet) {
		Set<Integer> rSet = new HashSet<Integer>();
		for (int i=0; i<fileNameDictionary.size(); i++) {
			if (!pSet.contains(i)) {
				rSet.add(i);
			}
		}
		return rSet;
	}
	
	public String getDocument(int docId) {
		return fileNameDictionary.get(docId);
	}
	
	public Double getDocumentByAvgSize(int docId) {
		return fileSizeDictionary.get(docId);
	}
	
	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		if (k <= 0) return null;
		PriorityQueue<TFrequency> pq = new PriorityQueue<TFrequency>();
		int maxSize = k;
		int curSize = 0;
		for (Map.Entry<Integer, HashMap<Integer, Integer>> entry: map.entrySet()) {
			TFrequency tf = new TFrequency(trieMap.get(entry.getKey()), entry.getValue().get(-1));
			if (curSize < maxSize) {
				pq.add(tf);
				curSize++;
			} else {
				if (pq.peek().compareTo(tf) < 0) {
					pq.poll();
					pq.add(tf);
				}
			}
		}
		List<String> retList = new ArrayList<String>();
		while (!pq.isEmpty()) {
			TFrequency tf = pq.poll();
			System.out.println(tf.term + " , " + tf.frequency);
			retList.add(0, tf.term);
		}
		return retList;
	}
	
	private class TFrequency implements Comparable<TFrequency> {
		String term;
		int frequency;
		
		public TFrequency(String term, int frequency) {
			this.term = term;
			this.frequency = frequency;
		}

		@Override
		public int compareTo(TFrequency other) {
			int retVal = this.frequency - other.frequency; 
			return (retVal == 0)? term.compareTo(other.term):retVal;
		}
	}
	
	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		//TODO : BONUS ONLY
		if (terms.length == 0) return null;
		HashMap<Integer, Integer> posting;
		Set<Integer> keySet = new HashSet<Integer>(map.get(trie.getId(terms[0])).keySet());
		keySet.remove(-1);
		Set<Integer> removeSet = new HashSet<Integer>();
		HashMap<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
		HashMap<String, Integer> rMap = new HashMap<String, Integer>();
		Integer val;
		for (Integer key: keySet) {
			tempMap.put(key, 0);
		}
		for (String term: terms) {
			posting = map.get(trie.getId(term));
			if (posting == null) return null;
			for (Integer key: keySet) {
				if (posting.containsKey(key) && (val = posting.get(key)) != 0) {
					val += tempMap.get(key);
					tempMap.put(key, val);
				} else {
					removeSet.add(key);
					tempMap.remove(key);
				}
			}
			keySet.removeAll(removeSet);
		}	
		if (tempMap.isEmpty()) return null;
		
		for (Entry<Integer, Integer> entry: tempMap.entrySet()) {
				System.out.println(fileNameDictionary.get(entry.getKey()) + "  " + entry.getValue());
				rMap.put(fileNameDictionary.get(entry.getKey()), entry.getValue());
		}
		return rMap;
	}
	
	@SuppressWarnings({ "unchecked" })
	private HashMap<Integer, HashMap<Integer, Integer>> getMap(IndexType type) {
		try {
		String fileName = null;
		switch(type) {
			case TERM:
				fileName = termFile;
				break;
			case AUTHOR:
				fileName = authorFile;
				break;
			case CATEGORY:
				fileName = categoryFile;
				break;
			case PLACE:
				fileName = placeFile;
		}
		ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(new File(indexDir + File.separatorChar + fileName)));
		HashMap<Integer, HashMap<Integer, Integer>> map = (HashMap<Integer, HashMap<Integer, Integer>>) objectInputStream.readObject();
		objectInputStream.close();
		return map;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> getFileDictionary() {
		try {
			ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(new File(indexDir + File.separatorChar + dictionaryFile)));
			ArrayList<String> fileDictionary = (ArrayList<String>) objectInputStream.readObject();
			objectInputStream.close();
			return fileDictionary;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return fileNameDictionary;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Double> getSizeDictionary() {
		try {
			ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(new File(indexDir + File.separatorChar + sizeFile)));
			ArrayList<Double> fileDictionary = (ArrayList<Double>) objectInputStream.readObject();
			objectInputStream.close();
			return fileDictionary;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return fileSizeDictionary;
	}
	
	public Trie getTrie() {
		try {
			ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(new File(indexDir + File.separatorChar + trieFile)));
			Trie trie = (Trie) objectInputStream.readObject();
			objectInputStream.close();
			return trie;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return trie;
	}

	@SuppressWarnings("unchecked")
	public HashMap<Integer, String> getTrieMap() {
		try {
			ObjectInputStream objectInputStream =  new ObjectInputStream(new FileInputStream(new File(indexDir + File.separatorChar + trieMapFile)));
			HashMap<Integer, String> trieMap = (HashMap<Integer, String>) objectInputStream.readObject();
			objectInputStream.close();
			return trieMap;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return trieMap;
	}
}
