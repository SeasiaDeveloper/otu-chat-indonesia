package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kingazis on 13/03/2018.
 */

public class DataMutasiSaldo {
    @SerializedName("uang_id")
    private int uang_id;
    @SerializedName("mbr_code")
    private String mbr_code;
    @SerializedName("uang_code")
    private String uang_code;
    @SerializedName("uang_amount")
    private Double uang_amount;
    @SerializedName("uang_masuk")
    private Double uang_masuk;
    @SerializedName("uang_keluar")
    private Double uang_keluar;
    @SerializedName("uang_desc")
    private String uang_desc;
    @SerializedName("uang_date")
    private String uang_date;
    @SerializedName("uang_status")
    private String uang_status;
    @SerializedName("reverenci_code")
    private String reverenci_code;

    public int getUang_id() {
        return this.uang_id;
    }
    public void setUang_id(int uang_id) {
        this.uang_id = uang_id;
    }
    public String getMbr_code() {
        return this.mbr_code;
    }
    public void setMbr_code(String mbr_code) {
        this.mbr_code = mbr_code;
    }
    public String getUang_code() {
        return this.uang_code;
    }
    public void setUang_code(String uang_code) {
        this.uang_code = uang_code;
    }

    public Double getUang_amount() {
        return this.uang_amount;
    }
    public void setUang_amount(Double uang_amount) {
        this.uang_amount = uang_amount;
    }
    public Double getUang_masuk() {
        return this.uang_masuk;
    }
    public void setUang_masuk(Double uang_masuk) {
        this.uang_masuk = uang_masuk;
    }
    public Double getUang_keluar() {
        return this.uang_keluar;
    }
    public void setUang_keluar(Double uang_keluar) {
        this.uang_keluar = uang_keluar;
    }

    public String getUang_desc() {
        return this.uang_desc;
    }
    public void setUang_desc(String uang_desc) {
        this.uang_desc = uang_desc;
    }
    public String getUang_date() {
        return this.uang_date;
    }
    public void setUang_date(String uang_date) {
        this.uang_date = uang_date;
    }
    public String getUang_status() {
        return this.uang_status;
    }
    public void setUang_status(String uang_status) {
        this.uang_status = uang_status;
    }
    public String getReverenci_code() {
        return this.reverenci_code;
    }
    public void setReverenci_code(String reverenci_code) {
        this.reverenci_code = reverenci_code;
    }

}
