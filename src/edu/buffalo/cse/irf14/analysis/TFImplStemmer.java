package edu.buffalo.cse.irf14.analysis;

public class TFImplStemmer extends TokenFilter {
	Token token;
	String text;
	Stemmer stemmer;
	
	public TFImplStemmer(TokenStream stream) {
		super(stream);
		stemmer = new Stemmer();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if(tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			if (!text.matches("[^\\p{Alpha}]\\S*")) {
				token.setTermText(stemmer.stemThis(text.toLowerCase()));
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
