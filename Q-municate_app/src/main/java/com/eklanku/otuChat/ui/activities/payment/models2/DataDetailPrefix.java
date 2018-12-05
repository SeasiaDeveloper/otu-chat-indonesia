package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataDetailPrefix {

    @SerializedName("provider")
    @Expose
    private String provider;

    @SerializedName("prefix")
    @Expose
    private String prefix;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
