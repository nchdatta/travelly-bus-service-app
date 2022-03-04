package com.example.busserviceapp.APIClient;

import com.example.busserviceapp.DistanceMatrixApiModel.DistanceResponse;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DistanceApiInterface {
    @GET("maps/api/distancematrix/json")
    Call<DistanceResponse> getDistanceInfo(@QueryMap Map<String, String> parameters);
}
