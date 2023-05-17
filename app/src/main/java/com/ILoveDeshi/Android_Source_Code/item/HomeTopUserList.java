package com.ILoveDeshi.Android_Source_Code.item;

import java.io.Serializable;

public class HomeTopUserList implements Serializable {

    private String name;
    private String user_image;
    private String total_point;
    private String type;
    private String id;

    public HomeTopUserList(String name, String total_point, String user_image, String type, String id) {
        this.name = name;
        this.total_point = total_point;
        this.user_image = user_image;
        this.type = type;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getTotal_point() {
        return total_point;
    }

    public void setTotal_point(String total_point) {
        this.total_point = total_point;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
