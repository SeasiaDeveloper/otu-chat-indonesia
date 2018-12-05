package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class PaketTopup {
    @SerializedName("id")
    private String id;

    @SerializedName("nama_paket")
    private String nama_paket;

    @SerializedName("harga_paket")
    private String harga_paket;

    @SerializedName("status")
    private String status;

    public PaketTopup() {}

    public PaketTopup(String id, String nama_paket, String harga_paket, String status) {
        this.id          = id;
        this.nama_paket  = nama_paket;
        this.harga_paket = harga_paket;
        this.status      = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaPaket() {
        return nama_paket;
    }

    public void setNamaPaket(String nama_paket) {
        this.nama_paket = nama_paket;
    }

    public String getHargaPaket() {
        return harga_paket;
    }

    public void setHargaPaket(String harga_paket) {
        this.harga_paket = harga_paket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
