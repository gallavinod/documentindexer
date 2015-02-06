package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.Trie;
import edu.buffalo.cse.irf14.query.WildCard;

public class WildCardTest {

	@Test
	public void testGetWords() {
		Trie trie = new Trie();
		trie.insert("Sabatons");
		trie.insert("Sabatonz");
		trie.insert("Sabulous");
		trie.insert("Sabukous");
		trie.insert("Sabering");
		trie.insert("Sackbuts");
		trie.insert("Sacksful");
		WildCard wildCard = new WildCard(trie);
		ArrayList<Integer> arrayList = wildCard.getWords("Saba?ons");
		assertEquals(trie.getId("Sabatons"), arrayList.get(0).intValue());
		arrayList = wildCard.getWords("Sabu?ous");
		assertEquals(trie.getId("Sabulous"), arrayList.get(0).intValue());
		assertEquals(trie.getId("Sabukous"), arrayList.get(1).intValue());
		arrayList = wildCard.getWords("Sack*");
		assertEquals(trie.getId("Sackbuts"), arrayList.get(0).intValue());
		assertEquals(trie.getId("Sacksful"), arrayList.get(1).intValue());
		arrayList = wildCard.getWords("S?ck*s");
		assertEquals(trie.getId("Sackbuts"), arrayList.get(0).intValue());
		arrayList = wildCard.getWords("S*g");
		assertEquals(trie.getId("Sabering"), arrayList.get(0).intValue());
		arrayList = wildCard.getWords("Sabaton?");
		assertEquals(trie.getId("Sabatons"), arrayList.get(0).intValue());
		assertEquals(trie.getId("Sabatonz"), arrayList.get(1).intValue());
		arrayList = wildCard.getWords("*s");
		assertEquals(trie.getId("Sabatons"), arrayList.get(0).intValue());
	}
}
