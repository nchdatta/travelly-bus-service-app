package com.example.busserviceapp.APIClient;

import com.example.busserviceapp.DirectionApiModel.DirectionResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DirectionApiInterface {
    @GET("maps/api/directions/json")
    Call<DirectionResponse> getDirectionInfo(@QueryMap Map<String, String> parameters);
}
