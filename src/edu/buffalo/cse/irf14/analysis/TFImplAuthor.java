package edu.buffalo.cse.irf14.analysis;

public class TFImplAuthor extends TokenFilter {
	Token token;
	String text;
	
	public TFImplAuthor(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText().toLowerCase();
			token.setTermText(text);
			return true;
		}
		return false;
	}

	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}
}
