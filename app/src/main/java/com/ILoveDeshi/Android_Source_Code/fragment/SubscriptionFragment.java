package com.ILoveDeshi.Android_Source_Code.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.adapter.PackageAdapter;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.response.PackageRP;
import com.ILoveDeshi.Android_Source_Code.response.PackageTransRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Constant;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionFragment extends Fragment {

    public static Function function;
    private OnClick onClick;
    public PackageRP packageRP;
    private PackageAdapter packageAdapter;
    private ViewPager viewPager;
    private Timer timer;
    public static Activity activity;
    private ConstraintLayout conSlider, conNoData;
    private Runnable Update;
    private FloatingActionButton fabInfo;
    private final long DELAY_MS = 700;
    private final long PERIOD_MS = 10000;
    private final Handler handler = new Handler();

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.subscription_fragment, container, false);
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.adClick));
        }
        activity = requireActivity();
        conNoData = view.findViewById(R.id.con_noDataFound);
        viewPager = view.findViewById(R.id.slider_package);
        conSlider = view.findViewById(R.id.conSlider);
        onClick = (position, title, type, status_type, id, tag) -> {
            PackageTaskFragment taskFragment = new PackageTaskFragment();
            Bundle bundle = new Bundle();
            bundle.putString("packId", title);
            taskFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, taskFragment, getResources().getString(R.string.task_list)).addToBackStack(getResources().getString(R.string.task_list)).commitAllowingStateLoss();
        };

        function = new Function(requireActivity(), onClick);
        int columnWidth = function.getScreenWidth();
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setPageMargin(dpToPx(5));
        viewPager.setClipToPadding(false);
        viewPager.setPadding(70, 0, 70, 0);
        callData();
        conNoData.setVisibility(View.GONE);
        fabInfo = view.findViewById(R.id.fabInfo);
        fabInfo.setOnClickListener(view1 -> {
            function.alertBox(Constant.appRP.getAds_subscriptio_payment() + "");
        });
        return view;
    }

    private void callData() {
        if (function.isNetworkAvailable()) {
            function.showProgressDialog(getActivity());
            getSubscribePackage();
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


    @Override
    public void onDestroyView() {
        MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        super.onDestroyView();
    }

    @SuppressLint("SetTextI18n")
    public static void sendReviewInfo(String img, String packNme, String pid, String uid) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_send_package);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        ImageView appImage = dialog.findViewById(R.id.app_image);
        MaterialTextView appName = dialog.findViewById(R.id.app_name);
        TextInputEditText etDetails = dialog.findViewById(R.id.etDetails);
        MaterialButton buttonUpload = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        Glide.with(activity).load(img)
                .placeholder(R.drawable.placeholder_landscape).into(appImage);
        appName.setText(packNme + " " + activity.getString(R.string.sub_for));

        buttonUpload.setOnClickListener(v -> {
            if (!Objects.requireNonNull(etDetails.getText()).toString().isEmpty()) {
                sendTransaction(pid, uid, etDetails.getText().toString());
                dialog.dismiss();
            } else {
                function.alertBox(activity.getString(R.string.please_enter_trax));
            }
        });
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void getSubscribePackage() {
        if (getActivity() != null) {
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("AUM", "getSubscribePackage");
            jsObj.addProperty("uid", function.userId());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<PackageRP> call = apiService.getPackageList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<PackageRP>() {
                @Override
                public void onResponse(@NotNull Call<PackageRP> call, @NotNull Response<PackageRP> response) {
                    if (getActivity() != null) {
                        try {
                            packageRP = response.body();
                            if (Objects.requireNonNull(packageRP).getStatus().equals("1")) {
                                if (packageRP.getPackageLists().size() != 0) {
                                    packageAdapter = new PackageAdapter(getActivity(), "slider", packageRP.getPackageLists(), onClick);
                                    viewPager.setAdapter(packageAdapter);
                                    viewPager.setOffscreenPageLimit(packageRP.getPackageLists().size() - 1);
                                    Update = () -> {
                                        if (viewPager.getCurrentItem() == (packageAdapter.getCount() - 1)) {
                                            viewPager.setCurrentItem(0, true);
                                        } else {
                                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                        }
                                    };
                                    if (packageAdapter.getCount() > 1) {
                                        timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                handler.post(Update);
                                            }
                                        }, DELAY_MS, PERIOD_MS);
                                    }

                                } else {
                                    if (packageAdapter == null) {
                                        conSlider.setVisibility(View.GONE);
                                        conNoData.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else if (packageRP.getStatus().equals("2")) {
                                function.suspend(packageRP.getMessage());
                            } else {
                                function.alertBox(packageRP.getMessage());
                                conNoData.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    function.hideProgressDialog(getActivity());
                }

                @Override
                public void onFailure(@NotNull Call<PackageRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = requireActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void sendTransaction(String pid, String uid, String detail) {
        if (activity != null) {
            function.showProgressDialog(activity);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
            jsObj.addProperty("AUM", "sendTransaction");
            jsObj.addProperty("user_id", uid);
            jsObj.addProperty("pid", pid);
            jsObj.addProperty("details", detail);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<PackageTransRP> call = apiService.getPackageTrans(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<PackageTransRP>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(@NotNull Call<PackageTransRP> call, @NotNull Response<PackageTransRP> response) {
                    if (activity != null) {
                        try {
                            PackageTransRP transRP = response.body();
                            if (Objects.requireNonNull(transRP).getStatus().equals("1")) {
                                if (transRP.getSuccess().equals("1")) {
                                    function.alertBox(transRP.getMsg());
                                }
                            } else {
                                function.alertBox(activity.getString(R.string.failed_try_again));
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(activity.getResources().getString(R.string.failed_try_again));
                        }

                    }
                    function.hideProgressDialog(activity);
                }

                @Override
                public void onFailure(@NotNull Call<PackageTransRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    function.hideProgressDialog(activity);
                    function.alertBox(activity.getString(R.string.failed_try_again));
                }
            });
        }
    }

}
