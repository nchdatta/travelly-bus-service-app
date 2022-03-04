package com.example.busserviceapp.modelclass;

public class BusList {
    String busname,busroute,reachingtime,key;

    public BusList() {

    }

    public BusList(String busname, String busroute, String reachingtime, String key) {
        this.busname = busname;
        this.busroute = busroute;
        this.reachingtime = reachingtime;
        this.key = key;
    }

    public String getBusname() {
        return busname;
    }

    public void setBusname(String busname) {
        this.busname = busname;
    }

    public String getBusroute() {
        return busroute;
    }

    public void setBusroute(String busroute) {
        this.busroute = busroute;
    }

    public String getReachingtime() {
        return reachingtime;
    }

    public void setReachingtime(String reachingtime) {
        this.reachingtime = reachingtime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
