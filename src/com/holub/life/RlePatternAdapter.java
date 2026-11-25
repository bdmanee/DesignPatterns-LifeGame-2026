package com.holub.life;

import com.holub.rle.RleData;
import com.holub.rle.RleDecoder;

import java.io.IOException;
import java.io.Reader;

/**
 * Adapter: lets the game use RleDecoder through the PatternReader
 * interface. RleDecoder works with strings and {x, y} lists and has no
 * idea what a LifePattern is, so this class converts between the two.
 */
public class RlePatternAdapter implements PatternReader {

    private final RleDecoder decoder;
    private String lastRule = null;

    public RlePatternAdapter() {
        this(new RleDecoder());
    }

    public RlePatternAdapter(RleDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public LifePattern read(Reader in, String defaultName) throws IOException {
        RleData data;
        try {
            data = decoder.decode(readAll(in));
        } catch (IllegalArgumentException e) {
            throw new IOException("Not a valid RLE file: " + e.getMessage(), e);
        }

        lastRule = data.getRule();

        // The header size is sometimes wrong, so use whichever is bigger.
        int width = Math.max(data.getWidth(), 1);
        int height = Math.max(data.getHeight(), 1);
        for (int[] cell : data.getLiveCells()) {
            width = Math.max(width, cell[0] + 1);
            height = Math.max(height, cell[1] + 1);
        }

        boolean[][] cells = new boolean[height][width];
        for (int[] cell : data.getLiveCells()) {
            cells[cell[1]][cell[0]] = true;
        }

        String name = data.getName();
        if (name == null || name.isEmpty()) {
            name = defaultName;
        }

        return new LifePattern(name, cells);
    }

    /** Rule written in the last file read (e.g. "B3/S23"), or null. */
    public String getLastRule() {
        return lastRule;
    }

    static String readAll(Reader in) throws IOException {
        StringBuilder text = new StringBuilder();
        char[] buffer = new char[4096];
        int count;

        while ((count = in.read(buffer)) != -1) {
            text.append(buffer, 0, count);
        }

        return text.toString();
    }
}
