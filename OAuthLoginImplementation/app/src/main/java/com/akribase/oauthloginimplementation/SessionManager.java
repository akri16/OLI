package com.akribase.oauthloginimplementation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.akribase.oauthloginimplementation.model.AuthToken;
import com.akribase.oauthloginimplementation.model.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String TAG = "SessionManager";

    Gson gson = new Gson();
    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UserSession";
    private static final String AUTH_TOKEN = "AuthToken";
    private static final String USER = "User";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    Context context;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addUserDetails(User user){
        String json = gson.toJson(user);
        editor.putString(USER, json);
        editor.commit();
    }

    public void addToken(AuthToken token){
        String json = gson.toJson(token);
        editor.putString(AUTH_TOKEN, json);
        editor.commit();
    }

    public User getUserDetails(){
        String json = pref.getString(USER, "");
        return gson.fromJson(json, User.class);
    }

    public AuthToken getToken(){
        String json = pref.getString(AUTH_TOKEN, "");
        return gson.fromJson(json, AuthToken.class);
    }

    public void truncateSession(){
        editor.clear();
        editor.commit();
    }
}
