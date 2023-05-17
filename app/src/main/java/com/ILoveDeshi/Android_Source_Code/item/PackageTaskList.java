package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PackageTaskList implements Serializable {

    @SerializedName("id")
    private final String id;

    @SerializedName("pack_id")
    private final String pack_id;

    @SerializedName("points")
    private final String points;

    @SerializedName("pack_name")
    private final String pack_name;

    @SerializedName("name")
    private final String name;

    @SerializedName("ads")
    private final String ads;

    @SerializedName("vpn")
    private final String vpn;

    public PackageTaskList(String id, String pack_id, String points, String pack_name, String name, String ads, String vpn) {
        this.id = id;
        this.pack_id = pack_id;
        this.points = points;
        this.pack_name = pack_name;
        this.name = name;
        this.ads = ads;
        this.vpn = vpn;
    }

    public String getId() {
        return id;
    }

    public String getPack_id() {
        return pack_id;
    }

    public String getPoints() {
        return points;
    }

    public String getPack_name() {
        return pack_name;
    }

    public String getName() {
        return name;
    }

    public String getAds() {
        return ads;
    }

    public String getVpn() {
        return vpn;
    }
}
