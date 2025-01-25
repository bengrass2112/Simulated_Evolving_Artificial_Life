package ast;

import parse.TokenType;

/**
 * A representation of a critter rule.
 * 
 * Expects a Condition and command (two children)
 */
public class Rule extends AbstractNode {

	private Rule() {
		super();
		this.value = TokenType.ARR;
	}

	public Rule(Condition left, Command right) {
		super(left, TokenType.ARR, right);
	}

	@Override
	public Node shallowClone() {
		return new Rule();
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		children.get(0).prettyPrint(sb);
		sb.append(value.toString() + " ");
		children.get(1).prettyPrint(sb);
		sb.append(" ;");
		return sb;
	}
}
