package com.example.busserviceapp.modelclass;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Parcel implements Parcelable {
    LatLng start,destination,busLatlng;
    String key,busName;

    public Parcel() {
    }

    public Parcel(LatLng start, LatLng destination, LatLng busLatlng, String key, String busName) {
        this.start = start;
        this.destination = destination;
        this.busLatlng = busLatlng;
        this.key = key;
        this.busName = busName;
    }

    protected Parcel(android.os.Parcel in) {
        start = in.readParcelable(LatLng.class.getClassLoader());
        destination = in.readParcelable(LatLng.class.getClassLoader());
        busLatlng = in.readParcelable(LatLng.class.getClassLoader());
        key = in.readString();
        busName = in.readString();
    }

    public static final Creator<Parcel> CREATOR = new Creator<Parcel>() {
        @Override
        public Parcel createFromParcel(android.os.Parcel in) {
            return new Parcel(in);
        }

        @Override
        public Parcel[] newArray(int size) {
            return new Parcel[size];
        }
    };

    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public LatLng getBusLatlng() {
        return busLatlng;
    }

    public void setBusLatlng(LatLng busLatlng) {
        this.busLatlng = busLatlng;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int i) {
        parcel.writeParcelable(start, i);
        parcel.writeParcelable(destination, i);
        parcel.writeParcelable(busLatlng, i);
        parcel.writeString(key);
        parcel.writeString(busName);
    }
}
