/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jvallabh, saket, nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	
	private static final Pattern pattern = Pattern.compile("<AUTHOR>(.*)</AUTHOR>");
	
	private static boolean authorsPresent = false;
	private static boolean isOrgPresent = false;
    private static Matcher matcher;
	
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		// TODO YOU MUST IMPLEMENT THIS
		authorsPresent = false;
		isOrgPresent = false;
		Document document = new Document();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			
			String tempString = getFileID(filename);	
			if(tempString != null) {
				document.setField(FieldNames.FILEID, tempString);
			}
			
			tempString = getCategory(filename);
			if(tempString != null) {
				document.setField(FieldNames.CATEGORY, tempString);
			}
			
			tempString = getTitle(reader);
			if(tempString != null) {
				document.setField(FieldNames.TITLE, tempString);
			}
			
			String firstLine = getLine(reader);
			tempString = parseAuthorLine(firstLine);
			String[] authors = getAuthors(tempString);
			if(authors != null) {
				if (isOrgPresent) {
				document.setField(FieldNames.AUTHOR, authors[0]);	
				document.setField(FieldNames.AUTHORORG, authors[1]);
				} else {
					document.setField(FieldNames.AUTHOR, authors);						
				}
			}
			String content = getContent(firstLine, reader);
			try {
				firstLine = getPlaceAndDateLine(content);
				tempString = getPlace(firstLine);
				if(tempString != null) {
					document.setField(FieldNames.PLACE, tempString);
				}
				tempString = getDate(firstLine);
				if(tempString != null) {
					document.setField(FieldNames.NEWSDATE, tempString);
				}
			} catch(Exception ex) {
				
			}
			tempString = getNewsContent(content);
			if(tempString != null) {
				document.setField(FieldNames.CONTENT, tempString);
			}
			reader.close();
		} catch (Exception ex) {
			try {
				reader.close();
			}catch(Exception e){
				
			}
			throw new ParserException();
		}
		
		return document;
	}
	private static String getTitle(BufferedReader reader) throws IOException{
		return getLine(reader);
	}
	
	private static String getLine(BufferedReader reader) throws IOException{
		String line = null;
		while ((line = reader.readLine()) == null || line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")) {
			if(line == null) {
				break;
			}
		}
		return line;
	}
	
	private static String parseAuthorLine(String line) {
		matcher = pattern.matcher(line);
		if(matcher.find()) {
			authorsPresent = true;
			return matcher.replaceAll(matcher.group(1)).trim();
		}
		return null;
	}
	
    private static String[] getAuthors(String authorLine) throws ParserException {
    	if(authorLine == null) {
    		return null;
    	}
    	authorLine = authorLine.substring(2).trim();
    	String[] authors = authorLine.split("[ ]*and[ ]*");
    	if(authors.length == 1) {
    		authors = authorLine.split("[ ]*,[ ]*");
    		if (authors.length > 1) {
        		isOrgPresent = true;
    		}
    	}
    	/*String[] retAuthors = new String[authors.length];
    	int count = 0;
    	for (String author: authors) {
    		retAuthors[count++] = author.trim();
    	}*/
    	return authors;
    }
    
    private static String getPlaceAndDateLine(String content) {
    	return content.substring(0, content.indexOf("-")-1);
    }
    
    private static String getFileID(String filename) {
    	return filename.substring(filename.lastIndexOf(File.separatorChar)+1);
    }
    
    private static String getCategory(String filename) {
    	String parentPath = filename.substring(0, filename.lastIndexOf(File.separatorChar));
    	return parentPath.substring(parentPath.lastIndexOf(File.separatorChar)+1);
    }
    
    private static String getPlace(String placeDateLine) {
    	return placeDateLine.substring(0, placeDateLine.lastIndexOf(",")).trim();
    }
    
    private static String getDate(String placeDateLine) {
    	return placeDateLine.substring(placeDateLine.lastIndexOf(",") + 1).trim();
    }
    
    private static String getContent(String authorLine, BufferedReader reader) throws IOException {
    	StringBuilder content = new StringBuilder();
    	if(!authorsPresent) {
    		content.append(authorLine);
    	}
    	String line = getLine(reader);
    	while(line != null) {
    		line.trim();
    		content.append(line);
    		content.append(" ");
    		line = getLine(reader);
    	}
    	return content.toString();
    }
    
    private static String getNewsContent(String content) {
    	return content.substring(content.indexOf("-") + 1).trim();
    }   
    
}
