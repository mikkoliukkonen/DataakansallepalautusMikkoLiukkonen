package com.example.dataakansalleliukkonenmikko;

public class MunicipalityData {

    private String name;
    private int population;
    private int populationChange;
    private double workplaceSelfSufficiency;
    private double employmentRate;
    private double temperature;
    private String weatherDescription;

    public MunicipalityData(String name,
                            int population,
                            int populationChange,
                            double workplaceSelfSufficiency,
                            double employmentRate,
                            double temperature,
                            String weatherDescription) {
        this.name = name;
        this.population = population;
        this.populationChange = populationChange;
        this.workplaceSelfSufficiency = workplaceSelfSufficiency;
        this.employmentRate = employmentRate;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
    }

    public String getName() {
        return name;
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

    public double getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }
}