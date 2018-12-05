package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataProvider {
    @SerializedName("name_provaider")
    private String name_provaider;

    public DataProvider() {
    }

    public DataProvider(String name_provaider) {
        this.name_provaider = name_provaider;

    }

    public String getName_provaider() {
        return name_provaider;
    }

    public void setName_provaider(String name_provaider) {
        this.name_provaider = name_provaider;
    }

}
