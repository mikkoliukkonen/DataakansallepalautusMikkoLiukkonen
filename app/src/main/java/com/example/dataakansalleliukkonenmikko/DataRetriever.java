package com.example.dataakansalleliukkonenmikko;

public class DataRetriever {
    private StatisticsRetriever statisticsRetriever;
    private WeatherRetriever weatherRetriever;

    public DataRetriever() {
        statisticsRetriever = new StatisticsRetriever();
        weatherRetriever = new WeatherRetriever();
    }

    public MunicipalityData getMunicipalityData(String municipalityName) {
        try {
            StatisticsData statisticsData = statisticsRetriever.getStatisticsData(municipalityName);
            WeatherData weatherData = weatherRetriever.getWeatherData(municipalityName);

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

        } catch (Exception e) {
            e.printStackTrace();

            return new MunicipalityData(
                    municipalityName,
                    0,
                    0,
                    0,
                    0,
                    0,
                    "Tietojen haku epäonnistui",
                    ""
            );
        }
    }
}