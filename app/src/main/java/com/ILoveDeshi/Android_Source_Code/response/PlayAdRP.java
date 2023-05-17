package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlayAdRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("daily_ads_limit")
    private String daily_ads_limit;

    @SerializedName("remain_spin")
    private String remain_spin;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    @SerializedName("ad_clicked")
    private String ad_clicked;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDaily_ads_limit() {
        return daily_ads_limit;
    }

    public String getRemain_spin() {
        return remain_spin;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getAd_clicked() {
        return ad_clicked;
    }
}
