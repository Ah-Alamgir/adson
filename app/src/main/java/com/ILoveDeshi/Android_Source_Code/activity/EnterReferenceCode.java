package com.ILoveDeshi.Android_Source_Code.activity;

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
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterReferenceCode extends AppCompatActivity {

    private Function function;
    private MaterialToolbar toolbar;
    private String userId;
    private InputMethodManager imm;
    private TextInputEditText editText;
    private MaterialButton buttonContinue, buttonSkip;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_reference_code);

        function = new Function(EnterReferenceCode.this);
        function.forceRTLIfSupported();
        userId = getIntent().getStringExtra("user_id");

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.reference_code));
        function.setStatusBarGradiant(EnterReferenceCode.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText = findViewById(R.id.editText_erc);
        buttonContinue = findViewById(R.id.button_continue_erc);
        buttonSkip = findViewById(R.id.button_skip_erc);

        buttonContinue.setOnClickListener(v -> {

            editText.setError(null);
            String refCode = Objects.requireNonNull(editText.getText()).toString();

            if (refCode.equals("") || refCode.isEmpty()) {
                editText.requestFocus();
                editText.setError(getResources().getString(R.string.please_enter_reference_code));
            } else {

                editText.clearFocus();
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                if (function.isNetworkAvailable()) {
                    referenceCode(userId, refCode);
                } else {
                    function.alertBox(getResources().getString(R.string.internet_connection));
                }

            }

        });

        buttonSkip.setOnClickListener(v -> {
            startActivity(new Intent(EnterReferenceCode.this, MainActivity.class));
            finishAffinity();
        });

    }

    public void referenceCode(String userId, String code) {
        function.showProgressDialog(EnterReferenceCode.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EnterReferenceCode.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("user_refrence_code", code);
        jsObj.addProperty("AUM", "apply_user_refrence_code");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.submitReferenceCode(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {
                    DataRP dataRP = response.body();
                    if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                        if (dataRP.getSuccess().equals("1")) {
                            Toast.makeText(EnterReferenceCode.this, dataRP.getMsg(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EnterReferenceCode.this, MainActivity.class));
                            finishAffinity();
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
                function.hideProgressDialog(EnterReferenceCode.this);
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(EnterReferenceCode.this);
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
