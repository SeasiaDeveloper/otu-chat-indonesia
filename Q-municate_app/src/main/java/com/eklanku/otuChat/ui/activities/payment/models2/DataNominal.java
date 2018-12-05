package com.eklanku.otuChat.ui.activities.payment.models2;

import com.google.gson.annotations.SerializedName;

public class DataNominal {
    @SerializedName("product_kode")
    private String product_kode;

    @SerializedName("product_name")
    private String product_name;

    @SerializedName("h2h_code")
    private String h2h_code;

    @SerializedName("harga_jual")
    private Double harga_jual;

    @SerializedName("product_status")
    private String product_status;

    @SerializedName("product_date")
    private String product_date;

    @SerializedName("type_product")
    private String type_product;

    @SerializedName("epoint")
    private Double epoint;

    public DataNominal() {}

    public DataNominal(String product_kode, String product_name, String h2h_code,
                       Double harga_jual, String product_status, String product_date, String type_product, Double epoint) {
        this.product_kode   = product_kode;
        this.product_name   = product_name;
        this.h2h_code       = h2h_code;
        this.harga_jual     = harga_jual;
        this.product_status = product_status;
        this.product_date   = product_date;
        this.type_product   = type_product;
        this.epoint   = epoint;
    }

    public String getProductKode() {
        return product_kode;
    }

    public void setProductKode(String product_kode) {
        this.product_kode = product_kode;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public String getH2hCode() {
        return h2h_code;
    }

    public void setH2hCode(String h2h_code) {
        this.h2h_code = h2h_code;
    }

    public Double getHargaJual() {
        return harga_jual;
    }

    public void setHargaJual(Double harga_jual) {
        this.harga_jual = harga_jual;
    }

    public String getProductStatus() {
        return product_status;
    }

    public void setProductStatus(String product_status) {
        this.product_status = product_status;
    }

    public String getProductDate() {
        return product_date;
    }

    public void setProductDate(String product_date) {
        this.product_date = product_date;
    }

    public String getTypeProduct() {
        return type_product;
    }

    public void setTypeProduct(String type_product) {
        this.type_product= type_product;
    }

    public Double getEpoint() {
        return epoint;
    }

    public void setEpoint(Double epoint) {
        this.epoint = epoint;
    }


}

