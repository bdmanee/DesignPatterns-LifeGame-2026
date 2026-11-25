package com.holub.life;

/**
 * Builds BirthSurvivalRule objects step by step:
 *
 * <pre>
 * LifeRule rule = new RuleBuilder()
 *         .named("HighLife")
 *         .bornWith(3, 6)
 *         .survivesWith(2, 3)
 *         .build();
 *
 * LifeRule same = RuleBuilder.fromNotation("B36/S23").named("HighLife").build();
 * </pre>
 */
public class RuleBuilder {

    private String name = null;
    private final boolean[] birth = new boolean[9];
    private final boolean[] survival = new boolean[9];

    public RuleBuilder named(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be empty");
        }

        this.name = name.trim();
        return this;
    }

    public RuleBuilder bornWith(int... neighborCounts) {
        for (int count : neighborCounts) {
            birth[check(count)] = true;
        }

        return this;
    }

    public RuleBuilder survivesWith(int... neighborCounts) {
        for (int count : neighborCounts) {
            survival[check(count)] = true;
        }

        return this;
    }

    public LifeRule build() {
        if (birth[0]) {
            // B0 would turn the whole empty board on in one step,
            // which the neighborhood optimization can't handle.
            throw new IllegalStateException("B0 rules are not supported");
        }

        // without a name the rule is called by its notation
        return new BirthSurvivalRule(name, birth, survival);
    }

    /**
     * Read a rule like "B3/S23". Case and spaces are ignored and the
     * two parts can be in either order. "B2/S" (nothing survives) is allowed.
     */
    public static RuleBuilder fromNotation(String notation) {
        if (notation == null) {
            throw new IllegalArgumentException("Rule notation cannot be null");
        }

        String text = notation.replace(" ", "").toUpperCase();
        String[] parts = text.split("/");

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Rule must look like B3/S23: " + notation);
        }

        RuleBuilder builder = new RuleBuilder();
        boolean sawBirth = false;
        boolean sawSurvival = false;

        for (String part : parts) {
            if (part.startsWith("B") && !sawBirth) {
                builder.bornWith(digits(part.substring(1), notation));
                sawBirth = true;
            } else if (part.startsWith("S") && !sawSurvival) {
                builder.survivesWith(digits(part.substring(1), notation));
                sawSurvival = true;
            } else {
                throw new IllegalArgumentException(
                        "Rule must look like B3/S23: " + notation);
            }
        }

        return builder;
    }

    private static int[] digits(String text, String notation) {
        int[] result = new int[text.length()];

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c < '0' || c > '8') {
                throw new IllegalArgumentException(
                        "Bad neighbor count '" + c + "' in " + notation);
            }

            result[i] = c - '0';
        }

        return result;
    }

    private static int check(int count) {
        if (count < 0 || count > 8) {
            throw new IllegalArgumentException(
                    "Neighbor count must be 0..8, got " + count);
        }

        return count;
    }
}
