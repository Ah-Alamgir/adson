package com.ILoveDeshi.Android_Source_Code.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.ILoveDeshi.Android_Source_Code.activity.MainActivity;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.ProfileRP;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class EditProfileFragment extends Fragment {

    private Function function;
    private ArrayList<Image> galleryImages;
    private String imageProfile, documentImage = "";
    private InputMethodManager imm;
    private ConstraintLayout conMain;
    private ConstraintLayout conNoData;
    private MaterialButton buttonSubmit;
    private CircleImageView imageViewUser;
    private int REQUEST_GALLERY_PICKER = 100;
    private boolean isProfile = false, isRemove = false;
    private TextInputEditText editTextName, editTextEmail, editTextPhoneNo, editTextInstagram, editTextYoutube;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.edit_profile));
        }
        galleryImages = new ArrayList<>();
        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        function = new Function(requireActivity());
        conNoData = view.findViewById(R.id.con_noDataFound);
        imageViewUser = view.findViewById(R.id.imageView_user_editPro);
        editTextName = view.findViewById(R.id.editText_name_editPro);
        editTextEmail = view.findViewById(R.id.editText_email_editPro);
        editTextPhoneNo = view.findViewById(R.id.editText_phone_editPro);
        conMain = view.findViewById(R.id.con_main_cp_fragment);
        editTextInstagram = view.findViewById(R.id.editText_instagram_editPro);
        editTextYoutube = view.findViewById(R.id.editText_youtube_editPro);
        buttonSubmit = view.findViewById(R.id.button_editPro);
        TextInputLayout textInputEmail = view.findViewById(R.id.textInput_email_editPro);

        if (function.getLoginType().equals("google") || function.getLoginType().equals("facebook")) {
            editTextName.setCursorVisible(false);
            editTextName.setFocusable(false);
            textInputEmail.setVisibility(View.GONE);
        } else {
            textInputEmail.setVisibility(View.VISIBLE);
        }

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            profile(function.userId());
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

        setHasOptionsMenu(true);
        return view;

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void profile(String userId) {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("AUM", "user_profile");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ProfileRP>() {
                @Override
                public void onResponse(@NotNull Call<ProfileRP> call, @NotNull Response<ProfileRP> response) {

                    if (getActivity() != null) {

                        try {
                            ProfileRP profileRP = response.body();
                            if (Objects.requireNonNull(profileRP).getStatus().equals("1")) {
                                if (profileRP.getSuccess().equals("1")) {
                                    imageProfile = profileRP.getUser_image();
                                    if (profileRP.getUser_image().contains(".jpg") || profileRP.getUser_image().contains(".png")) {
                                        Glide.with(getActivity().getApplicationContext()).load(profileRP.getUser_image())
                                                .placeholder(R.drawable.profile).into(imageViewUser);
                                    }
                                    editTextName.setText(profileRP.getName());
                                    editTextEmail.setText(profileRP.getEmail());
                                    editTextPhoneNo.setText(profileRP.getPhone());

                                    imageViewUser.setOnClickListener(v -> {
                                        chooseGalleryImage();
                                    });
                                    buttonSubmit.setOnClickListener(v -> save());
                                    conMain.setVisibility(View.VISIBLE);
                                } else {
                                    conNoData.setVisibility(View.VISIBLE);
                                    function.alertBox(profileRP.getMsg());
                                }
                            } else if (profileRP.getStatus().equals("2")) {
                                function.suspend(profileRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                function.alertBox(profileRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }
                    function.hideProgressDialog(requireActivity());
                }

                @Override
                public void onFailure(@NotNull Call<ProfileRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    function.hideProgressDialog(requireActivity());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PICKER) {
            if (resultCode == RESULT_OK && data != null) {
                galleryImages = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                Uri uri_banner = Uri.fromFile(new File(galleryImages.get(0).getPath()));
                documentImage = galleryImages.get(0).getPath();
                //function.showToast(documentImage + " : Image");
                Glide.with(requireActivity()).load(uri_banner)
                        .placeholder(R.drawable.placeholder_landscape).into(imageViewUser);
            }
        }
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

    private void save() {
        String name = Objects.requireNonNull(editTextName.getText()).toString();
        String email = Objects.requireNonNull(editTextEmail.getText()).toString();
        String phoneNo = Objects.requireNonNull(editTextPhoneNo.getText()).toString();
        String instagram = Objects.requireNonNull(editTextInstagram.getText()).toString();
        String youtube = Objects.requireNonNull(editTextYoutube.getText()).toString();

        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPhoneNo.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if ((function.getLoginType().equals("normal")) && (!isValidMail(email) || email.isEmpty())) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else {
            if (function.isNetworkAvailable()) {
                editTextName.clearFocus();
                editTextEmail.clearFocus();
                editTextPhoneNo.clearFocus();
                imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextInstagram.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextYoutube.getWindowToken(), 0);
                profileUpdate(function.userId(), name, phoneNo, youtube, instagram, documentImage);
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void profileUpdate(String userId, String sendName, String sendPhone, String userYoutube, String userInstagram, String profileImage) {
        if (getActivity() != null) {
            function.showProgressDialog(requireActivity());
            MultipartBody.Part body = null;
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("name", sendName);
            jsObj.addProperty("phone", sendPhone);
            jsObj.addProperty("is_remove", isRemove);
            jsObj.addProperty("user_youtube", userYoutube);
            jsObj.addProperty("user_instagram", userInstagram);
            jsObj.addProperty("AUM", "user_profile_update");
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(profileImage));
            body = MultipartBody.Part.createFormData("user_image", new File(profileImage).getName(), requestFile);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), API.toBase64(jsObj.toString()));
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.getEditProfile(requestBody, body);
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {
                    if (getActivity() != null) {
                        try {
                            DataRP dataRP = response.body();
                            if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    Toast.makeText(getActivity(), dataRP.getMsg(), Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    function.alertBox(dataRP.getMsg());
                                }
                            } else if (dataRP.getStatus().equals("2")) {
                                function.suspend(dataRP.getMessage());
                            } else {
                                function.alertBox(dataRP.getMessage());
                            }
                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            function.alertBox(getResources().getString(R.string.failed_try_again));
                        }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.profile));
        }
    }
}
