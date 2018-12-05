package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataMember {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("gender")
    private String gender;

    @SerializedName("product_code")
    private String product_code;

    @SerializedName("wallet")
    private Double wallet;

    @SerializedName("shop_name")
    private String shop_name;

    @SerializedName("shop_image")
    private String shop_image;

    @SerializedName("shop_description")
    private String shop_description;

    @SerializedName("shop_id")
    private String shop_id;

    @SerializedName("shop_status")
    private String shop_status;

    @SerializedName("eklanku_id")
    private String eklanku_id;

    @SerializedName("shop_province_name")
    private String shop_province;

    @SerializedName("shop_regency_name")
    private String shop_regency;

    public DataMember() {}

    public DataMember(String id, String name, String phone, String email, String gender, String product_code,
                      Double wallet, String shop_name, String shop_image, String shop_description, String shop_id,
                      String shop_status, String eklanku_id, String shop_province, String shop_regency) {
        this.id               = id;
        this.name             = name;
        this.phone            = phone;
        this.email            = email;
        this.gender           = gender;
        this.product_code     = product_code;
        this.wallet           = wallet;
        this.shop_name        = shop_name;
        this.shop_image       = shop_image;
        this.shop_description = shop_description;
        this.shop_id          = shop_id;
        this.shop_status      = shop_status;
        this.eklanku_id       = eklanku_id;
        this.shop_province    = shop_province;
        this.shop_regency     = shop_regency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProductCode() {
        return product_code;
    }

    public void setProductCode(String product_code) {
        this.product_code = product_code;
    }

    public Double getWallet() {
        return wallet;
    }

    public void setWallet(Double wallet) {
        this.wallet = wallet;
    }

    public String getShopName() {
        return shop_name;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShopImage() {
        return shop_image;
    }

    public void setShopImage(String shop_image) {
        this.shop_image = shop_image;
    }

    public String getShopDescription() {
        return shop_description;
    }

    public void setShopDescription(String shop_description) {
        this.shop_description = shop_description;
    }

    public String getShopId() {
        return shop_id;
    }

    public void setShopId(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getShopStatus() {
        return shop_status;
    }

    public void setShopStatus(String shop_status) {
        this.shop_status = shop_status;
    }

    public String getEklankuId() {
        return eklanku_id;
    }

    public void setEklankuId(String eklanku_id) {
        this.eklanku_id= eklanku_id;
    }

    public String getShopProvince() {
        return shop_province;
    }

    public void setShopProvince(String shop_province) {
        this.shop_province = shop_province;
    }

    public String getShopRegency() {
        return shop_regency;
    }

    public void setShopRegency(String shop_regency) {
        this.shop_regency = shop_regency;
    }
}
