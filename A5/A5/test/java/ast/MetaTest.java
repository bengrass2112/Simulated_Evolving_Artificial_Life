package ast;

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author AndreyWYao
 *
 *         This test case provides functionality that generates a String
 *         representation of a random valid program based on the given
 *         parameters
 */
class MetaTest {

	static int EXPR = 2;
	static int COND = 1;
	static int ONEEXPR = 1;
	static int REL = 1;
	static int UPDATE = 2;

	static Random random = new Random();
	@Test
	void test() {
		System.out.print(makeProgram(7));
	}

	static String makeProgram(int count) {
		String genome = "";
		for (int i = 0; i < count; i++) {
			genome += makeCondition() + " --> " + makeCommand();
			genome += ";\n";
		}
		return genome;
	}
	static String makeExpr() {
		String expr = "";
		for (int i = 0; i < EXPR; i++) {
			expr += makeOneExpr();
			switch (random.nextInt(5)) {
			case 0:
				expr += " + ";
				break;
			case 1:
				expr += " - ";
				break;
			case 2:
				expr += " * ";
				break;
			case 3:
				expr += " / ";
				break;
			case 4:
				expr += " mod ";
				break;
			}
		}
		expr += makeOneExpr();
		return expr;
	}

	static String makeOneExpr() {
		boolean bra = random.nextBoolean();
		String expr = "";
		if (bra) {
			expr += "(";
		}
		for (int i = 0; i < ONEEXPR; i++) {
			if (random.nextBoolean()) {
				expr += makeMem(true);
			} else {
				expr += random.nextInt(1000);
			}
			switch (random.nextInt(5)) {
			case 0:
				expr += " + ";
				break;
			case 1:
				expr += " - ";
				break;
			case 2:
				expr += " * ";
				break;
			case 3:
				expr += " / ";
				break;
			case 4:
				expr += " mod ";
				break;
			}
		}
		if (random.nextBoolean()) {
			expr += makeMem(true);
		} else {
			expr += random.nextInt(1000);
		}
		if (bra) {
			expr += ")";
		}
		return expr;
	}

	static String makeMem(boolean number) {
		if (random.nextBoolean() || number) {
			return "mem[" + random.nextInt(8) + "]";
		} else {
			return "mem[" + makeOneExpr() + "]";
		}
	}
	
	static String makeRelation() {
		String rel = "";
		rel += makeExpr();
		switch (random.nextInt(5)) {
		case 0:
			rel += " > ";
			break;
		case 1:
			rel += " < ";
			break;
		case 2:
			rel += " >= ";
			break;
		case 3:
			rel += " <= ";
			break;
		case 4:
			rel += " != ";
			break;
		}
		rel += makeExpr();
		return rel;
	}

	static String makeCommand() {
		String cmd = "";
		for(int i=0;i<random.nextInt(4);i++) {
			cmd += makeUpdate();
			cmd += " ";
		}
		return cmd + makeAction();
	}

	static String makeUpdate() {
		return makeMem(false) + " := " + makeExpr();
	}

	static String makeAction() {
		switch(random.nextInt(11)) {
		case 0:
			return " wait ";
		case 1:
			return " forward ";
		case 2:
			return " backward ";
		case 3:
			return " left ";
		case 4:
			return " right ";
		case 5:
			return " eat ";
		case 6:
			return " attack ";
		case 7:
			return " grow ";
		case 8:
			return " bud ";
		case 9:
			return " mate ";
		case 10:
			return " serve[" + makeExpr() + "] ";
		default:
			return " grow ";
		}
	}

	static String makeCondition() {
		String con = "";
		boolean bra = random.nextBoolean();
		if(bra) {
			con+="{";
		}
		for (int i = 0; i < COND; i++) {
			if (random.nextBoolean()) {
				con += makeRelation();
			} else {
				con += makeCondition();
			}
			con += random.nextBoolean() ? " and " : " or ";
		}
		con += makeRelation();
		if(bra) {
			con+="}";
		}
		return con;
	}
}
