package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.jetbrains.annotations.NotNull;
import org.nguyenhoanglam.imagepicker.model.Config;
import org.nguyenhoanglam.imagepicker.model.Image;
import org.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountVerification extends AppCompatActivity {

    private Function function;
    public MaterialToolbar toolbar;
    private String name, documentImage;
    private MaterialButton button;
    private ImageView imageView;
    private ConstraintLayout conImage;
    private ArrayList<Image> galleryImages;
    private InputMethodManager imm;
    private int REQUEST_GALLERY_PICKER = 100;
    private MaterialTextView textViewTitle, textViewImage;
    private TextInputEditText editTextUserName, editTextFullName, editTextMsg;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);

        function = new Function(AccountVerification.this);
        function.forceRTLIfSupported();

        name = getIntent().getStringExtra("name");

        galleryImages = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.request_verification));
        function.setStatusBarGradiant(AccountVerification.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        textViewTitle = findViewById(R.id.textView_title_av);
        textViewImage = findViewById(R.id.textView_image_av);
        editTextUserName = findViewById(R.id.editText_userName_av);
        editTextFullName = findViewById(R.id.editText_full_name_av);
        editTextMsg = findViewById(R.id.editText_msg_av);
        imageView = findViewById(R.id.imageView_av);
        conImage = findViewById(R.id.con_image_av);
        button = findViewById(R.id.button_av);

        editTextUserName.clearFocus();
        editTextUserName.setCursorVisible(false);
        editTextUserName.setFocusable(false);

        textViewTitle.setText(getResources().getString(R.string.apply_for)
                + " " + getResources().getString(R.string.app_name)
                + " " + getResources().getString(R.string.verification));

        if (function.isNetworkAvailable()) {
            getData();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
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
                Glide.with(AccountVerification.this).load(uri_banner)
                        .placeholder(R.drawable.placeholder_landscape).into(imageView);
                textViewImage.setText(galleryImages.get(0).getPath());
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

    private void getData() {
        editTextUserName.setText(name);
        button.setOnClickListener(view -> {
            String name = Objects.requireNonNull(editTextUserName.getText()).toString();
            String full_name = Objects.requireNonNull(editTextFullName.getText()).toString();
            String msg = Objects.requireNonNull(editTextMsg.getText()).toString();
            form(name, full_name, msg, documentImage);
        });
        conImage.setOnClickListener(view -> chooseGalleryImage());
    }

    private void form(String name, String full_name, String msg, String document) {

        editTextUserName.setError(null);
        editTextFullName.setError(null);
        editTextMsg.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editTextUserName.requestFocus();
            editTextUserName.setError(getResources().getString(R.string.please_enter_name));
        } else if (full_name.equals("") || full_name.isEmpty()) {
            editTextFullName.requestFocus();
            editTextFullName.setError(getResources().getString(R.string.please_enter_full_name));
        } else if (msg.equals("") || msg.isEmpty()) {
            editTextMsg.requestFocus();
            editTextMsg.setError(getResources().getString(R.string.please_enter_message));
        } else if (document == null || document.equals("") || document.isEmpty()) {
            function.alertBox(getResources().getString(R.string.please_select_image));
        } else {

            editTextFullName.clearFocus();
            editTextMsg.clearFocus();
            imm.hideSoftInputFromWindow(editTextFullName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextMsg.getWindowToken(), 0);

            if (function.isNetworkAvailable()) {
                submit(function.userId(), full_name, msg, document);
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    public void submit(String userId, String sendFullName, String sendMessage, String document) {
        function.showProgressDialog(AccountVerification.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AccountVerification.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("full_name", sendFullName);
        jsObj.addProperty("message", sendMessage);
        jsObj.addProperty("AUM", "profile_verify");
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
                        if (dataRP.getSuccess().equals("1")) {
                            editTextFullName.setText("");
                            editTextMsg.setText("");
                            documentImage = "";
                            textViewImage.setText(getResources().getString(R.string.add_thumbnail_file));
                            Glide.with(AccountVerification.this)
                                    .load(R.drawable.placeholder_landscape)
                                    .placeholder(R.drawable.placeholder_landscape).into(imageView);

                            onBackPressed();

                            Toast.makeText(AccountVerification.this, dataRP.getMsg(), Toast.LENGTH_SHORT).show();

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
                function.hideProgressDialog(AccountVerification.this);
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(AccountVerification.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onBackPressed();
    }

}
