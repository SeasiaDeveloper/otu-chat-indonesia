package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kingazis on 13/03/2018.
 */

public class DataLapHistoryTrx {
    @SerializedName("id_member")
    private String id_member;
    @SerializedName("invoice")
    private String invoice;//tgl	vstatus	harga	tujuan	keterangan	vsn	mbr_name	tgl_sukses
    @SerializedName("tgl")
    private String tgl;
    @SerializedName("vstatus")
    private String vstatus;
    @SerializedName("harga")
    private Double harga;
    @SerializedName("tujuan")
    private String tujuan;
    @SerializedName("keterangan")
    private String keterangan;
    @SerializedName("vsn")
    private String vsn;
    @SerializedName("mbr_name")
    private String mbr_name;
    @SerializedName("tgl_sukses")
    private String tgl_sukses;

    public String getMember_id() {
        return this.id_member;
    }
    public void setMember_id(String id_member) {
        this.id_member = id_member;
    }
    public String getInvoice() {
        return this.invoice;
    }
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
    public String getTgl() {
        return this.tgl;
    }
    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getStatus() {
        return this.vstatus;
    }
    public void setStatus(String vstatus) {
        this.vstatus = vstatus;
    }
    public Double getHarga() {
        return this.harga;
    }
    public void setHarga(Double harga) {
        this.harga = harga;
    }
    public String getTujuan() {
        return this.tujuan;
    }
    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getKeterangan() {
        return this.keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    public String getVsn() {
        return this.vsn;
    }
    public void setVsn(String vsn) {
        this.vsn = vsn;
    }
    public String getMbr_name() {
        return this.mbr_name;
    }
    public void setMbr_name(String mbr_name) {
        this.mbr_name = mbr_name;
    }
    public String getTgl_sukses() {
        return this.tgl_sukses;
    }
    public void setTgl_sukses(String tgl_sukses) {
        this.tgl_sukses = tgl_sukses;
    }

}
