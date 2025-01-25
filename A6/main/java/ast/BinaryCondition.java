package ast;

import parse.TokenType;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 * 
 * class invar: children.size() ==2
 */
public class BinaryCondition extends Condition {

    /**
     * Create an AST representation of l op r.
     *
     * @param l
     * @param op
     * @param r
     */
	public BinaryCondition(Condition l, TokenType type, Condition r) {
		super(l, type, r);
    }

	// Create a binary condition with no children
	private BinaryCondition(TokenType type) {
		super(type);
	}

	@Override
	public Node shallowClone() {
		return new BinaryCondition(value);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if (parent.value() == TokenType.AND && value == TokenType.OR) {
			sb.append("{");
			children.get(0).prettyPrint(sb);
			sb.append(value.toString() + " ");
			children.get(1).prettyPrint(sb);
			sb.append("}");
		} else {
			children.get(0).prettyPrint(sb);
			sb.append(value.toString() + " ");
			children.get(1).prettyPrint(sb);
		}
		sb.append(" ");
		return sb;
	}
}
