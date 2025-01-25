package controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import ast.Program;
import ast.ProgramImpl;
import model.Constants;
import model.Critter;
import model.Interpreter;
import model.InterpreterFactory;
import model.ReadOnlyWorld;
import model.Tile;
import model.World;
import parse.Parser;
import parse.ParserFactory;

public class ControllerImpl implements Controller {

	Interpreter interpreter;
	public StringBuilder worldName = new StringBuilder();

	public ControllerImpl() {
		interpreter = InterpreterFactory.getInterpreter();
	}

	@Override
	public ReadOnlyWorld getReadOnlyWorld() {
		return World.world();
	}

	@Override
	public void newWorld() {
		World.createWorld(Constants.HEIGHT, Constants.WIDTH);
		World.world().worldName = "Default world";
	}

	/**
	 * Creates a new world with a specified height and width
	 * 
	 * @param height the desired height of the world
	 * @param column the desired width of the world
	 */
	public void newWorld(int height, int width) {
		World.createWorld(height, width);
		World.world().worldName = "Random world";
	}

	@Override
	public boolean loadWorld(String filename) throws IllegalArgumentException {

		File file = new File(filename);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			System.out.println("World File Not Found");
			e.printStackTrace();
			return false;
		}

		Tile t;
		String line;

		try {
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("( )+", " ").trim();
				String[] words = line.split(" ");
				if (line.startsWith("//") || line.trim().isEmpty()) {
					continue;
				} else {
					int column, row;
					switch (words[0]) {
					case "name":
						if (words.length > 1) {
							System.out.print("World name: ");
							for (int i = 1; i < words.length; i++) {
								worldName.append(words[i] + " ");
							}
							System.out.println(worldName.toString());
						} else {
							System.out.println("Warning: no name for the world was found in the file.");
						}
						break;
					case "size":
						int width = Integer.parseInt(words[1]);
						int height = Integer.parseInt(words[2]);
						if (height <= 0) {
							System.out.println("Invalid world height provided. Default height provided.");
							height = Constants.HEIGHT;
						} else if (width <= 0) {
							System.out.println("Invalid world width provided. Default width provided.");
							width = Constants.WIDTH;
						}
						newWorld(height, width);
						break;
					case "rock":
						column = Integer.parseInt(words[1]);
						row = Integer.parseInt(words[2]);
						t = World.world().tileAt(column, row);
						if (t.empty()) {
							t.setContents(-1);
						}
						break;
					case "food":
						column = Integer.parseInt(words[1]);
						row = Integer.parseInt(words[2]);
						if (Integer.parseInt(words[3]) < 0) {
							System.out.println("Negative value of food provided. Using its absolute value.");
						}
						int food = -1 * Math.abs(Integer.parseInt(words[3])) - 1;
						t = World.world().tileAt(column, row);
						if (t.empty()) {
							t.setContents(food);
						}
						break;
					case "critter":
						column = Integer.parseInt(words[2]);
						row = Integer.parseInt(words[3]);
						int direction = Integer.parseInt(words[4]) % 6;
						int i = filename.lastIndexOf(File.separator);
						String path = (i > -1) ? filename.substring(0, i) : filename;
						File critterFile = new File(path + "/" + words[1]);
						loadCritter(column, row, direction, critterFile);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}
			}
			World.world().worldName = worldName.toString();
			br.close();
			return true;
		} catch (IOException e) {
			System.out.println("Error. Please make sure you're passing in a valid world file");
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			System.out.println("Error. Please make sure tile coordinates are valid");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Loads a specified critter file into the World.world(). If the critter file is
	 * invalid, default values are supplied and warning message is printed
	 * 
	 * @param column      the column location of the critter
	 * @param row         the row location of the critter
	 * @param direction   the direction the critter is facing
	 * @param critterFile the critter file
	 * @return true if the critter was successfully added to the world, false
	 *         otherwise
	 * @throws IOException if the critter file is invalid
	 */
	public boolean loadCritter(int column, int row, int direction, File critterFile) {

		if (!World.world().tileAt(column, row).empty()) {
			return false;
		}

		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(critterFile)));

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

			// print warning message when mem value not provided
			for (int i = 0; i < reached.length; i++) {
				if (!reached[i]) {
					System.out.print("No value for ");
					switch (i) {
					case 0:
						System.out.print("species found. ");
						break;
					case 1:
						System.out.print("memsize found. ");
						break;
					case 2:
						System.out.print("defense found. ");
						break;
					case 3:
						System.out.print("offense found. ");
						break;
					case 4:
						System.out.print("size found. ");
						break;
					case 5:
						System.out.print("energy found. ");
						break;
					case 6:
						System.out.print("posture found. ");
						break;
					}
					System.out.println("Default value used instead.");
				}
			}

			// reading program into string builder
			StringBuilder sb = new StringBuilder();
			sb.append(line);
			sb.append("\n");
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();

			InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
			Reader r = new BufferedReader(new InputStreamReader(is));
			Parser parser = ParserFactory.getParser();
			Program program = parser.parse(r);
			r.close();

			int[] mem = new int[memsize];
			mem[0] = memsize;
			mem[1] = defense;
			mem[2] = offense;
			mem[3] = size;
			mem[4] = energy;
			mem[6] = posture;

			Critter critter = new Critter(species, (ProgramImpl) program, mem, column, row, direction);
			World.world().insertCritter(critter, column, row);
			return true;

		} catch (IOException e) {
			System.out.println("Error reading critter file.");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * {@inheritDoc} Selects random coordinate from array list and adds critter to
	 * that random coordinate
	 */
	@Override
	public boolean loadCritters(String fileName, int n) {

		List<List<Integer>> empty = World.world().emptyTiles();
		Collections.shuffle(empty);

		for (int i = 0; i < Math.min(empty.size(), n); i++) {
			List<Integer> coordinate = empty.get(i);
			loadCritter(coordinate.get(0), coordinate.get(1), (int) (Math.random() * 6), new File(fileName));
		}
		return empty.isEmpty() || empty == null;
	}

	@Override
	public boolean advanceTime(int n) {

		if (n < 0 || World.world() == null) {
			return false;
		}

		for (int i = 0; i < n; i++) {
			World.world().step();
		}
		return true;

	}

	@Override
	public void printWorld(PrintStream out) {

		Tile[][] tiles = World.world().tiles();

		for (int i = tiles.length - 1; i > -1; i--) {
			if (i % 2 == 1) {
				out.print("   ");
			}
			for (int j = 0; j < tiles[i].length; j++) {
				out.print(tiles[i][j] + "     ");
			}

			out.print("\n");

		}
	}

}
