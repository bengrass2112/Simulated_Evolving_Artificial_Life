package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ast.Program;
import ast.ProgramImpl;
import controller.ControllerImpl;
import model.Critter;
import model.Tile;
import model.World;
import parse.Parser;
import parse.ParserFactory;
import spark.Request;
import spark.Spark;

public class Server {

	private int sessionId = 1000;

	private int default_species = 1;

	private final ControllerImpl controller;
	private final int PORT;
	private final String READ;
	private final String WRITE;
	private final String ADMIN;

	private final Gson gson;
	private final Map<Integer, Access> users;

	private TimerTask simulation;
	private volatile float rate;
	private final Timer scheduler;

	/**
	 * ENUMS to save read/write/admin privileges for each client that connects
	 */
	public enum Access {
		READ, WRITE, ADMIN, UNKNOWN;
	}

	/**
	 * Initializes the server
	 */
	public Server(int port, String read, String write, String admin) {

		controller = new ControllerImpl();
		PORT = port;
		READ = read;
		WRITE = write;
		ADMIN = admin;
		users = new Hashtable<Integer, Access>();
		gson = new Gson();
		try {
			String path = InetAddress.getLocalHost().getHostAddress();
			System.out.println(path);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		scheduler = new Timer();

		simulation = new TimerTask() {
			public void run() {
				advanceSimulation(1);
			}
		};

	}

	/**
	 * Runs the server and responds to any client post/get/delete requests
	 */
	public void run() {
		Spark.port(PORT);
		defineLogin();
		defineListAllCritters();
		defineRetrieveCritter();
		defineCreateWorld();
		defineRemoveCritter();
		defineExceptions();
		defineCreateCritter();
		defineRun();
		defineWorldState();
		defineEntityCreation();
		defineAdvanceSteps();

		controller.newWorld();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Spark.stop();
			}
		});
	}

	/**
	 * Advances the world by a set number of time steps
	 */
	private void defineAdvanceSteps() {

		Spark.post("step", (request, response) -> {
			if (checkAccess(request, Access.WRITE)) {
				if (rate != 0) {
					response.status(406);
				} else {
					response.status(200);
					JsonElement count = gson.fromJson(request.body(), JsonObject.class).get("count");
					if (count == null) {
						this.advanceSimulation(1);
					} else {
						int hihi = Math.max(1, count.getAsInt());
						this.advanceSimulation(hihi);
					}
					response.body("Ok");
				}
			} else {
				response.status(401);
			}
			response.type("text/plain");
			return response.body();
		});

	}

	/**
	 * Allows for the creation of food or rock in the world
	 */
	private void defineEntityCreation() {
		Spark.post("world/create_entity", (request, response) -> {
			if (checkAccess(request, Access.WRITE)) {
				JsonObject json = gson.fromJson(request.body(), JsonObject.class);
				int row = json.get("row").getAsInt();
				int col = json.get("col").getAsInt();
				String type = json.get("type").getAsString();
				Tile tile = World.world().tileAt(col, row);
				response.status(201);
				if (type.equals("food")) {
					int amount = json.get("amount").getAsInt();
					if (amount <= 0) {
						amount = 1;
					}
					if (tile != null && tile.empty()) {
						tile.setContents(amount * -1 - 1);
						response.body("Ok");
					} else {
						response.status(406);
					}
				} else if (type.equals("rock")) {
					if (tile != null && tile.empty()) {
						tile.setContents(-1);
						response.body("Ok");
					} else {
						response.status(406);
					}
				}
			} else {
				response.status(401);
			}
			response.type("text/plain");
			return response.body();
		});
	}

	/**
	 * Allows client to run the simulation
	 */
	private void defineRun() {
		Spark.post("run", (request, response) -> {
			if (checkAccess(request, Access.WRITE)) {
				response.status(200);
				JsonObject json = gson.fromJson(request.body(), JsonObject.class);
				Float rate = json.get("rate").getAsFloat();
				if (rate == 0) {
					changeSimulationRate(0);
					response.body("Ok");
				} else if (rate > 0) {
					changeSimulationRate(rate);
					response.body("Ok");
				} else {
					response.status(406);
				}
			} else {
				response.status(401);
			}
			response.type("text/plain");
			return response.body();
		});
	}

	/**
	 * Removes a critter from the current world
	 */
	private void defineRemoveCritter() {
		Spark.delete("critter\\/(\\d)+", (request, response) -> {
			String critterId = request.uri().substring(request.uri().indexOf("critter/") + 8);
			int id = Integer.parseInt(critterId);
			int session_id = Integer.parseInt(request.queryParams("session_id"));

			if (checkAccess(request, Access.ADMIN)) {
				World.lock.writeLock().lock();
				boolean removed = false;
				Iterator<Critter> iter = World.world().critters().iterator();
				while (iter.hasNext()) {
					Critter c = iter.next();
					if (c.id() == id) {
						iter.remove();
						removed = true;
						response.status(204);
					}
				}
				World.lock.writeLock().unlock();
				if (!removed) {
					response.status(404);
				}
			} else {
				boolean removed = false;
				Iterator<Critter> iter = World.world().critters().iterator();
				World.lock.writeLock().lock();
				while (iter.hasNext()) {
					Critter c = iter.next();
					if (c.id() == id) {
						if (c.hasOwner(session_id)) {
							iter.remove();
							removed = true;
							response.status(204);
							break;
						} else {
							response.status(401);
							break;
						}
					}
				}
				World.lock.writeLock().unlock();
				if (!removed) {
					response.status(404);
				}
			}
			return response.body();
		});
	}

	/**
	 * Allows the client to load a new critter into the world
	 */
	private void defineCreateCritter() {

		Spark.post("critters", (request, response) -> {

			JsonObject json = gson.fromJson(request.body(), JsonObject.class);

			if (checkAccess(request, Access.WRITE)) {
				response.status(201);
				JsonElement number = json.get("num");
				JsonElement positions = json.get("positions");

				String species = this.getNextDefaultSpecies();
				JsonElement temp = json.get("species_id");
				if (temp != null) {
					species = temp.getAsString();
				}

				InputStream is = new ByteArrayInputStream(json.get("program").getAsString().getBytes());
				Reader reader = new BufferedReader(new InputStreamReader(is));
				Parser parser = ParserFactory.getParser();
				Program program = parser.parse(reader);
				reader.close();

				JsonArray memories = json.get("mem").getAsJsonArray();
				int[] mem = new int[memories.size()];
				for (int i = 0; i < memories.size(); i++) {
					mem[i] = memories.get(i).getAsInt();
				}
				JsonArray ids = new JsonArray();

				if (number != null && positions == null) {

					int num = number.getAsInt();

					List<List<Integer>> empty = World.world().emptyTiles();

					Collections.shuffle(empty);

					for (int i = 0; i < Math.min(empty.size(), num); i++) {
						List<Integer> coordinate = empty.get(i);
						int dir = (int) Math.random() * 6;
						int c = coordinate.get(0);
						int r = coordinate.get(1);
						int[] memCopy = Arrays.copyOf(mem, mem.length);
						ProgramImpl programCopy = (ProgramImpl) program.clone();
						Critter critter = new Critter(species, programCopy, memCopy, c, r, dir);
						critter.setOwner(sessionId);
						World.world().addCritter(critter, c, r);
						ids.add(critter.id());
					}
					JsonObject body = new JsonObject();
					body.add("species_id", gson.fromJson(species, JsonElement.class));
					body.add("ids", ids);
					response.body(body.toString());

				} else if (number == null && positions != null) {

					JsonArray array = positions.getAsJsonArray();
					JsonObject jobj;
					int size = array.size();

					for (int j = 0; j < size; j++) {
						jobj = array.get(j).getAsJsonObject();
						int r = jobj.get("row").getAsInt();
						int c = jobj.get("col").getAsInt();
						int dir = jobj.get("direction").getAsInt();
						ProgramImpl programCopy = (ProgramImpl) program.clone();
						int[] memCopy = Arrays.copyOf(mem, mem.length);
						Critter critter = new Critter(species, programCopy, memCopy, c, r, dir);
						critter.setOwner(sessionId);
						World.world().addCritter(critter, c, r);
						ids.add(critter.id());
					}
					JsonObject body = new JsonObject();
					body.add("species_id", gson.fromJson(species, JsonElement.class));
					body.add("ids", ids);
					response.body(body.toString());

				} else {
					response.status(404);
				}
			} else {
				response.status(401);
			}
			return response.body();
		});
	}

	/**
	 * Allows a client to create a new world simulation
	 */
	private void defineCreateWorld() {

		Spark.post("world", (request, response) -> {
			JsonObject json = gson.fromJson(request.body(), JsonObject.class);
			response.type("text/plain");
			response.status(201);

			if (!checkAccess(request, Access.ADMIN)) {
				response.status(401);
			} else {

				String world = json.get("description").getAsString();

				String name = UUID.randomUUID().toString() + ".txt";
				File temp = new File(name);
				BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
				bw.write(world);
				bw.close();
				controller.loadWorld(name);
				World.world().worldName = controller.worldName.toString();
				temp.delete();
				response.body("Ok");
			}
			return response.body();

		});
	}

	/**
	 * Returns the current state of the world
	 */
	private void defineWorldState() {

		Spark.get("world", (request, response) -> {

			int user = Integer.parseInt(request.queryParams("session_id"));
			JsonObject worldState = new JsonObject();

			response.status(200);
			response.type("application/json");

			World world = World.world();
			worldState.addProperty("current_timestep", world.getSteps());
			worldState.addProperty("rate", rate);
			worldState.addProperty("name", world.worldName);
			worldState.addProperty("population", world.getNumberOfAliveCritters());
			worldState.addProperty("width", world.WIDTH);
			worldState.addProperty("height", world.HEIGHT);

			int row = 0;
			int col = 0;
			int width = world.WIDTH;
			int height = world.HEIGHT;
//			try {
				row = Integer.parseInt(request.queryParams("from_row"));
				col = Integer.parseInt(request.queryParams("from_col"));
				width = Integer.parseInt(request.queryParams("width"));
				height = Integer.parseInt(request.queryParams("height"));
//			} catch (NumberFormatException | NullPointerException e) {
//				e.printStackTrace();
//			}

			JsonArray array = new JsonArray();
			Tile[][] tiles = World.world().tiles();

			for (int i = 0; i < tiles.length; i++) {
				for (int j = 0; j < tiles[i].length; j++) {
					Tile tile = tiles[i][j];
					int grid = tile.col() - col % 2 == 0 ? 0 : 1;
					if (tile.row() > row && tile.col() >= col && width > tile.col() - col
							&& tile.row() < height + row + grid) {
						array.add(parseTile(tiles[i][j], user));
					}
				}
			}

			worldState.add("state", array);
			response.body(worldState.toString());

			return response.body();

		});

	}

	/**
	 * Allows client to retrieve information regarding a specific critter
	 */
	private void defineRetrieveCritter() {

		Spark.get("critter\\/(\\d)+", (request, response) -> {

			String critterId = request.uri().substring(request.uri().indexOf("critter/") + 8);
			int id = Integer.parseInt(critterId);
			int session_id = Integer.parseInt(request.queryParams("session_id"));
			World.lock.readLock().lock();
			Critter critter = World.world().critters().stream().filter(c -> c.id() == id).findFirst().orElse(null);
			World.lock.readLock().unlock();
			response.status(200);
			response.type("application/json");
			response.body(parseCritter(critter, session_id).toString());

			return response.body();

		});

	}

	/**
	 * Allows client to receive a list of all critters
	 */
	private void defineListAllCritters() {

		Spark.get("critters", (request, response) -> {

			int session_id = Integer.parseInt(request.queryParams("session_id"));
			World.lock.readLock().lock();
			List<Critter> critter = World.world().critters();
			JsonObject[] jsonCritters = new JsonObject[critter.size()];
			for (int i = 0; i < jsonCritters.length; i++) {
				jsonCritters[i] = parseCritter(critter.get(i), session_id);
			}
			World.lock.readLock().lock();
			response.status(200);
			response.type("application/json");
			response.body(jsonCritters.toString());

			return response.body();

		});

	}

	/**
	 * Allows clients to login to the server and initializes read/write privileges
	 */
	private void defineLogin() {

		Spark.post("login", (request, response) -> {

			String body = request.body();
			JsonObject jsonIn = gson.fromJson(body, JsonObject.class);

			response.type("application/json");
			response.status(200);

			JsonObject jsonOut = new JsonObject();
			String level = jsonIn.get("level").getAsString();
			String password = jsonIn.get("password").getAsString();

			switch (level) {
			case "read":
				if (password.equals(READ)) {
					int s = getNextId();
					users.put(getNextId(), Access.READ);
					jsonOut.addProperty("session_id", s);
				} else {
					response.status(401);
				}
				break;

			case "write":
				if (password.equals(WRITE)) {
					int s = getNextId();
					users.put(getNextId(), Access.WRITE);
					jsonOut.addProperty("session_id", s);
				} else {
					response.status(401);
				}
				break;

			case "admin":
				if (password.equals(ADMIN)) {
					int s = getNextId();
					users.put(s, Access.ADMIN);
					jsonOut.addProperty("session_id", s);
				} else {
					response.status(401);
				}
				break;

			default:
				response.status(401);
				break;
			}
			response.body(jsonOut.toString());
			return response.body();
		});
	}

	/**
	 * Returns the next session id for incoming clients
	 */
	private int getNextId() {
		int id = sessionId;
		sessionId += 1;
		return id;
	}

	/**
	 * An exception
	 */
	private void defineExceptions() {
		Spark.exception(Exception.class, (e, request, response) -> {
			e.printStackTrace();
			response.status(404);
		});
	}

	/**
	 * Returns the next default species name
	 */
	private String getNextDefaultSpecies() {
		return "default_species " + ++default_species;
	}

	/**
	 * Returns whether the user has an access level higher or equal to level
	 */
	private boolean checkAccess(Request req, Access level) {

		try {
			Access user = users.get(Integer.parseInt(req.queryParams("session_id")));
			if (user != null) {
				switch (user) {
				case ADMIN:
					return true;
				case WRITE:
					return level != Access.ADMIN;
				case READ:
					return level == Access.UNKNOWN || level == Access.READ;
				case UNKNOWN:
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns a JsonObject containing the critter's state
	 */
	private JsonObject parseCritter(Critter c, int user) {
		JsonObject critter = new JsonObject();
		if (c.hasOwner(user) || users.get(user) == Access.ADMIN) {
			critter.addProperty("id", c.id());
			critter.addProperty("species_id", c.getSpecies());
			critter.addProperty("program", c.getProgramString());
			critter.addProperty("row", c.row());
			critter.addProperty("col", c.col());
			critter.addProperty("direction", c.dir());
			critter.addProperty("mem", c.getMemory().toString());
			critter.addProperty("recently_executed_rule", c.lastRuleIndex());
		} else {
			critter.addProperty("id", c.id());
			critter.addProperty("species_id", c.getSpecies());
			critter.addProperty("row", c.row());
			critter.addProperty("col", c.col());
			critter.addProperty("direction", c.dir());
			critter.addProperty("mem", c.getMemory().toString());
		}
		return critter;
	}

	/**
	 * Returns a JsonObject list containing the tile's state
	 */
	private JsonObject parseTile(Tile tile, int user) {

		if (tile != null) {
			int c = tile.contents();
			JsonObject temp = new JsonObject();
			if (c > 0) {
				temp = parseCritter(tile.critter(), user);
			} else if (c < -1) {
				temp.addProperty("type", "food");
				temp.addProperty("value", tile.contents());
				temp.addProperty("row", tile.row());
				temp.addProperty("col", tile.col());
			} else if (c == -1) {
				temp.addProperty("type", "rock");
				temp.addProperty("row", tile.row());
				temp.addProperty("col", tile.col());
			} else {
				temp.addProperty("type", "nothing");
				temp.addProperty("row", tile.row());
				temp.addProperty("col", tile.col());
			}
			return temp;
		}
		return new JsonObject();

	}

	/**
	 * Returns true if simulation rate is changed successfully
	 *
	 * @param rate
	 * @return true if simulation rate is changed successfully
	 */
	private boolean changeSimulationRate(float rate) {
		if (rate < 0) {
			return false;
		} else {
			simulation.cancel();
			if (rate > 0) {
				scheduler.scheduleAtFixedRate(simulation, 0, (long) (1000 / rate));
			}
			return true;
		}
	}

	/**
	 * Advances the simulation by {@code steps} time-steps
	 */
	private void advanceSimulation(int steps) {
		if (steps <= 0) {
			steps = 1;
		}
		controller.advanceTime(steps);

	}

}