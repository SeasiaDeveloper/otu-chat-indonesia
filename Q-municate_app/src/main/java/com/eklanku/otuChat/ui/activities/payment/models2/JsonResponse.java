package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class JsonResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("result")
    private String datajson;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getJsonString() {
        return datajson;
    }

    public void setJsonString(String datajson) {
        this.datajson = datajson;
    }
}
