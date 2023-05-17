package com.ILoveDeshi.Android_Source_Code.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.ILoveDeshi.Android_Source_Code.BuildConfig;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.LoginRP;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private Boolean isCancelled = false;
    private Function function;
    private String id = "", type = "", statusType = "", title = "";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        function = new Function(SplashScreen.this);
        function.login();
        function.forceRTLIfSupported();
        switch (function.getTheme()) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }

        setContentView(R.layout.activity_splace_screen);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        function.changeStatusBarColor();

        MaterialTextView buildVersion = findViewById(R.id.buildVersion);
        buildVersion.setText(getString(R.string.init) + BuildConfig.VERSION_NAME);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
            if (type.equals("single_status")) {
                statusType = getIntent().getStringExtra("status_type");
            }
            if (type.equals("category") || type.equals("single_status")) {
                title = getIntent().getStringExtra("title");
            }
        }

        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
        }

        splashScreen();

    }

    public void splashScreen() {

        if (function.isNetworkAvailable()) {
            int SPLASH_TIME_OUT = 3000;
            new Handler().postDelayed(() -> {
                if (!isCancelled) {
                    switch (type) {
                        case "payment_withdraw":
                            callActivity();
                            break;
                        case "account_verification":
                            startActivity(new Intent(SplashScreen.this, AVStatus.class));
                            finishAffinity();
                            break;
                        case "account_status":
                            startActivity(new Intent(SplashScreen.this, Suspend.class)
                                    .putExtra("id", id));
                            finishAffinity();
                            break;
                        default:
                            if (function.isLogin()) {
                                login();
                            } else {
                                if (function.pref.getBoolean(function.isVerification, false)) {
                                    startActivity(new Intent(SplashScreen.this, Verification.class));
                                    finishAffinity();
                                } else {
                                    if (function.pref.getBoolean(function.showLogin, true)) {
                                        function.editor.putBoolean(function.showLogin, false);
                                        function.editor.commit();
                                        Intent i = new Intent(SplashScreen.this, Login.class);
                                        startActivity(i);
                                        finishAffinity();
                                    } else {
                                        callActivity();
                                    }
                                }
                            }
                            break;
                    }
                }

            }, SPLASH_TIME_OUT);

        } else {
            callActivity();
        }

    }

    public void login() {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("AUM", "user_login");
        jsObj.addProperty("user_id", function.userId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLoginDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {
                    LoginRP loginRP = response.body();
                    if (Objects.requireNonNull(loginRP).getStatus().equals("1")) {
                        String loginType = function.getLoginType();
                        if (loginRP.getSuccess().equals("1")) {
                            OneSignal.sendTag("user_id", function.userId());
                            OSDeviceState device = OneSignal.getDeviceState();
                            OneSignal.sendTag("player_id", Objects.requireNonNull(device).getUserId());

                            if (loginType.equals("google")) {
                                if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                    callActivity();
                                } else {
                                    function.editor.putBoolean(function.prefLogin, false);
                                    function.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();
                                }
                            } else if (loginType.equals("facebook")) {

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                                if (isLoggedIn) {
                                    callActivity();
                                } else {

                                    LoginManager.getInstance().logOut();

                                    function.editor.putBoolean(function.prefLogin, false);
                                    function.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();

                                }

                            } else {
                                callActivity();
                            }
                        } else {
                            OneSignal.sendTag("user_id", function.userId());

                            if (loginType.equals("google")) {

                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(SplashScreen.this, task -> {

                                        });

                            } else if (loginType.equals("facebook")) {
                                LoginManager.getInstance().logOut();
                            }

                            function.editor.putBoolean(function.prefLogin, false);
                            function.editor.commit();
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finishAffinity();
                        }
                    } else {
                        function.alertBox(loginRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void callActivity() {
        openSplash();
    }

    private void openSplash() {
        int apiVersion = android.os.Build.VERSION.SDK_INT;
        if (apiVersion >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.GET_ACCOUNTS
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(SplashScreen.this, MainActivity.class)
                                            .putExtra("type", type)
                                            .putExtra("id", id)
                                            .putExtra("status_type", statusType)
                                            .putExtra("title", title));
                                    finishAffinity();
                                }, 1000);
                            } else {
                                Toast.makeText(SplashScreen.this, getString(R.string.allow_permission), Toast.LENGTH_SHORT).show();
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.GET_ACCOUNTS
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(SplashScreen.this, MainActivity.class)
                                            .putExtra("type", type)
                                            .putExtra("id", id)
                                            .putExtra("status_type", statusType)
                                            .putExtra("title", title));
                                    finishAffinity();
                                }, 1000);
                            } else {
                                Toast.makeText(SplashScreen.this, getString(R.string.allow_permission), Toast.LENGTH_SHORT).show();
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }
}


