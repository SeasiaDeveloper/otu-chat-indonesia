package com.eklanku.otuChat.ui.activities.payment.models2;

import com.eklanku.otuChat.ui.activities.payment.models2.DataDetailCekMemberTransfer;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataCekMemberTransfer {

    @SerializedName("id_member")
    private String id_member;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("respTime")
    private String respTime;

    public String getId_member() {
        return id_member;
    }

    public void setId_member(String id_member) {
        this.id_member = id_member;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRespTime() {
        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    @SerializedName("status")
    private String status;

    @SerializedName("respMessage")
    private String respMessage;

    public List<DataDetailCekMemberTransfer> getDatarespon() {
        return datarespon;
    }

    public void setDatarespon(List<DataDetailCekMemberTransfer> datarespon) {
        this.datarespon = datarespon;
    }

    @SerializedName("datarespon")
    private List<DataDetailCekMemberTransfer> datarespon;

}
