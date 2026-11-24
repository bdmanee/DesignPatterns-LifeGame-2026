package com.holub.life;

/**
 * Visitor for walking through the Cell composite.
 * row and column are the absolute position of the cell on the board.
 */
public interface CellVisitor {

    void visitResident(Resident resident, int row, int column);

    default void visitNeighborhood(Neighborhood neighborhood, int row, int column) {
        // most visitors only care about the residents
    }
}
