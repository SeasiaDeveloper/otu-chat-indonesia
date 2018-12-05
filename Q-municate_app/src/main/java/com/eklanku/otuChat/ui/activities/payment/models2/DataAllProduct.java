package com.eklanku.otuChat.ui.activities.payment.models2;

import com.eklanku.otuChat.ui.activities.payment.models2.DataProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataAllProduct {

    @SerializedName("errNumber")
    String errNumber;

    @SerializedName("userID")
    String userID;

    @SerializedName("data")
    List<DataProduct> data;

    @SerializedName("respTime")
    String respTime;

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

    public List<DataProduct> getData() {
        return data;
    }

    public void setData(List<DataProduct> data) {
        this.data = data;
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

    @SerializedName("status")
    String status;

    @SerializedName("respMessage")
    String respMessage;
}
