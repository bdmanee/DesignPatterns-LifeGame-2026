package com.holub.rle;

import java.util.ArrayList;
import java.util.List;

/**
 * Decoder for the Run Length Encoded format used by Golly and the
 * LifeWiki (https://conwaylife.com/wiki/Run_Length_Encoded).
 *
 * <pre>
 * #N Glider
 * x = 3, y = 3, rule = B3/S23
 * bo$2bo$3o!
 * </pre>
 *
 * This class does not know anything about the Life game classes.
 */
public class RleDecoder {

    public RleData decode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("RLE text cannot be null");
        }

        String name = null;
        String rule = null;
        int width = -1;
        int height = -1;
        StringBuilder body = new StringBuilder();
        boolean headerFound = false;

        for (String rawLine : text.split("\r?\n")) {
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("#")) {
                if (line.startsWith("#N") && name == null) {
                    name = line.substring(2).trim();
                }
                continue;
            }

            if (!headerFound && line.startsWith("x")) {
                headerFound = true;

                for (String part : line.split(",")) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length != 2) {
                        continue;
                    }

                    String key = keyValue[0].trim().toLowerCase();
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "x":
                            width = parseSize(value);
                            break;
                        case "y":
                            height = parseSize(value);
                            break;
                        case "rule":
                            rule = value;
                            break;
                        default:
                            break;
                    }
                }
                continue;
            }

            body.append(line);
        }

        if (!headerFound) {
            throw new IllegalArgumentException("Missing 'x = .., y = ..' header line");
        }

        List<int[]> cells = decodeBody(body.toString());

        return new RleData(name, rule, width, height, cells);
    }

    private List<int[]> decodeBody(String body) {
        List<int[]> cells = new ArrayList<>();
        int x = 0;
        int y = 0;
        int count = 0;

        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c)) {
                count = count * 10 + (c - '0');
                continue;
            }

            int run = (count == 0) ? 1 : count;
            count = 0;

            if (c == '!') {
                return cells;
            } else if (c == '$') {
                y += run;
                x = 0;
            } else if (c == 'b' || c == '.') {
                x += run;
            } else if (Character.isLetter(c)) {
                // 'o' is a live cell. Other letters are states of
                // multi-state rules, we treat them as alive.
                for (int j = 0; j < run; j++) {
                    cells.add(new int[] {x, y});
                    x++;
                }
            } else {
                throw new IllegalArgumentException(
                        "Unexpected character '" + c + "' in RLE data");
            }
        }

        return cells;   // some files have no '!' at the end
    }

    private int parseSize(String value) {
        try {
            int size = Integer.parseInt(value);
            if (size < 0) {
                throw new IllegalArgumentException("Negative size in RLE header");
            }
            return size;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bad size in RLE header: " + value);
        }
    }
}
