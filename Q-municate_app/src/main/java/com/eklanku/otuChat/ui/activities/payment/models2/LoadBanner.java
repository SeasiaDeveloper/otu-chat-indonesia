package com.eklanku.otuChat.ui.activities.payment.models2;

import com.eklanku.otuChat.ui.activities.payment.models.DataBanner;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadBanner {

    @SerializedName("errNumber")
    private String errNumber;

    @SerializedName("userID")
    private String userID;

    @SerializedName("aplUse")
    private String aplUse;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("status")
    private String status;

    @SerializedName("respMessage")
    private List<DataBanner> respMessage;

    public String getErrNumber() {
        return errNumber;
    }

    public void setErrNumber(String errNumber) {
        this.errNumber = errNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAplUse() {
        return aplUse;
    }

    public void setAplUse(String aplUse) {
        this.aplUse = aplUse;
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


    public List<DataBanner> getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(List<DataBanner> respMessage) {
        this.respMessage = respMessage;
    }


}
