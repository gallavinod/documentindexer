package edu.buffalo.cse.irf14.query;

public class OperandQuery extends Query {
	public Index index;
	public String value;
	public String absValue;
	public boolean hasQuotes;
	public boolean isNot;

	public OperandQuery() {
		this(null, Index.TERM);
	}
	
	public OperandQuery(String value) {
		this(value, Index.TERM);
	}
	
	public OperandQuery(String value, Index index) {
		this.absValue = value;
		if (value.contains("-")) {
			this.hasQuotes = true;
			value = value.replaceAll("-", " ");
		}
		this.value = value.toLowerCase();
		this.index = index;
	}
	
	@Override
	public void toString(StringBuilder sb) {
		if (isNot) sb.append("<");
		sb.append(index.toString());
		sb.append(":");
		if(hasQuotes) sb.append("\"");
		sb.append(absValue);
		if(hasQuotes) sb.append("\"");
		if (isNot) sb.append(">");
	}
	
	@Override
	public void setIndex(Index index) {
		this.index = index;
	}

	@Override
	public void toggle() {
		isNot = (isNot == false);
	}
}
