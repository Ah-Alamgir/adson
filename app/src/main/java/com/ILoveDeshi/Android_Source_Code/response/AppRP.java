package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppRP implements Serializable {

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

    @SerializedName("app_contact")
    private String app_contact;

    @SerializedName("interstitial_ad_click")
    private String interstitial_ad_click;

    @SerializedName("app_update_status")
    private String app_update_status;

    @SerializedName("app_new_version")
    private int app_new_version;

    @SerializedName("app_update_desc")
    private String app_update_desc;

    @SerializedName("app_redirect_url")
    private String app_redirect_url;

    @SerializedName("cancel_update_status")
    private String cancel_update_status;

    @SerializedName("online_game_points")
    private int online_game_points;

    @SerializedName("mediation_app_id")
    private String mediation_app_id;

    @SerializedName("mediation_client_key")
    private String mediation_client_key;

    @SerializedName("mediation_banner")
    private String mediation_banner;

    @SerializedName("mediation_inter")
    private String mediation_inter;

    @SerializedName("mediation_reward")
    private String mediation_reward;

    @SerializedName("mediation_inter_video")
    private String mediation_inter_video;

    @SerializedName("google_auth_id")
    private String google_auth_id;

    @SerializedName("youtube_api_key")
    private String youtube_api_key;

    @SerializedName("mediation_show")
    private boolean mediation_show;

    @SerializedName("online_game_timer")
    private int online_game_timer;

    @SerializedName("live_mode")
    private boolean live_mode;

    @SerializedName("ads_subscriptio_payment")
    private String ads_subscriptio_payment;

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

    public String getApp_contact() {
        return app_contact;
    }

    public String getInterstitial_ad_click() {
        return interstitial_ad_click;
    }

    public String getApp_update_status() {
        return app_update_status;
    }

    public int getApp_new_version() {
        return app_new_version;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public String getApp_redirect_url() {
        return app_redirect_url;
    }

    public String getCancel_update_status() {
        return cancel_update_status;
    }

    public int getOnline_game_points() {
        return online_game_points;
    }

    public String getMediation_app_id() {
        return mediation_app_id;
    }

    public String getMediation_client_key() {
        return mediation_client_key;
    }

    public String getMediation_banner() {
        return mediation_banner;
    }

    public String getMediation_inter() {
        return mediation_inter;
    }

    public String getMediation_reward() {
        return mediation_reward;
    }

    public String getMediation_inter_video() {
        return mediation_inter_video;
    }

    public String getGoogle_auth_id() {
        return google_auth_id;
    }

    public String getYoutube_api_key() {
        return youtube_api_key;
    }

    public boolean isMediation_show() {
        return mediation_show;
    }

    public int getOnline_game_timer() {
        return online_game_timer;
    }

    public boolean isLive_mode() {
        return live_mode;
    }

    public String getAds_subscriptio_payment() {
        return ads_subscriptio_payment;
    }
}
