package com.akribase.oauthloginimplementation;

import com.akribase.oauthloginimplementation.apiservice.BackendService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private static RestClient instance;
    private Retrofit authClient;

    public static RestClient getInstance() {
        if(instance== null){
            instance = new RestClient();
        }
        return instance;
    }

    private RestClient() {
        authClient = new Retrofit.Builder()
                .baseUrl(BackendService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getAuthClient() {
        return authClient;
    }
}
