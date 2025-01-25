package ast;

import parse.TokenType;

/** A critter program expression that has an integer value. */
public class Expr extends AbstractNode {

	protected Expr(TokenType type) {
		super(type);
	}

	public Expr(Expr left, TokenType value, Expr right) {
		super(left, value, right);
	}
	

	@Override
	public Node shallowClone() {
		return new Expr(value);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (parent != null && (parent.value() == TokenType.MUL || parent.value() == TokenType.MOD
				|| parent.value() == TokenType.DIV) && (value == TokenType.PLUS || value == TokenType.MINUS)) {
			sb.append("(");
			children.get(0).prettyPrint(sb);
			sb.append(value.toString());
			children.get(1).prettyPrint(sb);
			sb.append(")");
		} else {
			children.get(0).prettyPrint(sb);
			sb.append(" " + value.toString() + " ");
			children.get(1).prettyPrint(sb);
		}
		return sb;
	}

}
