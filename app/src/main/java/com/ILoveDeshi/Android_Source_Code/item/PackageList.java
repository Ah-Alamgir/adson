package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PackageList implements Serializable {

    @SerializedName("id")
    private final String id;

    @SerializedName("name")
    private final String name;

    @SerializedName("image")
    private final String image;

    @SerializedName("description")
    private final String description;

    @SerializedName("price")
    private final String price;

    @SerializedName("task")
    private final String task;

    @SerializedName("curcy")
    private final String curcy;

    @SerializedName("uid")
    private final String uid;

    @SerializedName("pid")
    private final String pid;

    public PackageList(String id, String name, String image, String description, String price, String task, String curcy, String uid, String pid) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.task = task;
        this.curcy = curcy;
        this.uid = uid;
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getTask() {
        return task;
    }

    public String getCurcy() {
        return curcy;
    }

    public String getUid() {
        return uid;
    }

    public String getPid() {
        return pid;
    }
}