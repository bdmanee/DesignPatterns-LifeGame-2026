package com.holub.life;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The rules shown in the Rules menu. */
public final class RuleCatalog {

    private RuleCatalog() {
    }

    public static List<LifeRule> presets() {
        List<LifeRule> rules = new ArrayList<>();

        rules.add(new ConwayRule());
        rules.add(new HighLifeRule());
        rules.add(RuleBuilder.fromNotation("B2/S").named("Seeds").build());
        rules.add(RuleBuilder.fromNotation("B3678/S34678").named("Day & Night").build());
        rules.add(RuleBuilder.fromNotation("B3/S012345678").named("Life Without Death").build());
        rules.add(RuleBuilder.fromNotation("B3/S12345").named("Maze").build());

        return Collections.unmodifiableList(rules);
    }
}
