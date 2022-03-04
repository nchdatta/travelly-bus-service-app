package com.example.busservicedriverapp.modelclass;

public class ReportModel {
    String date;
    String detailse;
    String name;

    public ReportModel() {
    }

    public ReportModel(String date, String detailse, String name) {
        this.date = date;
        this.detailse = detailse;
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetailse() {
        return detailse;
    }

    public void setDetailse(String detailse) {
        this.detailse = detailse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
