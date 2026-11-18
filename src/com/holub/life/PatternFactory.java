package com.holub.life;

public final class PatternFactory {

    private PatternFactory() {
        // Prevent creating factory objects.
    }

    public static LifePattern createPattern(String name) {

        if (name == null) {
            throw new IllegalArgumentException(
                    "Pattern name cannot be null"
            );
        }

        switch (name.toLowerCase()) {

            case "glider":
                return new LifePattern("Glider", new boolean[][] {
                        {false, true,  false},
                        {false, false, true },
                        {true,  true,  true }
                });

            case "blinker":
                return new LifePattern("Blinker", new boolean[][] {
                        {false, false, false},
                        {true,  true,  true },
                        {false, false, false}
                });

            case "block":
                return new LifePattern("Block", new boolean[][] {
                        {true, true},
                        {true, true}
                });

            default:
                throw new IllegalArgumentException(
                        "Unknown pattern: " + name
                );
        }
    }
}
