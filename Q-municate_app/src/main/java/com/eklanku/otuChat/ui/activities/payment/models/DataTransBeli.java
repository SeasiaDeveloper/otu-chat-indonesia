package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataTransBeli {
    @SerializedName("transaksi")
    private String transaksi;

    @SerializedName("harga")
    private Double harga;

    @SerializedName("jenis")
    private String jenis;

    @SerializedName("nohp")
    private String nohp;

    @SerializedName("id_pel")
    private String id_pel;

    @SerializedName("pin")
    private String pin;

    @SerializedName("cmd_save")
    private String cmd_save;

    public DataTransBeli() {}

    public DataTransBeli(String transaksi, Double harga, String jenis,
                         String nohp, String id_pel, String pin, String cmd_save) {
        this.transaksi = transaksi;
        this.harga     = harga;
        this.jenis     = jenis;
        this.nohp     = nohp;
        this.id_pel    = id_pel;
        this.pin       = pin;
        this.cmd_save  = cmd_save;
    }

    public String getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(String transaksi) {
        this.transaksi = transaksi;
    }

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getnoHpp() {
        return nohp;
    }

    public void setnoHp(String jenis) {
        this.nohp = nohp;
    }

    public String getIdPel() {
        return id_pel;
    }

    public void setIdPel(String id_pel) {
        this.id_pel = id_pel;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCmdSave() {
        return cmd_save;
    }

    public void setCmdSave(String cmd_save) {
        this.cmd_save= cmd_save;
    }
}
