package com.holub.life;

import java.awt.Color;

/** Decides which color a cell is drawn with. */
public interface CellColorStrategy {

    Color colorFor(boolean alive, int age);

    String getName();
}
