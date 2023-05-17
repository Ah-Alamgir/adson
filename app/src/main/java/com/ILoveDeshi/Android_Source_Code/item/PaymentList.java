package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("mode_title")
    private String mode_title;

    public String getId() {
        return id;
    }

    public String getMode_title() {
        return mode_title;
    }
}
