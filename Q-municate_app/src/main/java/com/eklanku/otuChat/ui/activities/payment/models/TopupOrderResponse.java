package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopupOrderResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("errNumber")
    private String error;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @SerializedName("userID")
    private String userID;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("banklist")
    private List<TopupDetailM> banklist;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("respMessage")
    private String respMessage;

    public String getRespTime() {
        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

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

    public List<TopupDetailM> getBanklist() {
        return banklist;
    }

    public void setBanklist(List<TopupDetailM> banklist) {
        this.banklist = banklist;
    }
}
