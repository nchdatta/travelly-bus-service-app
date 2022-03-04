package com.example.busserviceapp.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public interface FragmentCommunication {

    public void getSelectedMarkerLocation(String mrkerKey, LatLng latLng);
}
