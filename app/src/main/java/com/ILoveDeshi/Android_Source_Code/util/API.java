package com.ILoveDeshi.Android_Source_Code.util;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;

import com.ILoveDeshi.Android_Source_Code.BuildConfig;
import com.google.gson.annotations.SerializedName;


public class API {

    @SerializedName("kay")
    private final String kay;
    @SerializedName("pkId")
    private final String pkId;
    @SerializedName("user")
    private final String user;
    @SerializedName("apiKey")
    private final String apiKey;

    public API(Activity activity) {
        apiKey = BuildConfig.API_KEY;
        user = BuildConfig.BUYER_NAME;
        pkId = BuildConfig.APPLICATION_ID;
        kay = BuildConfig.LICENSE_KEY;
    }

    public API(Context context) {
        apiKey = BuildConfig.API_KEY;
        user = BuildConfig.BUYER_NAME;
        pkId = BuildConfig.APPLICATION_ID;
        kay = BuildConfig.LICENSE_KEY;
    }

    public static String toBase64(String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

}
