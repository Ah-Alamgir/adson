package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.VideoAd;
import com.ILoveDeshi.Android_Source_Code.response.PlayAdRP;
import com.ILoveDeshi.Android_Source_Code.response.SubmitAdPlayRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayAd extends AppCompatActivity {

    private Function function;
    private VideoAd videoAd;
    private MaterialToolbar toolbar;
    private String pid, pName;
    private boolean isTaskCompleted = false, isLastAd = false;
    private ImageView topBack;

    private MaterialTextView packName, adClicked, tvAdRemain, tvAdRemainTxt, tvPlayAd, adClickSuccess;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ad);
        pid = getIntent().getExtras().getString("pid");
        pName = getIntent().getExtras().getString("type");

        videoAd = type -> {

        };
        function = new Function(PlayAd.this, videoAd);
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.adClick));
        function.setStatusBarGradiant(PlayAd.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        topBack = findViewById(R.id.topBack);
        packName = findViewById(R.id.packName);
        adClicked = findViewById(R.id.adClicked);
        tvAdRemain = findViewById(R.id.tvAdRemain);
        tvAdRemainTxt = findViewById(R.id.tvAdRemainTxt);
        tvPlayAd = findViewById(R.id.tvPlayAd);
        adClickSuccess = findViewById(R.id.adClickSuccess);
        tvPlayAd.setOnClickListener(view -> {
            if (isTaskCompleted) {
                function.alertBox(getString(R.string.task_completed));
            } else {
                showNewAd();
            }
        });
        if (function.isNetworkAvailable()) {
            if (function.isLogin()) {
                playAdData(function.userId(), pName, pid);
            } else {
                function.alertBox(getString(R.string.you_have_not_login));
            }
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void showNewAd() {
        function.showProgressDialog(PlayAd.this);

    }

    private void playAdData(String userId, String type, String pid) {
        function.showProgressDialog(PlayAd.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PlayAd.this));
        jsObj.addProperty("AUM", "getPlayAd");
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", type);
        jsObj.addProperty("pid", pid);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PlayAdRP> call = apiService.getPlayAdData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PlayAdRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<PlayAdRP> call, @NotNull Response<PlayAdRP> response) {
                try {
                    PlayAdRP playAdRP = response.body();
                    if (Objects.requireNonNull(playAdRP).getStatus().equals("1")) {
                        Glide.with(PlayAd.this).load(playAdRP.getImage())
                                .placeholder(R.drawable.placeholder_landscape).into(topBack);
                        packName.setText(playAdRP.getName());
                        adClicked.setText(getString(R.string.ad_clicked) + " " + playAdRP.getAd_clicked());
                        tvAdRemain.setText(playAdRP.getRemain_spin());
                        tvAdRemainTxt.setText(getString(R.string.ad_remain) + " Out of " + playAdRP.getDaily_ads_limit());
                        if (playAdRP.getRemain_spin().equals("0")) {
                            tvPlayAd.setText(getString(R.string.task_completed));
                            isTaskCompleted = true;
                        } else {
                            tvPlayAd.setVisibility(View.VISIBLE);
                        }
                        if (playAdRP.getRemain_spin().equals("1")) {
                            tvPlayAd.setText(getString(R.string.claim));
                            adClickSuccess.setVisibility(View.VISIBLE);
                            adClickSuccess.setText(getString(R.string.click_on_ad));
                            function.showToast("Next is last ad, click on the ad.");
                            isLastAd = true;
                        }
                    }
                    function.hideProgressDialog(PlayAd.this);
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.showToast(getResources().getString(R.string.failed_try_again));
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlayAdRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.showToast(getResources().getString(R.string.failed_try_again));
                function.hideProgressDialog(PlayAd.this);
            }
        });

    }

    private void sendPlayAdData(String userId, String type, String pid) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PlayAd.this));
        jsObj.addProperty("AUM", "sendPlayAd");
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", type);
        jsObj.addProperty("pid", pid);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitAdPlayRP> call = apiService.submitPlayAdData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitAdPlayRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitAdPlayRP> call, @NotNull Response<SubmitAdPlayRP> response) {
                try {
                    SubmitAdPlayRP submitAdPlayRP = response.body();
                    if (Objects.requireNonNull(submitAdPlayRP).getStatus().equals("1")) {
                        adClicked.setText(getString(R.string.ad_clicked) + " " + submitAdPlayRP.getAd_clicked());
                        tvAdRemain.setText(submitAdPlayRP.getRemain_spin());
                        tvAdRemainTxt.setText(getString(R.string.ad_remain) + " Out of " + submitAdPlayRP.getDaily_ads_limit());
                        if (submitAdPlayRP.getRemain_spin().equals("0")) {
                            tvPlayAd.setText(getString(R.string.task_completed));
                            isTaskCompleted = true;
                        } else {
                            tvPlayAd.setVisibility(View.VISIBLE);
                        }
                        if (submitAdPlayRP.getRemain_spin().equals("1")) {
                            tvPlayAd.setText(getString(R.string.claim));
                            adClickSuccess.setVisibility(View.VISIBLE);
                            adClickSuccess.setText(getString(R.string.click_on_ad));
                            function.showToast(getString(R.string.click_on_ad));
                            isLastAd = true;
                        }
                        adClickSuccess.setText(submitAdPlayRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.showToast(getResources().getString(R.string.failed_try_again));
                }
            }

            @Override
            public void onFailure(@NotNull Call<SubmitAdPlayRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.showToast(getResources().getString(R.string.failed_try_again));
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
