package ast;

import java.util.ArrayList;

import parse.TokenCategory;
import parse.TokenType;

public class MutationReplace extends Mutate {

    public MutationReplace() {
        mt = MutationType.REPLACE;
    }

    /**
     * Replaces specified node with randomly selected subtree of right type. If no
     * subtree of the right type exists, return original program.
     */
    public Program mutator(Program p, int index) {

        AbstractNode replace = (AbstractNode) p.nodeAt(index);

        if (replace.value() == null) {
            printNoMutation();
            return p;
        }

        ArrayList<Node> valid = validNodes(p, replace);
        Node duplicate;

        if (valid.size() == 0) {
            printNoMutation();
            return p;
        } else {
            duplicate = valid.get(rand.nextInt(valid.size())).clone();
        }

        replace(replace, duplicate);
        printMutation(replace, (AbstractNode) duplicate);
        return p;

    }

    /**
     * Returns a list with all nodes in tree of same type as t, excluding Node n
     * 
     * @param t the runtime type of a node
     * @param n the node to be replaced
     * @return a list of nodes of same type as t
     */
    private static ArrayList<Node> validNodes(Program p, AbstractNode n) {

        ArrayList<Node> r = new ArrayList<>();

        for (int i = 0; i < p.size(); i++) {
            AbstractNode j = (AbstractNode) p.nodeAt(i);
            if (j.value != null) {
                TokenCategory tc = j.value.category();

                if (n.value.category() == tc) {
                    if (tc == TokenCategory.OTHER) {
                        if (n.value == TokenType.NUM && j.value == TokenType.NUM) {
                            r.add(j);
                        } else if (n.value == TokenType.MEM && j.value == TokenType.MEM) {
                            r.add(j);
                        }
                    } else {
                        r.add(j);
                    }
                }
            }
        }

        r.remove(n);
        return r;

    }

    /**
     * Replaces the node n1 with a copy of n2
     * 
     * @param n1 the node to be replaced
     * @param n2 the node to replace with
     */
    public void replace(Node n1, Node n2) {

        Node parent = n1.getParent();
        int index = parent.getChildren().indexOf(n1);
        parent.getChildren().set(index, n2);
        n2.setParent(parent);

    }

    @Override
    public void printMutation(AbstractNode first, AbstractNode second) {
        if (first.value() == TokenType.NUM) {
            System.out.println(
                    "// Replace mutation: the node " + ((Factor) first).number + " is now " + ((Factor) second));
        } else {
            System.out.println("// Replace mutation: the node " + first + " is now " + second);
        }

    }

}
