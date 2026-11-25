package com.holub.life;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observes a LifeBoard and keeps the population history for the graph.
 * It is an observer of the board and a subject for the statistics view.
 */
public class StatisticsTracker implements GameStateListener {

    public static final int DEFAULT_HISTORY_SIZE = 300;

    private final LifeBoard board;
    private final int historySize;

    // populationHistory.get(i) is the population at generation firstGeneration + i
    private final List<Integer> populationHistory = new ArrayList<>();
    private int firstGeneration = 0;

    private BoardStatistics latest;
    private int peakPopulation = 0;
    private int peakGeneration = 0;

    private final List<StatisticsListener> listeners = new CopyOnWriteArrayList<>();

    public StatisticsTracker(LifeBoard board) {
        this(board, DEFAULT_HISTORY_SIZE);
    }

    public StatisticsTracker(LifeBoard board, int historySize) {
        if (historySize < 2) {
            throw new IllegalArgumentException("History must hold at least 2 values");
        }

        this.board = board;
        this.historySize = historySize;

        board.addGameStateListener(this);
        update(board.getGeneration());
    }

    @Override
    public void onGameStateChanged(int generation, int aliveCells) {
        update(generation);
    }

    private void update(int generation) {
        StatisticsVisitor visitor = new StatisticsVisitor();
        board.accept(visitor);
        latest = visitor.getResult(generation);

        record(generation, latest.getPopulation());

        for (StatisticsListener listener : listeners) {
            listener.statisticsChanged(this);
        }
    }

    private void record(int generation, int population) {
        int lastGeneration = firstGeneration + populationHistory.size() - 1;

        if (populationHistory.isEmpty() || generation < firstGeneration) {
            // empty, or went back before the start (clear, load, undo)
            populationHistory.clear();
            firstGeneration = generation;
            peakPopulation = 0;
        } else if (generation <= lastGeneration) {
            // went back a few generations (undo, clear) or the board was
            // edited without a tick: forget the newer values
            while (firstGeneration + populationHistory.size() > generation) {
                populationHistory.remove(populationHistory.size() - 1);
            }
            recomputePeak();
        }

        populationHistory.add(population);

        while (populationHistory.size() > historySize) {
            populationHistory.remove(0);
            firstGeneration++;
        }

        if (population >= peakPopulation) {
            peakPopulation = population;
            peakGeneration = generation;
        }
    }

    private void recomputePeak() {
        peakPopulation = 0;
        peakGeneration = firstGeneration;

        for (int i = 0; i < populationHistory.size(); i++) {
            if (populationHistory.get(i) >= peakPopulation) {
                peakPopulation = populationHistory.get(i);
                peakGeneration = firstGeneration + i;
            }
        }
    }

    public BoardStatistics getLatest() {
        return latest;
    }

    public List<Integer> getPopulationHistory() {
        return Collections.unmodifiableList(new ArrayList<>(populationHistory));
    }

    public int getFirstGeneration() {
        return firstGeneration;
    }

    public int getPeakPopulation() {
        return peakPopulation;
    }

    public int getPeakGeneration() {
        return peakGeneration;
    }

    public void addStatisticsListener(StatisticsListener listener) {
        listeners.add(listener);
        listener.statisticsChanged(this);
    }

    public void removeStatisticsListener(StatisticsListener listener) {
        listeners.remove(listener);
    }

    public void detach() {
        board.removeGameStateListener(this);
    }
}
