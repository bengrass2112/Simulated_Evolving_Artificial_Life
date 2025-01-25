package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class World implements ReadOnlyWorld {

    // Singleton world
    private static World world;
    private final Interpreter interpreter;

    public final int HEIGHT, WIDTH;

    private List<Critter> critters;
    static List<Critter> temp;
    public static List<Tile> updatedTiles;

    private static Tile[][] tiles;

    private int steps = 0;

    private World(int height, int width) {
        HEIGHT = height;
        WIDTH = width;
        interpreter = InterpreterFactory.getInterpreter();
        // make the odd column even by adding 1
        tiles = new Tile[height][width / 2 + width % 2];
        critters = new ArrayList<Critter>();
        temp = new ArrayList<Critter>();
        updatedTiles = new ArrayList<Tile>();

        // creating the first row of tiles
        for (int i = 0; i < tiles[0].length; i++) {
            tiles[0][i] = new Tile(2 * i, i);
        }
        // creating the rest of rows of tiles
        for (int j = 1; j < tiles.length; j++) {
            for (int i = 0; i < tiles[j].length; i++) {
                if (j % 2 == 1) {
                    int c = tiles[j - 1][i].column + 1;
                    int r = tiles[j - 1][i].row + 1;
                    tiles[j][i] = new Tile(c, r);
                } else {
                    int c = tiles[j - 1][i].column - 1;
                    int r = tiles[j - 1][i].row;
                    tiles[j][i] = new Tile(c, r);
                }
            }
        }

        if (width % 2 == 1) {
            for (int j = 1; j < tiles.length; j += 2) {
                tiles[j] = Arrays.copyOfRange(tiles[j], 0, tiles[j].length - 1);
            }
        }
    }

    public static World createWorld(int height, int width) {
        if (world == null) {
            world = new World(height, width);
            List<List<Integer>> empty = World.world().emptyTiles();
            for (int i = 0; i < world.HEIGHT * world.WIDTH / 20; i++) {
                int randomCoordinate = (int) (Math.random() * empty.size());
                List<Integer> coordinate = empty.get(randomCoordinate);
                int c = coordinate.get(0);
                int r = coordinate.get(1);
                World.tileAt(c, r).setContents(-1);
            }

        }
        return world;
    }

    @Override
    public int getSteps() {
        return steps;
    }

    @Override
    public int getNumberOfAliveCritters() {
        return critters.stream().mapToInt(e -> e.alive() ? 1 : 0).sum();
    }

    @Override
    public ReadOnlyCritter getReadOnlyCritter(int c, int r) {
        Tile tile = tileAt(c, r);
        return tile == null ? null : tile.critter();
    }

    @Override
    public int getTerrainInfo(int c, int r) {
        Tile tile = tileAt(c, r);
        if (tile == null) {
            return -1;
        } else if (tile.contents() > 0) {
            throw new IllegalArgumentException("getTerrain Info cannot access Critter at c=" + c + ",r=" + r);
        } else {
            return tile.contents();
        }
    }

    /**
     * Returns the list of critters currently alive
     */
    public List<Critter> critters() {
        return critters;
    }

    /**
     * @return the world object
     */
    public static World world() {
        return world;
    }

    /**
     * Advances the state of the world 1 timestep
     */
    public void step() {
        critters.addAll(temp);
        temp.clear();
        updatedTiles.clear();
        for (Critter c : critters) {
            if (c.alive()) {
                interpreter.evaluate(c);
                addFood();
            }
        }
        // TODO: iterate over updatedTiles and re-render them on the screen.
        critters = critters.stream().filter(e -> e.alive()).collect(Collectors.toList());
        steps++;
    }

    /**
     * Chooses MANA_COUNT hexes to add MANA_AMOUNT food to if the hex is empty or
     * contains food.
     */
    public void addFood() {

        if ((int) (Math.random() * getNumberOfAliveCritters()) == 0) {
            for (int i = 0; i < Constants.MANNA_COUNT; i++) {
                int row = (int) (Math.random() * tiles.length);
                int col = -1;

                if (WIDTH % 2 == 0)
                    col = (int) (Math.random() * tiles[0].length);
                else {
                    col = (int) (Math.random() * tiles[row].length);
                }

                Tile t = tiles[row][col];

                if (t.contents() == 0)
                    t.setContents((Constants.MANNA_AMOUNT + 1) * -1);
                else if (t.contents() < -1) {
                    t.setContents(t.contents() + (Constants.MANNA_AMOUNT * -1));
                }

            }
        }
    }

    /**
     * @param dir integer value representing direction relative to the critters
     *            current direction
     * @example dir 0 returns the contents of the tile directly in front of the
     *          critter
     * @example dir 3 returns the contents of the tile directly behind the critter
     * @return The contents of the tile one tile away in direction, dir, relative to
     *         the critter's current direction
     */
    public static int nearby(Critter c, int relDir) {
        int col = c.col(), row = c.row(), dir = (c.dir() + relDir % 6 + 6) % 6;

        switch (dir) {
        case 0:
            row++;
            break;
        case 1:
            col++;
            row++;
            break;
        case 2:
            col++;
            break;
        case 3:
            row--;
            break;
        case 4:
            row--;
            col--;
            break;
        case 5:
            col--;
            break;
        }

        Tile t = tileAt(col, row);

        if (t == null)
            return -1;

        return t.contents();
    }

    /**
     * Reports the contents of a tile dist hexes away in the critters current
     * direction.
     * 
     * @param dist how far ahead the critter is looking. Negative dist's are treated
     *             as 0.
     * @return the contents of the tile dist hexes ahead in the critters current
     *         direction, or -1 if no such tile exists.
     */
    public static int ahead(Critter c, int dist) {
        if (dist < 0)
            dist = 0;

        Tile t = c.nextTile(dist);

        if (t == null)
            return -1;

        return t.contents();
    }

    /**
     * Moves a critter forward or backward 1 hex, after checking if it is a valid
     * move.
     * 
     * @Effect moves a critter forward or backward 1 hex only if the target tile
     *         exists and is empty.
     * @Effect updates the contents of both tiles involved in the movement and
     *         changes the critter pointer of both Tiles.
     * @param moveDir is either 1 or -1, representing forward or backward movement
     *                respectively
     * @return true if the move is successful
     */
    public static boolean moveCritter(Critter c, int moveDir) {
        int col = c.col();
        int row = c.row();

        Tile initialTile = tileAt(col, row);

        Tile finalTile = c.nextTile(moveDir);

        if (finalTile == null || finalTile.contents() != 0) {
            return false;
        }

        initialTile.setContents(0);
        initialTile.setCritter(null);
        finalTile.setCritter(c);

        updatedTiles.add(initialTile);
        updatedTiles.add(finalTile);

        return true;
    }

    /**
     * Updates the contents of a Tile dist hexes away from a Critter, or does
     * nothing if the hex does not exist
     * 
     * @param dist a non-negative integer representing the distance away from the
     *             critters current tile (might be 0)
     * @requires newContents <= 0 (a critter cannot move into this tile)
     * @effect changes the contents of the tile to newContents, and sets its critter
     *         field to null
     * @example sets a hex with a dead critter to its food value
     * @example sets a hex with food to the new amt of food after a critter eats
     */
    public static void changeContents(Critter c, int dist, int newContents) {
        Tile tile = c.nextTile(dist);

        if (tile == null)
            return;

        tile.setContents(newContents);
        tile.setCritter(null);

        updatedTiles.add(tile);
    }

    /**
     * Adds a critter to the hex at the start of the simulation or after a budding
     * or mating, or the user loads a Critter file
     */
    public static boolean addCritter(Critter c, int col, int row) {
        Tile tile = tileAt(col, row);

        if (tile != null && tile.contents() == 0) {
            tile.setCritter(c);
            temp.add(c);
            return true;
        }

        updatedTiles.add(tile);
        return false;
    }

    /**
     * @returns the Tile at col c and row r in the hex map or null if out of bounds.
     */
    public static Tile tileAt(int col, int row) {
        // this is the column index in the array
        int i = col / 2;
        // this is the row index in the array
        int j = (row - i) * 2 - col % 2;
        if (0 <= j && 0 <= i && j < tiles.length && i < tiles[j].length) {
            // arrays are row first
            return tiles[j][i];
        }
        return null;
    }

    public static Tile[][] tiles() {
        return tiles;
    }

    /**
     * Returns the coordinates of all the empty tiles in a nested list
     * 
     * @return
     */
    public List<List<Integer>> emptyTiles() {

        List<List<Integer>> empty = new ArrayList<List<Integer>>();

        for (Tile[] row : tiles) {
            for (Tile t : row) {
                if (t.empty()) {
                    List<Integer> tile = new ArrayList<Integer>();
                    tile.add(t.column);
                    tile.add(t.row);
                    empty.add(tile);
                }
            }
        }

        return empty;
    }

    /**
     * Clears the world
     */
    public static void clearWorld() {
        world = null;
    }

}
