package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class TopupDetailM {
    @SerializedName("id")
    private String id;

    @SerializedName("eklanpay_id")
    private String eklanpay_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("payment_status")
    private String payment_status;

    @SerializedName("invoice_code")
    private String invoice_code;

    @SerializedName("invoice_date")
    private String invoice_date;

    @SerializedName("status")
    private String status;

    @SerializedName("nominal")
    private String nominal;

    @SerializedName("key_transfer")
    private String key_transfer;

    public TopupDetailM() {}

    public TopupDetailM(String id, String eklanpay_id, String user_id, String payment_status, String invoice_code,
                        String invoice_date, String status, String nominal, String key_transfer) {
        this.id             = id;
        this.eklanpay_id    = eklanpay_id;
        this.user_id        = user_id;
        this.payment_status = payment_status;
        this.invoice_code   = invoice_code;
        this.invoice_date   = invoice_date;
        this.status         = status;
        this.nominal        = nominal;
        this.key_transfer   = key_transfer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEklanpayId() {
        return eklanpay_id;
    }

    public void setEklanpayId(String eklanpay_id) {
        this.eklanpay_id = eklanpay_id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getPaymentStatus() {
        return payment_status;
    }

    public void setPaymentStatus(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getInvoiceCode() {
        return invoice_code;
    }

    public void setInvoiceCode(String invoice_code) {
        this.invoice_code = invoice_code;
    }

    public String getInvoiceDate() {
        return invoice_date;
    }

    public void setInvoiceDate(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getKeyTransfer() {
        return key_transfer;
    }

    public void setKeyTransfer(String key_transfer) {
        this.key_transfer = key_transfer;
    }

    //detail data bank
    @SerializedName("bank")
    private String bank;

    @SerializedName("norec")
    private String norec;

    @SerializedName("anbank")
    private String anbank;

    @SerializedName("isactive")
    private String isactive;

    public String getEklanpay_id() {
        return eklanpay_id;
    }

    public void setEklanpay_id(String eklanpay_id) {
        this.eklanpay_id = eklanpay_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getInvoice_code() {
        return invoice_code;
    }

    public void setInvoice_code(String invoice_code) {
        this.invoice_code = invoice_code;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getKey_transfer() {
        return key_transfer;
    }

    public void setKey_transfer(String key_transfer) {
        this.key_transfer = key_transfer;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getNorec() {
        return norec;
    }

    public void setNorec(String norec) {
        this.norec = norec;
    }

    public String getAnbank() {
        return anbank;
    }

    public void setAnbank(String anbank) {
        this.anbank = anbank;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }


}
