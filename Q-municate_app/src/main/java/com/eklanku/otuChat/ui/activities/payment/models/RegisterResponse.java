package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
