package parse;

import java.io.Reader;

import ast.Action;
import ast.BinaryCondition;
import ast.Command;
import ast.Condition;
import ast.Expr;
import ast.Factor;
import ast.Program;
import ast.ProgramImpl;
import ast.Relation;
import ast.Rule;
import ast.Unary;
import ast.Update;
import exceptions.SyntaxError;

/** Represents the parser that constructs an AST from an input stream */
class ParserImpl implements Parser {

    @Override
    /**
     * @Returns a program Node representing an AST containing all the rules that
     *          dictate a critters behavior.
     */
    public Program parse(Reader r) {
        Tokenizer t = new Tokenizer(r);
        Program p = null;
        try {
            p = parseProgram(t);
        } catch (SyntaxError e) {
            System.out.println(e.getMessage());
        }
        return p;
    }

    /**
     * Parses a program from the stream of tokens provided by the Tokenizer,
     * consuming tokens representing the program. All following methods with a name
     * "parseX" have the same spec except that they parse syntactic form X.
     *
     * @return the root of a fully parsed AST
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     */
    public static ProgramImpl parseProgram(Tokenizer t) throws SyntaxError {
        ProgramImpl p = new ProgramImpl();
        while (t.hasNext()) {
            Rule r = parseRule(t);
            p.addRule(r);
            Token token = t.peek();
            if (token.getType() == TokenType.SEMICOLON) {
                throw new SyntaxError("Exception found in parseRule. End of rule expected at line " + t.lineNumber()
                        + " but instead found " + token);
            }
        }

        return p;
    }

    /**
     * Parses the condition and command to the left and right of '-->'
     * 
     * @return the root for a logically complete Rule.
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     */
    public static Rule parseRule(Tokenizer t) throws SyntaxError {
        Condition cond = parseCondition(t);
        consume(t, TokenType.ARR);
        Command comm = parseCommand(t);
        Token token = t.peek();

        return new Rule(cond, comm);
    }

    /**
     * Parses Conjunctions until there are no more OR statements
     * 
     * @return the root for a logically complete Condition.
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     */
    public static Condition parseCondition(Tokenizer t) throws SyntaxError {
        Condition c = parseConjunction(t);
        while (t.peek().getType() == TokenType.OR) {
            consume(t, TokenType.OR);
            Condition c2 = parseConjunction(t);
            c = new BinaryCondition(c, TokenType.OR, c2);
        }
        return c;
    }

    /**
     * Parses relations until there are no more AND statements
     * 
     * @return the root for part of a complete condition
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     */
    public static Condition parseConjunction(Tokenizer t) throws SyntaxError {
        Condition c = parseRelation(t);
        while (t.peek().getType() == TokenType.AND) {
            consume(t, TokenType.AND);
            Condition c2 = parseRelation(t);
            c = new BinaryCondition(c, TokenType.AND, c2);
        }
        return c;
    }

    /**
     * Parses expressions on either side of each relOp
     * 
     * @return the root of a complete relation comprised of two expressions
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     * @throws a           special SyntaxError if the token supplied as a relOp is
     *                     not a valid relOp.
     */
    public static Condition parseRelation(Tokenizer t) throws SyntaxError {
        Condition c;

        if (t.peek().getType() == TokenType.LBRACE) {
            consume(t, TokenType.LBRACE);
            c = parseCondition(t);
            consume(t, TokenType.RBRACE);
        } else {
            Expr exprL = parseExpression(t);
            Token relOp = t.next();
            if (!relOp.isRelation()) {
                throw new SyntaxError("Exception found in parseRelation. Syntax error at line " + t.lineNumber() + ". '"
                        + relOp + "' is not a valid relation token.");
            }
            Expr exprR = parseExpression(t);
            c = new Relation(exprL, relOp.getType(), exprR);
        }

        return c;
    }

    /**
     * Parses terms until there are no more addOps
     * 
     * @return the root of a logically complete expression
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     */
    public static Expr parseExpression(Tokenizer t) throws SyntaxError {
        Expr e = parseTerm(t);
        while (t.peek().isAddOp()) {
            TokenType addOp = t.next().getType();
            Expr e2 = parseTerm(t);
            e = new Expr(e, addOp, e2);
        }
        return e;
    }

    /**
     * Parses factors until there are no more mulOps
     * 
     * @return the root of part of an Expr.
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     */
    public static Expr parseTerm(Tokenizer t) throws SyntaxError {
        Expr e = parseFactor(t);
        while (t.peek().isMulOp()) {
            TokenType addOp = t.next().getType();
            Expr e2 = parseFactor(t);
            e = new Expr(e, addOp, e2);
        }
        return e;
    }

    /**
     * Parses a factor into either a final leaf node (a Factor object) or a Unary
     * node with 1 child.
     * 
     * @return an Expr Node which is either a Factor Node (no children) or a Unary
     *         Node (one child).
     * @example A Factor contains only a value like a number or MEMSUGAR
     * @example A Unary has one Expr child like mem[expr], sensor[expr] or a
     *          negative number.
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     am unexpected terminal symbol appears.
     */
    public static Expr parseFactor(Tokenizer t) throws SyntaxError {
        Expr e;
        if (t.peek().getType() == TokenType.LPAREN) {
            consume(t, TokenType.LPAREN);
            e = parseExpression(t);
            consume(t, TokenType.RPAREN);
        } else {
            Token token = t.next();
            // FACTORS: <number>, MEMSUGAR (MEMSUGAR is converted to mem[expr], a Unary
            if (token.isNum()) {
                e = new Factor(TokenType.NUM, token.toNumToken().getValue());
            }
            // Change to mem address, sugar shouldn't be in AST
            else if (token.isMemSugar()) {
                Factor f = sugarToInt(token.getType());
                e = new Unary(TokenType.MEM, f);
            }

            // UNARY OPERATORS: mem[expr], sensor[expr], <negative number>
            else if (token.getType() == TokenType.MEM) {
                consume(t, TokenType.LBRACKET);
                Expr e2 = parseExpression(t);
                consume(t, TokenType.RBRACKET);
                e = new Unary(TokenType.MEM, e2);
            } else if (token.isSensor()) {
                if (t.peek().getType() == TokenType.LBRACKET) {
                    consume(t, TokenType.LBRACKET);
                    Expr e2 = parseExpression(t);
                    consume(t, TokenType.RBRACKET);
                    e = new Unary(token.getType(), e2);
                } else {
                    e = new Factor(TokenType.SMELL);
                }
            } else if (token.getType() == TokenType.MINUS) {
                Expr e2 = parseExpression(t);
                e = new Unary(TokenType.MINUS, e2); // Is is okay to use minus as the negative sign
            } else {
                throw new SyntaxError("Exception found in parseFactor. Syntax error at line " + t.lineNumber() + ". '"
                        + token + "' is not used as a valid token.");
            }
        }

        return e;
    }

    /**
	 * Parses updates until there are no more, then parses a single action, if there
	 * is one, that appears at the end of the list.
	 * 
	 * @return The completely parsed command node containing an array of all updates
	 *         as well as an Action (Action may be null).
	 * @throws SyntaxError at the specific line and token that caused the error if
	 *                     the input defies the grammar.
	 * @throws a           special SyntaxError if no command was supplied after the
	 *                     '-->' token.
	 * @throws a           special SyntaxError if more than one action is passed.
	 */
    public static Command parseCommand(Tokenizer t) throws SyntaxError {
        Command c = new Command();
        while (t.hasNext()) {
            if (!(t.peek().isAction())) {
                c.addUpdate(parseUpdate(t));
                if (t.peek().getType() == TokenType.SEMICOLON) {
                    consume(t, TokenType.SEMICOLON);
                    return c;
                }
            } else {
				if (TokenType.SERVE == t.peek().getType()) {
					t.next();
					consume(t, TokenType.LBRACKET);
					Expr e = parseExpression(t);
					consume(t, TokenType.RBRACKET);
					Action a = new Action(TokenType.SERVE, e);
					c.addAction(a);
				} else {
					Action a = new Action(t.next().getType());
					c.addAction(a);
				}

				if (t.peek().getType() == TokenType.SEMICOLON) {
					consume(t, TokenType.SEMICOLON);
					return c;
				}
				else if (t.peek().getType().category() == TokenCategory.ACTION) {
					throw new SyntaxError("Exception found in parseCommand. More than one action passed at line "
							+ t.lineNumber() + ".");
				}

				// Consumes a semicolon, because an Action should always end a rule if it
				// appears.
				// consume(t, TokenType.SEMICOLON);
            }
        }

		if (c.empty()) {
			throw new SyntaxError(
					"Exception found in parseCommand. No command was supplied at line " + t.lineNumber() + ".");
		}
        return c;
    }

    /**
     * Parses an individual update by parsing the MEM[expr] on the left and the
     * expression on the right of ':='.
     * 
     * @return The root of a complete update comprised of a mem[expr] and an expr.
     * @throws SyntaxError at the specific line and token that caused the error if
     *                     the input defies the grammar.
     * @throws a           special SyntaxError if MEM was expected and not found
     */
    public static Update parseUpdate(Tokenizer t) throws SyntaxError {
        Unary mem = parseMem(t);
        consume(t, TokenType.ASSIGN);
        Expr r = parseExpression(t);

        Update u = new Update(mem, TokenType.ASSIGN, r);

        return u;
    }

	/**
	 * Consumes a token of the expected type.
	 * @throws exceptions if the wrong kind of token is encountered.
	 */
	public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
		Token token = t.next();
		if (!(token.getType() == tt)) {
			throw new SyntaxError(
					"Exception found in consume. Syntax error at line " + t.lineNumber() + ". Expected " + tt
							+ " but found " + token);
		}
	}

    /**
     * Parses the mem[expr] on the left of the ":=" assigns operator.
     * 
     * @return The root of a Unary that represents a mem[expr]
     * @throws a special SyntaxError if MEM was expected and not found.
     */
    public static Unary parseMem(Tokenizer t) throws SyntaxError {
        Token token = t.next();
        if (!((token.getType() == TokenType.MEM) || token.getType().category() == TokenCategory.MEMSUGAR)) {
            throw new SyntaxError("Exception found in parseMem. MEM was expected on line " + t.lineNumber()
                    + " but instead " + token + " was found.");
        } else {
            if (token.getType() == TokenType.MEM) {
                consume(t, TokenType.LBRACKET);
                Expr e2 = parseExpression(t);
                consume(t, TokenType.RBRACKET);
                Unary e = new Unary(TokenType.MEM, e2);
                return e;
            } else {
                Factor f = sugarToInt(token.getType());
                Unary e = new Unary(TokenType.MEM, f);
                return e;
            }
        }
    }

    public static Factor sugarToInt(TokenType tt) throws SyntaxError {
        Factor f;
        switch (tt) {
        case ABV_MEMSIZE:
            f = new Factor(TokenType.NUM, 0);
            break;
        case ABV_DEFENSE:
            f = new Factor(TokenType.NUM, 1);
            break;
        case ABV_OFFENSE:
            f = new Factor(TokenType.NUM, 2);
            break;
        case ABV_SIZE:
            f = new Factor(TokenType.NUM, 3);
            break;
        case ABV_ENERGY:
            f = new Factor(TokenType.NUM, 4);
            break;
        case ABV_PASS:
            f = new Factor(TokenType.NUM, 5);
            break;
        case ABV_POSTURE:
            f = new Factor(TokenType.NUM, 6);
            break;
        default:
            throw new SyntaxError("Conversion between MEMSUGAR and mem[expr] failed.");
        }

        return f;
    }
}