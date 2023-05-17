package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.interfaces.VideoAd;
import com.ILoveDeshi.Android_Source_Code.response.SingleAppRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAppFragment extends Fragment {

    public static Function function;
    private OnClick onClick;
    private VideoAd videoAd;
    private String type, appId, mode;
    private ConstraintLayout conNoData;
    private LayoutAnimationController animation;
    private Boolean isOver = false;
    private int paginationIndex = 1;
    private MaterialButton btnInstallApp, btnRun, btnCollect;
    private CircleImageView imageView;
    public static ConstraintLayout conRoot, conMainRoot;
    public static String appPackage, appName, installPoint, runPoint, appInstall, installedId;
    private MaterialTextView tvInstallCoin, tvRunCoin, tvAppName, tvAppDesc, tvTimer, tvInstalled;
    private Long appTime;
    private CountDownTimer countDownTimer;
    public static boolean isOpen;
    public static boolean isTouch;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_app_fragment, container, false);
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.detail_item));
        }
        type = requireArguments().getString("type");
        appId = requireArguments().getString("id");
        mode = requireArguments().getString("mode");

        if (mode.equals("latest_app_list")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.latestApps));
            }
        } else if (mode.equals("most_app_list")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.mostInstalledApps));
            }
        }

        videoAd = type -> {
            if (isOpen) {
                if (function.isGoodMan(requireActivity(), appPackage)) {
                    if (function.ImReallyFast(requireActivity(), appPackage)) {
                        function.appWorld(requireActivity(), appPackage);
                        btnCollect.setVisibility(View.VISIBLE);
                        countDownTimer = new CountDownTimer(appTime * 60 * 1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                Log.e("seconds remaining: ", String.valueOf(millisUntilFinished / 1000));
                            }

                            public void onFinish() {
                                if (isOpen) {
                                    isOpen = false;
                                    function.showToast("Congratulation!");
                                } else {
                                    if (isTouch) {
                                        conMainRoot.setVisibility(View.VISIBLE);
                                        function.showToast(getString(R.string.notRun));
                                    }
                                }
                            }
                        }.start();
                        btnCollect.setOnClickListener(v -> {
                            btnCollect.setVisibility(View.GONE);
                            if (isOpen) {
                                countDownTimer.cancel();
                                function.showToast(getString(R.string.notRun));
                                btnRun.setVisibility(View.VISIBLE);
                                isOpen = false;
                                isTouch = false;
                            } else {
                                countDownTimer.cancel();
                                isTouch = false;
                                isOpen = false;
                                function.secureAllWorld(function.userId(), appName + " " + getString(R.string.openApp), runPoint);
                            }
                        });
                    }
                }
            } else {
                if (Constant.appRP.isLive_mode()) {
                    if (function.isGoodMan(requireActivity(), appPackage)) {
                        if (function.ImReallyFast(requireActivity(), appPackage)) {
                            function.secureThisWorld(function.userId(), appName + " " + getString(R.string.app_install), appId, Integer.parseInt(installPoint));
                            int a = Integer.parseInt(tvInstalled.getText().toString());
                            tvInstalled.setText("" + a + 1);
                        } else {
                            function.alertBox(getResources().getString(R.string.install_from_store));
                        }
                    } else {
                        function.showToast(getResources().getString(R.string.app_not_found));
                        function.rateApp(appPackage);
                    }
                }
            }
        };
        function = new Function(requireActivity(), videoAd);
        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        conNoData = view.findViewById(R.id.con_noDataFound);
        btnInstallApp = view.findViewById(R.id.btnInstall);
        btnRun = view.findViewById(R.id.btnRun);
        btnCollect = view.findViewById(R.id.btnCollect);
        tvAppDesc = view.findViewById(R.id.tvAppDesc);
        tvAppName = view.findViewById(R.id.tvAppName);
        tvInstallCoin = view.findViewById(R.id.tvInstallPoint);
        tvRunCoin = view.findViewById(R.id.tvRunPoint);
        tvTimer = view.findViewById(R.id.tvTimer);
        imageView = view.findViewById(R.id.imageView_pro);
        conRoot = view.findViewById(R.id.conRoot);
        tvInstalled = view.findViewById(R.id.tvInstalled);
        conMainRoot = view.findViewById(R.id.conMainRoot);

        conNoData.setVisibility(View.GONE);
        btnRun.setVisibility(View.GONE);
        btnCollect.setVisibility(View.GONE);
        btnInstallApp.setVisibility(View.GONE);
        callData();

        setHasOptionsMenu(true);
        return view;
    }

    private void callData() {
        if (function.isNetworkAvailable()) {
            getHomeApps(appId);
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_searchView) {
            SearchFragment searchFragment = new SearchFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, searchFragment, getString(R.string.searc)).commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHomeApps(String Id) {
        function.showProgressDialog(requireActivity());
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(requireActivity()));
        jsObj.addProperty("AUM", "get_single_app");
        jsObj.addProperty("id", Id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SingleAppRP> call = apiService.getSingleApp(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SingleAppRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SingleAppRP> call, @NotNull Response<SingleAppRP> response) {
                try {
                    SingleAppRP spinnerRP = response.body();
                    if (Objects.requireNonNull(spinnerRP).getStatus().equals("1")) {
                        btnInstallApp.setOnClickListener(v -> {
                            isOpen = false;
                            btnInstallApp.setVisibility(View.GONE);
                            function.VideoAdDialog("btn_click");
                            installPoint = spinnerRP.getApp_coins();
                        });
                        btnRun.setOnClickListener(v -> {
                            isOpen = true;
                            isTouch = true;
                            btnRun.setVisibility(View.GONE);
                            appTime = Long.valueOf(spinnerRP.getApp_run_time());
                            function.VideoAdDialog("btn_click");
                        });
                        appPackage = spinnerRP.getApp_url();
                        appName = spinnerRP.getApp_name();
                        runPoint = spinnerRP.getApp_run_coins();
                        appInstall = spinnerRP.getApp_install();
                        installedId = spinnerRP.getApp_id();
                        tvInstalled.setText(appInstall);
                        if (installedId != null && installedId.equals(appId)) {
                            btnInstallApp.setVisibility(View.GONE);
                            btnRun.setVisibility(View.VISIBLE);
                            btnRun.setText(getString(R.string.openAppFor)
                                    + spinnerRP.getApp_run_time()
                                    + getString(R.string.minute)
                                    + getString(R.string.toGetPoint)
                                    + spinnerRP.getApp_run_coins()
                                    + getString(R.string.reward_point));
                        } else {
                            btnInstallApp.setVisibility(View.VISIBLE);
                        }
                        tvAppDesc.setText(spinnerRP.getApp_desc());
                        tvAppName.setText(spinnerRP.getApp_name());
                        tvInstalled.setText(getString(R.string.totalInstall) + appInstall);
                        tvInstallCoin.setText(getString(R.string.appPoint) + spinnerRP.getApp_coins());
                        tvRunCoin.setText(getString(R.string.appCoin) + spinnerRP.getApp_run_coins());
                        tvTimer.setText(getString(R.string.appTime) + spinnerRP.getApp_run_time() + getString(R.string.minute));
                        if (spinnerRP.getApp_image().endsWith(".jpg") || spinnerRP.getApp_image().endsWith(".png")) {
                            Glide.with(requireActivity()).load(spinnerRP.getApp_image())
                                    .placeholder(R.drawable.placeholder_portable).into(imageView);
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
                function.hideProgressDialog(requireActivity());
            }

            @Override
            public void onFailure(@NotNull Call<SingleAppRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(requireActivity());
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (type.equals("install")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
            }
        } else {
            if (mode.equals("latest_app_list")) {
                if (MainActivity.toolbar != null) {
                    MainActivity.toolbar.setTitle(getResources().getString(R.string.latestApps));
                }
            } else if (mode.equals("most_app_list")) {
                if (MainActivity.toolbar != null) {
                    MainActivity.toolbar.setTitle(getResources().getString(R.string.mostInstalledApps));
                }
            }
        }
    }
}
