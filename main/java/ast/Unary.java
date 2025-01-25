package ast;

import parse.TokenType;

public class Unary extends Expr {

	/**
	 * A class that represents -factor, mem[expr], Sensor[expr]
	 * 
	 * Requires only one child.
	 */
	private Unary(TokenType type) {
		super(type);
	}

	public Unary(TokenType type, Expr expression) {
		super(type);
		children.add(expression);
		expression.setParent(this);
	}
	public StringBuilder prettyPrint(StringBuilder sb) {
		switch (value){
		case MEM:
			sb.append("mem[");
			children.get(0).prettyPrint(sb);
			sb.append("]");
			break;
		case NEARBY:
		case AHEAD:
		case RANDOM:
			sb.append(value.toString() + " [");
			children.get(0).prettyPrint(sb);
			sb.append("]");
			break;
		case MINUS:
			sb.append("-(");
			children.get(0).prettyPrint(sb);
			sb.append(")");
			break;
		default:
			assert (false);
			break;
		}
		return sb;
	}

	@Override
	public Node shallowClone() {
		return new Unary(value);
	}

}
