package com.holub.life;

/** Finds the age of one cell. */
class CellAgeFinder implements CellVisitor {

    private final int row;
    private final int column;
    private int age = 0;

    CellAgeFinder(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public void visitResident(Resident resident, int row, int column) {
        if (row == this.row && column == this.column) {
            age = resident.getAge();
        }
    }

    int getAge() {
        return age;
    }
}
