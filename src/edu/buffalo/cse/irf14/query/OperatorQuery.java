package edu.buffalo.cse.irf14.query;

public class OperatorQuery extends Query {
	public Query left;
	public Query right;
	public Operator op;
	public boolean hasParenths;
	
	public OperatorQuery(Operator op) {
		this.op = op;
	}
	
	@Override
	public void toString(StringBuilder sb) {
		if(hasParenths) sb.append("[ ");
		left.toString(sb);
		sb.append(" " + op.getValue() + " ");
		right.toString(sb);
		if(hasParenths) sb.append(" ]");
	}
	
	@Override
	public void setIndex(Index index) {
		left.setIndex(index);
		right.setIndex(index);
	}

	@Override
	public void toggle() {
		if (op == Operator.AND) op = Operator.OR;
		if (op == Operator.OR) op = Operator.AND;
		left.toggle();
		right.toggle();
	}
}
