package com.ILoveDeshi.Android_Source_Code.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HomeTopUserRP implements Serializable {

    @SerializedName("name_1")
    private String name_1;

    @SerializedName("name_2")
    private String name_2;

    @SerializedName("name_3")
    private String name_3;

    @SerializedName("point_1")
    private String point_1;

    @SerializedName("point_2")
    private String point_2;

    @SerializedName("point_3")
    private String point_3;

    @SerializedName("image_1")
    private String image_1;

    @SerializedName("image_2")
    private String image_2;

    @SerializedName("image_3")
    private String image_3;

    @SerializedName("status")
    private String status;

    @SerializedName("phone_1")
    private String phone_1;

    @SerializedName("phone_2")
    private String phone_2;

    @SerializedName("phone_3")
    private String phone_3;

    public String getName_1() {
        return name_1;
    }

    public String getName_2() {
        return name_2;
    }

    public String getName_3() {
        return name_3;
    }

    public String getPoint_1() {
        return point_1;
    }

    public String getPoint_2() {
        return point_2;
    }

    public String getPoint_3() {
        return point_3;
    }

    public String getImage_1() {
        return image_1;
    }

    public String getImage_2() {
        return image_2;
    }

    public String getImage_3() {
        return image_3;
    }

    public String getStatus() {
        return status;
    }

    public String getPhone_1() {
        return phone_1;
    }

    public String getPhone_2() {
        return phone_2;
    }

    public String getPhone_3() {
        return phone_3;
    }
}
