package com.sparkle.roam.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendRequest {
    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("days")
    @Expose
    private String days;

    public SendRequest(String userID, String days) {
        this.userID = userID;
        this.days = days;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
