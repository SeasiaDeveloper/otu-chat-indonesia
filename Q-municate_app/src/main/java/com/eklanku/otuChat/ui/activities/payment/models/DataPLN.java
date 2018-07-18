package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataPLN {
    private String providers;

    public DataPLN() {
    }

    public DataPLN(String providers) {
        this.providers = providers;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(String providers) {
        this.providers = providers;
    }
}

