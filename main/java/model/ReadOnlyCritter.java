package model;

/**
 * Read-only critter interface that allows the course staff to query your critter.
 *
 * <p>NEVER remove or change any methods in this file except reformatting. Feel free to add
 * additional methods in this file. It might be helpful in later assignments.
 */
public interface ReadOnlyCritter {
    /** @return critter species. */
    String getSpecies();

    /**
     * Hint: you should consider making an defensive copy of the array.
     *
     * @return an array representation of critter's memory.
     */
    int[] getMemory();

    /** @return current program string of the critter. */
    String getProgramString();

    /** @return last rule executed by the critter, {@code null} if it has not executed any. */
    String getLastRuleString();
}
