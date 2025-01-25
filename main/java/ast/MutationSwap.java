package ast;

import java.util.ArrayList;

import parse.TokenCategory;
import parse.TokenType;

public class MutationSwap extends Mutate {

    public MutationSwap() {
        mt = MutationType.SWAP;
    }

    /**
     * Swaps two child nodes of the node at index if such a swap is valid
     */
    public Program mutator(Program p, int index) {

        AbstractNode n = (AbstractNode) p.nodeAt(index);

        if (n.value() == null) {
            if (n.getChildren().size() == 0) {
                printNoMutation();
                return p;
            } else { // if given program node
                ArrayList<Node> children = (ArrayList<Node>) n.getChildren();
                swap(children);
                return p;
            }
        }

        TokenCategory tc = n.value.category();
        TokenType tt = n.value;

        boolean swappable = n instanceof ProgramImpl || tc == TokenCategory.ADDOP || tc == TokenCategory.MULOP
                || tc == TokenCategory.RELOP
                || (tc == TokenCategory.OTHER && (tt == TokenType.AND || tt == TokenType.OR));

        if (!swappable) {
            return p;
        }

        ArrayList<Node> children = (ArrayList<Node>) n.getChildren();

        if (children.size() > 1) {
            swap(children);
        }

        return p;

    }

    /**
     * Swaps position of two rules of a node
     * 
     * @param l a list of children
     */
    public void swap(ArrayList<Node> l) {

        AbstractNode n1;
        AbstractNode n2;

        int one;
        int two;

        do {
            one = rand.nextInt(l.size());
            two = rand.nextInt(l.size());
        } while (one == two);

        n1 = (AbstractNode) l.get(one);
        n2 = (AbstractNode) l.get(two);

        printMutation(n1, n2);

        l.set(one, n2);
        l.set(two, n1);

    }

    @Override
    public void printMutation(AbstractNode first, AbstractNode second) {
        if (first.value() == TokenType.NUM && second.value() == TokenType.NUM) {
            System.out.println("// Swap mutation: the nodes " + ((Factor) first).number + " and " + ((Factor) second)
                    + " were swapped");
        } else {
            System.out.println(
                    "// Swap mutation: the nodes " + first.value() + " and " + second.value() + " were swapped");
        }

    }

}
