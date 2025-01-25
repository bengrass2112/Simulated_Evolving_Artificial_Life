package ast;

import java.util.Random;

import ast.Mutate.MutationType;

/** A data structure representing a critter program. */
public class ProgramImpl extends AbstractNode implements Program {

    public ProgramImpl() {
        super();
        value = null;
    }

    public Program mutate() {

        // select random index to mutate
        int index = (int) Math.random() * this.size();

        // create random mutation
        Mutation m = null;
        MutationType mt = MutationType.values()[new Random().nextInt(MutationType.values().length)];

        switch (mt) {
        case REPLACE:
            m = MutationFactory.getReplace();
            break;
        case SWAP:
            m = MutationFactory.getSwap();
            break;
        case TRANSFORM:
            m = MutationFactory.getTransform();
            break;
        }

        return mutate(index, m);

    }

    public Program mutate(int index, Mutation m) {

        if (index >= this.size()) {
            throw new IndexOutOfBoundsException("Index exceeds number of node in parse tree");
        } else {
            return ((Mutate) m).mutator(this, index);
        }

    }

    public void addRule(Rule r) {
        children.add(r);
		r.setParent(this);
    }

    @Override
    public Node shallowClone() {
        return new ProgramImpl();
    }

    @Override
    public StringBuilder prettyPrint(StringBuilder sb) {
        for (Node child : children) {
            child.prettyPrint(sb);
            sb.append("\n");
        }
        return sb;
    }
}
