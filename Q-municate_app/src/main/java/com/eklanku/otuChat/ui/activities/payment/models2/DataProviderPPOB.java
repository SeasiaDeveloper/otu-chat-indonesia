package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataProviderPPOB {

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    public DataProviderPPOB() {
    }

    public DataProviderPPOB(String name_provaider) {
        this.name = name_provaider;

    }

    public String getName_provaider() {
        return name;
    }

    public void setName_provaider(String name_provaider) {
        this.name = name_provaider;
    }

}
