/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

/**
 * @author jvallabh, saket, nikhillo
 * Class that converts a given string into a {@link TokenStream} instance
 */
public class Tokenizer {
	String delim;
	boolean isSpecial = false;
	boolean hasNums = false;
	TFImplDate tokenFilter = new TFImplDate();
	/**
	 * Default constructor. Assumes tokens are whitespace delimited
	 */
	public Tokenizer() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		this.delim = " ";
	}
	
	/**
	 * Overloaded constructor. Creates the tokenizer with the given delimiter
	 * @param delim : The delimiter to be used
	 */
	public Tokenizer(String delim) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		this.delim = delim;
	}
	
	/**
	 * Method to convert the given string into a TokenStream instance.
	 * This must only break it into tokens and initialize the stream.
	 * No other processing must be performed. Also the number of tokens
	 * would be determined by the string and the delimiter.
	 * So if the string were "hello world" with a whitespace delimited
	 * tokenizer, you would get two tokens in the stream. But for the same
	 * text used with lets say "~" as a delimiter would return just one
	 * token in the stream.
	 * @param str : The string to be consumed
	 * @return : The converted TokenStream as defined above
	 * @throws TokenizerException : In case any exception occurs during
	 * tokenization
	 */
	public TokenStream consume(String str) throws TokenizerException {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if(str == null || str.equals("")) {
			throw new TokenizerException();
		}
		try {
			str = tokenFilter.parse(str);
			/*str = processString(str);
			if (str.contains("([A-Z]+[a-z]*)(\\s+)([A-Z]+[a-z]*)")) { 
				isSpecial = true;*/
			//str = str.replaceAll("([A-Z]+[a-z]*)(\\s+)([A-Z]+[a-z]*)","$1"+"XXX"+"$3");
			//}
			String[] strs = str.trim().split("[" + delim + "]+");
			TokenStream tstream = new TokenStream();
			for (String s: strs) {
					//s = s.contains("XXX") ? s.replaceAll("XXX", " ") : s;
				tstream.add(new Token(s));
			}
			//isSpecial = false;
			return tstream;
		} catch (Exception ex) {
			throw new TokenizerException();
		}
	}
	
	public String processString(String str) {
		//Removes all instances of numbers('98.92', '99,000') from the string
		str = str.replaceAll("\\d+([,.]*\\d)*","");
		
		//Removes all accents from string if accented
		if (hasAccent(str)) {
			str = removeAccent(str);
		}
		return str;
	}

	public TokenStream consumeWD(String str) throws TokenizerException {
		if(str == null || str.equals("")) {
			throw new TokenizerException();
		}
		try {
			str = str.replaceAll("-", " ");
			String[] strs = str.trim().split("[" + delim + "]+");
			TokenStream tstream = new TokenStream();
			for (String s: strs) {
				tstream.add(new Token(s));
			}
			return tstream;
		} catch (Exception ex) {
			throw new TokenizerException();
		}
	}
	
	public boolean hasAccent(String str) {
		return Normalizer.isNormalized(str, Normalizer.Form.NFD);
	}
	
	public String removeAccent(String str) {
	    String nfdString = Normalizer.normalize(str, Normalizer.Form.NFD);
	    return nfdString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
}