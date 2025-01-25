package ast;

import java.util.Random;

public abstract class Mutate implements Mutation {

    protected static Random rand = new Random();
    protected MutationType mt;

    public enum MutationType {
        REPLACE, SWAP, TRANSFORM;
    }

    @Override
    public boolean equals(Mutation m) {
        return this.getClass().equals(m.getClass());
    }

    /**
     * Mutates a program p in a unique way
     * 
     * @param p     the program to mutate
     * @param index the node at which to mutate
     * @return a mutated program
     */
    public abstract Program mutator(Program p, int index);

    /**
     * Prints the result of a mutation
     * 
     * @param first  the initial state
     * @param second the final state
     */
    public abstract void printMutation(AbstractNode first, AbstractNode second);

    public void printNoMutation() {
        System.out.println("// The " + mt + " mutation was not successful");
    }

}
