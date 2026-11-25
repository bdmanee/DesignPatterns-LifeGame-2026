package com.holub.life;

public class HighLifeRule implements LifeRule {

    @Override
    public boolean nextState(boolean currentlyAlive, int livingNeighbors) {

        if (currentlyAlive) {
            return livingNeighbors == 2 || livingNeighbors == 3;
        }

        return livingNeighbors == 3 || livingNeighbors == 6;
    }

    @Override
    public String getName() {
        return "HighLife";
    }
}
