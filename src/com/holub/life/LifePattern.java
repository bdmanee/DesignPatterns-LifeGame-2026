package com.holub.life;

/**
 * A named shape of live cells. Patterns are used as prototypes:
 * PatternRegistry keeps one of each and hands out copies.
 */
public final class LifePattern implements Cloneable {

    private final String name;
    private final boolean[][] cells;

    public LifePattern(String name, boolean[][] cells) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern name cannot be empty");
        }
        if (cells == null || cells.length == 0) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }

        this.name = name;

        // rows can have different lengths in the input, so make
        // every row as wide as the widest one
        int width = 0;
        for (boolean[] row : cells) {
            width = Math.max(width, row.length);
        }

        this.cells = new boolean[cells.length][width];

        for (int row = 0; row < cells.length; row++) {
            System.arraycopy(cells[row], 0, this.cells[row], 0, cells[row].length);
        }
    }

    /**
     * Make a pattern from rows of text where 'O' (or '*') is a live
     * cell and anything else is dead, e.g. {".O.", "..O", "OOO"}.
     */
    public static LifePattern fromRows(String name, String... rows) {
        boolean[][] cells = new boolean[rows.length][];

        for (int row = 0; row < rows.length; row++) {
            cells[row] = new boolean[rows[row].length()];

            for (int column = 0; column < rows[row].length(); column++) {
                char c = rows[row].charAt(column);
                cells[row][column] = (c == 'O' || c == '*');
            }
        }

        return new LifePattern(name, cells);
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return cells.length;
    }

    public int getWidth() {
        return cells[0].length;
    }

    public int getAliveCount() {
        int count = 0;
        for (boolean[] row : cells) {
            for (boolean cell : row) {
                if (cell) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean[][] getCells() {
        boolean[][] copy = new boolean[cells.length][];

        for (int row = 0; row < cells.length; row++) {
            copy[row] = cells[row].clone();
        }

        return copy;
    }

    /** Deep copy. The cell array is copied too. */
    @Override
    public LifePattern clone() {
        return new LifePattern(name, cells);
    }

    public LifePattern rename(String newName) {
        return new LifePattern(newName, cells);
    }

    /** A new pattern turned 90 degrees clockwise. */
    public LifePattern rotateClockwise() {
        int height = getHeight();
        int width = getWidth();
        boolean[][] rotated = new boolean[width][height];

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                rotated[column][height - 1 - row] = cells[row][column];
            }
        }

        return new LifePattern(name, rotated);
    }

    /** A new pattern mirrored left to right. */
    public LifePattern flipHorizontal() {
        boolean[][] flipped = new boolean[getHeight()][getWidth()];

        for (int row = 0; row < getHeight(); row++) {
            for (int column = 0; column < getWidth(); column++) {
                flipped[row][getWidth() - 1 - column] = cells[row][column];
            }
        }

        return new LifePattern(name, flipped);
    }

    public boolean sameShapeAs(LifePattern other) {
        return other != null && java.util.Arrays.deepEquals(cells, other.cells);
    }
}
