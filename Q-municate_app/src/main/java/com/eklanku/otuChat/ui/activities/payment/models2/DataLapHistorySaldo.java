package com.eklanku.otuChat.ui.activities.payment.models2;

import com.eklanku.otuChat.ui.activities.payment.models.DataMutasiSaldo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataLapHistorySaldo {
    @SerializedName("data_bnbonus")
    private String data_bnbonus;

    @SerializedName("data_cashbc")
    private String data_cashbc;

    @SerializedName("data_kuota")
    private String data_kuota;

    @SerializedName("data_saldo")
    private String data_saldo;

    @SerializedName("data_transaksi")
    private List<DataMutasiSaldo> data_transaksi;

    public List<DataMutasiSaldo> getDataTransaksi() {
        return data_transaksi;
    }

    public void setDataTransaksi(List<DataMutasiSaldo> data) {
        this.data_transaksi = data;
    }

    public String getBonus() {
        return data_bnbonus;
    }

    public void setBonus(String transaksi_id) {
        this.data_bnbonus = transaksi_id;
    }

    public String getCashback() {
        return data_cashbc;
    }

    public void setCashback(String transaksi_code) {
        this.data_cashbc = transaksi_code;
    }

    public String getKuota() {
        return data_kuota;
    }

    public void setKuota(String transaksi_amount) {
        this.data_kuota = transaksi_amount;
    }

    public String getSaldo() {
        return data_saldo;
    }

    public void setSaldo(String transaksi_status) {
        this.data_saldo = transaksi_status;
    }
}
