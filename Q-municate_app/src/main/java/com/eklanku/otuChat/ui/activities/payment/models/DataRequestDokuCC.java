package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataRequestDokuCC {

    @SerializedName("res_response_msg")
    @Expose
    String res_response_msg;

    @SerializedName("res_response_code")
    @Expose
    String res_response_code;

    public String getRes_response_msg() {
        return res_response_msg;
    }

    public void setRes_response_msg(String res_response_msg) {
        this.res_response_msg = res_response_msg;
    }



    /*public String getRes_response_code() {
        return res_response_code;
    }

    public void setRes_response_code(String res_response_code) {
        this.res_response_code = res_response_code;
    }*/



    @SerializedName("errNumber")
    String errNumber;

    public String getErrNumber() {
        return errNumber;
    }

    public void setErrNumber(String errNumber) {
        this.errNumber = errNumber;
    }



    @SerializedName("res_tid")
    String res_tid;

    @SerializedName("res_trx_code")
    String res_trx_code;

    @SerializedName("res_currency")
    String res_currency;

    @SerializedName("res_approval_code")
    String res_approval_code;

    @SerializedName("res_eci")
    String res_eci;

    @SerializedName("res_chain_mall_id")
    String res_chain_mall_id;

    @SerializedName("res_card_number")
    String res_card_number;

    @SerializedName("res_amount")
    String res_amount;

    @SerializedName("res_message")
    String res_message;

    @SerializedName("res_issuer_bank")
    String res_issuer_bank;

    @SerializedName("res_mall_id")
    String res_mall_id;

    @SerializedName("res_liability")
    String res_liability;

    @SerializedName("res_mid")
    String res_mid;

    @SerializedName("res_result")
    String res_result;

    @SerializedName("res_payment_date")
    String res_payment_date;

    @SerializedName("res_three_d_secure_status")
    String res_three_d_secure_status;

    @SerializedName("res_bank")
    String res_bank;

    public String getRes_tid() {
        return res_tid;
    }

    public void setRes_tid(String res_tid) {
        this.res_tid = res_tid;
    }

    public String getRes_trx_code() {
        return res_trx_code;
    }

    public void setRes_trx_code(String res_trx_code) {
        this.res_trx_code = res_trx_code;
    }

    public String getRes_currency() {
        return res_currency;
    }

    public void setRes_currency(String res_currency) {
        this.res_currency = res_currency;
    }

    public String getRes_approval_code() {
        return res_approval_code;
    }

    public void setRes_approval_code(String res_approval_code) {
        this.res_approval_code = res_approval_code;
    }

    public String getRes_eci() {
        return res_eci;
    }

    public void setRes_eci(String res_eci) {
        this.res_eci = res_eci;
    }

    public String getRes_chain_mall_id() {
        return res_chain_mall_id;
    }

    public void setRes_chain_mall_id(String res_chain_mall_id) {
        this.res_chain_mall_id = res_chain_mall_id;
    }

    public String getRes_card_number() {
        return res_card_number;
    }

    public void setRes_card_number(String res_card_number) {
        this.res_card_number = res_card_number;
    }

    public String getRes_amount() {
        return res_amount;
    }

    public void setRes_amount(String res_amount) {
        this.res_amount = res_amount;
    }

    public String getRes_message() {
        return res_message;
    }

    public void setRes_message(String res_message) {
        this.res_message = res_message;
    }

    public String getRes_issuer_bank() {
        return res_issuer_bank;
    }

    public void setRes_issuer_bank(String res_issuer_bank) {
        this.res_issuer_bank = res_issuer_bank;
    }

    public String getRes_mall_id() {
        return res_mall_id;
    }

    public void setRes_mall_id(String res_mall_id) {
        this.res_mall_id = res_mall_id;
    }

    public String getRes_liability() {
        return res_liability;
    }

    public void setRes_liability(String res_liability) {
        this.res_liability = res_liability;
    }

    public String getRes_mid() {
        return res_mid;
    }

    public void setRes_mid(String res_mid) {
        this.res_mid = res_mid;
    }

    public String getRes_result() {
        return res_result;
    }

    public void setRes_result(String res_result) {
        this.res_result = res_result;
    }

    public String getRes_payment_date() {
        return res_payment_date;
    }

    public void setRes_payment_date(String res_payment_date) {
        this.res_payment_date = res_payment_date;
    }

    public String getRes_three_d_secure_status() {
        return res_three_d_secure_status;
    }

    public void setRes_three_d_secure_status(String res_three_d_secure_status) {
        this.res_three_d_secure_status = res_three_d_secure_status;
    }

    public String getRes_bank() {
        return res_bank;
    }

    public void setRes_bank(String res_bank) {
        this.res_bank = res_bank;
    }

    public String getRes_invoice_number() {
        return res_invoice_number;
    }

    public void setRes_invoice_number(String res_invoice_number) {
        this.res_invoice_number = res_invoice_number;
    }

    public String getRes_response_code() {
        return res_response_code;
    }

    public void setRes_response_code(String res_response_code) {
        this.res_response_code = res_response_code;
    }

    public String getRes_session_id() {
        return res_session_id;
    }

    public void setRes_session_id(String res_session_id) {
        this.res_session_id = res_session_id;
    }

    public String getRes_payment_channel() {
        return res_payment_channel;
    }

    public void setRes_payment_channel(String res_payment_channel) {
        this.res_payment_channel = res_payment_channel;
    }

    @SerializedName("res_invoice_number")
    String res_invoice_number;

  /*  @SerializedName("res_response_code")
    String res_response_code;*/

    @SerializedName("res_session_id")
    String res_session_id;

    @SerializedName("res_payment_channel")
    String res_payment_channel;
}
