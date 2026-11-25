package com.holub.life;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prototype manager for patterns. A pattern is registered once and
 * create() always returns a copy, so nobody can change the stored
 * prototype. New patterns (for example ones loaded from a file) can be
 * added at runtime without changing this class.
 */
public class PatternRegistry {

    public interface Listener {
        void patternRegistered(LifePattern pattern);
    }

    private final Map<String, LifePattern> prototypes = new LinkedHashMap<>();
    private final List<Listener> listeners = new ArrayList<>();

    private static PatternRegistry defaultRegistry = null;

    /** The registry used by the application, filled with the built-in patterns. */
    public static synchronized PatternRegistry getDefault() {
        if (defaultRegistry == null) {
            defaultRegistry = new PatternRegistry();
            defaultRegistry.registerBuiltInPatterns();
        }
        return defaultRegistry;
    }

    public void register(LifePattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }

        LifePattern copy = pattern.clone();
        prototypes.put(key(pattern.getName()), copy);

        for (Listener listener : listeners) {
            listener.patternRegistered(copy.clone());
        }
    }

    public boolean contains(String name) {
        return name != null && prototypes.containsKey(key(name));
    }

    /** Returns a copy of the registered prototype. Names are not case sensitive. */
    public LifePattern create(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Pattern name cannot be null");
        }

        LifePattern prototype = prototypes.get(key(name));

        if (prototype == null) {
            throw new IllegalArgumentException("Unknown pattern: " + name);
        }

        return prototype.clone();
    }

    /** Names in the order they were registered. */
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (LifePattern pattern : prototypes.values()) {
            names.add(pattern.getName());
        }
        return Collections.unmodifiableList(names);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private static String key(String name) {
        return name.trim().toLowerCase();
    }

    private void registerBuiltInPatterns() {
        register(LifePattern.fromRows("Glider",
                ".O.",
                "..O",
                "OOO"));

        register(LifePattern.fromRows("Blinker",
                "...",
                "OOO",
                "..."));

        register(LifePattern.fromRows("Block",
                "OO",
                "OO"));

        register(LifePattern.fromRows("Toad",
                ".OOO",
                "OOO."));

        register(LifePattern.fromRows("Beacon",
                "OO..",
                "OO..",
                "..OO",
                "..OO"));

        register(LifePattern.fromRows("LWSS",
                ".O..O",
                "O....",
                "O...O",
                "OOOO."));

        register(LifePattern.fromRows("R-pentomino",
                ".OO",
                "OO.",
                ".O."));

        register(LifePattern.fromRows("Pulsar",
                "..OOO...OOO..",
                ".............",
                "O....O.O....O",
                "O....O.O....O",
                "O....O.O....O",
                "..OOO...OOO..",
                ".............",
                "..OOO...OOO..",
                "O....O.O....O",
                "O....O.O....O",
                "O....O.O....O",
                ".............",
                "..OOO...OOO.."));

        register(LifePattern.fromRows("Gosper Glider Gun",
                "........................O...........",
                "......................O.O...........",
                "............OO......OO............OO",
                "...........O...O....OO............OO",
                "OO........O.....O...OO..............",
                "OO........O...O.OO....O.O...........",
                "..........O.....O.......O...........",
                "...........O...O....................",
                "............OO......................"));
    }
}
