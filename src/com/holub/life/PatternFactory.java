package com.holub.life;

/**
 * Kept so old code keeps working. It now just asks the
 * PatternRegistry for a copy of the prototype instead of
 * using a switch statement.
 */
public final class PatternFactory {

    private PatternFactory() {
        // Prevent creating factory objects.
    }

    public static LifePattern createPattern(String name) {
        return PatternRegistry.getDefault().create(name);
    }
}
