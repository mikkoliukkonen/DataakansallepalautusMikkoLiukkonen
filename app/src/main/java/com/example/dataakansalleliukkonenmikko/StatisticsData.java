package com.example.dataakansalleliukkonenmikko;

public class StatisticsData {

    private int population;
    private int populationChange;
    private double workplaceSelfSufficiency;
    private double employmentRate;

    public StatisticsData(int population,
                          int populationChange,
                          double workplaceSelfSufficiency,
                          double employmentRate) {
        this.population = population;
        this.populationChange = populationChange;
        this.workplaceSelfSufficiency = workplaceSelfSufficiency;
        this.employmentRate = employmentRate;
    }

    public int getPopulation() {
        return population;
    }

    public int getPopulationChange() {
        return populationChange;
    }

    public double getWorkplaceSelfSufficiency() {
        return workplaceSelfSufficiency;
    }

    public double getEmploymentRate() {
        return employmentRate;
    }
}