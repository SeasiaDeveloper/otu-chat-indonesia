package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataDetailProfile {

    //get data profile
    @SerializedName("errNumber")
    private String errNumber;

    @SerializedName("userID")
    private String userID;

    public List<DataDetailProfile> getData() {
        return data;
    }

    public void setData(List<DataDetailProfile> data) {
        this.data = data;
    }

    @SerializedName("data")
    private List<DataDetailProfile> data;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("status")
    private String status;

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    @SerializedName("respMessage")
    private String respMessage;

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


    @SerializedName("mbr_id")
    private String mbr_id;

    @SerializedName("name_member")
    private String name_member;

    @SerializedName("upline")
    private String upline;

    @SerializedName("mbr_carier")
    private String mbr_carier;

    @SerializedName("email")
    private String email;

    @SerializedName("no_ktp")
    private String no_ktp;

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("kota")
    private String kota;

    @SerializedName("sponsor_name")
    private String sponsor_name;

    @SerializedName("hp_sponsor")
    private String hp_sponsor;

    public String getMbr_id() {
        return mbr_id;
    }

    public void setMbr_id(String mbr_id) {
        this.mbr_id = mbr_id;
    }

    public String getName_member() {
        return name_member;
    }

    public void setName_member(String name_member) {
        this.name_member = name_member;
    }

    public String getUpline() {
        return upline;
    }

    public void setUpline(String upline) {
        this.upline = upline;
    }

    public String getMbr_carier() {
        return mbr_carier;
    }

    public void setMbr_carier(String mbr_carier) {
        this.mbr_carier = mbr_carier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(String no_ktp) {
        this.no_ktp = no_ktp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getSponsor_name() {
        return sponsor_name;
    }

    public void setSponsor_name(String sponsor_name) {
        this.sponsor_name = sponsor_name;
    }

    public String getHp_sponsor() {
        return hp_sponsor;
    }

    public void setHp_sponsor(String hp_sponsor) {
        this.hp_sponsor = hp_sponsor;
    }

    public String getJml_bonus() {
        return jml_bonus;
    }

    public void setJml_bonus(String jml_bonus) {
        this.jml_bonus = jml_bonus;
    }

    @SerializedName("jml_bonus")
    private String jml_bonus;






}
