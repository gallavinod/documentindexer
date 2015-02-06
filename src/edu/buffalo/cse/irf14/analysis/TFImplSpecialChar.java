package edu.buffalo.cse.irf14.analysis;

public class TFImplSpecialChar extends TokenFilter{

	private Token token;
	private String text;
	
	public TFImplSpecialChar(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		if(tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			text = text.replaceAll("[^\\w\\s-.]|_", "");
			if (!(text.matches("-\\S+") || text.matches("[\\d]+-[\\d]+"))) {
				text = text.replaceAll("-", "");
			}
			if (text.trim().equals("")) {
				tokenStream.remove();
			} else { 
				token.setTermText(text);
			}
			return true;
		} else {
		return false;
		}
	}

	@Override
	public TokenStream getStream() {
		return tokenStream;
	}
	

}