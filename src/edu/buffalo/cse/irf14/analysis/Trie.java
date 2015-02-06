package edu.buffalo.cse.irf14.analysis;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Trie implements Serializable {
	HashMap<Character, Trie> map;
	int termid;
	static int index = 0;
	
	public Trie() {
		map = new HashMap<Character, Trie>();
		termid = -1;
	}
	
	public int insert(String str) {
		char[] chars = str.toCharArray();
		Trie trie = this;
		HashMap<Character, Trie> hmap;
		for (char ch: chars) {
			hmap = trie.map;
			if (hmap.containsKey(ch)) {
				trie = hmap.get(ch);
			} else {
				trie = new Trie();
				hmap.put(ch, trie);
			}
		}
		if (trie.termid == -1)
			trie.termid = index++;
		return trie.termid;
	}
	
	public int getId(String str) {
		char[] chars = str.toCharArray();
		Trie trie = this;
		HashMap<Character, Trie> hmap;
		for (char ch: chars) {
			hmap = trie.map;
			if (hmap.containsKey(ch)) {
				trie = hmap.get(ch);
			} else {
				return -1;
			}
		}
		return trie.termid;
	}
	
	public HashMap<Character, Trie> getMap() {
		return map;
	}
	
	public int getTermId() {
		return termid;
	}
}