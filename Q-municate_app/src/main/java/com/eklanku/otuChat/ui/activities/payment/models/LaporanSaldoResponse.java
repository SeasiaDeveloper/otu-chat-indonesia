package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LaporanSaldoResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("data")
    private List<DataLapHistorySaldo> data;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<DataLapHistorySaldo> getData() {
        return this.data;
    }

    public void setData(List<DataLapHistorySaldo> data) {
        this.data = data;
    }

}
