package ast;

import java.util.ArrayList;

import parse.TokenCategory;
import parse.TokenType;

public class MutationTransform extends Mutate {

    public MutationTransform() {
        mt = MutationType.TRANSFORM;
    }

    /**
     * Returns a mutated program, where a specified node is randomly transformed to
     * a different node of the same TokenType
     */
    public Program mutator(Program p, int index) {

        AbstractNode n = (AbstractNode) p.nodeAt(index);
        AbstractNode ref = (AbstractNode) n.clone();

        if (n.value() == null) {
            printNoMutation();
            return p;
        }

        TokenCategory tc = n.value.category();
        TokenType tt = n.value;

        switch (tc) {
        case OTHER:
            switch (tt) {
            case NUM:
                int adj = Integer.MAX_VALUE / rand.nextInt();
                if (adj < 0) {
                    ((Factor) n).number -= adj;
                } else {
                    ((Factor) n).number += adj;
                }
                break;
            case AND:
                n.value = TokenType.OR;
                break;
            case OR:
                n.value = TokenType.AND;
                break;
            default:
                break;
            }
            printMutation(ref, n);
            return p;
        case MEMSUGAR:
            break;
        case SENSOR:
            break;
        default:
            n.value = getRandomToken(tc, tt);
            break;
        }

        printMutation(ref, n);
        return p;

    }

    /**
     * Returns a random TokenType of TokenCategory
     * 
     * @param tokenCategory the TokenCategory to pick tokens from
     * @param tokenType     the specific token type of the given node
     * @return a random TokenType
     */
    private TokenType getRandomToken(TokenCategory tokenCategory, TokenType tokenType) {

        ArrayList<TokenType> a = new ArrayList<TokenType>();
        for (TokenType t : TokenType.values()) {
            if (t.category() == tokenCategory) {
                a.add(t);
            }
        }

        a.remove(tokenType);
        return a.get(rand.nextInt(a.size()));

    }

    public void printMutation(AbstractNode first, AbstractNode second) {
        System.out
                .println("// Transform mutation: The node was a " + first.value() + ", but is now a " + second.value());
    }

}
