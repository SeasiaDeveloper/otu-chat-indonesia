package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataKota {
    @SerializedName("errNumber")
    String errNumber;

    @SerializedName("userID")
    String userID;

    @SerializedName("data")
    ArrayList<DataKota> data;

    @SerializedName("id_kota")
    String id_kota;

    @SerializedName("kota")
    String kota;

    @SerializedName("provinsi")
    String provinsi;

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

    public ArrayList<DataKota> getData() {
        return data;
    }

    public void setData(ArrayList<DataKota> data) {
        this.data = data;
    }

    public String getId_kota() {
        return id_kota;
    }

    public void setId_kota(String id_kota) {
        this.id_kota = id_kota;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
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

    @SerializedName("respTime")
    String respTime;

    @SerializedName("status")
    String status;

    @SerializedName("respMessage")
    String respMessage;
}
