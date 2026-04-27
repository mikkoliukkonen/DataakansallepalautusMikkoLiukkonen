package com.example.dataakansalleliukkonenmikko;

public class WeatherData {

    private double temperature;
    private String description;

    public WeatherData(double temperature, String description) {
        this.temperature = temperature;
        this.description = description;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }
}