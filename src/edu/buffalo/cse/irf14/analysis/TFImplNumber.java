package edu.buffalo.cse.irf14.analysis;

public class TFImplNumber extends TokenFilter {
	Token token;
	String text;
	
	public TFImplNumber(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			text = text.replaceAll("\\d+([,.]*\\d)*","");
			if (text.trim().equals("")) {
				tokenStream.remove();
			} else {
				token.setTermText(text);
			}
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
