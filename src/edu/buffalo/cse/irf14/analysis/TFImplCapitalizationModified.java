package edu.buffalo.cse.irf14.analysis;

public class TFImplCapitalizationModified extends TokenFilter{
	
	private Token token;
	private String text;
	
	public TFImplCapitalizationModified(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if(tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			text = text.toLowerCase();
			token.setTermText(text);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}
}