package com.example.dataakansalleliukkonenmikko;

public class WeatherData {

    private double temperature;
    private String description;
    private String iconCode;

    public WeatherData(double temperature, String description, String iconCode) {
        this.temperature = temperature;
        this.description = description;
        this.iconCode = iconCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public String getIconCode() {
        return iconCode;
    }
}