package com.holub.life;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The model of the game. It owns the Cell composite and the
 * generation counter and knows nothing about Swing components,
 * so it can be tested without opening a window.
 *
 * Views register as GameStateListeners and are told whenever
 * the board changes.
 */
public class LifeBoard {

    public static final int DEFAULT_NEIGHBORHOODS = 32;
    public static final int DEFAULT_CELLS_PER_NEIGHBORHOOD = 8;

    private final Cell outermostCell;
    private final List<GameStateListener> listeners =
            new CopyOnWriteArrayList<>();

    private int generation = 0;

    public LifeBoard() {
        this(DEFAULT_NEIGHBORHOODS, DEFAULT_CELLS_PER_NEIGHBORHOOD);
    }

    public LifeBoard(int neighborhoods, int cellsPerNeighborhood) {
        if (neighborhoods < 1 || cellsPerNeighborhood < 1) {
            throw new IllegalArgumentException("Board size must be positive");
        }

        outermostCell = new Neighborhood(
                neighborhoods,
                new Neighborhood(cellsPerNeighborhood, new Resident())
        );
    }

    /** Width (and height) of the board in cells. */
    public int getSize() {
        return outermostCell.widthInCells();
    }

    public int getGeneration() {
        return generation;
    }

    /** Compute and move to the next generation. */
    public void tick() {
        generation++;

        if (outermostCell.figureNextState(
                Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY,
                Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY)) {
            outermostCell.transition();
        }

        fireChanged();
    }

    public boolean isAlive(int row, int column) {
        return outermostCell.isAliveAt(row, column);
    }

    public boolean setAlive(int row, int column, boolean alive) {
        boolean changed = outermostCell.setAliveAt(row, column, alive);

        if (changed) {
            fireChanged();
        }

        return changed;
    }

    public void toggle(int row, int column) {
        setAlive(row, column, !isAlive(row, column));
    }

    /** Put a pattern on the board with its upper-left corner at
     *  (startRow, startColumn). Cells that fall off the board are
     *  ignored. Existing live cells are not turned off. */
    public void placePattern(LifePattern pattern, int startRow, int startColumn) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }

        boolean[][] cells = pattern.getCells();

        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                if (cells[row][column]) {
                    outermostCell.setAliveAt(
                            startRow + row, startColumn + column, true);
                }
            }
        }

        fireChanged();
    }

    public void clear() {
        outermostCell.clear();
        generation = 0;
        fireChanged();
    }

    public int getAliveCellCount() {
        AliveCellCounter counter = new AliveCellCounter();
        outermostCell.accept(counter, 0, 0);
        return counter.getCount();
    }

    /** Positions of the living cells. Point.x is the column, Point.y the row. */
    public List<Point> getAliveCells() {
        AliveCellCollector collector = new AliveCellCollector();
        outermostCell.accept(collector, 0, 0);
        return collector.getAliveCells();
    }

    /** Save the board using the original Holub memento format. */
    public void store(OutputStream out) throws IOException {
        Storable memento = outermostCell.createMemento();
        outermostCell.transfer(memento, new Point(0, 0), Cell.STORE);
        memento.flush(out);
    }

    /** Load a board saved by store(). The generation goes back to 0. */
    public void load(InputStream in) throws IOException {
        outermostCell.clear();

        Storable memento = outermostCell.createMemento();
        memento.load(in);
        outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

        generation = 0;
        fireChanged();
    }

    public void redraw(Graphics g, Rectangle bounds) {
        outermostCell.redraw(g, bounds, true);
    }

    public void addGameStateListener(GameStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        listeners.add(listener);
    }

    public void removeGameStateListener(GameStateListener listener) {
        listeners.remove(listener);
    }

    protected void fireChanged() {
        int aliveCells = getAliveCellCount();

        for (GameStateListener listener : listeners) {
            listener.onGameStateChanged(generation, aliveCells);
        }
    }

    /** Used when restoring a saved state (for example by undo). */
    void setGeneration(int generation) {
        this.generation = generation;
    }
}
