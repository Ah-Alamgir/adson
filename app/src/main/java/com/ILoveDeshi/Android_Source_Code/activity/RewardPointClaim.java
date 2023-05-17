package com.ILoveDeshi.Android_Source_Code.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.item.PaymentList;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.PaymentModeRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RewardPointClaim extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Function function;
    private MaterialToolbar toolbar;
    private Spinner spinner;
    private MaterialButton buttonSubmit;
    private InputMethodManager imm;
    private List<PaymentList> paymentLists;
    private TextInputEditText editTextDetail;
    private MaterialCardView cardView;
    private ConstraintLayout conNoData;
    private String paymentType, userId, userPoints;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_point_claim);

        function = new Function(RewardPointClaim.this);
        function.forceRTLIfSupported();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        paymentLists = new ArrayList<>();

        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        userPoints = intent.getStringExtra("user_points");

        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.payment_detail));
        function.setStatusBarGradiant(RewardPointClaim.this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conNoData = findViewById(R.id.con_noDataFound);
        cardView = findViewById(R.id.cardView_reward_point_claim);
        spinner = findViewById(R.id.spinner_reward_point_claim);
        editTextDetail = findViewById(R.id.editText_detail_reward_point_claim);
        buttonSubmit = findViewById(R.id.button_reward_point_claim);

        cardView.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        buttonSubmit.setOnClickListener(v -> detail());

        if (function.isNetworkAvailable()) {
            paymentMethod();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //first list item selected by default and sets the preset accordingly
        if (position == 0) {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_upload));
        } else {
            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
        }
        paymentType = paymentLists.get(position).getMode_title();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void detail() {

        String detail = editTextDetail.getText().toString();

        editTextDetail.setError(null);

        if (paymentType.equals(getResources().getString(R.string.select_payment_type)) || paymentType.equals("") || paymentType.isEmpty()) {
            function.alertBox(getResources().getString(R.string.please_select_payment));
        } else if (detail.equals("") || detail.isEmpty()) {
            editTextDetail.requestFocus();
            editTextDetail.setError(getResources().getString(R.string.please_enter_detail));
        } else {

            editTextDetail.clearFocus();
            imm.hideSoftInputFromWindow(editTextDetail.getWindowToken(), 0);

            if (function.isNetworkAvailable()) {
                detailSubmit(userId, userPoints, paymentType, detail);
            } else {
                function.alertBox(getResources().getString(R.string.internet_connection));
            }

        }

    }

    public void paymentMethod() {
        function.showProgressDialog(RewardPointClaim.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RewardPointClaim.this));
        jsObj.addProperty("AUM", "get_payment_mode");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentModeRP> call = apiService.getPaymentMode(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentModeRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentModeRP> call, @NotNull Response<PaymentModeRP> response) {

                try {
                    PaymentModeRP paymentModeRP = response.body();
                    if (Objects.requireNonNull(paymentModeRP).getStatus().equals("1")) {
                        paymentLists.addAll(paymentModeRP.getPaymentLists());
                        if (paymentLists.size() != 0) {
                            List<String> arrayList = new ArrayList<String>();
                            for (int i = 0; i < paymentLists.size(); i++) {
                                arrayList.add(paymentLists.get(i).getMode_title());
                            }

                            spinner.setOnItemSelectedListener(RewardPointClaim.this);

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RewardPointClaim.this, android.R.layout.simple_spinner_item, arrayList);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);

                            cardView.setVisibility(View.VISIBLE);

                        }

                    } else {
                        function.alertBox(paymentModeRP.getMessage());
                        conNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(RewardPointClaim.this);
            }

            @Override
            public void onFailure(@NotNull Call<PaymentModeRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(RewardPointClaim.this);
                conNoData.setVisibility(View.VISIBLE);
                function.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }


    public void detailSubmit(final String userId, final String userPoints, String paymentMode, String detail) {
        function.hideProgressDialog(RewardPointClaim.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RewardPointClaim.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("user_points", userPoints);
        jsObj.addProperty("payment_mode", paymentMode);
        jsObj.addProperty("bank_details", detail);
        jsObj.addProperty("AUM", "user_redeem_request");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.submitPaymentDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {
                    DataRP dataRP = response.body();
                    if (Objects.requireNonNull(dataRP).getStatus().equals("1")) {
                        onBackPressed();
                    } else if (dataRP.getStatus().equals("2")) {
                        function.suspend(dataRP.getMessage());
                    } else {
                        function.alertBox(dataRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(RewardPointClaim.this);
            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(RewardPointClaim.this);
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
