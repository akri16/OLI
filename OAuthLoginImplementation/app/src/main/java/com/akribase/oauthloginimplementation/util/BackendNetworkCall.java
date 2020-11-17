package com.akribase.oauthloginimplementation.util;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.akribase.oauthloginimplementation.apiservice.BackendService;
import com.akribase.oauthloginimplementation.model.BackendResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackendNetworkCall<T> implements Callback<BackendResponse<T>> {
    private static final String TAG = "NetworkCall";

    MutableLiveData<Resource<T>> resource;
    @Override
    public void onResponse(Call<BackendResponse<T>> call, Response<BackendResponse<T>> response) {
        BackendResponse<T> backendResponse = response.body();

        if(response.isSuccessful()){
            String statusCode = backendResponse.getStatusCode();
            String msg = backendResponse.getMessage();
            if(!statusCode.equals(BackendService.SUCCESS_STATUS)){
                resource.setValue(Resource.error(
                        String.format("%s: %s", msg, statusCode), null));
                return;
            }
            resource.setValue(Resource.success(backendResponse.getData()));

        }else {
            String msg = backendResponse == null? response.message():backendResponse.getMessage();
            resource.setValue(Resource.error(msg, null));
            try {
                Log.e(TAG, response.message() + response.body() + response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<BackendResponse<T>> call, Throwable t) {
        resource.setValue(Resource.error(t.toString(), null));
        Log.e(TAG, "onFailure: ",t);
    }

    public BackendNetworkCall(MutableLiveData<Resource<T>> resource) {
        this.resource = resource;
    }

}
