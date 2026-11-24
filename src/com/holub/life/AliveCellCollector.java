package com.holub.life;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects the positions of all living cells.
 * Point.x is the column and Point.y is the row.
 */
public class AliveCellCollector implements CellVisitor {

    private final List<Point> aliveCells = new ArrayList<>();

    @Override
    public void visitResident(Resident resident, int row, int column) {
        if (resident.isAlive()) {
            aliveCells.add(new Point(column, row));
        }
    }

    public List<Point> getAliveCells() {
        return aliveCells;
    }
}
