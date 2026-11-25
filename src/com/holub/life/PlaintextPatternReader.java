package com.holub.life;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the LifeWiki "plaintext" (.cells) format:
 * lines starting with '!' are comments ("!Name: Glider" gives the name),
 * 'O' is a live cell and '.' is a dead one.
 */
public class PlaintextPatternReader implements PatternReader {

    @Override
    public LifePattern read(Reader in, String defaultName) throws IOException {
        String name = defaultName;
        List<String> rows = new ArrayList<>();

        for (String line : RlePatternAdapter.readAll(in).split("\r?\n")) {
            if (line.startsWith("!")) {
                if (line.startsWith("!Name:")) {
                    name = line.substring("!Name:".length()).trim();
                }
                continue;
            }
            rows.add(line);
        }

        // drop empty lines at the end
        while (!rows.isEmpty() && rows.get(rows.size() - 1).trim().isEmpty()) {
            rows.remove(rows.size() - 1);
        }

        if (rows.isEmpty()) {
            throw new IOException("No cells in file");
        }

        return LifePattern.fromRows(name, rows.toArray(new String[0]));
    }
}
