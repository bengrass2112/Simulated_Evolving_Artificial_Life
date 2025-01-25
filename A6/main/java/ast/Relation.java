package ast;

import parse.TokenType;

public class Relation extends Condition {

	/**
	 * Requires two children
	 * 
	 * @param type
	 */
	private Relation(TokenType type) {
		super(type);
	}

	public Relation(Expr left, TokenType type, Expr right) {
		super(type);
		children.add(left);
		children.add(right);
		left.setParent(this);
		right.setParent(this);
	}

	@Override
	public Node shallowClone() {
		return new Relation(value);
	}

	public StringBuilder prettyPrint(StringBuilder sb) {
		children.get(0).prettyPrint(sb);
		sb.append(" " + value.toString() + " ");
		children.get(1).prettyPrint(sb);
		sb.append(" ");
		return sb;
	}

}
