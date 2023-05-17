package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppsRatingList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("app_name")
    private String app_name;

    @SerializedName("app_image")
    private String app_image;

    @SerializedName("app_coins")
    private String app_coins;

    public AppsRatingList(String id, String app_name, String app_image, String app_coins) {
        this.id = id;
        this.app_name = app_name;
        this.app_image = app_image;
        this.app_coins = app_coins;
    }

    public String getId() {
        return id;
    }

    public String getApp_name() {
        return app_name;
    }

    public String getApp_image() {
        return app_image;
    }

    public String getApp_coins() {
        return app_coins;
    }
}

