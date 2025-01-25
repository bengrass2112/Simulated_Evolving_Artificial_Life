package model;

/**
 * Read-only state interface that allows the course staff to query your critter world.
 *
 * <p>NEVER remove or change any methods in this file except reformatting. Feel free to add
 * additional methods in this file. It might be helpful in later assignments.
 */
public interface ReadOnlyWorld {
    /** @return number of steps */
    int getSteps();

    /** @return number of alive critters. */
    int getNumberOfAliveCritters();

    /**
     * @param c column id.
     * @param r row id.
     * @return the critter at the specified hex, {@code null} if there is no critter there. Out of
     *     bound hexes should also return null.
     */
    ReadOnlyCritter getReadOnlyCritter(int c, int r);

    /**
     * @param c column id.
     * @param r row id.
     * @return 0 is empty, -1 is rock, -X is (X-1) food. Treat out-of-bound hex as rock.
     * @throws IllegalArgumentException if the hex is occupied by a critter.
     */
    int getTerrainInfo(int c, int r);
}
