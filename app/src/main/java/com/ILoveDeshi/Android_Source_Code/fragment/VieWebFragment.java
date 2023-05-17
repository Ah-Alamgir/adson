package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.response.WebAppRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.util.NiceCountDownTimer;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VieWebFragment extends Fragment implements NiceCountDownTimer.OnCountDownListener {

    private Function function;
    private String id, type, title, points;
    private String appTime;
    private MaterialCardView btnViewVideo;
    private Boolean isWebView = false;
    public static boolean pointGive;
    NiceCountDownTimer niceCountDownTimer;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_vieweb, container, false);

        function = new Function(requireActivity());
        type = requireArguments().getString("type");
        id = requireArguments().getString("id");
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.webOpen));
        }
        btnViewVideo = view.findViewById(R.id.btnViewWeb);
        callData();
        return view;
    }

    private void callData() {
        if (getActivity() != null) {
            if (function.isNetworkAvailable()) {
                getWebsite(id);
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private void getWebsite(String Id) {
        function.showProgressDialog(requireActivity());
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(requireActivity()));
        jsObj.addProperty("AUM", "get_web");
        jsObj.addProperty("id", Id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<WebAppRP> call = apiService.getWeb(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<WebAppRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<WebAppRP> call, @NotNull Response<WebAppRP> response) {
                try {
                    WebAppRP spinnerRP = response.body();
                    if (Objects.requireNonNull(spinnerRP).getStatus().equals("1")) {
                        title = spinnerRP.getWebsite_title();
                        points = spinnerRP.getWebsite_coins();
                        appTime = spinnerRP.getWebsite_timer();

                        if (Constant.appRP.isLive_mode()) {
                            btnViewVideo.setOnClickListener(view -> {
                                Intent ytIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(spinnerRP.getWebsite_url()));
                                startActivity(ytIntent);
                                startTimer(Integer.parseInt(appTime) * 60);
                            });
                        } else {
                            function.showToast(getString(R.string.no_data_found));
                        }
                    } else if (spinnerRP.getStatus().equals("2")) {
                        function.suspend(spinnerRP.getMessage());
                    } else {
                        function.alertBox(spinnerRP.getMessage());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(requireActivity());
            }

            @Override
            public void onFailure(@NotNull Call<WebAppRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(requireActivity());
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void onBackPressed() {
        onDestroyView();
    }

    @Override
    public void onDestroyView() {
        niceCountDownTimer = null;
        super.onDestroyView();
        if (type.equals("all")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.allWebsites));
            }
        } else {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
            }
        }
    }

    private void startTimer(int time) {
        niceCountDownTimer = new NiceCountDownTimer(0, time, 0, this);
        niceCountDownTimer.setTimerPattern("s");
        niceCountDownTimer.start(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isWebView) {
            isWebView = false;
            function.showToast(getString(R.string.task_completed));
            function.secureTheWorld(function.userId(), title + " " + getString(R.string.webOpen), points, id);
        } else {
            if (!pointGive) {
                if (niceCountDownTimer != null) {
                    niceCountDownTimer.pause();
                    niceCountDownTimer = null;
                    function.showToast(getString(R.string.task_completed_not));
                }
            }
        }

        if (pointGive) {
            pointGive = false;
            function.showToast(getString(R.string.task_completed) + " " + getString(R.string.you_have) + points + getString(R.string.point));
        }
    }

    @Override
    public void onCountDownActive(String time) {
        function.showToast(time + " Seconds Remaining");
    }

    @Override
    public void onCountDownFinished() {
        isWebView = true;
        function.showToast(getString(R.string.task_completed));
    }
}
