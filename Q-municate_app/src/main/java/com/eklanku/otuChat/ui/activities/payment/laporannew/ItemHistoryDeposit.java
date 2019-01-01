package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryDeposit {


    private String tgl_deposit;
    private String status_deposit;
    private String codeunix;
    private String total_transfer;
    private String bank;
    private String jumlah_deposit;
    private String nomor_rekening;

    public ItemHistoryDeposit(String tgl_deposit, String status_deposit, String codeunix, String total_transfer,
                              String bank, String jumlah_deposit, String nomor_rekening, String pemilik) {
        this.tgl_deposit = tgl_deposit;
        this.status_deposit = status_deposit;
        this.codeunix = codeunix;
        this.total_transfer = total_transfer;
        this.bank = bank;
        this.jumlah_deposit = jumlah_deposit;
        this.nomor_rekening = nomor_rekening;
        this.pemilik = pemilik;
    }

    public String getNomor_rekening() {
        return nomor_rekening;
    }

    public void setNomor_rekening(String nomor_rekening) {
        this.nomor_rekening = nomor_rekening;
    }

    public String getPemilik() {
        return pemilik;
    }

    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }

    private String pemilik;

    public String getTgl_deposit() {
        return tgl_deposit;
    }

    public void setTgl_deposit(String tgl_deposit) {
        this.tgl_deposit = tgl_deposit;
    }

    public String getStatus_deposit() {
        return status_deposit;
    }

    public void setStatus_deposit(String status_deposit) {
        this.status_deposit = status_deposit;
    }

    public String getCodeunix() {
        return codeunix;
    }

    public void setCodeunix(String codeunix) {
        this.codeunix = codeunix;
    }

    public String getTotal_transfer() {
        return total_transfer;
    }

    public void setTotal_transfer(String total_transfer) {
        this.total_transfer = total_transfer;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getJumlah_deposit() {
        return jumlah_deposit;
    }

    public void setJumlah_deposit(String Jumlah_deposit) {
        this.jumlah_deposit = Jumlah_deposit;
    }


}
