package com.example.busservicedriverapp.modelclass;

public class BusCatagory {
    String bus_catagory;
    String bus_route;

    public BusCatagory(String bus_catagory, String bus_route) {
        this.bus_catagory = bus_catagory;
        this.bus_route = bus_route;
    }

    public String getBus_catagory() {
        return bus_catagory;
    }

    public void setBus_catagory(String bus_catagory) {
        this.bus_catagory = bus_catagory;
    }

    public String getBus_route() {
        return bus_route;
    }

    public void setBus_route(String bus_route) {
        this.bus_route = bus_route;
    }
}