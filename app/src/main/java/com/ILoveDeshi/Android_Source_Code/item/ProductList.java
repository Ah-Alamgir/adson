package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductList implements Serializable {

    @SerializedName("id")
    private final String id;

    @SerializedName("title")
    private final String title;

    @SerializedName("price")
    private final String price;

    @SerializedName("discount_price")
    private final String discount_price;

    @SerializedName("visit_utl")
    private final String visit_utl;

    @SerializedName("image")
    private final String image;

    @SerializedName("description")
    private final String description;

    public ProductList(String id, String title, String price, String discount_price, String visit_utl, String image, String description) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.discount_price = discount_price;
        this.visit_utl = visit_utl;
        this.image = image;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public String getVisit_utl() {
        return visit_utl;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}

