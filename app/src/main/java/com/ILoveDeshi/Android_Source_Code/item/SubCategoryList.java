package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubCategoryList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("external_image")
    private String external_image;

    @SerializedName("external_url")
    private String external_url;

    public SubCategoryList(String id, String external_image, String external_url) {
        this.id = id;
        this.external_image = external_image;
        this.external_url = external_url;
    }

    public String getId() {
        return id;
    }

    public String getExternal_image() {
        return external_image;
    }

    public String getExternal_url() {
        return external_url;
    }
}
