package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataListPPOB {

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("isactive")
    private String isactive;

    @SerializedName("group")
    private String group;

    public DataListPPOB(String code, String name, String isactive, String group) {
        this.code = code;
        this.name = name;
        this.isactive = isactive;
        this.group = group;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


}
