package com.holub.life;

public interface LifeRule {

    boolean nextState(boolean currentlyAlive, int livingNeighbors);

    default String getName() {
        return getClass().getSimpleName();
    }

}
