package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistorySaldo {

    private String mutasi_id;
    private String tgl_mutasi;
    private String mutasi_status;
    private String sisa_saldo;
    private String uang_masuk;

    public String getMutasi_id() {
        return mutasi_id;
    }

    public void setMutasi_id(String mutasi_id) {
        this.mutasi_id = mutasi_id;
    }

    public String getTgl_mutasi() {
        return tgl_mutasi;
    }

    public void setTgl_mutasi(String tgl_mutasi) {
        this.tgl_mutasi = tgl_mutasi;
    }

    public String getMutasi_status() {
        return mutasi_status;
    }

    public void setMutasi_status(String mutasi_status) {
        this.mutasi_status = mutasi_status;
    }

    public String getSisa_saldo() {
        return sisa_saldo;
    }

    public void setSisa_saldo(String sisa_saldo) {
        this.sisa_saldo = sisa_saldo;
    }

    public String getUang_masuk() {
        return uang_masuk;
    }

    public void setUang_masuk(String uang_masuk) {
        this.uang_masuk = uang_masuk;
    }

    public String getUang_keluar() {
        return uang_keluar;
    }

    public void setUang_keluar(String uang_keluar) {
        this.uang_keluar = uang_keluar;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    private String uang_keluar;
    private String keterangan;

    public ItemHistorySaldo(String mutasi_id, String tgl_mutasi, String mutasi_status,
                            String sisa_saldo, String uang_masuk, String uang_keluar, String keterangan) {
        this.mutasi_id = mutasi_id;
        this.tgl_mutasi = tgl_mutasi;
        this.mutasi_status = mutasi_status;
        this.sisa_saldo = sisa_saldo;
        this.uang_masuk = uang_masuk;
        this.uang_keluar = uang_keluar;
        this.keterangan = keterangan;
    }



}
