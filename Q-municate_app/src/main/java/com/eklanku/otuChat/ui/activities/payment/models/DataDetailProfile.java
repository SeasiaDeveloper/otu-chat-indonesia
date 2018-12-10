package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataDetailProfile {

    @SerializedName("o_id_sponsor")
    String o_id_sponsor;

    @SerializedName("o_nama_sponsor")
    String o_nama_sponsor;

    @SerializedName("o_nama_member")
    String o_nama_member;

    @SerializedName("o_hp")
    String o_hp;

    @SerializedName("o_mail")
    String o_mail;

    @SerializedName("o_alamat")
    String o_alamat;

    @SerializedName("o_kota")
    String o_kota;

    @SerializedName("o_tgl_lahir")
    String o_tgl_lahir;

    @SerializedName("o_tgl_daftar")
    String o_tgl_daftar;

    @SerializedName("o_bank")
    String o_bank;

    @SerializedName("o_norec")
    String o_norec;

    @SerializedName("o_pemilikrekening")
    String o_pemilikrekening;

    @SerializedName("o_jabatan_sponsor")
    String o_jabatan_sponsor;

    @SerializedName("o_jabatanmember")
    String o_jabatanmember;

    public String getO_id_sponsor() {
        return o_id_sponsor;
    }

    public void setO_id_sponsor(String o_id_sponsor) {
        this.o_id_sponsor = o_id_sponsor;
    }

    public String getO_nama_sponsor() {
        return o_nama_sponsor;
    }

    public void setO_nama_sponsor(String o_nama_sponsor) {
        this.o_nama_sponsor = o_nama_sponsor;
    }

    public String getO_nama_member() {
        return o_nama_member;
    }

    public void setO_nama_member(String o_nama_member) {
        this.o_nama_member = o_nama_member;
    }

    public String getO_hp() {
        return o_hp;
    }

    public void setO_hp(String o_hp) {
        this.o_hp = o_hp;
    }

    public String getO_mail() {
        return o_mail;
    }

    public void setO_mail(String o_mail) {
        this.o_mail = o_mail;
    }

    public String getO_alamat() {
        return o_alamat;
    }

    public void setO_alamat(String o_alamat) {
        this.o_alamat = o_alamat;
    }

    public String getO_kota() {
        return o_kota;
    }

    public void setO_kota(String o_kota) {
        this.o_kota = o_kota;
    }

    public String getO_tgl_lahir() {
        return o_tgl_lahir;
    }

    public void setO_tgl_lahir(String o_tgl_lahir) {
        this.o_tgl_lahir = o_tgl_lahir;
    }

    public String getO_tgl_daftar() {
        return o_tgl_daftar;
    }

    public void setO_tgl_daftar(String o_tgl_daftar) {
        this.o_tgl_daftar = o_tgl_daftar;
    }

    public String getO_bank() {
        return o_bank;
    }

    public void setO_bank(String o_bank) {
        this.o_bank = o_bank;
    }

    public String getO_norec() {
        return o_norec;
    }

    public void setO_norec(String o_norec) {
        this.o_norec = o_norec;
    }

    public String getO_pemilikrekening() {
        return o_pemilikrekening;
    }

    public void setO_pemilikrekening(String o_pemilikrekening) {
        this.o_pemilikrekening = o_pemilikrekening;
    }

    public String getO_jabatan_sponsor() {
        return o_jabatan_sponsor;
    }

    public void setO_jabatan_sponsor(String o_jabatan_sponsor) {
        this.o_jabatan_sponsor = o_jabatan_sponsor;
    }

    public String getO_jabatanmember() {
        return o_jabatanmember;
    }

    public void setO_jabatanmember(String o_jabatanmember) {
        this.o_jabatanmember = o_jabatanmember;
    }

    public String getO_hp_sponsor() {
        return o_hp_sponsor;
    }

    public void setO_hp_sponsor(String o_hp_sponsor) {
        this.o_hp_sponsor = o_hp_sponsor;
    }

    public String getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(String no_ktp) {
        this.no_ktp = no_ktp;
    }

    @SerializedName("o_hp_sponsor")
    String o_hp_sponsor;

    @SerializedName("no_ktp")
    String no_ktp;


    @SerializedName("errNumber")
    String errNumber;

    @SerializedName("userID")
    String userID;

    @SerializedName("data")
    List<DataDetailProfile> data;

    @SerializedName("respTime")
    String respTime;

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

    public List<DataDetailProfile> getData() {
        return data;
    }

    public void setData(List<DataDetailProfile> data) {
        this.data = data;
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

    @SerializedName("status")
    String status;

    @SerializedName("respMessage")
    String respMessage;

}
