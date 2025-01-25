package model;

import java.util.List;

import ast.Action;
import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.Factor;
import ast.Node;
import ast.Program;
import ast.Relation;
import ast.Rule;
import ast.Unary;
import ast.Update;
import exceptions.SyntaxError;
import parse.TokenCategory;
import parse.TokenType;

/** Represents the evaluator that interprets AST's from a given Program node */
public class Interpreter {

	/**
	 * Evaluates a program p until an action is resolved or it evaluates 999 times
	 * with no action resolution.
	 */
	public void evaluate(Critter c) {
		Program p = c.program();

		c.loopReset();

		// TODO:
		// If reaches 999 it automatically WAITS, remember to setLast, make an AST? IDK
		while (c.getMemory()[5] < 999) {
			try {
				boolean action = evaluateProgram(p, c);
				if (action)
					break;
			} catch (SyntaxError e) {
				System.out.println(e.getMessage());
			}

			c.getMemory()[5]++;
		}

		if (c.getMemory()[5] == 999) {
			c.waitAction();

//			Factor f1 = new Factor(TokenType.NUM, 1);
//			Factor f2 = new Factor(TokenType.NUM, 1);
//
//			Condition r = new Relation(f1, TokenType.EQ, f2);
//
//			Action a = new Action(TokenType.WAIT);
//			Command comm = new Command();
//			comm.addAction(a);
//
//			Rule rule = new Rule(r, comm);
//
//			c.setLastRule(rule);
		}

	}

	/**
	 * @return true if an action was resolved during this evaluation
	 * @throws SyntaxError if a child of Program p is not a Rule.
	 */
	public boolean evaluateProgram(Program p, Critter c) throws SyntaxError {
		for (int i = 0; i < p.getChildren().size(); i++) {
			Rule r = null;
			try {
				r = (Rule) p.getChildren().get(i);
			} catch (ClassCastException e) {
				throw new SyntaxError("Error encountered while evaluating Program. Expected a Rule but found "
						+ p.getChildren().get(i));
			}

			// If the condition is true, resolve the command, if no action reset i and begin
			// again from the start of Program. If action, set critters lastRule to this
			// rule.
			if (evaluateRule(r, c)) {
				if (resolveCommand(r, c)) {
					c.setLastRule(i);
					return true;
				}
				return false;
			}
		}

		return false;
	}

	/**
	 * @return true if the condition evaluates to true
	 * @throws SyntaxError if the first child of the Rule is not a Condition
	 */
	public boolean evaluateRule(Node r, Critter c) throws SyntaxError {
		try {
			return evaluateCondition((Condition) r.getChildren().get(0), c);
		} catch (ClassCastException e) {
			throw new SyntaxError("Error encountered while evaluating Rule. Expected a Condition but found "
					+ r.getChildren().get(0));
		}

	}

	/**
	 * @return true if the subtree of the condition rooted at this node evaluates to
	 *         true
	 * @throws a special SyntaxError if one of the children of a BinaryCondition is
	 *           not a Condition
	 */
	public boolean evaluateCondition(Condition con, Critter c) throws SyntaxError {
		if (con.value().category() == TokenCategory.RELOP) {
			return evaluateRelation((Relation) con, c);
		} else {
			Condition child1, child2;
			try {
				child1 = (Condition) con.getChildren().get(0);
				child2 = (Condition) con.getChildren().get(1);
			} catch (ClassCastException e) {
				throw new SyntaxError(
						"Error encountered while evaluating Condition. One of the children of BinaryCondition " + c
								+ " was not a Condition.");
			}

			if (con.value() == TokenType.OR) {
				return evaluateCondition(child1, c) || evaluateCondition(child2, c);
			} else {
				return evaluateCondition(child1, c) && evaluateCondition(child2, c);
			}
		}
	}

	/**
	 * @return true if the relation evaluates to true
	 * @throws SyntaxError if the relation operation is not '<', '<=', '==', '!=',
	 *                     '>' or '>='
	 */
	public boolean evaluateRelation(Relation rel, Critter c) throws SyntaxError {
		TokenType relOp = rel.value();
		Node child1 = rel.getChildren().get(0), child2 = rel.getChildren().get(1);

		switch (relOp) {
		case LT:
			return evaluateExpression(child1, c) < evaluateExpression(child2, c);
		case LE:
			return evaluateExpression(child1, c) <= evaluateExpression(child2, c);
		case EQ:
			return evaluateExpression(child1, c) == evaluateExpression(child2, c);
		case NE:
			return evaluateExpression(child1, c) != evaluateExpression(child2, c);
		case GT:
			return evaluateExpression(child1, c) > evaluateExpression(child2, c);
		case GE:
			return evaluateExpression(child1, c) >= evaluateExpression(child2, c);
		default:
			throw new SyntaxError(
					"Error encountered while evaluating Relation. Expected a relation operation but found " + relOp);
		}
	}

	/**
	 * @return The integer representation of an expression
	 * @throws SyntaxError if the arithmetic operation is not '+', '-', '*', '/' or
	 *                     '%'
	 */
	public int evaluateExpression(Node e, Critter c) throws SyntaxError {
		if (e instanceof Factor) {
			return evaluateFactor((Factor) e, c);
		} else if (e instanceof Unary) {
			return evaluateUnary((Unary) e, c);
		} else {
			TokenType op = e.value();
			Node child1 = e.getChildren().get(0), child2 = e.getChildren().get(1);

			switch (op) {
			case PLUS:
				return evaluateExpression(child1, c) + evaluateExpression(child2, c);
			case MINUS:
				return evaluateExpression(child1, c) - evaluateExpression(child2, c);
			case MUL:
				return evaluateExpression(child1, c) * evaluateExpression(child2, c);
			case DIV:
				int num = evaluateExpression(child1, c);
				int denom = evaluateExpression(child2, c);
				if (denom == 0)
					return 0;
				else
					return num / denom;
			case MOD:
				int n = evaluateExpression(child1, c);
				int divisor = evaluateExpression(child2, c);
				if (divisor == 0)
					return 0;
				else
					return n % divisor;
			default:
				throw new SyntaxError(
						"Error encountered while evaluating Expression. Expected an arithmetic operation but found "
								+ op);
			}
		}
	}

	/**
	 * @return 0 if the factor is smell, otherwise returns the numeric value of the
	 *         Factor
	 */
	public int evaluateFactor(Factor f, Critter c) {
		if (f.value() == TokenType.SMELL) {
			return c.smell();
		}
		return f.number();
	}

	/**
	 * @return the numeric representation of the Unary
	 * @throws SyntaxError if the child of Unary u is not an Expression.
	 * @throws a           special SyntaxError if the Unary sensor is not a valid
	 *                     sensor
	 * @throws a           special SyntaxError if the Unary u is not a valid Unary.
	 */
	public int evaluateUnary(Unary u, Critter c) throws SyntaxError {
		TokenType type = u.value();
		Expr e;
		try {
			e = (Expr) u.getChildren().get(0);
		} catch (ClassCastException ex) {
			throw new SyntaxError(
					"Error encountered while evaluating Unary. The child of Unary " + u + " was not an Expression.");
		}

		if (type == TokenType.MEM) {
			int address = evaluateExpression(e, c);
			int[] mem = c.getMemory();

			if (address >= 0 && address < mem[0]) {
				return mem[address];
			} else {
				return 0;
			}
		} else if (type == TokenType.MINUS) {
			return -1 * evaluateExpression(e, c);
		} else if (type.category() == TokenCategory.SENSOR) {
			int arg = evaluateExpression(e, c);
			if (type == TokenType.NEARBY) {
				return c.nearby(arg);
			} else if (type == TokenType.RANDOM) {
				return c.random(arg);
			} else if (type == TokenType.AHEAD) {
				return c.ahead(arg);
			} else {
				throw new SyntaxError(
						"Error encountered while evaluating Unary. Expected a Unary sensor but found " + type);
			}
		} else {
			throw new SyntaxError("Error encountered while evaluating Unary. Expected a Unary but found " + type);
		}
	}

	/**
	 * @return true if an action was resolved
	 */
	public boolean resolveCommand(Node r, Critter c) throws SyntaxError {
		Command com = (Command) r.getChildren().get(1);
		int size = com.getChildren().size();
		for (int i = 0; i < size; i++) {
			Node upOrAct = com.getChildren().get(i);
			if (i == size - 1) {
				if (upOrAct.value().category() == TokenCategory.ACTION) {
					resolveAction((Action) upOrAct, c);
					return true;
				}
			}
			resolveUpdate((Update) upOrAct, c);
		}
		return false;
	}

	/**
	 * Resolves the Action a by updating the world state as specified by the action
	 * description
	 * 
	 * @throws SyntaxError if Action passed is not a valid Action.
	 */
	public void resolveAction(Action a, Critter c) throws SyntaxError {
		TokenType act = a.value();

		switch (act) {

		case SERVE:
			int amt = evaluateExpression(a.getChildren().get(0), c);
			c.serve(amt);
			break;
		case WAIT:
			c.waitAction();
			break;
		case FORWARD:
			c.forward();
			break;
		case BACKWARD:
			c.backward();
			break;
		case LEFT:
			c.left();
			break;
		case RIGHT:
			c.right();
			break;
		case EAT:
			c.eat();
			break;
		case ATTACK:
			c.attack();
			break;
		case GROW:
			c.grow();
			break;
		case BUD:
			c.bud();
			break;
		case MATE:
			c.mate();
			break;
		default:
			throw new SyntaxError("Error encountered while evaluating Action. " + act + " is not a valid Action.");
		}
	}

	/**
	 * Resolves the Update u by updating the critters internal state
	 * 
	 * @throws SyntaxError if mem[expr] is not present as the first child of Update
	 *                     u
	 * @throws SyntaxError if expr is not present as the second child of Update u
	 */
	public void resolveUpdate(Update u, Critter c) throws SyntaxError {
		Unary left;
		try {
			left = (Unary) u.getChildren().get(0);
		} catch (ClassCastException e) {
			throw new SyntaxError(
					"Error encountered while evaluating Update. Mem expected on the left side of assigns but found "
							+ u.getChildren().get(0));
		}

		Expr e;
		try {
			e = (Expr) u.getChildren().get(1);
		} catch (ClassCastException ex) {
			throw new SyntaxError(
					"Error encountered while evaluating Update. Expression expected on the right side of assigns but found "
							+ u.getChildren().get(1));
		}

		int address = evaluateExpression(left.getChildren().get(0), c);
		int value = evaluateExpression(e, c);
		int[] mem = c.getMemory();

		if (address > 5 && address < mem[0]) {
			if (address == 6 && (value < 0 || value > 100)) {
				return;
			}
			mem[address] = value;
		} else {
			return;
		}
	}

	/**
	 * Used in mate to determine if the last rule executed by a Critter was mate
	 * 
	 * @return true if the the passed Rule has the mate action
	 */
	public boolean isMate(Rule r) {
		List<Node> upOrActs = r.getChildren().get(1).getChildren();
		for (int i = 0; i < upOrActs.size(); i++) {
			if (upOrActs.get(i).value() == TokenType.MATE)
				return true;
		}
		return false;
	}
}
