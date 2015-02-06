package edu.buffalo.cse.irf14.query;

public enum Index {
	AUTHOR("Author"), CATEGORY("Category"), PLACE("Place"), TERM("Term");
	
	private String value;
	private Index(String value) {
		this.value = value;
	}
	public String toString() {
		return value;
	}

	public static Index getIndex(String value) {
		if (value.equalsIgnoreCase("Author")) {
			return Index.AUTHOR;
		} else if (value.equalsIgnoreCase("Category")) {
			return Index.CATEGORY;
		} else if (value.equalsIgnoreCase("Place")) {
			return Index.PLACE;
		} else if (value.equalsIgnoreCase("Term")) {
			return Index.TERM;
		}
		return null;
	}
		
}
