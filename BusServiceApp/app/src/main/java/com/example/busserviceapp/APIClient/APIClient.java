package com.example.busserviceapp.APIClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    public static synchronized Retrofit instance() {

        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl("https://maps.googleapis.com/")
                            .addConverterFactory(GsonConverterFactory.create());
            retrofit = builder.client(httpClient).build();
        }
        return retrofit;
    }
}