package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopupDetailResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("data")
    private List<TopupDetailM> topupDetails;

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

    public List<TopupDetailM> getTopupDetailM() {
        return topupDetails;
    }

    public void setTopupDetailM(List<TopupDetailM> topupDetails) {
        this.topupDetails = topupDetails;
    }


    //GET Data Bank





}
