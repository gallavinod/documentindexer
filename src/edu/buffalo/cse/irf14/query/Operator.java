package edu.buffalo.cse.irf14.query;

public enum Operator {
	AND("AND"), OR("OR"), NOT("NOT");
	private String value;
	
	private Operator(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public boolean isNot() {
		return this.equals(NOT);
	}
	
	public static Operator getOperator(String value) {
		if (value.equals("AND")) return Operator.AND;
		if (value.equals("OR")) return Operator.OR;
		if (value.equals("NOT")) return Operator.NOT;
		return null;
	}	
}
