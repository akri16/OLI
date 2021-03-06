package com.akribase.oauthloginimplementation.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user")
    private String id;

    @SerializedName("full_name")
    private String name;

    @SerializedName("verified")
    private String verified;

    @SerializedName("email")
    private String email;

    @SerializedName("accounts_connected")
    private Accounts accounts;

    public User(String id, String name, String verified, String email, Accounts accounts) {
        this.id = id;
        this.name = name;
        this.verified = verified;
        this.email = email;
        this.accounts = accounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", verified='" + verified + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
