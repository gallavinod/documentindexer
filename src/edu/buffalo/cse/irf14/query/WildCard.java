package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import edu.buffalo.cse.irf14.analysis.Trie;

/**
 * @author jvallabh, saket
 * This class gets the possible termIds for a given wildcard.
 */
public class WildCard {
	
	private Trie trie;
	
	public WildCard(Trie trie) {
		this.trie = trie;
	}
	
	/**
	 * Returns a list of possible termIds for the given wildcard.
	 * @param wildCard String
	 * @return ArrayList of termIds
	 */
	public ArrayList<Integer> getWords (String wildCard) {
		HashSet<Integer> hashSet = new HashSet<Integer>();
		getWords(wildCard, hashSet, trie);
		return new ArrayList<Integer>(hashSet);
	}
	
	private void getWords (String wildCard, HashSet<Integer> hashSet, Trie trie) {
		if (wildCard == null) return;
		HashMap<Character, Trie> hmap;
		int length = wildCard.length();
		for (int i=0 ; i < length ; i++) {
			hmap = trie.getMap();
			char ch = wildCard.charAt(i);
			if(ch == '?') {
				Set<Entry<Character, Trie>> entrySet  = hmap.entrySet();
				if(entrySet == null) return;
				for(Entry<Character, Trie> entry: entrySet) {
					getWords(wildCard.substring(i+1), hashSet, entry.getValue());
				}
			} else if(ch == '*'){
				Set<Entry<Character, Trie>> entrySet  = hmap.entrySet();
				if(entrySet == null) return;
				for(Entry<Character, Trie> entry: entrySet) {
					getWords(wildCard.substring(i), hashSet, entry.getValue());
					getWords(wildCard.substring(i+1), hashSet, entry.getValue());
				}
			} else if (hmap.containsKey(ch)) {
				trie = hmap.get(ch);
			} else {
				return;
			}
		}
		int index = trie.getTermId();
		if(index != -1) {
			hashSet.add(index);
		}
	}

}
