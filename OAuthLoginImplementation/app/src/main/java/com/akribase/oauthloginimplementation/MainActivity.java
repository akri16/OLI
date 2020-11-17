package com.akribase.oauthloginimplementation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import com.akribase.oauthloginimplementation.databinding.ActivityMainBinding;
import com.akribase.oauthloginimplementation.model.AuthData;
import com.akribase.oauthloginimplementation.model.AuthToken;
import com.akribase.oauthloginimplementation.model.User;
import com.akribase.oauthloginimplementation.util.Resource;
import com.akribase.oauthloginimplementation.util.Status;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.browser.BrowserWhitelist;
import net.openid.appauth.browser.VersionedBrowserMatcher;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String STATE = UUID.randomUUID().toString();

    private static final int RC_GOOGLE = 100;
    private static final int RC_DISCORD = 101 ;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityMainBinding binding;
    private AuthViewmodel authViewmodel;
    private SessionManager sessionManager;
    private AuthorizationServiceConfiguration mServiceConfiguration;
    private AuthorizationService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        authViewmodel = new ViewModelProvider(this).get(AuthViewmodel.class);
        authViewmodel.init();
        initObservers();

        //AppAuth initialization
        mServiceConfiguration =
                new AuthorizationServiceConfiguration(
                        Uri.parse("https://discord.com/api/oauth2/authorize"), // Authorization endpoint
                        Uri.parse("https://discord.com/api/oauth2/token")); // Token endpoint

        AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder()

                .build();
        authService = new AuthorizationService(this, appAuthConfig);

        //SignIn Client Setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.logs.setMovementMethod(new ScrollingMovementMethod());
        binding.signInBtn.setOnClickListener(view -> {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account == null){
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE);
                log("Initiating Client Sign In with Google Provider");
            }else{
                log("Client Already Signed In");
            }
        });

        binding.signoutBtn.setOnClickListener(view -> {
            signout();
        });

        binding.addDiscord.setOnClickListener(view -> {
            if (checkAccountAvailabilityFor("adding discord")) {
                getDiscordCode();
            }
        });
    }

    private boolean checkAccountAvailabilityFor(String action) {
        if(sessionManager.getToken() == null){
            log(String.format("No session available for %s. Please login.", action));
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(sessionManager.getToken() == null && GoogleSignIn.getLastSignedInAccount(this) != null){
            mGoogleSignInClient.signOut();
        }
    }

    @SuppressLint("DefaultLocale")
    public void initObservers(){
        authViewmodel.getAccessTokenLiveData().observe(this, authDataResource -> {
            if(authDataResource.status == Status.SUCCESS){
                AuthData data = authDataResource.data;
                User user = data.getUser();
                String accessToken = data.getToken().getAccessToken();
                log(String.format("Successfully Obtained the Access Code: %s [%d]",
                        accessToken.substring(0, 10),
                        accessToken.length()));
                Log.d(TAG, "initObservers: " + accessToken);
                log("Obtained User details: " + user.getEmail());
                sessionManager.addToken(data.getToken());
            }else{
                log("Unable to Obtain the access token: " + authDataResource.getMessage());
            }
        });

        authViewmodel.getAddDiscordLiveData().observe(this, authDataResource -> {
            if(authDataResource.status == Status.SUCCESS){
                log("Successfully Added Discord");
            }else{
                log("Unable to add Discord: " + authDataResource.getMessage());
            }
        });

        authViewmodel.getLogoutLivedata().observe(this, resource -> {
            if(resource.status == Status.SUCCESS){
                log("Successfully logged out of the backend");
            }else{
                log("Unable to log out from the backend: " + resource.getMessage());
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account == null){
                log("Sign In with Google failed");
                return;
            }
            // Signed in successfully, show authenticated UI.
            log("Sign In with Google Successful");
            String idToken = account.getIdToken();
            log(String.format("Obtained AuthCode from the provider, %s [%d]",
                    idToken.substring(0, 10) , idToken.length()));

            Log.d(TAG, "handleSignInResult: " + idToken);

            authViewmodel.getAccessToken(idToken);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.e(TAG, "handleSignInResult: " + e.getStatusCode(), e);
            log("Sign In with Google failed");
        }
    }

    private void getDiscordCode(){
        String discordClientId = getString(R.string.discord_client_id);
        log("Initiating Discord Login Flow....");

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                mServiceConfiguration,
                discordClientId,
                ResponseTypeValues.CODE,
                Uri.parse("http://localhost"));

        AuthorizationRequest authorizationRequest = builder
                .setPrompt("consent")
                .setScopes("identify", "email")
                .build();


        Intent intent = authService.getAuthorizationRequestIntent(authorizationRequest);
        startActivityForResult(intent, RC_DISCORD);
    }

    private void addDiscordToBackend(String discordToken){
        Log.d(TAG, "addDiscordToBackend: "+ discordToken);
        AuthToken authToken = sessionManager.getToken();
        Log.d(TAG, "addDiscordToBackend: "+ authToken.getAccessToken());
        if(authToken == null || authToken.getAccessToken() == null){
            log("No Access token available for this request Obtain an AccessToken first");
            return;
        }

        authViewmodel.addDiscord(authToken.getAccessToken(), discordToken);
    }

    private void log(String msg){
        String exMsg = binding.logs.getText().toString();
        msg = String.join("\n", exMsg, msg, "");
        binding.logs.setText(msg);
    }

    private void signout(){
        if(checkAccountAvailabilityFor("SignOut")) {
            authViewmodel.logout(sessionManager.getToken().getAccessToken());
            sessionManager.truncateSession();
            mGoogleSignInClient.signOut();
            log("Signed out from the provider successfully");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        //Result returned from Discord SignIn
        else if(requestCode == RC_DISCORD) {
            AuthorizationResponse authResponse = AuthorizationResponse.fromIntent(data);
            AuthorizationException authException = AuthorizationException.fromIntent(data);
            if(authException != null){
                log("Error SigningIn using discord: " + authException.errorDescription);
                return;
            }

            String authCode = authResponse.authorizationCode;
            if(authCode == null){
                log("Error SigningIn using discord: Bad AuthCode returned");
                return;
            }

            log(String.format("Successfully Obtained a Discord AuthCode: %s [%d]",
                    authCode.substring(0, 10),
                    authCode.length()));

            authService.performTokenRequest(
                    authResponse.createTokenExchangeRequest(),
                    (resp1, ex) -> {
                        if (resp1 != null) {
                            String accessCode = resp1.accessToken;
                            log(String.format("Successfully Obtained a Discord AccessToken: %s [%d]",
                                    accessCode.substring(0, 10),
                                    accessCode.length()));
                            addDiscordToBackend(accessCode);
                        } else {
                            log("Unable to obtain a Discord Access Token");
                        }
                    });
        }
    }
}