package com.eklanku.otuChat.ui.activities.payment.models2;

import com.eklanku.otuChat.ui.activities.payment.models2.DataProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadDataResponseProduct {
    @SerializedName("userID")
    private String userID;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("status")
    private String status;

    @SerializedName("respMessage")
    private String respMessage;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("provider")
    private String provider;

    @SerializedName("products")
    private List<DataProduct> products;

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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<DataProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DataProduct> products) {
        this.products = products;
    }
}
