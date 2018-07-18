package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadDataResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("result")
    private List<DataNominal> result;

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

    public List<DataNominal> getResult() {
        return result;
    }

    public void setResult(List<DataNominal> result) {
        this.result = result;
    }

    //rina
    @SerializedName("userID")
    private String userID;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("respMessage")
    private String respMessage;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("productList")
    private List<DataListPPOB> productList;


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

//    public List<DataNominal> getProductList() {
//        return productList;
//    }

    public List<DataListPPOB> getProductList() {
        return productList;
    }

    public void setProductList(List<DataListPPOB> productList) {
        this.productList = productList;
    }
}
