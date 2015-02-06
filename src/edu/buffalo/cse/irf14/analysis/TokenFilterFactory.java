/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.List;

import edu.buffalo.cse.irf14.document.FieldNames;


/**
 * Factory class for instantiating a given TokenFilter
 * @author jvallabh, saket, nikhillo
 *
 */
public class TokenFilterFactory {
	
	private static TokenFilterFactory tokenFilterFactory;
	private static HashMap<FieldNames, List<TokenFilterType>> filterMap = new HashMap<FieldNames, List<TokenFilterType>>();
	
	private TokenFilterFactory() {
		
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
	public static TokenFilterFactory getInstance() {
		if(tokenFilterFactory == null) {
			tokenFilterFactory = new TokenFilterFactory();
		}
		return tokenFilterFactory;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		switch(type) {
			case SPECIALCHARS:
				return new TFImplSpecialChar(stream);
			case CAPITALIZATION:
				return new TFImplCapitalization(stream);
			case CAPITALIZATIONMODIFIED:
				return new TFImplCapitalizationModified(stream);
			case NUMERIC:
				return new TFImplNumber(stream);
			case ACCENT:
				return new TFImplAccent(stream);
			case DATE:
				return new TFImplDate(stream);
			case STEMMER:
				return new TFImplStemmer(stream);
			case STOPWORD:
				return new TFImplStopWords(stream);
			case SYMBOL:
				return new TFImplSymbol(stream);
			case PLACE:
				return new TFImplPlace(stream);
			case AUTHOR:
				return new TFImplAuthor(stream);
		}
		
		return null;
	}

	public List<TokenFilterType> getFilterTypes(FieldNames fieldName) {
		return filterMap.get(fieldName);
	}
}
