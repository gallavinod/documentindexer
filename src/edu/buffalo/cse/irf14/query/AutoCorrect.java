package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.TreeMap;
/**
 * @author jvallabh, saket
 * This class is used to Auto-Correct the words.
 *
 */
public class AutoCorrect {
	
	private TreeMap<Integer, ArrayList<String>> distanceMap;
    
	/**
	 * This function takes the wrong word as input and returns the list of all possible correct words.
	 * @param wrongWord String that should be corrected.
	 * @param dictionaryWords List of Correct Words(Words from dictionary)
	 * @return List of possible corrected words.
	 */
	public ArrayList<String> getCorrectedWord(String wrongWord, ArrayList<String> dictionaryWords) {
		buildDistanceMap(wrongWord, dictionaryWords);
		if(distanceMap.firstKey() == 0){
			return null;
		} else {
			return distanceMap.firstEntry().getValue();
		}
	}
	
	private void buildDistanceMap(String wrongWord, ArrayList<String> dictionaryWords) {
		distanceMap = new TreeMap<Integer, ArrayList<String>>();
		for(String word: dictionaryWords) {
			int distance = distance(wrongWord, word);
			if(!distanceMap.containsKey(distance)) {
				distanceMap.put(distance, new ArrayList<String>());
			}
			distanceMap.get(distance).add(word);
		}
	}
	
    private int distance(String a, String b) {
        int[][] distance = new int[a.length() + 1][b.length() + 1];        
        
        for (int i = 0; i <= a.length(); i++)  {
            distance[i][0] = i;                 
        }
        
        for (int j = 1; j <= b.length(); j++) {                                
            distance[0][j] = j;                
        }
 
        for (int i = 1; i <= a.length(); i++) {                                
            for (int j = 1; j <= b.length(); j++) {                             
                distance[i][j] = Math.min(                                        
                        distance[i - 1][j] + 1,                                  
                        Math.min(distance[i][j - 1] + 1,                                  
                        distance[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1)));
            }
        }
        return distance[a.length()][b.length()];
    }
}