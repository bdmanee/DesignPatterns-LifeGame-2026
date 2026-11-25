package com.holub.life;

/** Numbers about the board at one moment. Immutable. */
public final class BoardStatistics {

    private final int generation;
    private final int population;
    private final int oldestAge;
    private final double averageAge;
    private final int boundingWidth;
    private final int boundingHeight;

    public BoardStatistics(int generation, int population, int oldestAge,
                           double averageAge, int boundingWidth, int boundingHeight) {
        this.generation = generation;
        this.population = population;
        this.oldestAge = oldestAge;
        this.averageAge = averageAge;
        this.boundingWidth = boundingWidth;
        this.boundingHeight = boundingHeight;
    }

    public int getGeneration()      { return generation; }
    public int getPopulation()      { return population; }
    public int getOldestAge()       { return oldestAge; }
    public double getAverageAge()   { return averageAge; }
    public int getBoundingWidth()   { return boundingWidth; }
    public int getBoundingHeight()  { return boundingHeight; }
}
