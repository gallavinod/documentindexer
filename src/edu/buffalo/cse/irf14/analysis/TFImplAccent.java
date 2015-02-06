package edu.buffalo.cse.irf14.analysis;

import java.text.Normalizer;

public class TFImplAccent extends TokenFilter {
	Token token;
	String text;
	
	
	public TFImplAccent(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			//text = removeAccent(text);
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

	public boolean hasAccent(String str) {
		return Normalizer.isNormalized(str, Normalizer.Form.NFD);
	}
	
	public String removeAccent(String str) {
		String nfdString = Normalizer.normalize(str, Normalizer.Form.NFD);
	    nfdString = nfdString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	    
	    return nfdString;
	}
	
}
