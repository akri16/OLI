package com.akribase.oauthloginimplementation.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.akribase.oauthloginimplementation.RestClient;
import com.akribase.oauthloginimplementation.apiservice.BackendService;
import com.akribase.oauthloginimplementation.model.AuthData;
import com.akribase.oauthloginimplementation.model.BackendResponse;
import com.akribase.oauthloginimplementation.util.BackendNetworkCall;
import com.akribase.oauthloginimplementation.util.Resource;

import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthRepository {
    private static AuthRepository instance;
    private Retrofit retrofit;
    private BackendService backendService;

    public static AuthRepository getInstance() {
        if(instance == null){
            instance = new AuthRepository();
        }
        return instance;
    }

    private AuthRepository() {
        retrofit = RestClient.getInstance().getAuthClient();
        backendService = retrofit.create(BackendService.class);
    }

    public LiveData<Resource<AuthData>> loginByGoogle(String idToken){
        String bearerToken = "Bearer " + idToken;
        MutableLiveData<Resource<AuthData>> resource = new MutableLiveData<>();
        backendService.getAccesssToken(bearerToken).enqueue(new BackendNetworkCall<>(resource));
        return resource;
    }

    public LiveData<Resource<AuthData>> addDiscord(String accessCode, String discordToken){
        String bearerAccessCode = "Bearer " + accessCode;
        String bearerDiscordToken = "Bearer " + discordToken;
        MutableLiveData<Resource<AuthData>> resource = new MutableLiveData<>();
        backendService.addDiscord(bearerAccessCode, bearerDiscordToken).enqueue(new BackendNetworkCall<>(resource));
        return resource;
    }

    public LiveData<Resource<Void>> logout(String accessCode){
        String bearerToken = "Bearer " + accessCode;
        MutableLiveData<Resource<Void>> resource = new MutableLiveData<>();
        backendService.logout(bearerToken).enqueue(new BackendNetworkCall<>(resource));
        return resource;
    }

}
