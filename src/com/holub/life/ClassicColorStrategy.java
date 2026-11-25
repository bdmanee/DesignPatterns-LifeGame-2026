package com.holub.life;

import java.awt.Color;

/** Every live cell has the same color (the look before this change). */
public class ClassicColorStrategy implements CellColorStrategy {

    @Override
    public Color colorFor(boolean alive, int age) {
        return alive ? NeonTheme.CYAN : NeonTheme.BACKGROUND;
    }

    @Override
    public String getName() {
        return "Classic Colors";
    }
}
