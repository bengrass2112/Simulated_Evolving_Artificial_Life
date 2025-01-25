package model;

/**
 * Overview: represents a single tile in the world
 */
public class Tile {

    final int column, row;
    private int contents;

    private Critter critter = null;

    public Tile(int c, int r) {
        column = c;
        row = r;
    }

    /**
     * @return the column of this tile
     */
    public int col() {
        return column;
    }

    /**
     * @return the row of this tile
     */
    public int row() {
        return row;
    }

    /**
     * @return the numerical representation of the contents of this hex. n < -1 is
     *         (-n)-1 food, n = -1 is rock, n = 0 is empty and n > 0 is critter.
     */
    public int contents() {
		World.lock.readLock().lock();
		int content;
        if (critter != null)
			content = critter.appearance();
		else
			content = contents;
		World.lock.readLock().unlock();
		return content;
    }

    /**
     * @return true if hex is empty
     */
    public boolean empty() {
		if (contents() == 0)
            return true;
        return false;
    }

    /**
     * Sets the contents of this tile to newContents
     */
    public void setContents(int newContents) {
		if (contents != newContents) {
        contents = newContents;
			World.world().updatedTiles.add(this);
		}
    }

    /**
     * returns the Critter, or null
     */
    public Critter critter() {
        return critter;
    }

    /**
     * Sets critter field to a valid critter or to null
     */
    public void setCritter(Critter c) {
		if (critter != c) {
			critter = c;
			World.world().updatedTiles.add(this);
		}
    }

    /**
     * @return the slice, representing row - column;
     */
    public int slice() {
        return row - column;
    }

    /**
     * @return a character representing the contents of the Tile
     */
    public String toString() {

		if (contents() < -1) {
            return "F";
		} else if (contents() == -1) {
            return "#";
		} else if (contents() == 0) {
            return "-";
        } else {
            return critter.dir() % 6 + "";
        }
    }
}
