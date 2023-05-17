package com.ILoveDeshi.Android_Source_Code.activity;

import static com.ILoveDeshi.Android_Source_Code.util.Constant.STATION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ILoveDeshi.Android_Source_Code.BuildConfig;
import com.ILoveDeshi.Android_Source_Code.rest.ApiClient;
import com.ILoveDeshi.Android_Source_Code.rest.ApiInterface;
import com.ILoveDeshi.Android_Source_Code.util.API;
import com.ILoveDeshi.Android_Source_Code.util.Function;
import com.bumptech.glide.Glide;
import com.ILoveDeshi.Android_Source_Code.R;
import com.ILoveDeshi.Android_Source_Code.response.AboutUsRP;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutUs extends AppCompatActivity {

    private Function function;
    private WebView webView;
    private ImageView imageView;
    private ConstraintLayout conMain, conNoData;
    private MaterialCardView cardViewEmail, cardViewWebsite, cardViewPhone, cardDeveloped;
    private MaterialTextView textViewAppName, textViewAppVersion, textViewAppAuthor, textViewAppContact,
            textViewAppEmail, textViewAppWebsite, tvDeveloper;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        function = new Function(AboutUs.this);
        function.forceRTLIfSupported();
        MaterialToolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.about_us));
        function.setStatusBarGradiant(AboutUs.this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        conMain = findViewById(R.id.con_about_us);
        conNoData = findViewById(R.id.con_noDataFound);
        imageView = findViewById(R.id.app_logo_about_us);
        webView = findViewById(R.id.webView_about_us);
        cardViewEmail = findViewById(R.id.cardView_email_about);
        cardViewWebsite = findViewById(R.id.cardView_website_about);
        cardViewPhone = findViewById(R.id.cardView_phone_about);
        cardDeveloped = findViewById(R.id.cardDeveloped);
        cardDeveloped.setVisibility(View.VISIBLE);
        tvDeveloper = findViewById(R.id.tvDeveloper);
        textViewAppName = findViewById(R.id.textView_app_name_about_us);
        textViewAppVersion = findViewById(R.id.textView_app_version_about_us);
        textViewAppAuthor = findViewById(R.id.textView_app_author_about_us);
        textViewAppContact = findViewById(R.id.textView_app_contact_about_us);
        textViewAppEmail = findViewById(R.id.textView_app_email_about_us);
        textViewAppWebsite = findViewById(R.id.textView_app_website_about_us);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        if (function.isNetworkAvailable()) {
            about();
        } else {
            function.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void about() {
        function.showProgressDialog(AboutUs.this);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AboutUs.this));
        jsObj.addProperty("AUM", "app_about");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AboutUsRP> call = apiService.getAboutUs(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AboutUsRP>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(@NotNull Call<AboutUsRP> call, @NotNull Response<AboutUsRP> response) {
                try {
                    AboutUsRP aboutUsRp = response.body();
                    if (Objects.requireNonNull(aboutUsRp).getStatus().equals("1")) {
                        textViewAppName.setText(aboutUsRp.getApp_name());
                        Glide.with(AboutUs.this).load(aboutUsRp.getApp_logo())
                                .placeholder(R.drawable.placeholder_portable)
                                .into(imageView);
                        textViewAppVersion.setText(BuildConfig.VERSION_NAME);
                        textViewAppAuthor.setText(aboutUsRp.getApp_author());
                        textViewAppContact.setText(aboutUsRp.getApp_contact());
                        textViewAppEmail.setText(aboutUsRp.getApp_email());
                        textViewAppWebsite.setText(aboutUsRp.getApp_website());
                        cardDeveloped.setVisibility(View.VISIBLE);
                        webView.setBackgroundColor(Color.TRANSPARENT);
                        webView.setFocusableInTouchMode(false);
                        webView.setFocusable(false);
                        webView.getSettings().setDefaultTextEncodingName("UTF-8");
                        webView.getSettings().setJavaScriptEnabled(true);
                        String mimeType = "text/html";
                        String encoding = "utf-8";

                        String text = "<html dir=" + function.isWebViewTextRtl() + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/poppins_medium.ttf\")}body{font-family: MyFont;color: " + function.webViewText() + "line-height:1.6}"
                                + "a {color:" + function.webViewLink() + "text-decoration:none}"
                                + "</style></head>"
                                + "<body>"
                                + aboutUsRp.getApp_description()
                                + "</body></html>";
                        tvDeveloper.setText(BuildConfig.BASE_HOST);
                        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
                        if (tvDeveloper.getText().toString().equals(BuildConfig.BASE_HOST) && cardDeveloped.getVisibility() == View.VISIBLE) {
                            conMain.setVisibility(View.VISIBLE);
                            cardViewEmail.setOnClickListener(v -> {
                                try {
                                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{aboutUsRp.getApp_email()});
                                    emailIntent.setData(Uri.parse("mailto:"));
                                    startActivity(emailIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    function.alertBox(getResources().getString(R.string.wrong));
                                }
                            });
                            cardDeveloped.setOnClickListener(v -> {
                                try {
                                    String url = BuildConfig.LISENSE_FROM + STATION + BuildConfig.TAKEN_BY;
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(browserIntent);
                                } catch (Exception e) {
                                    function.alertBox(getResources().getString(R.string.wrong));
                                }
                            });
                            cardViewWebsite.setOnClickListener(v -> {
                                try {
                                    String url = aboutUsRp.getApp_website();
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        url = "http://" + url;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(browserIntent);
                                } catch (Exception e) {
                                    function.alertBox(getResources().getString(R.string.wrong));
                                }
                            });
                            cardViewPhone.setOnClickListener(v -> {
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + aboutUsRp.getApp_contact()));
                                    startActivity(callIntent);
                                } catch (Exception e) {
                                    function.alertBox(getResources().getString(R.string.wrong));
                                }
                            });
                        }
                    } else {
                        function.alertBox(aboutUsRp.getMessage());
                        conNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    function.alertBox(getResources().getString(R.string.failed_try_again));
                }
                function.hideProgressDialog(AboutUs.this);
            }

            @Override
            public void onFailure(@NotNull Call<AboutUsRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                function.hideProgressDialog(AboutUs.this);
                conNoData.setVisibility(View.VISIBLE);
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
