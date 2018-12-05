package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataDetailSaldoBonus {

    @SerializedName("id_member")
    private String id_member;

    public String getId_member() {
        return id_member;
    }

    public void setId_member(String id_member) {
        this.id_member = id_member;
    }

    public String getSisa_uang() {
        return sisa_uang;
    }

    public void setSisa_uang(String sisa_uang) {
        this.sisa_uang = sisa_uang;
    }

    public String getCarier_member() {
        return carier_member;
    }

    public void setCarier_member(String carier_member) {
        this.carier_member = carier_member;
    }

    public String getBonus_member() {
        return bonus_member;
    }

    public void setBonus_member(String bonus_member) {
        this.bonus_member = bonus_member;
    }

    @SerializedName("sisa_uang")
    private String sisa_uang;

    @SerializedName("carier_member")
    private String carier_member;

    @SerializedName("bonus_member")
    private String bonus_member;

}
