package ast;

import parse.TokenType;

/**
 * An abstract class representing a Boolean condition in a critter program.
 * 
 * requires 2 children (conditions)
 */
public abstract class Condition extends AbstractNode {


	public Condition(TokenType type) {
		super(type);
	}

	public Condition() {
		super();
	}

	public Condition(Condition left, TokenType type, Condition right) {
		super(left, type, right);
	}

}
