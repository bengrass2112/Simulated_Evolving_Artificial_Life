package ast;

import parse.TokenType;

public class Update extends AbstractNode {

	public Update(Expr l, TokenType type, Expr r) {
		super(l, type, r);
	}

	private Update(TokenType type) {
		super(type);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		children.get(0).prettyPrint(sb);
		sb.append(" := ");
		children.get(1).prettyPrint(sb);
		sb.append(" ");
		return sb;
	}

	@Override
	public Node shallowClone() {
		return new Update(value);
	}
}
