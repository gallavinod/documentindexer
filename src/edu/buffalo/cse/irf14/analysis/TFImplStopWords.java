package edu.buffalo.cse.irf14.analysis;

import java.util.HashSet;
import java.util.Set;

public class TFImplStopWords extends TokenFilter {
	
	private static Set<String> stopWords;
	String text;
	Token token;
	public static final String stopWordString = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
	
	//Adding stop words
	//Stop words from 'http://www.textfixer.com/resources/common-english-words.txt'
	static {
		stopWords = new HashSet<String>();
		String[] stopWordStrings = stopWordString.split(",");
		for (String stopWord: stopWordStrings) {
			stopWords.add(stopWord);
		}
	}
	
	public TFImplStopWords(TokenStream stream) {
		super(stream);
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (tokenStream.hasNext()) {
			token = tokenStream.next();
			text = token.getTermText();
			if (stopWords.contains(text)) tokenStream.remove();
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
