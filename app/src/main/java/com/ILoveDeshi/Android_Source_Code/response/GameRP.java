package com.ILoveDeshi.Android_Source_Code.response;

import com.ILoveDeshi.Android_Source_Code.item.GameList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GameRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("OnLooker")
    private List<GameList> gameLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<GameList> getGameLists() {
        return gameLists;
    }
}
