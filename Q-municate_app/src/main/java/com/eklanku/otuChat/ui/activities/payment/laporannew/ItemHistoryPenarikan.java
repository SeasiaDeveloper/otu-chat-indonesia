package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryPenarikan {

    private String tgl_penarikan;
    private String status_penarikan;
    private String jml_penarikan;
    private String bank;

    public String getTgl_penarikan() {
        return tgl_penarikan;
    }

    public void setTgl_penarikan(String tgl_penarikan) {
        this.tgl_penarikan = tgl_penarikan;
    }

    public String getStatus_penarikan() {
        return status_penarikan;
    }

    public void setStatus_penarikan(String status_penarikan) {
        this.status_penarikan = status_penarikan;
    }

    public String getJml_penarikan() {
        return jml_penarikan;
    }

    public void setJml_penarikan(String jml_penarikan) {
        this.jml_penarikan = jml_penarikan;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAtas_nama() {
        return atas_nama;
    }

    public void setAtas_nama(String atas_nama) {
        this.atas_nama = atas_nama;
    }

    public String getNomer_rekening() {
        return nomer_rekening;
    }

    public void setNomer_rekening(String nomer_rekening) {
        this.nomer_rekening = nomer_rekening;
    }

    private String atas_nama;
    private String nomer_rekening;

    public ItemHistoryPenarikan(String tgl_penarikan, String status_penarikan, String jml_penarikan, String bank, String atas_nama, String nomer_rekening) {
        this.tgl_penarikan = tgl_penarikan;
        this.status_penarikan = status_penarikan;
        this.jml_penarikan = jml_penarikan;
        this.bank = bank;
        this.atas_nama = atas_nama;
        this.nomer_rekening = nomer_rekening;
    }
}
