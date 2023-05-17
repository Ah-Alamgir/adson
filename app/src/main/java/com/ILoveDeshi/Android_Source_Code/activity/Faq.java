package com.ILoveDeshi.Android_Source_Code.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.FaqRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Faq extends AppCompatActivity {

    private Function function;
    private WebView webView;
    public MaterialToolbar toolbar;
    private ConstraintLayout conNoData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        function = new Function(Faq.this);
        function.forceRTLIfSupported();
        toolbar = findViewById(R.id.toolbar_main);
        function.setStatusBarGradiant(Faq.this);
        toolbar.setTitle(getResources().getString(R.string.faq));
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        webView = findViewById(R.id.webView_faq);
        conNoData = findViewById(R.id.con_noDataFound);

        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            faq();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void faq() {
        function.showProgressDialog(Faq.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Faq.this));
        jsObj.addProperty("AUM", "app_faq");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FaqRP> call = apiService.getFaq(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<FaqRP>() {
            @Override
            public void onResponse(@NotNull Call<FaqRP> call, @NotNull Response<FaqRP> response) {

                try {
                    FaqRP faqRP = response.body();
                    if (Objects.requireNonNull(faqRP).getStatus().equals("1")) {
                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        String mimeType = "text/html";
                        String encoding = "utf-8";

                        String text = "<html dir=" + function.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/montserrat_semibold.otf\")}body{font-family: MyFont;color: " + function.webViewText() + "line-height:1.6}"
                                + "a {color:" + function.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + faqRP.getApp_faq()
                                + "</body></html>";

                        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        function.alertBox(faqRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(Faq.this);
            }

            @Override
            public void onFailure(@NotNull Call<FaqRP> call, @NotNull Throwable t) {
                Log.e("onFailure_data", t.toString());
                conNoData.setVisibility(View.VISIBLE);
                function.hideProgressDialog(Faq.this);
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
