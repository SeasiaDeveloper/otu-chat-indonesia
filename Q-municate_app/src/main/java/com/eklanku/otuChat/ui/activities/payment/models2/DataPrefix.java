package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataPrefix {
    @SerializedName("errNumber")
    private String errNumber;

    @SerializedName("userID")
    private String userID;

    public List<DataDetailPrefix> getData() {
        return data;
    }

    public void setData(List<DataDetailPrefix> data) {
        this.data = data;
    }

    @SerializedName("data")
    private List<DataDetailPrefix> data;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("status")
    private String status;

    @SerializedName("respMessage")
    private String respMessage;


    public String getErrNumber() {
        return errNumber;
    }

    public void setErrNumber(String errNumber) {
        this.errNumber = errNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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
