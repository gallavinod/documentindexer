/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.Stack;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) throws QueryParserException{
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		if (!isValidQuery(userQuery))
			throw new QueryParserException();
		
		try {
			Stack<Query> stack = new Stack<Query>();
			StringBuffer sb = new StringBuffer(userQuery);
			
			while (sb.length() > 0) {
				int len = sb.length();
				if ((sb.charAt(len-1) == '"') || (sb.charAt(len-1) == ')')) {
					if (sb.charAt(len-1) == '"') {
						sb.setLength(len-1);
						int index = sb.lastIndexOf("\"");
						String str = sb.substring(index+1);
						OperandQuery oq = new OperandQuery(str);
						oq.hasQuotes = true;
						stack.push(oq);
						sb.setLength(index);
					} else if (sb.charAt(len-1) == ')') {
						int count = 0, i;
						for (i=len-1; i>0; i--) {
							if (sb.charAt(i) == ')') count++;
							else if (sb.charAt(i) == '(') {
								count--;
								if (count == 0) break;
							}
						}
						setBufferLength(sb, len-1);
						String str = sb.substring(i+1);
						OperatorQuery oq = (OperatorQuery)parse(str.trim(), defaultOperator);
						oq.hasParenths = true;
						stack.push(oq);
						setBufferLength(sb, i);
					}
					len = sb.length();
					if (len <= 0) break; 
					if (sb.charAt(len-1) == ':') {
						int index = sb.lastIndexOf(" ");
						String str = sb.substring(index+1, len-1);
						Index ind = Index.getIndex(str);
						if (ind == null) throw new QueryParserException();
						stack.peek().setIndex(ind);
						setBufferLength(sb, index);
					}
					if (sb.length() <= 0) break;
					while (sb.charAt(sb.length()-1) == ' ') {
						setBufferLength(sb, sb.length()-1);
						//sb = new StringBuffer(sb.toString().trim());
					}
				} else {
					int index = sb.lastIndexOf(" ");
					String str = sb.substring(index+1);
					Operator op = Operator.getOperator(str);
					if (op != null) {
						if (op.isNot()) {
							stack.peek().toggle();
							stack.push(new OperatorQuery(Operator.AND));
						} else {
							stack.push(new OperatorQuery(op));
						}
					} else {
						String[] strs = str.split(":");
						OperandQuery oq = new OperandQuery(strs[strs.length-1]);
						if (strs.length != 1) {
							oq.setIndex(Index.getIndex(strs[0]));
						}
						stack.push(oq);
					}
					setBufferLength(sb, index);
				}
			}
			
			boolean isOpFromStack = false;
			
			Query left = stack.pop();
			while (!stack.isEmpty()) {
				Query root = stack.pop();
				Query right;
				if (root instanceof OperatorQuery) {
					((OperatorQuery) root).left = left;
					right = stack.pop();
					((OperatorQuery) root).right = right;
					left = root;
					isOpFromStack = true;
				} else if (root instanceof OperandQuery) {
					right = root;
					root = new OperatorQuery(Operator.getOperator(defaultOperator));
					if (left instanceof OperandQuery) {
						((OperatorQuery)root).left = left;
						((OperatorQuery)root).right = right;
						left = root;
					} else {
						if (((OperatorQuery)left).right instanceof OperatorQuery) {
							((OperatorQuery)root).hasParenths = ((OperatorQuery)((OperatorQuery)left).right).hasParenths;
							((OperatorQuery)((OperatorQuery)left).right).hasParenths = false;
						}
						if (isOpFromStack) {
							((OperatorQuery)root).hasParenths = true;
						}
						((OperatorQuery)root).left = ((OperatorQuery)left).right;
						((OperatorQuery)root).right = right;
						((OperatorQuery)left).right = root;
					}
					isOpFromStack = false;
				}
			}
			return left;
		} catch (Exception e) {
			throw new QueryParserException();
		}
	}
	
	public static boolean isValidQuery(String str) {
		if (str == null) return false;
		int count = 0;
		int quoteCount = 0;
		char[] chars = str.toCharArray();
		for (char ch: chars) {
			if (ch == '"') quoteCount++;
			if (ch == '(') {
				count++;
			} else if (ch == ')') {
				if (count == 0) return false;
				count--;
			}
		}
		if (count > 0 || quoteCount%2 == 1) return false;
		return true;
	}
	
	public static void setBufferLength(StringBuffer stringBuffer, int length) {
		if (length < 0 ) length = 0;
		stringBuffer.setLength(length);
	}
}
