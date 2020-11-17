package com.akribase.oauthloginimplementation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.akribase.oauthloginimplementation.model.AuthData;
import com.akribase.oauthloginimplementation.repository.AuthRepository;
import com.akribase.oauthloginimplementation.util.Resource;
import com.akribase.oauthloginimplementation.util.SingleTimeObserver;

public class AuthViewmodel extends ViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<Resource<AuthData>> accessTokenLiveData;
    private MutableLiveData<Resource<AuthData>> addDiscordLiveData;
    private MutableLiveData<Resource<Void>> logoutLivedata;

    public void init(){
        if (accessTokenLiveData != null){
            return;
        }
        authRepository = AuthRepository.getInstance();
        accessTokenLiveData = new MutableLiveData<>();
        addDiscordLiveData = new MutableLiveData<>();
        logoutLivedata = new MutableLiveData<>();
    }

    public LiveData<Resource<AuthData>> getAccessTokenLiveData() {
        return accessTokenLiveData;
    }

    public LiveData<Resource<AuthData>> getAddDiscordLiveData() {
        return addDiscordLiveData;
    }

    public LiveData<Resource<Void>> getLogoutLivedata() {
        return logoutLivedata;
    }

    public void getAccessToken(String token) {
        LiveData<Resource<AuthData>> data;
        data = authRepository.loginByGoogle(token);
        data.observeForever(new SingleTimeObserver<>(accessTokenLiveData, data));
    }

    public void addDiscord(String authToken, String discordToken){
        LiveData<Resource<AuthData>> data;
        data = authRepository.addDiscord(authToken, discordToken);
        data.observeForever(new SingleTimeObserver<>(addDiscordLiveData, data));
    }

    public void logout(String authToken){
        LiveData<Resource<Void>> data;
        data = authRepository.logout(authToken);
        data.observeForever(new SingleTimeObserver<>(logoutLivedata, data));
    }
}
