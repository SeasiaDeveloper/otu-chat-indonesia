package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataHistoryOTU {

    @SerializedName("userID")
    private String userID;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("status")
    private String status;

    @SerializedName("respMessage")
    private String respMessage;

    @SerializedName("listData")
    private List<DataDetailHistosryOTU> listData;

    public List<DataDetailHistosryOTU> getListData() {
        return listData;
    }

    public void setListData(List<DataDetailHistosryOTU> listData) {
        this.listData = listData;
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

    public String getRespTime() {
        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
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




}
