package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataDetailCekMemberTransfer {

    @SerializedName("nama_member")
    private String nama_member;

    public String getNama_member() {
        return nama_member;
    }

    public void setNama_member(String nama_member) {
        this.nama_member = nama_member;
    }

    public String getId_member() {
        return id_member;
    }

    public void setId_member(String id_member) {
        this.id_member = id_member;
    }

    public String getStatus_member() {
        return status_member;
    }

    public void setStatus_member(String status_member) {
        this.status_member = status_member;
    }

    @SerializedName("id_member")
    private String id_member;

    @SerializedName("status_member")
    private String status_member;
}
