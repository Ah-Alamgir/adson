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
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.CheckOtpRP;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.RegisterRP;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
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


public class Register extends AppCompatActivity {

    private Function function;
    private String reference = "";
    private InputMethodManager imm;
    private MaterialCardView buttonSubmit;
    private String name, email, password, phoneNo;
    private TextInputEditText editTextName, editTextEmail, editTextPassword, editTextPhoneNo, editTextReference;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        function = new Function(Register.this);
        function.forceRTLIfSupported();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editTextName = findViewById(R.id.editText_name_register);
        editTextEmail = findViewById(R.id.editText_email_register);
        editTextPassword = findViewById(R.id.editText_password_register);
        editTextPhoneNo = findViewById(R.id.editText_phoneNo_register);
        editTextReference = findViewById(R.id.editText_reference_code_register);

        buttonSubmit = findViewById(R.id.button_submit);

        MaterialTextView textViewLogin = findViewById(R.id.textView_login_register);
        textViewLogin.setOnClickListener(v -> {
            function.editor.putBoolean(function.isVerification, false);
            function.editor.commit();
            startActivity(new Intent(Register.this, Login.class));
            finishAffinity();
        });

        checkOtp();

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void checkOtp() {
        function.showProgressDialog(Register.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Register.this));
        jsObj.addProperty("AUM", "otp_status");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CheckOtpRP> call = apiService.getOtpStatus(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CheckOtpRP>() {
            @Override
            public void onResponse(@NotNull Call<CheckOtpRP> call, @NotNull Response<CheckOtpRP> response) {

                try {
                    CheckOtpRP checkOtpRP = response.body();
                    if (Objects.requireNonNull(checkOtpRP).getStatus().equals("1")) {
                        if (checkOtpRP.getStatus().equals("1")) {
                            buttonSubmit.setOnClickListener(v -> form(checkOtpRP.getOtp_status()));
                        } else {
                            function.alertBox(checkOtpRP.getMessage());
                        }
                    } else {
                        function.alertBox(checkOtpRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Register.this);
            }

            @Override
            public void onFailure(@NotNull Call<CheckOtpRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(Register.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void form(String status) {
        name = Objects.requireNonNull(editTextName.getText()).toString();
        email = Objects.requireNonNull(editTextEmail.getText()).toString();
        password = Objects.requireNonNull(editTextPassword.getText()).toString();
        phoneNo = Objects.requireNonNull(editTextPhoneNo.getText()).toString();
        reference = Objects.requireNonNull(editTextReference.getText()).toString();

        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextPhoneNo.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.equals("") || password.isEmpty()) {
            editTextPassword.requestFocus();
            editTextPassword.setError(getResources().getString(R.string.please_enter_password));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else {

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            editTextPhoneNo.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);

            if (function.isNetworkAvailable()) {

                if (status.equals("true")) {

                    Random generator = new Random();
                    int n = generator.nextInt(9999 - 1000) + 1000;

                    verificationCall(email, String.valueOf(n));

                } else {
                    register(name, email, password, phoneNo, reference);
                }

            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }

        }
    }

    public void verificationCall(String sendEmail, String otp) {
        function.showProgressDialog(Register.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Register.this));
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
                            function.editor.putBoolean(function.isVerification, true);
                            function.editor.putString(function.regName, name);
                            function.editor.putString(function.regEmail, email);
                            function.editor.putString(function.regPassword, password);
                            function.editor.putString(function.regPhoneNo, phoneNo);
                            function.editor.putString(function.regReference, reference);
                            function.editor.putString(function.verificationCode, otp);
                            function.editor.commit();

                            editTextName.setText("");
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                            editTextPhoneNo.setText("");
                            editTextReference.setText("");

                            Toast.makeText(Register.this, dataRP.getMsg(), Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Register.this, Verification.class)
                                    .putExtra("name", name)
                                    .putExtra("email", email)
                                    .putExtra("password", password)
                                    .putExtra("phoneNo", phoneNo)
                                    .putExtra("reference", reference));
                            finishAffinity();

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
                function.hideProgressDialog(Register.this);
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                function.hideProgressDialog(Register.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @SuppressLint("HardwareIds")
    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone, String reference) {
        function.showProgressDialog(Register.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Register.this));
        jsObj.addProperty("AUM", "user_register");
        jsObj.addProperty("type", "normal");
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("phone", sendPhone);
        jsObj.addProperty("device_id", function.getDeviceId());
        jsObj.addProperty("user_refrence_code", reference);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegisterDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {

                    RegisterRP registerRP = response.body();
                    if (Objects.requireNonNull(registerRP).getStatus().equals("1")) {
                        if (registerRP.getSuccess().equals("1")) {
                            startActivity(new Intent(Register.this, Login.class));
                            finishAffinity();
                            Toast.makeText(Register.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();
                        } else {
                            function.alertBox(registerRP.getMsg());
                        }
                    } else {
                        function.alertBox(registerRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Register.this);
            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                function.hideProgressDialog(Register.this);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

}
