package com.ILoveDeshi.Android_Source_Code.response;

import com.ILoveDeshi.Android_Source_Code.item.AppsList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AppsRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("OnLooker")
    private List<AppsList> appsLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<AppsList> getAppsLists() {
        return appsLists;
    }
}
