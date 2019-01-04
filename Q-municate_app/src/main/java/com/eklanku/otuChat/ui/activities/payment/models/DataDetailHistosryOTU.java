package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

public class DataDetailHistosryOTU {

    @SerializedName("id_member")
    private String id_member;

    @SerializedName("invoice")
    private String invoice;

    @SerializedName("tgl")
    private String tgl;

    @SerializedName("vstatus")
    private String vstatus;

    @SerializedName("harga")
    private String harga;

    @SerializedName("tujuan")
    private String tujuan;

    @SerializedName("keterangan")
    private String keterangan;

    @SerializedName("vsn")
    private String vsn;

    @SerializedName("mbr_name")
    private String mbr_name;

    @SerializedName("tgl_sukses")
    private String tgl_sukses;

    @SerializedName("product_kode")
    private String product_kode;

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    @SerializedName("provider_name")
    private String provider_name;

    public String getProduct_kode() {
        return product_kode;
    }

    public void setProduct_kode(String product_kode) {
        this.product_kode = product_kode;
    }

    public String getType_product() {
        return type_product;
    }

    public void setType_product(String type_product) {
        this.type_product = type_product;
    }

    @SerializedName("type_product")
    private String type_product;

    public String getId_member() {
        return id_member;
    }

    public void setId_member(String id_member) {
        this.id_member = id_member;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getVstatus() {
        return vstatus;
    }

    public void setVstatus(String vstatus) {
        this.vstatus = vstatus;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getVsn() {
        return vsn;
    }

    public void setVsn(String vsn) {
        this.vsn = vsn;
    }

    public String getMbr_name() {
        return mbr_name;
    }

    public void setMbr_name(String mbr_name) {
        this.mbr_name = mbr_name;
    }

    public String getTgl_sukses() {
        return tgl_sukses;
    }

    public void setTgl_sukses(String tgl_sukses) {
        this.tgl_sukses = tgl_sukses;
    }


    //    2: deposit report
    @SerializedName("jumlah_deposit")
    private String jumlah_deposit;

    @SerializedName("status_deposit")
    private String status_deposit;

    @SerializedName("codeunix")
    private String codeunix;

    @SerializedName("bank")
    private String bank;

    @SerializedName("tgl_deposit")
    private String tgl_deposit;

    public String getNama_pemilik() {
        return nama_pemilik;
    }

    public void setNama_pemilik(String nama_pemilik) {
        this.nama_pemilik = nama_pemilik;
    }

    @SerializedName("nama_pemilik")
    private String nama_pemilik;

    public String getTotal_transfer() {
        return total_transfer;
    }

    public void setTotal_transfer(String total_transfer) {
        this.total_transfer = total_transfer;
    }

    @SerializedName("total_transfer")
    private String total_transfer;

    public String getJumlah_deposit() {
        return jumlah_deposit;
    }

    public void setJumlah_deposit(String jumlah_deposit) {
        this.jumlah_deposit = jumlah_deposit;
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

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getTgl_deposit() {
        return tgl_deposit;
    }

    public void setTgl_deposit(String tgl_deposit) {
        this.tgl_deposit = tgl_deposit;
    }

    //    3: balance statement
    @SerializedName("mutasi_id")
    private String mutasi_id;

    @SerializedName("sisa_saldo")
    private Double sisa_saldo;

    @SerializedName("uang_masuk")
    private Double uang_masuk;

    @SerializedName("uang_keluar")
    private Double uang_keluar;

    @SerializedName("tgl_mutasi")
    private String tgl_mutasi;

    @SerializedName("mutasi_status")
    private String mutasi_status;

    public String getMutasi_id() {
        return mutasi_id;
    }

    public void setMutasi_id(String mutasi_id) {
        this.mutasi_id = mutasi_id;
    }

    public Double getSisa_saldo() {
        return sisa_saldo;
    }

    public void setSisa_saldo(Double sisa_saldo) {
        this.sisa_saldo = sisa_saldo;
    }

    public Double getUang_masuk() {
        return uang_masuk;
    }

    public void setUang_masuk(Double uang_masuk) {
        this.uang_masuk = uang_masuk;
    }

    public Double getUang_keluar() {
        return uang_keluar;
    }

    public void setUang_keluar(Double uang_keluar) {
        this.uang_keluar = uang_keluar;
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

    //4. history penarikan
    @SerializedName("tgl_penarikan")
    private String tgl_penarikan;

    @SerializedName("status_penarikan")
    private String status_penarikan;

    @SerializedName("jml_penarikan")
    private String jml_penarikan;

    @SerializedName("atas_nama")
    private String atas_nama;

    @SerializedName("nomer_rekening")
    private String nomer_rekening;

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

    //5. history detail bonus
    @SerializedName("tgl_perolehan")
    private String tgl_perolehan;

    @SerializedName("jenis_bonus")
    private String jenis_bonus;

    @SerializedName("status_bonus")
    private String status_bonus;

    public String getJml_bonus() {
        return jml_bonus;
    }

    public void setJml_bonus(String jml_bonus) {
        this.jml_bonus = jml_bonus;
    }

    @SerializedName("jml_bonus")
    private String jml_bonus;

    public String getTgl_perolehan() {
        return tgl_perolehan;
    }

    public void setTgl_perolehan(String tgl_perolehan) {
        this.tgl_perolehan = tgl_perolehan;
    }

    public String getJenis_bonus() {
        return jenis_bonus;
    }

    public void setJenis_bonus(String jenis_bonus) {
        this.jenis_bonus = jenis_bonus;
    }

    public String getStatus_bonus() {
        return status_bonus;
    }

    public void setStatus_bonus(String status_bonus) {
        this.status_bonus = status_bonus;
    }

}
