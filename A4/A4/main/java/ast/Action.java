package ast;

import parse.TokenType;

/**
 * Can have one or zero child
 * 
 * @author AndreyWYao
 *
 */
public class Action extends AbstractNode {

	public Action(TokenType type) {
		super(type);
	}

	public Action(TokenType type, Expr e) {
		super(type);
		assert type == TokenType.SERVE;
		children.add(e);
		e.setParent(this);
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		if(value == TokenType.SERVE) {
			sb.append("serve[");
			children.get(0).prettyPrint(sb);
			sb.append("]");
		} else {
			sb.append(value.toString());
		}
		return sb;
	}

	@Override
	public Node shallowClone() {
		return new Action(value);
	}

}
