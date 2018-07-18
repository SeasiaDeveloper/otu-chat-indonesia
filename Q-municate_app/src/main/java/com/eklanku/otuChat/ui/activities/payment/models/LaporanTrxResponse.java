package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LaporanTrxResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("data")
    private List<DataLapHistoryTrx> data;

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

    public List<DataLapHistoryTrx> getData() {
        return this.data;
    }

    public void setData(List<DataLapHistoryTrx> data) {
        this.data = data;
    }

}
