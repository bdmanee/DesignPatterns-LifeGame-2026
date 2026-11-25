package com.holub.life;

/**
 * Collects all the statistics in one walk over the board:
 * population, oldest and average age, and the size of the box
 * around all live cells.
 */
public class StatisticsVisitor implements CellVisitor {

    private int population = 0;
    private long totalAge = 0;
    private int oldestAge = 0;

    private int minRow = Integer.MAX_VALUE;
    private int maxRow = Integer.MIN_VALUE;
    private int minColumn = Integer.MAX_VALUE;
    private int maxColumn = Integer.MIN_VALUE;

    @Override
    public void visitResident(Resident resident, int row, int column) {
        if (!resident.isAlive()) {
            return;
        }

        population++;

        int age = resident.getAge();
        totalAge += age;
        oldestAge = Math.max(oldestAge, age);

        minRow = Math.min(minRow, row);
        maxRow = Math.max(maxRow, row);
        minColumn = Math.min(minColumn, column);
        maxColumn = Math.max(maxColumn, column);
    }

    public BoardStatistics getResult(int generation) {
        if (population == 0) {
            return new BoardStatistics(generation, 0, 0, 0, 0, 0);
        }

        return new BoardStatistics(
                generation,
                population,
                oldestAge,
                totalAge / (double) population,
                maxColumn - minColumn + 1,
                maxRow - minRow + 1);
    }
}
