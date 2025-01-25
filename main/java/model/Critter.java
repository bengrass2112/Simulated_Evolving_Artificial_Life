package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ast.Mutation;
import ast.MutationFactory;
import ast.Node;
import ast.Program;
import ast.ProgramImpl;
import ast.Rule;

public class Critter implements ReadOnlyCritter {
	private int col, row;
	private int dir;
	private boolean living = true, matingCall = false;

	private String species;

	private int[] mem;
	private ProgramImpl program;
	private int lastRule = -1;
	private int complexity;
	private final int id;

	private int sessionId = -1;
	private static int ID = 1;

	/**
	 * Ensures that each value is within its appropriate range.
	 * 
	 * @return a new critter read in from a file.
	 */
	public Critter(String s, ProgramImpl p, int[] memory, int c, int r, int direction) {
		id = ID++;

		species = s;
		col = c;
		row = r;
		dir = Math.abs(direction) % 6;

		int memSize = memory.length;
		program = p;

		if (memSize < 7)
			memSize = 7;
		mem = new int[memSize];
		mem[0] = memSize;

		if (memory[1] < 1)
			memory[1] = 1;
		mem[1] = memory[1];

		if (memory[2] < 1)
			memory[2] = 1;
		mem[2] = memory[2];

		if (memory[3] < 1)
			memory[3] = 1;
		mem[3] = memory[3];

		if (memory[4] < 1)
			memory[4] = Constants.INITIAL_ENERGY;
		mem[4] = memory[4];

		if (memory[6] < 0 || memory[6] > 99)
			memory[6] = 0;
		mem[5] = memory[6];

		complexity = (p.getChildren().size() * Constants.RULE_COST) + ((mem[2] + mem[1]) * Constants.ABILITY_COST);
	}

	/**
	 * Called on a new Critter child, mutates the child with a probability of 1/4.
	 * 
	 * @Effect might modify one or more of the following: memSize, offense, defense
	 *         or the program.
	 */
	private void mutateChild() {
		while ((int) (Math.random() * 4) + 1 == 0) {
			if ((int) (Math.random() * 2) == 0) {
				int attr = (int) (Math.random() * 3);
				switch (attr) {
				// Memory Size
				case 0:
					int oldMemSize = mem[0];
					int newMemSize = 0;

					while (newMemSize < 7 || newMemSize == oldMemSize) {
						newMemSize = mem[0] + (int) (Math.random() * 6) - 3;
					}

					int[] newMem = new int[newMemSize];
					newMem[0] = newMemSize;

					for (int i = 1; i < oldMemSize; i++) {
						newMem[i] = mem[i];
					}

					mem = newMem;
					break;
				// Offense
				case 1:
					int oldOffense = mem[2];
					int newOffense = 0;
					while (newOffense <= 0 || newOffense == oldOffense) {
						newOffense = mem[2] + (int) (Math.random() * 4) - 2;
					}

					mem[2] = newOffense;
					break;
				// Defense
				case 2:
					int oldDefense = mem[1];
					int newDefense = 0;
					while (newDefense <= 0 || newDefense == oldDefense) {
						newDefense = mem[1] + (int) (Math.random() * 4) - 2;
					}

					mem[1] = newDefense;
					break;
				}
			} else {
				int mutation = (int) (Math.random() * 3);
				Mutation m = null;
				int numNodes = program.size();
				int nodeIndex = (int) (Math.random() * numNodes);

				switch (mutation) {
				// Replace
				case 0:
					m = MutationFactory.getReplace();
					program = (ProgramImpl) program.mutate(nodeIndex, m);
					break;
				// Swap
				case 1:
					m = MutationFactory.getSwap();
					program = (ProgramImpl) program.mutate(nodeIndex, m);
					break;
				// Transform
				case 2:
					m = MutationFactory.getTransform();
					program = (ProgramImpl) program.mutate(nodeIndex, m);
					break;
				}
			}
		}
	}

	/**
	 * Prepares the critter for the next step by resetting passNumber & matingCall
	 */
	public void loopReset() {
		matingCall = false;
		mem[5] = 0;
	}

	/**
	 * @return current critter column
	 */
	public int col() {
		return col;
	}

	/**
	 * @return current critter row
	 */
	public int row() {
		return row;
	}

	/**
	 * @return current critter direction
	 */
	public int dir() {
		return dir;
	}

	/**
	 * @return the root Program node of this Critter's ruleset
	 */
	public ProgramImpl program() {
		return program;
	}

	/**
	 * @return this critters appearance in the form SSPPD, where S is Size, P is
	 *         Posture and D is dir
	 */
	public int appearance() {
		return (mem[3] * 1000) + (mem[6] * 10) + dir;
	}

	@Override
	public String getSpecies() {
		return species;
	}

	@Override
	public int[] getMemory() {
		return mem;
	}

	@Override
	public String getProgramString() {
		return program.toString();
	}

	@Override
	public String getLastRuleString() {
		return program.getChildren().get(lastRule).toString();
	}

	/**
	 * @return the last rule with an action executed by this Critter
	 */
	public Rule lastRule() {
		return (Rule) program.getChildren().get(lastRule);
	}

	/**
	 * @Effect Sets the last field to the Rule node r.
	 * @Param r The root node of the last rule executed.
	 */
	public void setLastRule(int index) {

		lastRule = index;
	}

	/**
	 * Adds some energy to the Critters reserve and prevents energy from exceeding
	 * maxEnergy.
	 * 
	 * @param amt represents the amount of energy the critter attempted to consume
	 * @return the remaining energy after the critter reaches its maxEnergy value
	 * @example with a maxEnergy of 500, a current energy of 450 and an amt of 100,
	 *          current energy will reach 500 and this method will return 50.
	 */
	private int increaseEnergy(int amt) {
		if (maxEnergy() < mem[4] + amt) {
			int remainder = (mem[4] + amt) - maxEnergy();
			mem[4] = maxEnergy();
			return remainder;
		}
		mem[4] += amt;
		return 0;
	}

	/**
	 * @param amt represents the amount of energy the critter attempted to spend
	 * @Effect decreaes energy by amt, if amt exceeds energy the critter dies (which
	 *         will change the contents of its tile to the appropriate food amount)
	 * @return the energy actually available to spend.
	 * @example with a current energy of 10 and an amt of 9, this method sets energy
	 *          to 1 and returns 9
	 * @example with a current energy of 5 and an amt of 9, this method kills the
	 *          critter and returns 5
	 */
	private int decreaseEnergy(int amt) {
		if (mem[4] - amt <= 0) {
			living = false;
			World.world().changeContents(this, 0, ((mem[3] * Constants.FOOD_PER_SIZE) + 1) * -1);
			return mem[4];
		}
		mem[4] -= amt;

		flagTile();
		return amt;
	}

	/**
	 * Updates the col and row of a critter after a move
	 * 
	 * @param moveDir is either 1 (forward) or -1 (backward)
	 * @Effect increments/decrements the col and/or row of the critter depending on
	 *         its current dir
	 */
	private void updateCoordinates(int moveDir) {
		switch (dir) {
		case 0:
			row += moveDir;
			break;
		case 1:
			col += moveDir;
			row += moveDir;
			break;
		case 2:
			col += moveDir;
			break;
		case 3:
			row -= moveDir;
			break;
		case 4:
			row -= moveDir;
			col -= moveDir;
			break;
		case 5:
			col -= moveDir;
			break;
		}
	}

	/**
	 * @param moveDir describes how many tiles away, forward (+) or backwards (-)
	 *                the target tile is
	 * @return the tile moveDir tile ahead or behind this Critters location or null
	 *         if no such tile exists.
	 */
	public Tile nextTile(int moveDir) {
		int c = col, r = row;
		switch (dir % 6) {
		case 0:
			r += moveDir;
			break;
		case 1:
			c += moveDir;
			r += moveDir;
			break;
		case 2:
			c += moveDir;
			break;
		case 3:
			r -= moveDir;
			break;
		case 4:
			r -= moveDir;
			c -= moveDir;
			break;
		case 5:
			c -= moveDir;
			break;
		}
		return World.world().tileAt(c, r);
	}

	// ***********************************ACTIONS***********************************

	/**
	 * @Effect increases the critters energy by its size times SOLAR_FLUX
	 */
	public void waitAction() {
		increaseEnergy(mem[3] * Constants.SOLAR_FLUX);
	}

	/**
	 * @Effect Moves the critter forward 1 hex
	 */
	public void forward() {
		decreaseEnergy(mem[3] * Constants.MOVE_COST);
		if (living) {
			boolean success = World.world().moveCritter(this, 1);
			if (success)
				updateCoordinates(1);
		}
	}

	/**
	 * @Effect Moves the critter backward 1 hex
	 */
	public void backward() {
		decreaseEnergy(mem[3] * Constants.MOVE_COST);
		if (living) {
			boolean success = World.world().moveCritter(this, -1);
			if (success)
				updateCoordinates(-1);
		}
	}

	/**
	 * Turns the critter right while ensuring dir stays in bounds.
	 * 
	 * @Effect Turns the critter 60 degrees right, ie increases dir by one.
	 */
	public void right() {
		decreaseEnergy(mem[3]);
		if (living)
			dir = (dir + 1) % 6;
		flagTile();
	}

	/**
	 * Turns the critter left while ensuring dir stays in bounds.
	 * 
	 * @Effect Turns the critter 60 degrees left, ie decreases dir by one.
	 */
	public void left() {
		decreaseEnergy(mem[3]);
		if (living)
			dir = (dir + 5) % 6;
		flagTile();
	}

	/**
	 * Allows the critter to consume some amount of food from the tile 1 hex ahead,
	 * as long as that tile contains food
	 * 
	 * @Effect increments critters energy by some amount and sets the food tile's
	 *         contents to the amount of food left over after eating
	 */
	public void eat() {

		decreaseEnergy(mem[3]);

		if (living) {
			Tile t = nextTile(1);

			if (t == null)
				return;

			int contents = t.contents();

			if (contents >= -1) {
				return;
			}

			int food = (contents * -1) - 1;

			int remainder = increaseEnergy(food);

			if (remainder > 0)
				World.world().changeContents(this, 1, (remainder + 1) * -1);
			else
				World.world().changeContents(this, 1, 0);
		}
	}

	/**
	 * converts some of the critters energy into food and adds that food to the tile
	 * 1 hex ahead only if that tile exists and is empty or food
	 * 
	 * @Effect changes critter energy and updates the contents of the hex 1 ahead of
	 *         the critter.
	 */
	public void serve(int amt) {

		decreaseEnergy(mem[3]);

		Tile t = nextTile(1);

		if (t == null)
			return;

		int contents = t.contents();

		if (!(contents <= 0))
			return;

		int amtServed = decreaseEnergy(amt);

		if (amtServed == 0) {
			return;
		}

		if (contents == 0)
			World.world().changeContents(this, 1, ((amtServed + 1) * -1));
		else {
			World.world().changeContents(this, 1, contents + (amtServed * -1));
		}
	}

	/**
	 * Subtracts energy from the target of the attack, or does nothing if the hex
	 * immediately in front of the Critter is not another Critter.
	 * 
	 * @Effect Removes energy from the target of the attack equaled to the damage
	 *         the attacker deals.
	 */
	public void attack() {
		decreaseEnergy(mem[3] * Constants.ATTACK_COST);
		if (living) {
			Tile t = nextTile(1);

			if (t == null)
				return;

			Critter target = t.critter();

			if (target == null)
				return;

			target.decreaseEnergy(calculateDamage(this, target));
		}

	}

	/**
	 * Calculates the damage done by the attacker to the target
	 * 
	 * @param attacker the attacking critter
	 * @param target   the critter being attacked
	 * @return the amount of energy lost by the target
	 */
	private int calculateDamage(Critter attacker, Critter target) {
		int[] attMem = attacker.getMemory();
		int[] tarMem = target.getMemory();

		int attSize = attMem[3];
		int attOff = attMem[2];

		int tarSize = tarMem[3];
		int tarDef = attMem[1];

		int base = Constants.BASE_DAMAGE;

		double exponent = -1 * (Constants.DAMAGE_INC) * ((attSize * attOff) - (tarSize * tarDef));

		double p = 1 / (1 + Math.exp(exponent));

		int total = (int) Math.round(base * p);

		return total;
	}

	/**
	 * Grows the critter 1 unit of size and updates maxEnergy respectively.
	 * 
	 * @Effect modifies size and maxEnergy
	 */
	public void grow() {
		decreaseEnergy(complexity * mem[3] * Constants.GROW_COST);
		if (living) {
			mem[3] += 1;
		}
		flagTile();
	}

	/**
	 * Creates a new critter on the tile behind this critter if that tile exists and
	 * is empty
	 * 
	 * @Effect Spends energy to bud a new Critter onto the tile behind this critter
	 */
	public void bud() {
		decreaseEnergy(complexity * Constants.BUD_COST);

		if (living) {
			Tile t = nextTile(-1);

			if (t == null)
				return;

			int colBehind = t.col(), rowBehind = t.row();

			Critter c = budCritter(colBehind, rowBehind);
			World.world().addCritter(c, colBehind, rowBehind);
		}

	}

	/**
	 * Defines the actual process of budding a new child
	 * 
	 * @return a new critter budded from this critter
	 */
	private Critter budCritter(int c, int r) {
		int dir = Math.abs((int) (Math.random() * 6));

		int[] memory = mem;
		memory[3] = 1;
		memory[4] = Constants.INITIAL_ENERGY;

		for (int i = 6; i < mem[0]; i++) {
			memory[i] = 0;
		}

		Critter child = new Critter(species, program, memory, c, r, dir);

		child.mutateChild();

		child.complexity = (program.getChildren().size() * Constants.RULE_COST)
				+ ((memory[2] + memory[1]) * Constants.ABILITY_COST);

		return child;
	}

	/**
	 * Removes the failed mate energy from the critter and determines if the mate
	 * fails or succeeds
	 * 
	 * @Effect consumes energy for a failed mate or calls the overloaded mate method
	 *         for a successful mate
	 */
	public void mate() {

		matingCall = true;
		decreaseEnergy(mem[3]);

		if (living) {

			Tile ahead = nextTile(1);
			Tile behind = nextTile(-1);

			if (ahead == null)
				return;

			Critter partner = ahead.critter();

			if (partner != null && (partner.dir() + 3) % 6 == dir && partner.matingCall) {

				Tile partnerBehind = partner.nextTile(-1);

				if (behind.contents() == 0 && partnerBehind.contents() == 0) {
					if ((int) (Math.random() * 2) == 0)
						mate(partner, behind);
					else
						mate(partner, partnerBehind);
				} else if (behind.contents() == 0)
					mate(partner, behind);
				else if (partnerBehind.contents() == 0)
					mate(partner, partnerBehind);
			}
		}
	}

	/**
	 * Called for a successful mate, refunds failed mate energy and detracts
	 * successful mate energy
	 * 
	 * @Effect refunds energy for a failed mate and removes successful mate energy
	 */
	private void mate(Critter partner, Tile tile) {
		increaseEnergy(mem[3]);
		partner.increaseEnergy(partner.getMemory()[3]);

		decreaseEnergy(Constants.MATE_COST * complexity);
		partner.decreaseEnergy(Constants.MATE_COST * partner.complexity);

		if (living && partner.alive()) {
			Critter c = mateChild(this, partner, tile);
			World.world().addCritter(c, tile.col(), tile.row());
		}
	}

	/**
	 * Defines the actual process of mating to create a critter
	 * 
	 * @return a new critter created by mating parent1 and parent 2
	 */
	private Critter mateChild(Critter parent1, Critter parent2, Tile t) {
		int[] memory;
		int[] mem1 = parent1.getMemory();
		int[] mem2 = parent2.getMemory();

		int c = t.col(), r = t.row();
		int direction = Math.abs((int) (Math.random() * 6));
		String species = parent1.species + "-" + parent2.species;
		ProgramImpl program;

		// Setting up memory
		if ((int) (Math.random() * 2) == 0)
			memory = mem1;
		else
			memory = mem2;

		for (int i = 1; i < memory.length; i++) {
			if (i <= 2) {

			} else {
				memory[i] = 0;
			}
		}

		memory[3] = 1;
		memory[4] = Constants.INITIAL_ENERGY;

		// Setting up program
		Program program1 = parent1.program();
		Program program2 = parent2.program();
		List<Node> rules1 = program1.getChildren();
		List<Node> rules2 = program2.getChildren();

		// Ensures that program1 is longer
		if (rules1.size() < rules2.size()) {
			Program temp = program1;
			List<Node> tempRules = rules1;
			program1 = program2;
			rules1 = rules2;
			program2 = temp;
			rules2 = tempRules;
		}

		if ((int) (Math.random() * 2) == 0)
			program = (ProgramImpl) program1;
		else
			program = (ProgramImpl) program2;

		// The child at index i, where i is >= rules2.size(), is already correctly from
		// rules1.
		for (int i = 0; i < program.getChildren().size(); i++) {
			if ((int) (Math.random() * 2) == 0)
				program.addRule((Rule) rules1.get(i));
			else
				program.addRule((Rule) rules2.get(i));
		}

		Critter critter = new Critter(species, program, memory, c, r, direction);

		critter.mutateChild();

		critter.complexity = (program.getChildren().size() * Constants.RULE_COST)
				+ ((memory[2] + memory[1]) * Constants.ABILITY_COST);

		return critter;
	}

	// ***********************************SENSORS***********************************

	/**
	 * @return the contents of the Tile dist hexes ahead, or -1 if such a tile does
	 *         not exist
	 */
	public int ahead(int dist) {
		int contents = World.world().ahead(this, dist);
		int sspp = contents / 10;
		int d = contents % 10;

		if (contents > 0) {
			int relD = (dir + (6 - d)) % 6;
			return sspp * 10 + relD;
		} else
			return contents;
	}

	/**
	 * @param relDir the direction to look in, relative to the critter
	 * @example relDir 0 reports the contents of the tile directly ahead of the
	 *          critter, regardless of its orientation
	 * @return the contents of the Tile 1 hex away in direction dir (relative to the
	 *         critter) or -1 if such a tile does not exist
	 */
	public int nearby(int relDir) {
		int contents = World.world().nearby(this, relDir);
		int sspp = contents / 10;
		int d = contents % 10;

		if (contents > 0) {
			int relD = (dir + (6 - d)) % 6;
			return sspp * 10 + relD;
		} else
			return contents;
	}

	/**
	 * Returns information about the nearest food, uo to a maximum of 10 hexes
	 * 
	 * @Effect returns a number with the format distance to nearest food (including
	 *         turns) * 1,000 + relative Direction to nearest food
	 */
	public int smell() {
		GraphNode.reset();
		PriorityQueue<GraphNode> frontier = new PriorityQueue<GraphNode>();
		frontier.add(new GraphNode(World.world().tileAt(col, row), 0, -1, dir));
		while (frontier.size() != 0) {
			GraphNode shortest = frontier.poll();

			if (shortest.tile().contents() < -1)
				return (shortest.dist() * 1000) + shortest.relDir();

			List<GraphNode> expanded = expand(shortest);
			frontier.addAll(expanded);
		}
		return 1000000;
	}

	private List<GraphNode> expand(GraphNode n) {
		int curDir = n.curDir();
		Tile[] neighbors = World.world().neighbors(n.tile());
		ArrayList<GraphNode> expanded = new ArrayList<GraphNode>();

		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] != null && GraphNode.reached.get(neighbors[i]) == null) {
				int addedDist = Math.min(Math.abs(curDir - i), 6 - Math.abs(curDir - i));

				if (n.relDir() == -1 && n.dist() + addedDist <= 10)
					expanded.add(new GraphNode(neighbors[i], n.dist() + addedDist, addedDist, i));
				else if (n.dist() + addedDist <= 10)
					expanded.add(new GraphNode(neighbors[i], n.dist() + addedDist + 1, n.relDir(), i));
			}
		}

		return expanded;
	}

	/**
	 * @return a random number between 0 and arg or 0 if arg < 2
	 */
	public int random(int arg) {
		if (arg < 2)
			return 0;
		else {
			return (int) (Math.random() * arg);
		}
	}

	/**
	 * @return true if this critter is alive
	 */
	public boolean alive() {
		return living;
	}

	public int maxEnergy() {
		return mem[3] * Constants.ENERGY_PER_SIZE;
	}

	/**
	 * Flags the tile the critter is currently on as updated
	 */
	private void flagTile() {
		Tile tile = World.world().tileAt(col, row);
		World.world().updatedTiles.add(tile);
	}

	public void setOwner(int sessionId) {
		this.sessionId = sessionId;
	}

	public boolean hasOwner(int user) {
		return sessionId == user;
	}

	public int id() {
		return id;
	}

	public int lastRuleIndex() {
		return lastRule;
	}

	public static Object[] parseFile(String filename) throws NumberFormatException, IOException {

		File file = new File(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

		String species = "Unnamed Species";
		int memsize = 7, defense = 1, offense = 1, size = 1, energy = Constants.INITIAL_ENERGY, posture = 0;
		boolean[] reached = new boolean[7];

		String line;

		state: while ((line = br.readLine()) != null) {
			line = line.replaceAll("( )+", " ");
			String[] words = line.split(" ");
			if (line.startsWith("//") || line.trim().isEmpty()) {
				continue;
			} else {
				switch (words[0]) {
				case "species:":
					species = words[1];
					reached[0] = true;
					break;
				case "memsize:":
					memsize = Integer.parseInt(words[1]);
					reached[1] = true;
					break;
				case "defense:":
					defense = Integer.parseInt(words[1]);
					reached[2] = true;
					break;
				case "offense:":
					offense = Integer.parseInt(words[1]);
					reached[3] = true;
					break;
				case "size:":
					size = Integer.parseInt(words[1]);
					reached[4] = true;
					break;
				case "energy:":
					energy = Integer.parseInt(words[1]);
					reached[5] = true;
					break;
				case "posture:":
					posture = Integer.parseInt(words[1]);
					reached[6] = true;
					break;
				default:
					break state;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(line);
		sb.append("\n");
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		br.close();

		Object[] info = new Object[3];
		info[0] = species;
		info[1] = sb.toString();
		info[2] = new int[] { memsize, defense, offense, size, energy, 0, posture };

		return info;

	}

}