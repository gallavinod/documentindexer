package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TFImplDate extends TokenFilter {
	
	private static final String regex1 = "(\\b\\d{1,2}\\D{0,3})\\b((?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?))\\s(\\d{4})";
	private static final String regex2 = "\\b((?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?))\\s?\\b(\\d{4}|\\d{1,2})?\\,?\\s?(\\d{4})?";
	private static final String regex3 = "\\b(\\d{1,4})\\s*(BC|B\\.C|AD|A\\.D)";
	private static final String regex4 = "(\\s\\d{4}\\s)";
	private static final String regex5 = "(\\d{1,2})\\:(\\d{1,2})\\s*(am|AM|PM|pm)";
	private static final String regex6 = "(\\d{1,2}\\:\\d{1,2}\\:\\d{1,2})*\\s*(\\w)*\\s*\\w*\\s*\\w*\\,\\s(\\d{8})";
	private static final String regex7 = "(\\d{4})\\-(\\d{1,2})";

	private static ArrayList<String> regexList = generateRegexList();
	private static HashMap<String, String> monthMap = generateMonthMap(); 

	private Pattern pattern;
	private Matcher matcher;
	private boolean found;
	
	public TFImplDate() {
		super();
	}
	
	public TFImplDate(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		if(tokenStream.hasNext()) {
			tokenStream.next();
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
	
	public String parse(String text) {
		int index=0;
		
		for (String regex: regexList) {
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(text);
			found = matcher.find();
			if (found && index == 0) {
				text = matcher.replaceAll(matcher.group(4) + monthMap.get(matcher.group(2)) + convertDay(matcher.group(1)));
			}else if (found && index == 1) {
				String str1 = matcher.group(4);
				String str2 = matcher.group(3);
				if(str1 == null && str2 != null) {
					if(str2.length()>2) {
						text = matcher.replaceAll(str2 + monthMap.get(matcher.group(1)) + "01 ");
					} else {
						text = matcher.replaceAll("1900" + monthMap.get(matcher.group(1)) + convertDay(str2)+ " ");
					}
				} else if(str2 == null && str1 == null){
					text = matcher.replaceAll("1900" + monthMap.get(matcher.group(1)) + "01 ");
				} else {
				    text = matcher.replaceAll(str1 + monthMap.get(matcher.group(1)) + convertDay(str2));
				}
			} else if (found && index == 2) {
				if(matcher.group(2).equals("A.D") || matcher.group(2).equals("AD"))
					text = matcher.replaceAll(String.format("%04d", Integer.parseInt(matcher.group(1))) + "0101");
				else
					text = matcher.replaceAll("-" + String.format("%04d", Integer.parseInt(matcher.group(1))) + "0101");
			} else if (found && index == 3) {
				text = matcher.replaceAll(" " + matcher.group(1).trim() + "0101" + " ");
			} else if (found && index == 4) {
				int extraHrs = 0;
				if (matcher.group(3).equals("pm") || matcher.group(3).equals("PM"))
					extraHrs = 12;
				int time = Integer.parseInt(matcher.group(1));
				text = matcher.replaceAll( time + extraHrs + ":" + matcher.group(2) + ":00");
			} else if (found && index == 5) {
				text = matcher.replaceAll(matcher.group(3) + "XXX" + matcher.group(1));
			} else if (found && index == 6) {
				text = matcher.replaceAll(matcher.group(1) + "0101" + "-" + matcher.group(1).substring(0, 2) + matcher.group(2) + "0101");
			}
			index++;
		}

		return text;
		}
	
	private String convertDay(String day) {
		day = day.trim();
		if (day.length() == 1) {
			return "0" + day;
		}
		return day;
	}
	
	private static ArrayList<String> generateRegexList() {
		ArrayList<String> regexList = new ArrayList<String>();
		regexList.add(regex1);
		regexList.add(regex2);
		regexList.add(regex3);
		regexList.add(regex4);
		regexList.add(regex5);
		regexList.add(regex6);
		regexList.add(regex7);
		return regexList;
	}
	
	private static HashMap<String, String> generateMonthMap() {
		HashMap<String, String> monthMap = new HashMap<String, String>();
		monthMap.put("January", "01");
		monthMap.put("Jan", "01");
		monthMap.put("February", "02");
		monthMap.put("Feb", "02");
		monthMap.put("March", "03");
		monthMap.put("Mar", "03");
		monthMap.put("April", "04");
		monthMap.put("Apr", "04");
		monthMap.put("May", "05");
		monthMap.put("June", "06");
		monthMap.put("Jun", "06");
		monthMap.put("July", "07");
		monthMap.put("Jul", "07");
		monthMap.put("August", "08");
		monthMap.put("Aug", "08");
		monthMap.put("September", "09");
		monthMap.put("Sep", "09");
		monthMap.put("October", "10");
		monthMap.put("Oct", "10");
		monthMap.put("November", "11");
		monthMap.put("Nov", "11");
		monthMap.put("December", "12");
		monthMap.put("Dec", "12");
		return monthMap;
	}
}
