package com.holub.life;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Memento of a LifeBoard: the generation number and every live cell
 * with its age. Only LifeBoard can create or read one, other classes
 * just keep it and give it back.
 */
public final class BoardSnapshot {

    private final int generation;
    private final Map<Point, Integer> aliveCells;   // position -> age

    BoardSnapshot(int generation, Map<Point, Integer> aliveCells) {
        this.generation = generation;
        this.aliveCells = Collections.unmodifiableMap(new HashMap<>(aliveCells));
    }

    int getGeneration() {
        return generation;
    }

    Map<Point, Integer> getAliveCells() {
        return aliveCells;
    }

    public int getPopulation() {
        return aliveCells.size();
    }

    /** true if both snapshots have the same live cells (ages are not compared). */
    public boolean sameCellsAs(BoardSnapshot other) {
        return other != null && aliveCells.keySet().equals(other.aliveCells.keySet());
    }
}
