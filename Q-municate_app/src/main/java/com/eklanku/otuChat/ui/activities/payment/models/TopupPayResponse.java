package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class TopupPayResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    //Rina
    @SerializedName("userID")
    private String userID;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("nominal")
    private String nominal;

    @SerializedName("bank")
    private String bank;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("res_response_msg")
    private String respMessage;

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

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

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

    public String getResponMessage2() {
        return responMessage2;
    }

    public void setResponMessage2(String responMessage2) {
        this.responMessage2 = responMessage2;
    }

    @SerializedName("respMessage")
    String responMessage2;

}
