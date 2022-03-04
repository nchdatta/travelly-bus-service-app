package com.example.busserviceapp.map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestUtil2 {
    private static RestUtil2 self;
    private String API_BASE_URL = "https://maps.googleapis.com/";
    private Retrofit retrofit;

    private RestUtil2() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(JacksonConverterFactory.create());
        retrofit = builder.client(httpClient).build();
    }

    public static RestUtil2 getInstance() {
        if (self == null) {
            synchronized(RestUtil2.class) {
                if (self == null) {
                    self = new RestUtil2();
                }
            }
        }
        return self;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
