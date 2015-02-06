package edu.buffalo.cse.irf14.query;

/**
 * @author jvallabh, saket
 * This class contains Query id and the Query object.
 */
public class Pair {
	
	private String queryID;
	private Query query;
	
	public Pair(String queryID, Query query) {
		this.queryID = queryID;
		this.query = query;
	}
	
	public String getQueryID() {
		return queryID;
	}
	
	public Query getQuery() {
		return query;
	}
}
