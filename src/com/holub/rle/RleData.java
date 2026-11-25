package com.holub.rle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of decoding an RLE file: the declared size, optional name and
 * rule, and the list of live cells as {x, y} pairs.
 */
public class RleData {

    private final String name;
    private final String rule;
    private final int width;
    private final int height;
    private final List<int[]> liveCells;

    public RleData(String name, String rule, int width, int height, List<int[]> liveCells) {
        this.name = name;
        this.rule = rule;
        this.width = width;
        this.height = height;
        this.liveCells = Collections.unmodifiableList(new ArrayList<>(liveCells));
    }

    /** Name from the "#N" line, or null. */
    public String getName() {
        return name;
    }

    /** Rule from the header, for example "B3/S23", or null. */
    public String getRule() {
        return rule;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** Each entry is {x, y}, with (0, 0) at the upper left. */
    public List<int[]> getLiveCells() {
        return liveCells;
    }
}
