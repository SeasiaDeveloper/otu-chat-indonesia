package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataBanner {

    public String getNama_promo() {
        return nama_promo;
    }

    public void setNama_promo(String nama_promo) {
        this.nama_promo = nama_promo;
    }

    public String getBaner_promo() {
        return baner_promo;
    }

    public void setBaner_promo(String baner_promo) {
        this.baner_promo = baner_promo;
    }

    @SerializedName("nama_promo")
    private String nama_promo;

    @SerializedName("baner_promo")
    private String baner_promo;
}
