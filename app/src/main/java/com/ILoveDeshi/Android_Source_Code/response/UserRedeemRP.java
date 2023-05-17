package com.ILoveDeshi.Android_Source_Code.response;

import com.ILoveDeshi.Android_Source_Code.item.UserRMList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserRedeemRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("OnLooker")
    private List<UserRMList> userRMLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<UserRMList> getUserRMLists() {
        return userRMLists;
    }
}
