package edu.buffalo.cse.irf14.analysis;

public class AnalyzerImpl implements Analyzer {
	
	private TokenFilterFactory tokenFilterFactory;
	private TokenFilter tokenFilter;
	private TokenFilterType[] tokenFilterTypes;
	private TokenStream tokenStream;
	
	public AnalyzerImpl(TokenStream tokenStream, TokenFilterType[] tokenFilterTypes) {
		tokenFilterFactory = TokenFilterFactory.getInstance();
		this.tokenFilterTypes = tokenFilterTypes; 
		this.tokenStream = tokenStream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		for (TokenFilterType tokenFilterType: tokenFilterTypes) {
			tokenFilter = tokenFilterFactory.getFilterByType(tokenFilterType, tokenStream);
			while (tokenFilter.increment()) {
				
			}
			tokenStream.reset();
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}

}
