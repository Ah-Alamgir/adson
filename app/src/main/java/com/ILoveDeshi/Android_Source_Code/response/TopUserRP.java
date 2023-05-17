package com.ILoveDeshi.Android_Source_Code.response;

import com.ILoveDeshi.Android_Source_Code.item.HomeTopUserList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TopUserRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("OnLooker")
    private List<HomeTopUserList> homeTopUserLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<HomeTopUserList> getHomeTopUserLists() {
        return homeTopUserLists;
    }
}
