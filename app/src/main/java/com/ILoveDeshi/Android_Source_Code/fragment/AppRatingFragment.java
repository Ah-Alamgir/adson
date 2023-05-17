package com.ILoveDeshi.Android_Source_Code.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.interfaces.OnClick;
import com.ILoveDeshi.Android_Source_Code.interfaces.VideoAd;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.SingleRatingAppRP;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.nguyenhoanglam.imagepicker.model.Config;
import org.nguyenhoanglam.imagepicker.model.Image;
import org.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRatingFragment extends Fragment {

    public static Function function;
    private OnClick onClick;
    private VideoAd videoAd;
    private Uri uri_banner;
    private ImageView appImageUpload;
    private String type, appId, mode, documentImage = "";
    private int REQUEST_GALLERY_PICKER = 100;
    private MaterialButton btnRatingApp, btnSendInfo;
    private CircleImageView imageView;
    private SingleRatingAppRP spinnerRP;
    private ArrayList<Image> galleryImages;
    public static ConstraintLayout conRoot, conMainRoot;
    public static String appPackage, appName, ratingPoints, ratingId;
    private MaterialTextView tvInstallCoin, tvTotalRate, tvAppName, ratingStatus, tvRatingDesc;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.rating_app_fragment, container, false);
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.detail_item));
        }
        type = requireArguments().getString("type");
        appId = requireArguments().getString("id");
        mode = requireArguments().getString("mode");
        galleryImages = new ArrayList<>();
        if (mode.equals("home_app_rating")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.detail_item));
            }
        } else if (mode.equals("all_app_rating")) {
            if (MainActivity.toolbar != null) {
                MainActivity.toolbar.setTitle(getResources().getString(R.string.detail_item));
            }
        }

        videoAd = type -> {

        };
        function = new Function(requireActivity(), videoAd);
        btnRatingApp = view.findViewById(R.id.btnInstall);
        btnSendInfo = view.findViewById(R.id.btnSendInfo);
        tvAppName = view.findViewById(R.id.tvAppName);
        tvInstallCoin = view.findViewById(R.id.tvInstallPoint);
        tvTotalRate = view.findViewById(R.id.tvRunPoint);
        ratingStatus = view.findViewById(R.id.tvTimer);
        imageView = view.findViewById(R.id.imageView_pro);
        conRoot = view.findViewById(R.id.conRoot);
        tvRatingDesc = view.findViewById(R.id.tvInstalled);
        conMainRoot = view.findViewById(R.id.conMainRoot);

        btnRatingApp.setVisibility(View.GONE);
        btnSendInfo.setVisibility(View.GONE);
        btnRatingApp.setText(requireActivity().getString(R.string.review_app));
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
        jsObj.addProperty("AUM", "get_single_rating_app");
        jsObj.addProperty("id", Id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SingleRatingAppRP> call = apiService.getSingleRatingApp(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SingleRatingAppRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<SingleRatingAppRP> call, @NotNull Response<SingleRatingAppRP> response) {
                try {
                    spinnerRP = response.body();
                    if (Objects.requireNonNull(spinnerRP).getStatus().equals("1")) {
                        btnRatingApp.setOnClickListener(v -> {
                            function.VideoAdDialog("btn_click");
                            ratingPoints = spinnerRP.getApp_coins();
                        });
                        btnSendInfo.setOnClickListener(v -> sendReviewInfo());

                        appPackage = spinnerRP.getApp_url();
                        appName = spinnerRP.getApp_name();
                        ratingId = spinnerRP.getApp_id();
                        if (ratingId != null && ratingId.equals(appId)) {
                            btnRatingApp.setVisibility(View.GONE);
                        } else {
                            btnRatingApp.setVisibility(View.VISIBLE);
                            btnSendInfo.setVisibility(View.VISIBLE);
                        }
                        tvAppName.setText(spinnerRP.getApp_name());
                        String app_status = spinnerRP.getApp_status();
                        if (app_status == null) {
                            tvRatingDesc.setText(spinnerRP.getApp_desc());
                        } else {
                            tvRatingDesc.setText(spinnerRP.getApp_message());
                        }
                        if (app_status == null) {
                            ratingStatus.setText(getString(R.string.status_rate) + getString(R.string.pending));
                        } else if (app_status.equals("0")) {
                            ratingStatus.setText(getString(R.string.status_rate) + getString(R.string.pending));
                        } else if (app_status.equals("1")) {
                            ratingStatus.setText(getString(R.string.status_rate) + getString(R.string.success));
                        } else if (app_status.equals("2")) {
                            ratingStatus.setText(getString(R.string.status_rate) + getString(R.string.rejected));
                        }
                        tvInstallCoin.setText(getString(R.string.ratePoint) + spinnerRP.getApp_coins());
                        tvTotalRate.setText(getString(R.string.total_rate) + spinnerRP.getApp_rating());
                        if (spinnerRP.getApp_image().endsWith(".jpg") || spinnerRP.getApp_image().endsWith(".png")) {
                            Glide.with(requireActivity()).load(spinnerRP.getApp_image())
                                    .placeholder(R.drawable.placeholder_portable).into(imageView);
                        }
                        btnRatingApp.setOnClickListener(view -> function.rateApp(spinnerRP.getApp_url()));
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
            public void onFailure(@NotNull Call<SingleRatingAppRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(requireActivity());
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
            if (mode.equals("home_app_rating")) {
                if (MainActivity.toolbar != null) {
                    MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
                }
            } else if (mode.equals("all_app_rating")) {
                if (MainActivity.toolbar != null) {
                    MainActivity.toolbar.setTitle(getResources().getString(R.string.review_app));
                }
            }
        }
    }

    private void sendReviewInfo() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_review_app);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        ImageView appImage = dialog.findViewById(R.id.app_image);
        appImageUpload = dialog.findViewById(R.id.imageView_av);
        MaterialTextView appName = dialog.findViewById(R.id.app_name);
        MaterialCardView uploadImage = dialog.findViewById(R.id.uploadImage);
        MaterialButton buttonUpload = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (spinnerRP.getApp_image().endsWith(".jpg") || spinnerRP.getApp_image().endsWith(".png")) {
            Glide.with(requireActivity()).load(spinnerRP.getApp_image())
                    .placeholder(R.drawable.placeholder_portable).into(appImage);
        }
        appName.setText(spinnerRP.getApp_name());

        uploadImage.setOnClickListener(view -> chooseGalleryImage());
        buttonUpload.setOnClickListener(v -> {
            if (!documentImage.isEmpty()) {
                sendToAdmin(appId, function.userId(), documentImage);
                dialog.dismiss();
            } else {
                function.showToast(getString(R.string.please_select_image));
            }
        });
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void chooseGalleryImage() {
        ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle(getResources().getString(R.string.app_name))
                .setImageTitle(getResources().getString(R.string.app_name))
                .setStatusBarColor(function.imageGalleryToolBar())
                .setToolbarColor(function.imageGalleryToolBar())
                .setProgressBarColor(function.imageGalleryProgressBar())
                .setMultipleMode(false)
                .setShowCamera(false)
                .start();
    }

    private void sendToAdmin(String appId, String userId, String document) {
        function.showProgressDialog(requireActivity());
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(requireActivity()));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("appId", appId);
        jsObj.addProperty("AUM", "sendAppReview");
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(document));
        MultipartBody.Part body = MultipartBody.Part.createFormData("document", new File(document).getName(), requestFile);
        RequestBody requestBodyData = RequestBody.create(MediaType.parse("multipart/form-data"), API.toBase64(jsObj.toString()));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.submitAccountVerification(requestBodyData, body);
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {
                try {
                    DataRP dataRP = response.body();
                    if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                        function.alertBox(dataRP.getMsg());
                    } else if (dataRP.getStatus().equals("2")) {
                        function.suspend(dataRP.getMessage());
                    } else {
                        function.alertBox(dataRP.getMessage());
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(requireActivity());
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(requireActivity());
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PICKER) {
            if (resultCode == RESULT_OK && data != null) {
                galleryImages = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                uri_banner = Uri.fromFile(new File(galleryImages.get(0).getPath()));
                documentImage = galleryImages.get(0).getPath();
                Glide.with(requireActivity()).load(uri_banner)
                        .placeholder(R.drawable.placeholder_landscape).into(appImageUpload);
            }
        }
    }
}
