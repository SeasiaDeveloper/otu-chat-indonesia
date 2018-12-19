package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataPeriodeBPJS {

    @SerializedName("errNumber")
    String errNumber;

    @SerializedName("userID")
    String userID;

    public List<DataDetailPeriodeBPJS> getPeriode() {
        return periode;
    }

    public void setPeriode(List<DataDetailPeriodeBPJS> periode) {
        this.periode = periode;
    }

    @SerializedName("periode")
    List<DataDetailPeriodeBPJS> periode;

    @SerializedName("respTime")
    String respTime;

    @SerializedName("status")
    String status;

    @SerializedName("respMessage")
    String respMessage;

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
