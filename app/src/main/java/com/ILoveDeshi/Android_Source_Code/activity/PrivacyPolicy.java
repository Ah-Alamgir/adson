package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.PrivacyPolicyRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PrivacyPolicy extends AppCompatActivity {

    private Function function;
    public MaterialToolbar toolbar;
    private WebView webView;
    private ConstraintLayout conNoData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        function = new Function(PrivacyPolicy.this);
        function.forceRTLIfSupported();
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.privacy_policy));
        function.setStatusBarGradiant(PrivacyPolicy.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conNoData = findViewById(R.id.con_noDataFound);
        webView = findViewById(R.id.webView_pp);
        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            privacyPolicy();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void privacyPolicy() {
        function.showProgressDialog(PrivacyPolicy.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PrivacyPolicy.this));
        jsObj.addProperty("AUM", "app_privacy_policy");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PrivacyPolicyRP> call = apiService.getPrivacyPolicy(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PrivacyPolicyRP>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(@NotNull Call<PrivacyPolicyRP> call, @NotNull Response<PrivacyPolicyRP> response) {

                try {
                    PrivacyPolicyRP privacyPolicyRP = response.body();
                    if (Objects.requireNonNull(privacyPolicyRP).getStatus().equals("1")) {
                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        webView.getSettings().setJavaScriptEnabled(true);
                        String mimeType = "text/html";
                        String encoding = "utf-8";

                        String text = "<html dir=" + function.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + function.webViewText() + "line-height:1.6}"
                                + "a {color:" + function.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + privacyPolicyRP.getApp_privacy_policy()
                                + "</body></html>";

                        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        function.alertBox(privacyPolicyRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(PrivacyPolicy.this);
            }

            @Override
            public void onFailure(@NotNull Call<PrivacyPolicyRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                conNoData.setVisibility(View.VISIBLE);
                function.hideProgressDialog(PrivacyPolicy.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
