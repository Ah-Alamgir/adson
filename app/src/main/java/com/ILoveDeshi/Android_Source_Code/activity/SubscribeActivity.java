package com.ILoveDeshi.Android_Source_Code.activity;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.interfaces.YouTubeActivityView;
import com.ILoveDeshi.Android_Source_Code.response.CheckOtpRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.util.YouTubePresenter;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.button.MaterialButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribeActivity extends YouTubeFailureRecoveryActivity implements YouTubePlayer.OnInitializedListener, EasyPermissions.PermissionCallbacks, YouTubeActivityView {

    private static final int RecoveryDialogRequest = 1;
    static final int RequestAccountPicker = 1000;
    static final int RequestAuthorization = 1001;
    static final int RequestGooglePlayServices = 1002;
    static final int RequestPermissionGetAccounts = 1003;
    private static final int RcSignIn = 12311;
    private static final String PrefAccountName = "accountName";
    private String id, userID, SUrl, SName, SCoins, yTLogo;
    private String isSubscribed;
    private GoogleAccountCredential mCredential;
    private YouTubePresenter presenter;
    private MaterialButton subscribeBtn;
    private int counter = 0;
    private Function method;
    private OnClick onClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_subscribe);
        final String emailId = getIntent().getExtras().getString(GoogleSignInActivity.userEmailId);
        id = getIntent().getExtras().getString(GoogleSignInActivity.id);
        SCoins = getIntent().getExtras().getString(GoogleSignInActivity.coin);
        SName = getIntent().getExtras().getString(GoogleSignInActivity.name);
        SUrl = getIntent().getExtras().getString(GoogleSignInActivity.url);
        yTLogo = getIntent().getExtras().getString(GoogleSignInActivity.ytLogo);
//
        method = new Function(SubscribeActivity.this);
        userID = method.pref.getString(method.profileId, null);
        // RewardData(userID, id); // get sid and uid
        subscribeBtn = findViewById(R.id.subscribe_now);
        TextView txtName = findViewById(R.id.txt_name);
        ImageView ytLogo = findViewById(R.id.ytLogo);
        TextView txtCoins = findViewById(R.id.txt_coins);
        presenter = new YouTubePresenter(this, this);

        txtName.setText(SName);
        txtCoins.setText(SCoins);
        Glide.with(SubscribeActivity.this).load(yTLogo)
                .placeholder(R.drawable.app_icon)
                .into(ytLogo);
        isSub();
        YouTubePlayerView supportFragment = findViewById(R.id.playerView);
        Objects.requireNonNull(supportFragment).initialize(Constant.appRP.getYoutube_api_key(), this);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Collections.singletonList(YouTubeScopes.YOUTUBE))
                .setBackOff(new ExponentialBackOff());

        findViewById(R.id.subscribe_now).setOnClickListener(v -> {
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PrefAccountName, emailId);
            editor.apply();
            getResultsFromApi();
        });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        if (!wasRestored) {
            youTubePlayer.cueVideo("ptG3luCO98k");
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            method.hideProgressDialog(SubscribeActivity.this);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RecoveryDialogRequest).show();
        } else {
            String errorMessage = String.format("Error: ", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
        method.hideProgressDialog(SubscribeActivity.this);
    }

    public YouTubePlayer.Provider getYouTubePlayerProvider() {
        return findViewById(R.id.playerView);
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            method.showProgressDialog(SubscribeActivity.this);
            presenter.subscribeToYouTubeChannel(mCredential, SUrl);
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            Dialog dialog = apiAvailability.getErrorDialog(
                    SubscribeActivity.this,
                    connectionStatusCode,
                    RequestGooglePlayServices);
            Objects.requireNonNull(dialog).show();
        }
    }

    private void chooseAccount() {

        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PrefAccountName, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(mCredential.newChooseAccountIntent(), RequestAccountPicker);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account for YouTube channel subscription.",
                    RequestPermissionGetAccounts, android.Manifest.permission.GET_ACCOUNTS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RecoveryDialogRequest:
                getYouTubePlayerProvider().initialize(Constant.appRP.getYoutube_api_key(), this);
                break;

            case RequestGooglePlayServices:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please " +
                            "install Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    getResultsFromApi();
                }
                break;

            case RequestAccountPicker:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PrefAccountName, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;

            case RequestAuthorization:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case RcSignIn:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (Objects.requireNonNull(result).isSuccess()) {
                    getResultsFromApi();
                } else {
                    Toast.makeText(this, "Permission Required if granted then check internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        getResultsFromApi();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "This app needs to access your Google account for YouTube channel subscription.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSubscribetionSuccess(String title) {
        method.hideProgressDialog(SubscribeActivity.this);
        method.secureTheSubs(method.userId(), SName + " Subscribe", SCoins, id);
    }

    @Override
    public void onSubscribetionFail() {
        method.hideProgressDialog(SubscribeActivity.this);
        if (counter < 3) {
            counter++;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                    .build();

            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RcSignIn);
        } else {
            Toast.makeText(SubscribeActivity.this, "Already Subscribed or Gmail id is blocked on YouTube", Toast.LENGTH_LONG).show();
        }
    }

    public void isSub() {
        method.showProgressDialog(SubscribeActivity.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SubscribeActivity.this));
        jsObj.addProperty("AUM", "isSub");
        jsObj.addProperty("sid", id);
        jsObj.addProperty("uid", method.userId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CheckOtpRP> call = apiService.getOtpStatus(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CheckOtpRP>() {
            @Override
            public void onResponse(@NotNull Call<CheckOtpRP> call, @NotNull Response<CheckOtpRP> response) {
                try {
                    CheckOtpRP checkOtpRP = response.body();
                    if (Objects.requireNonNull(checkOtpRP).getStatus().equals("1")) {
                        if (checkOtpRP.getStatus().equals("1")) {
                            isSubscribed = checkOtpRP.getOtp_status();
                            if (isSubscribed.equals("1")) {
                                subscribeBtn.setEnabled(false);
                                subscribeBtn.setText(getString(R.string.sub_done));
                            }
                        } else {
                            method.alertBox(checkOtpRP.getMessage());
                        }
                    } else {
                        method.alertBox(checkOtpRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
                method.hideProgressDialog(SubscribeActivity.this);
            }

            @Override
            public void onFailure(@NotNull Call<CheckOtpRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                method.hideProgressDialog(SubscribeActivity.this);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

}
