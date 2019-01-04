package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryTrx {

    private String trxKode, trxTanggal, trxStatus, trxNominal, trxJenis, trxInvoice;
    private  String trxMbrId;
    private String trxTujuan;
    private String trxKet;
    private String trxVsn;
    private String trxMbrName;
    private String trxTglSukses;

    public String getTrxProvide_name() {
        return trxProvide_name;
    }

    public void setTrxProvide_name(String trxProvide_name) {
        this.trxProvide_name = trxProvide_name;
    }

    private String trxProvide_name;

    public ItemHistoryTrx(String trxKode, String trxTanggal, String trxStatus, String trxNominal, String trxJenis, String trxInvoice,
                          String trxMbrId, String trxTujuan, String trxKet, String trxVsn, String trxMbrName, String trxTglSukses, String trxProvide_name) {

        this.trxKode = trxKode;
        this.trxTanggal = trxTanggal;
        this.trxStatus = trxStatus;
        this.trxNominal = trxNominal;
        this.trxJenis = trxJenis;
        this.trxInvoice = trxInvoice;

        this.trxMbrId = trxMbrId;
        this.trxTujuan = trxTujuan;
        this.trxKet = trxKet;
        this.trxVsn = trxVsn;
        this.trxMbrName = trxMbrName;
        this.trxTglSukses = trxTglSukses;
        this.trxProvide_name = trxProvide_name;

    }

    public String getTrxMbrId() {
        return trxMbrId;
    }

    public void setTrxMbrId(String trxMbrId) {
        this.trxMbrId = trxMbrId;
    }

    public String getTrxTujuan() {
        return trxTujuan;
    }

    public void setTrxTujuan(String trxTujuan) {
        this.trxTujuan = trxTujuan;
    }

    public String getTrxKet() {
        return trxKet;
    }

    public void setTrxKet(String trxKet) {
        this.trxKet = trxKet;
    }

    public String getTrxVsn() {
        return trxVsn;
    }

    public void setTrxVsn(String trxVsn) {
        this.trxVsn = trxVsn;
    }

    public String getTrxMbrName() {
        return trxMbrName;
    }

    public void setTrxMbrName(String trxMbrName) {
        this.trxMbrName = trxMbrName;
    }

    public String getTrxTglSukses() {
        return trxTglSukses;
    }

    public void setTrxTglSukses(String trxTglSukses) {
        this.trxTglSukses = trxTglSukses;
    }

    public String getTrxInvoice() {
        return trxInvoice;
    }

    public void setTrxInvoice(String trxInvoice) {
        this.trxInvoice = trxInvoice;
    }

    public String getTrxKode() {
        return trxKode;
    }

    public void setTrxKode(String trxKode) {
        this.trxKode = trxKode;
    }

    public String getTrxTanggal() {
        return trxTanggal;
    }

    public void setTrxTanggal(String trxTanggal) {
        this.trxTanggal = trxTanggal;
    }

    public String getTrxStatus() {
        return trxStatus;
    }

    public void setTrxStatus(String trxStatus) {
        this.trxStatus = trxStatus;
    }

    public String getTrxNominal() {
        return trxNominal;
    }

    public void setTrxNominal(String trxNominal) {
        this.trxNominal = trxNominal;
    }

    public String getTrxJenis() {
        return trxJenis;
    }

    public void setTrxJenis(String trxJenis) {
        this.trxJenis = trxJenis;
    }
}
