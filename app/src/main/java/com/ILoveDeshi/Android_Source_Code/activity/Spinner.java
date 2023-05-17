package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.interfaces.VideoAd;
import com.ILoveDeshi.Android_Source_Code.response.SpinnerRP;
import com.ILoveDeshi.Android_Source_Code.response.SubmitSpinnerRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class Spinner extends AppCompatActivity {

    private Function function;
    private VideoAd videoAd;
    private MaterialToolbar toolbar;
    private List<LuckyItem> spinnerLists;
    private LuckyWheelView luckyWheelView;
    private ImageView imageViewData;
    private ConstraintLayout conNoData, conMain;
    private MaterialButton buttonSpinner, buttonLogin;
    private MaterialTextView textViewNotLogin, textViewMsg;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);

        videoAd = type -> {
            int indexPosition = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(indexPosition);
        };
        function = new Function(Spinner.this, videoAd);
        function.forceRTLIfSupported();

        spinnerLists = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.spinner));
        function.setStatusBarGradiant(Spinner.this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageViewData = findViewById(R.id.imageView_not_login);
        buttonLogin = findViewById(R.id.button_not_login);
        textViewNotLogin = findViewById(R.id.textView_not_login);
        conNoData = findViewById(R.id.con_not_login);
        luckyWheelView = findViewById(R.id.luckyWheel_spinner);
        buttonSpinner = findViewById(R.id.button_spinner);
        conMain = findViewById(R.id.con_main_spinner);
        textViewMsg = findViewById(R.id.textView_msg_spinner);

        conMain.setVisibility(View.GONE);
        data(false, false);

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(Spinner.this, Login.class));
            finishAffinity();
        });

        if (function.isNetworkAvailable()) {
            if (function.isLogin()) {
                SpinnerData(function.userId());
            } else {
                data(true, true);
            }
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewNotLogin.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewNotLogin.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    private void SpinnerData(String userId) {
        spinnerLists.clear();
        function.showProgressDialog(Spinner.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Spinner.this));
        jsObj.addProperty("AUM", "get_spinner");
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SpinnerRP> call = apiService.getSpinnerData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SpinnerRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SpinnerRP> call, @NotNull Response<SpinnerRP> response) {

                try {
                    SpinnerRP spinnerRP = response.body();
                    if (Objects.requireNonNull(spinnerRP).getStatus().equals("1")) {
                        String msgOne = getResources().getString(R.string.daily_total_spins);
                        String msgTwo = getResources().getString(R.string.remaining_spins_today);

                        textViewMsg.setText(msgOne + " " + spinnerRP.getDaily_spinner_limit() + " "
                                + msgTwo + " " + spinnerRP.getRemain_spin());

                        for (int i = 0; i < spinnerRP.getSpinnerLists().size(); i++) {

                            LuckyItem objItem = new LuckyItem();
                            objItem.text = spinnerRP.getSpinnerLists().get(i).getPoints();
                            objItem.icon = R.drawable.coins;
                            objItem.color = Color.parseColor(spinnerRP.getSpinnerLists().get(i).getBg_color());

                            spinnerLists.add(objItem);
                        }

                        if (spinnerLists.size() != 0) {
                            luckyWheelView.setData(spinnerLists);
                            luckyWheelView.setRound(2);

                            if (spinnerRP.getRemain_spin().equals("0")) {
                                buttonSpinner.setVisibility(View.GONE);
                            } else {
                                buttonSpinner.setVisibility(View.VISIBLE);
                            }

                            conMain.setVisibility(View.VISIBLE);

                            buttonSpinner.setOnClickListener(view -> {
                                function.VideoAdDialog("btn_click");
                            });

                            luckyWheelView.setLuckyRoundItemSelectedListener(index -> {
                                if (index != 0) {
                                    index = index - 1;
                                }
                                int pointAdd = Integer.parseInt(spinnerLists.get(index).text);
                                if (pointAdd != 0) {
                                    sendSpinnerData(userId, pointAdd);
                                }
                            });
                        } else {
                            data(true, false);

                        }

                    } else if (spinnerRP.getStatus().equals("2")) {
                        function.suspend(spinnerRP.getMessage());
                    } else {
                        function.alertBox(spinnerRP.getMessage());
                        conNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Spinner.this);
            }

            @Override
            public void onFailure(@NotNull Call<SpinnerRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(Spinner.this);
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void sendSpinnerData(String userId, int point) {
        function.showProgressDialog(Spinner.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Spinner.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("ponints", String.valueOf(point));
        jsObj.addProperty("AUM", "save_spinner_points");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubmitSpinnerRP> call = apiService.submitSpinnerData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubmitSpinnerRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SubmitSpinnerRP> call, @NotNull Response<SubmitSpinnerRP> response) {

                try {
                    SubmitSpinnerRP submitSpinnerRP = response.body();
                    if (Objects.requireNonNull(submitSpinnerRP).getStatus().equals("1")) {
                        String msgOne = getResources().getString(R.string.daily_total_spins);
                        String msgTwo = getResources().getString(R.string.remaining_spins_today);
                        textViewMsg.setText(msgOne + " " + submitSpinnerRP.getDaily_spinner_limit() + " "
                                + msgTwo + " " + submitSpinnerRP.getRemain_spin());

                        if (submitSpinnerRP.getSuccess().equals("1")) {
                            if (submitSpinnerRP.getRemain_spin().equals("0")) {
                                buttonSpinner.setVisibility(View.GONE);
                            } else {
                                buttonSpinner.setVisibility(View.VISIBLE);
                            }
                        } else {
                            buttonSpinner.setVisibility(View.GONE);
                        }

                        function.alertBox(submitSpinnerRP.getMsg());

                    } else if (submitSpinnerRP.getStatus().equals("2")) {
                        function.suspend(submitSpinnerRP.getMessage());
                    } else {
                        function.alertBox(submitSpinnerRP.getMessage());
                        conNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Spinner.this);
            }

            @Override
            public void onFailure(@NotNull Call<SubmitSpinnerRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(Spinner.this);
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(spinnerLists.size() - 1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
