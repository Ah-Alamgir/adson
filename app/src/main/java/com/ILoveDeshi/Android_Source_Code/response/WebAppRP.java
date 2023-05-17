package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WebAppRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("website_title")
    private String website_title;

    @SerializedName("website_url")
    private String website_url;

    @SerializedName("website_coins")
    private String website_coins;

    @SerializedName("website_timer")
    private String website_timer;

    public WebAppRP(String status, String message, String success, String msg, String website_title, String website_url, String website_coins, String website_timer) {
        this.status = status;
        this.message = message;
        this.success = success;
        this.msg = msg;
        this.website_title = website_title;
        this.website_url = website_url;
        this.website_coins = website_coins;
        this.website_timer = website_timer;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getWebsite_title() {
        return website_title;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public String getWebsite_coins() {
        return website_coins;
    }

    public String getWebsite_timer() {
        return website_timer;
    }
}
