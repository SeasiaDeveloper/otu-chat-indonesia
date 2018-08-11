package com.eklanku.otuChat.ui.activities.contacts;

public class ContactsModelGroup {

    private String id;
    private int id_user;
    private String fullName;
    private String login;
    private String phone = "";
    private String isReg_type;

    public ContactsModelGroup(String login, String fullName, String isReg_type) {
        this.login = login;
        this.fullName = fullName;
        this.isReg_type = isReg_type;
        this.phone = login;
    }

    public ContactsModelGroup(String login, String fullName, String isReg_type, int id_user) {
        this.login = login;
        this.fullName = fullName;
        this.isReg_type = isReg_type;
        this.id_user = id_user;
        this.phone = login;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getLogin() {
        return login;
    }

    public String getIsReg_type() {
        return isReg_type;
    }

    public void setIsReg_type(String isReg_type) {
        this.isReg_type = isReg_type;
    }

    public String getPhone() {
        return phone;
    }
}
