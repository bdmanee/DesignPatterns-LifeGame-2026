package com.holub.life;

import java.util.Arrays;

/**
 * A rule written in the usual "B/S" notation, for example
 * B3/S23 for Conway. A dead cell is born when its number of living
 * neighbors is in the birth set, and a living cell survives when the
 * number is in the survival set.
 *
 * Objects are immutable. Use RuleBuilder to create them.
 */
public final class BirthSurvivalRule implements LifeRule {

    private final String name;
    private final boolean[] birth;
    private final boolean[] survival;

    BirthSurvivalRule(String name, boolean[] birth, boolean[] survival) {
        this.birth = birth.clone();
        this.survival = survival.clone();
        this.name = (name != null) ? name : getNotation();
    }

    @Override
    public boolean nextState(boolean currentlyAlive, int livingNeighbors) {
        if (livingNeighbors < 0 || livingNeighbors > 8) {
            return false;
        }

        return currentlyAlive ? survival[livingNeighbors] : birth[livingNeighbors];
    }

    @Override
    public String getName() {
        return name;
    }

    /** For example "B36/S23". */
    public String getNotation() {
        StringBuilder text = new StringBuilder("B");
        for (int i = 0; i <= 8; i++) {
            if (birth[i]) {
                text.append(i);
            }
        }

        text.append("/S");
        for (int i = 0; i <= 8; i++) {
            if (survival[i]) {
                text.append(i);
            }
        }

        return text.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BirthSurvivalRule)) {
            return false;
        }

        BirthSurvivalRule rule = (BirthSurvivalRule) other;
        return Arrays.equals(birth, rule.birth)
                && Arrays.equals(survival, rule.survival);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(birth) + Arrays.hashCode(survival);
    }

    @Override
    public String toString() {
        return name + " (" + getNotation() + ")";
    }
}
