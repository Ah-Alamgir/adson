package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RewardPointList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("activity_type")
    private String activity_type;

    @SerializedName("points")
    private String points;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    public String getId() {
        return id;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public String getPoints() {
        return points;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
