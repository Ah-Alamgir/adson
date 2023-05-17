package com.ILoveDeshi.Android_Source_Code.rest;

import android.util.Base64;

import com.ILoveDeshi.Android_Source_Code.BuildConfig;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    public static final String BASE_URL = BuildConfig.My_api;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient builder = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();

            byte[] data = Base64.decode(BASE_URL, Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            retrofit = new Retrofit.Builder()
                    .baseUrl(text)
                    .client(builder)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
