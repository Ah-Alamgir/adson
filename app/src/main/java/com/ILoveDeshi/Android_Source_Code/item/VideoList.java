package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoList implements Serializable {

    @SerializedName("id")
    private final String id;

    @SerializedName("video_url")
    private final String video_url;

    @SerializedName("video_point")
    private final String video_point;

    @SerializedName("video_timer")
    private final String video_timer;

    @SerializedName("video_views")
    private final String video_views;

    @SerializedName("video_title")
    private final String video_title;

    @SerializedName("vpn")
    private final String vpn;

    public VideoList(String id, String video_url, String video_point, String video_timer, String video_views, String video_title, String vpn) {
        this.id = id;
        this.video_url = video_url;
        this.video_point = video_point;
        this.video_timer = video_timer;
        this.video_views = video_views;
        this.video_title = video_title;
        this.vpn = vpn;
    }

    public String getId() {
        return id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getVideo_point() {
        return video_point;
    }

    public String getVideo_timer() {
        return video_timer;
    }

    public String getVideo_views() {
        return video_views;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getVpn() {
        return vpn;
    }
}
