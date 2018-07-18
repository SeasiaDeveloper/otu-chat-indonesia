package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataProduct {
    @SerializedName("code")
    private String code;

    @SerializedName("price")
    private String price;

    @SerializedName("name")
    private String name;

    @SerializedName("ep")
    private String ep;

    @SerializedName("isActive")
    private String isActive;

    @SerializedName("type")
    private String type;

    @SerializedName("provider")
    private String provider;

    public DataProduct() {
    }

    public DataProduct(String code, String price, String name, String ep, String isActive, String type, String provider) {
        this.code = code;
        this.price = price;
        this.name = name;
        this.ep = ep;
        this.isActive = isActive;
        this.type = type;
        this.provider = provider;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEp() {
        return ep;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
