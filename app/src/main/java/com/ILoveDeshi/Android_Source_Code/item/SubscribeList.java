package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubscribeList implements Serializable {

    @SerializedName("id")
    private final String id;
    @SerializedName("channel_name")
    private final String channel_name;
    @SerializedName("channel_username")
    private final String channel_username;
    @SerializedName("subscibed_coins")
    private final String subscibed_coins;
    @SerializedName("channel_logo")
    private final String channel_logo;

    public SubscribeList(String id, String channel_name, String channel_username, String subscibed_coins, String channel_logo) {
        this.id = id;
        this.channel_name = channel_name;
        this.channel_username = channel_username;
        this.subscibed_coins = subscibed_coins;
        this.channel_logo = channel_logo;
    }

    public String getId() {
        return id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getChannel_username() {
        return channel_username;
    }

    public String getSubscibed_coins() {
        return subscibed_coins;
    }

    public String getChannel_logo() {
        return channel_logo;
    }
}
