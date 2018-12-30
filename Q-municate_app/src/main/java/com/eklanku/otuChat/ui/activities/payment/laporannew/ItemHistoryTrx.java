package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryTrx {

    private String trxKode, trxTanggal, trxStatus, trxNominal, trxJenis, trxInvoice;

    public ItemHistoryTrx(String trxKode, String trxTanggal, String trxStatus, String trxNominal, String trxJenis, String trxInvoice) {

        this.trxKode = trxKode;
        this.trxTanggal = trxTanggal;
        this.trxStatus = trxStatus;
        this.trxNominal = trxNominal;
        this.trxJenis = trxJenis;
        this.trxInvoice = trxInvoice;
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
