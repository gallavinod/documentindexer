package edu.buffalo.cse.irf14.analysis;

public class TFImplSymbol extends TokenFilter {
	String text;
	Token token;
	
	public TFImplSymbol(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO
		if (tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			if (!text.matches("\\S*[\\p{Punct}]\\S*")) return true;
			
			if (text.contains("'")) {
				text = apostropheFilter(text);
			}
			if (text.matches("\\S*[.?!]+$")) {
				text = text.replaceAll("[.?!]+$","");
			}
			if (text.contains("-")) {
				text = hyphenFilter(text);
			}
			if (text.trim().equals("")) {
				tokenStream.remove();
			} else {
				token.setTermText(text);
			}
			/*tokenStream.remove();
			for(String s: text.split("\\s+")) {
				tokenStream.insert(new Token(s));
			}*/
			return true;
		}
		return false;
	}

	public String apostropheFilter(String text) {
		//TODO http://en.wikipedia.org/wiki/Wikipedia:List_of_English_contractions
		if (text.equals("let's")) return "let us";
		if (text.equals("shan't")) return "shall not";
		if (text.equals("won't")) return "will not";
		//if (text.equals("it's")) text = text.replaceAll("it's", "it is");
		if (text.endsWith("'s")) {
			text = text.replaceAll("'s", "");
		} else if (text.endsWith("'em")) {
			text = text.replaceAll("'em", " them");
		} else if (text.endsWith("'ve")) {
			text = text.replaceAll("'ve", " have");
		} else if (text.endsWith("n't")) {
			text = text.replaceAll("n't", " not");
		} else if (text.endsWith("'re")) {
			text = text.replaceAll("'re", " are");
		} else if (text.endsWith("'d")) {
			text = text.replaceAll("'d", " would");
		} else if (text.endsWith("'m")) {
			text = text.replaceAll("'m", " am");
		} else if (text.endsWith("'ll")) {
			text = text.replaceAll("'ll", " will");
		} else if (text.contains("\'")) {
			text = text.replaceAll("\'", "");
		}
		return text.trim();
	}

	public String hyphenFilter(String text) {
		if (text.matches("[\\p{Alnum}]+-[\\p{Alnum}]+")) {
			text = text.replaceAll("^([A-Za-z]+)(-)([A-Za-z]+)$", "$1 $3");
		} 
		text = text.replaceAll("^-+","");
		text = text.replaceAll("-+$","");
		return text;
	}
	
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}

}