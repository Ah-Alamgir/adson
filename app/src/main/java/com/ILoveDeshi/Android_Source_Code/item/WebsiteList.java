package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WebsiteList implements Serializable {

    @SerializedName("id")
    private final String id;

    @SerializedName("website_title")
    private final String website_title;

    @SerializedName("website_logo")
    private final String website_logo;

    @SerializedName("website_coins")
    private final String website_coins;

    @SerializedName("website_timer")
    private final String website_timer;

    @SerializedName("vpn")
    private final String vpn;

    public WebsiteList(String id, String website_title, String website_logo, String website_coins, String website_timer, String vpn) {
        this.id = id;
        this.website_title = website_title;
        this.website_logo = website_logo;
        this.website_coins = website_coins;
        this.website_timer = website_timer;
        this.vpn = vpn;
    }

    public String getId() {
        return id;
    }

    public String getWebsite_title() {
        return website_title;
    }

    public String getWebsite_logo() {
        return website_logo;
    }

    public String getWebsite_coins() {
        return website_coins;
    }

    public String getWebsite_timer() {
        return website_timer;
    }

    public String getVpn() {
        return vpn;
    }
}