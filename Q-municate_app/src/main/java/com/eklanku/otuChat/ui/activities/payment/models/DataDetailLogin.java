package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataDetailLogin {

    public String getMbr_token() {
        return mbr_token;
    }

    public void setMbr_token(String mbr_token) {
        this.mbr_token = mbr_token;
    }



    @SerializedName("mbr_token")
    private String mbr_token;



}
