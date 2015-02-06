package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import edu.buffalo.cse.irf14.query.AutoCorrect;

/**
 * @author jvallabh, saket
 * Test class to Auto-Correct.
 *
 */
public class AutoCorrectTest {

	@Test
	public void testGetCorrectedWords() {
		AutoCorrect autoCorrect = new AutoCorrect();
		String[] strings = {"Habile", "Habits", "Haboob", "Haceks", "Hacked"
		                              , "Hackee", "Hacker", "Hackie", "Hackle", "Hackly",
		                              "Hading", "Hadith", "Hadjee"};
		ArrayList<String> dictionaryWords = new ArrayList<String>(Arrays.asList(strings));
		assertEquals("Habits", autoCorrect.getCorrectedWord("Habit", dictionaryWords).get(0));
		assertNull(autoCorrect.getCorrectedWord("Habits", dictionaryWords));
		assertEquals("Hackle", autoCorrect.getCorrectedWord("Hackl", dictionaryWords).get(0));
		assertEquals("Hackly", autoCorrect.getCorrectedWord("Hackl", dictionaryWords).get(1));
		assertEquals("Haboob", autoCorrect.getCorrectedWord("Haboo", dictionaryWords).get(0));
		assertEquals("Hackee", autoCorrect.getCorrectedWord("Hadkee", dictionaryWords).get(0));
		assertEquals("Hadjee", autoCorrect.getCorrectedWord("Hadkee", dictionaryWords).get(1));
	}
}
