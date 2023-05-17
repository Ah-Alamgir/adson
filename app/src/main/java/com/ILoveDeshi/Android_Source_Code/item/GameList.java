package com.ILoveDeshi.Android_Source_Code.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GameList implements Serializable {

    @SerializedName("id")
    private final String id;
    @SerializedName("game_name")
    private final String game_name;
    @SerializedName("game_image")
    private final String game_image;
    @SerializedName("game_url")
    private final String game_url;
    @SerializedName("game_status")
    private final String game_status;

    public GameList(String id, String game_name, String game_image, String game_url, String game_status) {
        this.id = id;
        this.game_name = game_name;
        this.game_image = game_image;
        this.game_url = game_url;
        this.game_status = game_status;
    }

    public String getId() {
        return id;
    }

    public String getGame_name() {
        return game_name;
    }

    public String getGame_image() {
        return game_image;
    }

    public String getGame_url() {
        return game_url;
    }

    public String getGame_status() {
        return game_status;
    }
}
