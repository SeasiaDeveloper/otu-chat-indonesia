package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryDeposit {

    public ItemHistoryDeposit(String tanggal, String jmldeposit, String status, String bank) {
        this.tanggal = tanggal;
        this.jmldeposit = jmldeposit;
        this.status = status;
        this.bank = bank;
    }

    private String tanggal;
    private String jmldeposit;

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJmldeposit() {
        return jmldeposit;
    }

    public void setJmldeposit(String jmldeposit) {
        this.jmldeposit = jmldeposit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    private String status;
    private String bank;


}
