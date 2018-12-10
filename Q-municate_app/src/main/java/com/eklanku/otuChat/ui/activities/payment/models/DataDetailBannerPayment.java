package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataDetailBannerPayment {

    @SerializedName("nama_promo")
    String nama_promo;

    @SerializedName("baner_promo")
    String banner_promo;

    public String getNama_promo() {
        return nama_promo;
    }

    public void setNama_promo(String nama_promo) {
        this.nama_promo = nama_promo;
    }

    public String getBanner_promo() {
        return banner_promo;
    }

    public void setBanner_promo(String banner_promo) {
        this.banner_promo = banner_promo;
    }

    public String getLink_banner() {
        return link_banner;
    }

    public void setLink_banner(String link_banner) {
        this.link_banner = link_banner;
    }

    @SerializedName("link_banner")
    String link_banner;

}
