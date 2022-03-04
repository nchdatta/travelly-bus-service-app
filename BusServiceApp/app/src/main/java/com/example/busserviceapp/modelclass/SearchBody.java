package com.example.busserviceapp.modelclass;

import com.google.android.gms.maps.model.LatLng;

public class SearchBody {
    String address,name;

    public SearchBody() {
    }

    public SearchBody(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
