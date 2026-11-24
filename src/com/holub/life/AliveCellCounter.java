package com.holub.life;

public class AliveCellCounter implements CellVisitor {

    private int count = 0;

    @Override
    public void visitResident(Resident resident, int row, int column) {
        if (resident.isAlive()) {
            count++;
        }
    }

    public int getCount() {
        return count;
    }
}
