package ast;

import parse.TokenType;

/**
 * A class that is not supposed to have any children
 * 
 * requires children.size()==0
 * 
 * It can be number, smell
 *
 */
public class Factor extends Expr {

    protected int number;

    public Factor(TokenType value, int number) {
        super(value);
        this.number = number;
    }

    public Factor(TokenType value) {
        super(value);
        // assert (value == TokenType.NUM || value == TokenType.SMELL);
    }

	public int number() {
		return number;
	}

    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
        switch (value) {
        case NUM:
            sb.append(number);
            break;
        case SMELL:
            sb.append("SMELL");
            break;
        default:
            assert (false);
        }
        return sb;
    }

    @Override
    public Node shallowClone() {
        return new Factor(value, number);
    }

}
