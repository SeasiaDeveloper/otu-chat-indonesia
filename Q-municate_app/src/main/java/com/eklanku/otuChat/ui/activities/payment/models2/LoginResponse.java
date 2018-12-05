package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class LoginResponse {
    @SerializedName("userID")
    private String userID;

    @SerializedName("mbr_token")
    private String accessToken;

    @SerializedName("status")
    private String status;

    @SerializedName("respMessage")
    private String respMessage;

    @SerializedName("respTime")
    private String respTime;

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @SerializedName("data")
    private JSONObject data;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public String getRespTime() {
        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
    }

}
