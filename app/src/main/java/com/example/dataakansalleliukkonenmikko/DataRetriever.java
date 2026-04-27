package com.example.dataakansalleliukkonenmikko;

public class DataRetriever {

    private WeatherRetriever weatherRetriever;
    private StatisticsRetriever statisticsRetriever;

    public DataRetriever() {
        weatherRetriever = new WeatherRetriever();
        statisticsRetriever = new StatisticsRetriever();
    }

    public MunicipalityData getMunicipalityData(String municipalityName) throws Exception {
        WeatherData weatherData = weatherRetriever.getWeatherData(municipalityName);
        StatisticsData statisticsData = statisticsRetriever.getStatisticsData(municipalityName);

        return new MunicipalityData(
                municipalityName,
                statisticsData.getPopulation(),
                statisticsData.getPopulationChange(),
                statisticsData.getWorkplaceSelfSufficiency(),
                statisticsData.getEmploymentRate(),
                weatherData.getTemperature(),
                weatherData.getDescription(),
                weatherData.getIconCode()
        );
    }
}