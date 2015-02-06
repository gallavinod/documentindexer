/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;

import edu.buffalo.cse.irf14.document.FieldNames;

/**
 * @author jvallabh, saket, nikhillo
 * This factory class is responsible for instantiating "chained" {@link Analyzer} instances
 */
public class AnalyzerFactory {
	
	private static AnalyzerFactory analyzerFactory;
	//private static TokenFilterType[] myList = getFilterList(TokenFilterType.CAPITALIZATION, TokenFilterType.STOPWORD, TokenFilterType.NUMERIC, TokenFilterType.SYMBOL, TokenFilterType.SPECIALCHARS);
	//private static TokenFilterType[] myList = getFilterList(TokenFilterType.STOPWORD, TokenFilterType.CAPITALIZATION, TokenFilterType.NUMERIC, TokenFilterType.SYMBOL, TokenFilterType.SPECIALCHARS, TokenFilterType.STOPWORD);
	//8.5private static TokenFilterType[] myList = getFilterList(TokenFilterType.CAPITALIZATION, TokenFilterType.STOPWORD, TokenFilterType.NUMERIC, TokenFilterType.SYMBOL, TokenFilterType.SPECIALCHARS, TokenFilterType.STOPWORD);
	//private static TokenFilterType[] contentList = getFilterList(TokenFilterType.CAPITALIZATIONMODIFIED, TokenFilterType.STOPWORD);
	private static TokenFilterType[] contentList = getFilterList(TokenFilterType.CAPITALIZATIONMODIFIED, TokenFilterType.NUMERIC, TokenFilterType.SYMBOL, TokenFilterType.SPECIALCHARS, TokenFilterType.ACCENT);
	//private static TokenFilterType[] contentList = getFilterList(TokenFilterType.CAPITALIZATION, TokenFilterType.STOPWORD, TokenFilterType.DATE, TokenFilterType.NUMERIC, TokenFilterType.SYMBOL, TokenFilterType.SPECIALCHARS, TokenFilterType.STOPWORD, TokenFilterType.ACCENT);
	private static TokenFilterType[] placeList = getFilterList(TokenFilterType.PLACE);
	private static TokenFilterType[] authorList = getFilterList(TokenFilterType.AUTHOR);
	private static HashMap<FieldNames, TokenFilterType[]> listMap;
	static {
		listMap = new HashMap<FieldNames, TokenFilterType[]>();
		listMap.put(FieldNames.CONTENT, contentList);
		listMap.put(FieldNames.PLACE, placeList);
		listMap.put(FieldNames.AUTHOR, authorList);
	}
	
	private AnalyzerFactory() {
	}
	
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	public static AnalyzerFactory getInstance() {
		if(analyzerFactory == null) {
			analyzerFactory = new AnalyzerFactory();
		}
		return analyzerFactory;
	}
	
	/**
	 * Returns a fully constructed and chained {@link Analyzer} instance
	 * for a given {@link FieldNames} field
	 * Note again that the singleton factory instance allows you to reuse
	 * {@link TokenFilter} instances if need be
	 * @param name: The {@link FieldNames} for which the {@link Analyzer}
	 * is requested
	 * @param TokenStream : Stream for which the Analyzer is requested
	 * @return The built {@link Analyzer} instance for an indexable {@link FieldNames}
	 * null otherwise
	 */
	public Analyzer getAnalyzerForField(FieldNames name, TokenStream stream) {
		//TODO : YOU NEED TO IMPLEMENT THIS METHOD
		return new AnalyzerImpl(stream, listMap.get(name));
	}
	
	
	private static TokenFilterType[] getFilterList(TokenFilterType... filterTypes) {
		return filterTypes;
	}
}
