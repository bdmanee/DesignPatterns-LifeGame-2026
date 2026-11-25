package com.holub.life;

/**
 * Holds the color strategy used by Resident.redraw(). Residents are
 * created by cloning a prototype and there are 65536 of them, so they
 * all share this one setting instead of each keeping a reference.
 */
public final class CellColors {

    private static CellColorStrategy strategy = new ClassicColorStrategy();

    private CellColors() {
    }

    public static CellColorStrategy getStrategy() {
        return strategy;
    }

    public static void setStrategy(CellColorStrategy newStrategy) {
        if (newStrategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        strategy = newStrategy;
    }

    public static void reset() {
        strategy = new ClassicColorStrategy();
    }
}
