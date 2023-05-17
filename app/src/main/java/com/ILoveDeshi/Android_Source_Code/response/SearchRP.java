package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SearchRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    @SerializedName("desc")
    private String desc;

    @SerializedName("url")
    private String url;

    @SerializedName("website_url")
    private String website_url;

    @SerializedName("website_timer")
    private String website_timer;

    @SerializedName("website_coins")
    private String website_coins;

    public SearchRP(String status, String message, String success, String msg, String id, String name, String image, String desc, String url, String website_url, String website_timer, String website_coins) {
        this.status = status;
        this.message = message;
        this.success = success;
        this.msg = msg;
        this.id = id;
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.url = url;
        this.website_url = website_url;
        this.website_timer = website_timer;
        this.website_coins = website_coins;
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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public String getWebsite_timer() {
        return website_timer;
    }

    public String getWebsite_coins() {
        return website_coins;
    }
}
