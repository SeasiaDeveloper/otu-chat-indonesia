package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.util.Log;

public class ItemHistorySaldo {

    private String mutasi_id;
    private String tgl_mutasi;
    private String mutasi_status;

    public ItemHistorySaldo(String mutasi_id, String tgl_mutasi, String mutasi_status, String sisa_saldo) {
        this.mutasi_id = mutasi_id;
        this.tgl_mutasi = tgl_mutasi;
        this.mutasi_status = mutasi_status;
        this.sisa_saldo = sisa_saldo;
    }

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

    private String sisa_saldo;
}
