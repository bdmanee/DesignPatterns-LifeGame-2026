package com.holub.life;

public class ConwayRule implements LifeRule {

    @Override
    public boolean nextState(boolean currentlyAlive, int livingNeighbors) {

        if (currentlyAlive) {
            return livingNeighbors == 2 || livingNeighbors == 3;
        }

        return livingNeighbors == 3;
    }

    @Override
    public String getName() {
        return "Conway";
    }
}
