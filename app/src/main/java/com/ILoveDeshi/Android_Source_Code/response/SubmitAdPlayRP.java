package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubmitAdPlayRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("remain_spin")
    private String remain_spin;

    @SerializedName("ad_clicked")
    private String ad_clicked;

    @SerializedName("daily_ads_limit")
    private String daily_ads_limit;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getRemain_spin() {
        return remain_spin;
    }

    public String getAd_clicked() {
        return ad_clicked;
    }

    public String getDaily_ads_limit() {
        return daily_ads_limit;
    }
}
