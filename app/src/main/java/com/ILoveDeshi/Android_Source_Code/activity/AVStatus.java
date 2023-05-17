package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.AVStatusRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AVStatus extends AppCompatActivity {

    private Function function;
    private MaterialToolbar toolbar;
    private View view;
    private ImageView imageView;
    private MaterialButton button;
    private ImageView imageViewData;
    private MaterialButton buttonLogin;
    private ConstraintLayout conNoData, conMain, conAdminMsg;
    private MaterialTextView textViewData, textViewUserName, textViewStatusMsg, textViewStatus, textViewDate, textViewRequestDate, textViewResponseDate,
            textViewMsg, textViewAdminMsg, textViewNote;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avstatus);

        function = new Function(AVStatus.this);
        function.forceRTLIfSupported();
        
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.verification_status));
        function.setStatusBarGradiant(AVStatus.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conMain = findViewById(R.id.con_main_avs);
        conNoData = findViewById(R.id.con_not_login);
        imageViewData = findViewById(R.id.imageView_not_login);
        buttonLogin = findViewById(R.id.button_not_login);
        textViewData = findViewById(R.id.textView_not_login);
        imageView = findViewById(R.id.imageView_avs);
        button = findViewById(R.id.button_avs);
        view = findViewById(R.id.view_date_avs);
        textViewUserName = findViewById(R.id.textView_name_avs);
        textViewStatusMsg = findViewById(R.id.textView_statusMsg_avs);
        textViewStatus = findViewById(R.id.textView_avs);
        textViewDate = findViewById(R.id.textView_date_avs);
        textViewRequestDate = findViewById(R.id.textView_requestDate_avs);
        textViewResponseDate = findViewById(R.id.textView_responseDate_avs);
        textViewMsg = findViewById(R.id.textView_msg_avs);
        textViewAdminMsg = findViewById(R.id.textView_adminMsg_avs);
        textViewNote = findViewById(R.id.textView_note_avs);
        conAdminMsg = findViewById(R.id.con_adminMsg_avs);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        data(false, false);

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(AVStatus.this, Login.class));
            finishAffinity();
        });

        if (function.isNetworkAvailable()) {
            if (function.isLogin()) {
                detail(function.userId());
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
                textViewData.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewData.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    private void detail(String userId) {
        function.showProgressDialog(AVStatus.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AVStatus.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("AUM", "verfication_details");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AVStatusRP> call = apiService.getAVStatus(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AVStatusRP>() {
            @Override
            public void onResponse(@NotNull Call<AVStatusRP> call, @NotNull Response<AVStatusRP> response) {

                try {

                    AVStatusRP avStatusRP = response.body();
                    if (Objects.requireNonNull(avStatusRP).getStatus().equals("1")) {
                        if (avStatusRP.getSuccess().equals("1")) {
                            if (!avStatusRP.getDocument_img().equals("")) {
                                Glide.with(AVStatus.this).load(avStatusRP.getDocument_img())
                                        .placeholder(R.drawable.placeholder_portable)
                                        .into(imageView);

                                imageView.setOnClickListener(view -> startActivity(new Intent(AVStatus.this, ViewImage.class)
                                        .putExtra("path", avStatusRP.getDocument_img())));

                            }

                            if (avStatusRP.getAv_status().equals("1") || avStatusRP.getAv_status().equals("2")) {
                                if (avStatusRP.getAv_status().equals("1")) {
                                    textViewDate.setTextColor(getResources().getColor(R.color.green));
                                    textViewDate.setText(getResources().getString(R.string.approve_date));
                                } else {
                                    textViewDate.setTextColor(getResources().getColor(R.color.red));
                                    textViewDate.setText(getResources().getString(R.string.reject_date));
                                }
                                textViewResponseDate.setText(avStatusRP.getResponse_date());
                            } else {
                                view.setVisibility(View.GONE);
                                textViewResponseDate.setVisibility(View.GONE);
                                textViewDate.setVisibility(View.GONE);
                            }

                            textViewUserName.setText(avStatusRP.getUser_full_name());
                            textViewRequestDate.setText(avStatusRP.getRequest_date());
                            textViewMsg.setText(avStatusRP.getUser_messagesg());
                            textViewAdminMsg.setText(avStatusRP.getAdmin_message());

                            switch (avStatusRP.getAv_status()) {
                                case "0":
                                    button.setVisibility(View.VISIBLE);
                                    conAdminMsg.setVisibility(View.GONE);
                                    textViewNote.setVisibility(View.VISIBLE);
                                    textViewNote.setText(getResources().getString(R.string.new_request));
                                    textViewStatus.setText(getResources().getString(R.string.pending));
                                    textViewStatus.setTextColor(getResources().getColor(R.color.textView_app_color));
                                    textViewStatusMsg.setText(getResources().getString(R.string.account_pending));
                                    textViewStatusMsg.setTextColor(getResources().getColor(R.color.textView_app_color));
                                    break;
                                case "1":
                                    button.setVisibility(View.GONE);
                                    conAdminMsg.setVisibility(View.GONE);
                                    textViewNote.setVisibility(View.GONE);
                                    textViewStatus.setText(getResources().getString(R.string.approve));
                                    textViewDate.setTextColor(getResources().getColor(R.color.green));
                                    textViewStatusMsg.setText(getResources().getString(R.string.account_approve));
                                    textViewStatusMsg.setTextColor(getResources().getColor(R.color.green));
                                    break;
                                case "2":
                                    button.setVisibility(View.VISIBLE);
                                    conAdminMsg.setVisibility(View.VISIBLE);
                                    textViewNote.setVisibility(View.VISIBLE);
                                    textViewNote.setText(getResources().getString(R.string.reject_request));
                                    textViewStatus.setText(getResources().getString(R.string.reject));
                                    textViewDate.setTextColor(getResources().getColor(R.color.red));
                                    textViewStatusMsg.setText(getResources().getString(R.string.account_disapprove));
                                    textViewStatusMsg.setTextColor(getResources().getColor(R.color.red));
                                    break;
                            }

                            conMain.setVisibility(View.VISIBLE);

                            button.setOnClickListener(view -> {
                                startActivity(new Intent(AVStatus.this, AccountVerification.class)
                                        .putExtra("name", avStatusRP.getUser_full_name())
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            });

                        } else {
                            data(true, false);
                            function.alertBox(avStatusRP.getMsg());
                        }
                    } else if (avStatusRP.getStatus().equals("2")) {
                        function.suspend(avStatusRP.getMessage());
                    } else {
                        function.alertBox(avStatusRP.getMessage());
                    }

                } catch (
                        Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(AVStatus.this);
            }

            @Override
            public void onFailure(@NotNull Call<AVStatusRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                data(true, false);
                function.hideProgressDialog(AVStatus.this);
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
