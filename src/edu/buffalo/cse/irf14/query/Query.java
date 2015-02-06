package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a parsed query
 * @author jvallabh, saket, nikhillo
 *
 */
public abstract class Query {
	
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{ ");
		toString(stringBuilder);
		stringBuilder.append(" }");
		return stringBuilder.toString();
	}
	
	public abstract void toString(StringBuilder sb);
	
	public abstract void setIndex(Index index);

	public abstract void toggle();
}
