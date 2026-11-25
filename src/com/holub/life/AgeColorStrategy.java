package com.holub.life;

import java.awt.Color;

/**
 * Colors live cells by age: new cells are white, then the color goes
 * cyan -> purple -> pink as the cell gets older. Cells older than
 * maxAge all get the last color.
 */
public class AgeColorStrategy implements CellColorStrategy {

    private static final Color[] STOPS = {
            Color.WHITE, NeonTheme.CYAN, NeonTheme.PURPLE, NeonTheme.PINK
    };

    private final int maxAge;

    public AgeColorStrategy() {
        this(30);
    }

    public AgeColorStrategy(int maxAge) {
        if (maxAge < 1) {
            throw new IllegalArgumentException("maxAge must be positive");
        }
        this.maxAge = maxAge;
    }

    @Override
    public Color colorFor(boolean alive, int age) {
        if (!alive) {
            return NeonTheme.BACKGROUND;
        }

        double t = Math.min(1.0, Math.max(0, age) / (double) maxAge);

        // find the two stops t lies between and mix them
        double position = t * (STOPS.length - 1);
        int index = Math.min((int) position, STOPS.length - 2);
        double mix = position - index;

        return blend(STOPS[index], STOPS[index + 1], mix);
    }

    @Override
    public String getName() {
        return "Color by Age";
    }

    private static Color blend(Color a, Color b, double mix) {
        return new Color(
                (int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * mix),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * mix),
                (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * mix));
    }
}
