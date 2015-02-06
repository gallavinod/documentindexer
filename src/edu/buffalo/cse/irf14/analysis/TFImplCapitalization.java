package edu.buffalo.cse.irf14.analysis;

public class TFImplCapitalization extends TokenFilter{
	
	private Token token;
	private String text;
	private boolean firstLetter = true;
	
	public TFImplCapitalization(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if(tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			if(firstLetter) {
				text = text.toLowerCase();
				token.setTermText(text);
			} else {
				if (text.matches("[A-Z]\\S*") && !text.endsWith(".") && tokenStream.hasNext()) {
					Token ntoken = tokenStream.next();
					if (ntoken.getTermText().matches("[A-Z]\\S*")) {
						text = text + " " + ntoken.getTermText();
						token.setTermText(text);
						tokenStream.remove();
					}
				}
			}
			firstLetter = text.endsWith(".");
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
