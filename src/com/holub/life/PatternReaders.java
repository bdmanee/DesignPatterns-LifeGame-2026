package com.holub.life;

/** Picks a PatternReader from the file extension. */
public final class PatternReaders {

    private PatternReaders() {
    }

    public static PatternReader forFileName(String fileName) {
        String name = fileName.toLowerCase();

        if (name.endsWith(".rle")) {
            return new RlePatternAdapter();
        }
        if (name.endsWith(".cells") || name.endsWith(".txt")) {
            return new PlaintextPatternReader();
        }

        throw new IllegalArgumentException(
                "Unsupported pattern file (use .rle or .cells): " + fileName);
    }

    /** "gosperglidergun.rle" -> "gosperglidergun" */
    public static String baseName(String fileName) {
        String name = new java.io.File(fileName).getName();
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }
}
