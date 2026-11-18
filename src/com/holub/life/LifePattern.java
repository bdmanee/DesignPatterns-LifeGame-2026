package com.holub.life;

public final class LifePattern {

    private final String name;
    private final boolean[][] cells;

    public LifePattern(String name, boolean[][] cells) {
        this.name = name;

        this.cells = new boolean[cells.length][];

        for (int row = 0; row < cells.length; row++) {
            this.cells[row] = cells[row].clone();
        }
    }

    public String getName() {
        return name;
    }

    public boolean[][] getCells() {
        boolean[][] copy = new boolean[cells.length][];

        for (int row = 0; row < cells.length; row++) {
            copy[row] = cells[row].clone();
        }

        return copy;
    }
}
