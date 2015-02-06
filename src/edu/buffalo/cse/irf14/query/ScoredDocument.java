package edu.buffalo.cse.irf14.query;

public class ScoredDocument implements Comparable<ScoredDocument> {
	private String docName;
	private double score;
	
	public ScoredDocument(String docName, double score) {
		this.docName = docName;
		this.score = score;
	}

	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	@Override
	public int compareTo(ScoredDocument other) {
		double dif = this.score - other.score;
		return (dif < 0.0)? -1:1;
	}

}
