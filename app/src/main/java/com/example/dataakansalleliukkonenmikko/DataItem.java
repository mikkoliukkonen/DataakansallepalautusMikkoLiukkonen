package com.example.dataakansalleliukkonenmikko;

public class DataItem {

    private String title;
    private String value;

    public DataItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}