package com.akribase.oauthloginimplementation.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

public class AuthToken {
    private static final String TAG = "AuthToken";
    private final double TOKEN_EXPIRY = 1.037e+7;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    private Date expiryDate;

    public String getAccessToken() {
        return accessToken;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    private void calculateExpiry(){
        Calendar calendar = Calendar.getInstance();
        double expiry = calendar.getTimeInMillis() + TOKEN_EXPIRY * 1000D;
        expiryDate = new Date((long) expiry);
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "TOKEN_EXPIRY=" + TOKEN_EXPIRY +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
