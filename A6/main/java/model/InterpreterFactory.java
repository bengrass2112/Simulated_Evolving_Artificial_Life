package model;

/** A factory that gives access to instances of Evaluator */
public class InterpreterFactory {

	/**
	 * Return a {@code Evaluator} that can evaluate critter programs from their AST
	 * representation
	 *
	 * @return A critter program evaluator
	 */
	public static Interpreter getInterpreter() {
		return new Interpreter();
	}

}
