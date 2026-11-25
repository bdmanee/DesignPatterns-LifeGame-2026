package com.holub.life;

public final class RuleManager {

    private static LifeRule currentRule = new ConwayRule();

    private RuleManager() {
        // Prevent creating RuleManager objects.
    }

    public static LifeRule getRule() {
        return currentRule;
    }

    public static void setRule(LifeRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException(
                    "Rule cannot be null"
            );
        }

        currentRule = rule;
    }

    /** Go back to the default rule (Conway). Mostly for tests. */
    public static void reset() {
        currentRule = new ConwayRule();
    }
}
