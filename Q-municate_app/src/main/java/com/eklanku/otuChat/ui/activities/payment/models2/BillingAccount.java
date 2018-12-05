package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class BillingAccount {
    @SerializedName("id")
    private String id;

    @SerializedName("bill_name")
    private String bill_name;

    @SerializedName("bill_city")
    private String bill_city;

    @SerializedName("bill_logo")
    private String bill_logo;

    @SerializedName("bill_number")
    private String bill_number;

    @SerializedName("bill_account")
    private String bill_account;

    @SerializedName("bill_confirm1")
    private String bill_confirm1;

    @SerializedName("bill_confirm2")
    private String bill_confirm2;

    public BillingAccount() {}

    public BillingAccount(String id, String bill_name, String bill_city, String bill_logo, String bill_number,
                          String bill_account, String bill_confirm1, String bill_confirm2) {
        this.id            = id;
        this.bill_name     = bill_name;
        this.bill_city     = bill_city;
        this.bill_logo     = bill_logo;
        this.bill_number   = bill_number;
        this.bill_account  = bill_account;
        this.bill_confirm1 = bill_confirm1;
        this.bill_confirm2 = bill_confirm2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillName() {
        return bill_name;
    }

    public void setBillName(String bill_name) {
        this.bill_name = bill_name;
    }

    public String getBillCity() {
        return bill_city;
    }

    public void setBillCity(String bill_city) {
        this.bill_city = bill_city;
    }

    public String getBillLogo() {
        return bill_logo;
    }

    public void setBillLogo(String bill_logo) {
        this.bill_logo = bill_logo;
    }

    public String getBillNumber() {
        return bill_number;
    }

    public void setBillNumber(String bill_number) {
        this.bill_number = bill_number;
    }

    public String getBillAccount() {
        return bill_account;
    }

    public void setBillAccount(String bill_account) {
        this.bill_account = bill_account;
    }

    public String getBillConfirm1() {
        return bill_confirm1;
    }

    public void setBillConfirm1(String bill_confirm1) {
        this.bill_confirm1 = bill_confirm1;
    }

    public String getBillConfirm2() {
        return bill_confirm2;
    }

    public void setBillConfirm2(String bill_confirm2) {
        this.bill_confirm2 = bill_confirm2;
    }
}
