package com.example.busserviceapp.modelclass;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class MarkerTag implements Parcelable {
    String key;
    LatLng markerLocation;
    Location userLocation;

    public MarkerTag() {
    }

    public MarkerTag(String key, LatLng markerLocation, Location userLocation) {
        this.key = key;
        this.markerLocation = markerLocation;
        this.userLocation = userLocation;
    }

    protected MarkerTag(Parcel in) {
        key = in.readString();
        markerLocation = in.readParcelable(LatLng.class.getClassLoader());
        userLocation = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<MarkerTag> CREATOR = new Creator<MarkerTag>() {
        @Override
        public MarkerTag createFromParcel(Parcel in) {
            return new MarkerTag(in);
        }

        @Override
        public MarkerTag[] newArray(int size) {
            return new MarkerTag[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LatLng getMarkerLocation() {
        return markerLocation;
    }

    public void setMarkerLocation(LatLng markerLocation) {
        this.markerLocation = markerLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeParcelable(markerLocation, i);
        parcel.writeParcelable(userLocation, i);
    }
}
