package com.ILoveDeshi.Android_Source_Code.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.chaos.view.PinView;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.RegisterRP;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Verification extends AppCompatActivity {

    private Function function;
    private PinView pinView;
    private InputMethodManager imm;
    private String verification, name, email, password, phoneNo, reference;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        function = new Function(Verification.this);
        function.forceRTLIfSupported();

        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
            phoneNo = intent.getStringExtra("phoneNo");
            reference = intent.getStringExtra("reference");
        } else {
            name = function.pref.getString(function.regName, null);
            email = function.pref.getString(function.regEmail, null);
            password = function.pref.getString(function.regPassword, null);
            phoneNo = function.pref.getString(function.regPhoneNo, null);
            reference = function.pref.getString(function.regReference, null);
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pinView = findViewById(R.id.firstPinView);
        MaterialButton button_verification = findViewById(R.id.button_verification);
        MaterialButton button_register = findViewById(R.id.button_register_verification);
        MaterialTextView textView = findViewById(R.id.resend_verification);

        textView.setOnClickListener(v -> {

            Random generator = new Random();
            int n = generator.nextInt(9999 - 1000) + 1000;

            String stringEmail = function.pref.getString(function.regEmail, null);
            resendVerification(stringEmail, String.valueOf(n));

        });

        button_verification.setOnClickListener(v -> {
            verification = Objects.requireNonNull(pinView.getText()).toString();
            verification();
        });

        button_register.setOnClickListener(v -> {
            function.editor.putBoolean(function.isVerification, false);
            function.editor.commit();
            startActivity(new Intent(Verification.this, Register.class));
            finishAffinity();
        });

    }

    public void verification() {

        pinView.clearFocus();
        imm.hideSoftInputFromWindow(pinView.getWindowToken(), 0);

        if (verification == null || verification.equals("") || verification.isEmpty()) {
            function.alertBox(getResources().getString(R.string.please_enter_verification_code));
        } else {
            if (function.isNetworkAvailable()) {
                pinView.setText("");
                if (verification.equals(function.pref.getString(function.verificationCode, null))) {
                    register(name, email, password, phoneNo, reference);
                } else {
                    function.alertBox(getResources().getString(R.string.verification_message));
                }
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    @SuppressLint("HardwareIds")
    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone, String reference) {
        function.showProgressDialog(Verification.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("type", "normal");
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("phone", sendPhone);
        jsObj.addProperty("device_id", function.getDeviceId());
        jsObj.addProperty("user_refrence_code", reference);
        jsObj.addProperty("AUM", "user_register");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegisterDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {

                    RegisterRP registerRP = response.body();
                    if (Objects.requireNonNull(registerRP).getStatus().equals("1")) {
                        function.editor.putBoolean(function.isVerification, false);
                        function.editor.commit();

                        if (registerRP.getSuccess().equals("1")) {
                            startActivity(new Intent(Verification.this, Login.class));
                        } else {
                            startActivity(new Intent(Verification.this, Register.class));
                        }
                        finishAffinity();

                        Toast.makeText(Verification.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();

                    } else {
                        function.alertBox(registerRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Verification.this);
            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                function.hideProgressDialog(Verification.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }


    public void resendVerification(String sendEmail, String otp) {
        function.showProgressDialog(Verification.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Verification.this));
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("otp_code", otp);
        jsObj.addProperty("AUM", "user_register_verify_email");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.getVerification(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {

                    DataRP dataRP = response.body();
                    if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                        if (dataRP.getSuccess().equals("1")) {
                            function.editor.putString(function.verificationCode, otp);
                            function.editor.commit();
                        } else {
                            function.alertBox(dataRP.getMsg());
                        }
                    } else {
                        function.alertBox(dataRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Verification.this);
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                function.hideProgressDialog(Verification.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }
}
