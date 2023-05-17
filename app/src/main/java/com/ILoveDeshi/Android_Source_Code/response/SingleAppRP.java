package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SingleAppRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("app_name")
    private String app_name;

    @SerializedName("app_image")
    private String app_image;

    @SerializedName("app_desc")
    private String app_desc;

    @SerializedName("app_url")
    private String app_url;

    @SerializedName("app_install")
    private String app_install;

    @SerializedName("app_coins")
    private String app_coins;

    @SerializedName("app_run_coins")
    private String app_run_coins;

    @SerializedName("app_run_time")
    private String app_run_time;

    @SerializedName("app_id")
    private String app_id;

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

    public String getApp_name() {
        return app_name;
    }

    public String getApp_image() {
        return app_image;
    }

    public String getApp_desc() {
        return app_desc;
    }

    public String getApp_url() {
        return app_url;
    }

    public String getApp_install() {
        return app_install;
    }

    public String getApp_coins() {
        return app_coins;
    }

    public String getApp_run_coins() {
        return app_run_coins;
    }

    public String getApp_run_time() {
        return app_run_time;
    }

    public String getApp_id() {
        return app_id;
    }
}
