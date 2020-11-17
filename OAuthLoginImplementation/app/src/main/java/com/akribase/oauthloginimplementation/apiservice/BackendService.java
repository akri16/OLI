package com.akribase.oauthloginimplementation.apiservice;

import com.akribase.oauthloginimplementation.model.AuthData;
import com.akribase.oauthloginimplementation.model.BackendResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BackendService {
    String BASE_URL = "https://us-central1-acminternal.cloudfunctions.net";
    String SUCCESS_STATUS = "10000";

    //Google Login
    @POST("/App/v1/access/login/google")
    Call<BackendResponse<AuthData>> getAccesssToken(@Header("Authorization") String token);

    //Add Discord
    @POST("/App/v1/access/login/discord")
    Call<BackendResponse<AuthData>> addDiscord(
            @Header("Authorization") String token,
            @Header("discord_token") String discordToken
    );

    //Logout
    @DELETE("/App/v1/access/logout")
    Call<BackendResponse<Void>> logout(@Header("Authorization") String token);
}
