package com.ILoveDeshi.Android_Source_Code.response;

import com.ILoveDeshi.Android_Source_Code.item.PackageList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PackageRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("statuses")
    private List<PackageList> packageLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<PackageList> getPackageLists() {
        return packageLists;
    }
}
