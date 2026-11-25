package com.holub.life;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        outermostCell.figureNextState(
                Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY,
                Cell.DUMMY, Cell.DUMMY, Cell.DUMMY, Cell.DUMMY);

        // transition() is always called (even when nothing changes)
        // so that the age of stable cells keeps counting up.
        outermostCell.transition();

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

    /** Age of the cell at (row, column), 0 if dead or outside the board. */
    public int getAge(int row, int column) {
        CellAgeFinder finder = new CellAgeFinder(row, column);
        outermostCell.accept(finder, 0, 0);
        return finder.getAge();
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

    /** Run a visitor over every cell of the board. */
    public void accept(CellVisitor visitor) {
        outermostCell.accept(visitor, 0, 0);
    }

    /** Memento: remember the current cells, their ages and the generation. */
    public BoardSnapshot createSnapshot() {
        Map<Point, Integer> cells = new HashMap<>();

        accept((resident, row, column) -> {
            if (resident.isAlive()) {
                cells.put(new Point(column, row), resident.getAge());
            }
        });

        return new BoardSnapshot(generation, cells);
    }

    /** Put the board back to the state saved in the snapshot. */
    public void restore(BoardSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null");
        }

        outermostCell.clear();

        Map<Point, Integer> cells = snapshot.getAliveCells();
        for (Point cell : cells.keySet()) {
            outermostCell.setAliveAt(cell.y, cell.x, true);
        }

        accept((resident, row, column) -> {
            Integer age = cells.get(new Point(column, row));
            if (age != null) {
                resident.restoreAge(age);
            }
        });

        generation = snapshot.getGeneration();
        fireChanged();
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
}
