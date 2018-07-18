package com.eklanku.otuChat.ui.activities.payment.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransBeliResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("result")
    private List<DataTransBeli> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<DataTransBeli> getResult() {
        return result;
    }

    public void setResult(List<DataTransBeli> result) {
        this.result = result;
    }

    //rina ===> PPOB Asuransi
    @SerializedName("userID")
    private String userID;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("respMessage")
    private String respMessage;

    @SerializedName("respTime")
    private String respTime;

    @SerializedName("productCode")
    private String productCode;

    @SerializedName("billingReferenceID")
    private String billingReferenceID;

    @SerializedName("customerID")
    private String customerID;

    @SerializedName("customerMSISDN")
    private String customerMSISDN;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("period")
    private String period;

    @SerializedName("policeNumber")
    private String policeNumber;

    @SerializedName("lastPaidPeriod")
    private String lastPaidPeriod;

    @SerializedName("tenor")
    private String tenor;

    @SerializedName("lastPaidDueDate")
    private String lastPaidDueDate;

    @SerializedName("usageUnit")
    private String usageUnit;

    @SerializedName("penalty")
    private String penalty;

    @SerializedName("payment")
    private String payment;

    @SerializedName("minPayment")
    private String minPayment;

    @SerializedName("maxPayment")
    private String maxPayment;

    @SerializedName("additionalMessage")
    private String additionalMessage;

    @SerializedName("billing")
    private String billing;

    @SerializedName("sellPrice")
    private String sellPrice;

    @SerializedName("adminBank")
    private String adminBank;

    @SerializedName("profit")
    private String profit;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public String getRespTime() {
        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBillingReferenceID() {
        return billingReferenceID;
    }

    public void setBillingReferenceID(String billingReferenceID) {
        this.billingReferenceID = billingReferenceID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerMSISDN() {
        return customerMSISDN;
    }

    public void setCustomerMSISDN(String customerMSISDN) {
        this.customerMSISDN = customerMSISDN;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPoliceNumber() {
        return policeNumber;
    }

    public void setPoliceNumber(String policeNumber) {
        this.policeNumber = policeNumber;
    }

    public String getLastPaidPeriod() {
        return lastPaidPeriod;
    }

    public void setLastPaidPeriod(String lastPaidPeriod) {
        this.lastPaidPeriod = lastPaidPeriod;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public String getLastPaidDueDate() {
        return lastPaidDueDate;
    }

    public void setLastPaidDueDate(String lastPaidDueDate) {
        this.lastPaidDueDate = lastPaidDueDate;
    }

    public String getUsageUnit() {
        return usageUnit;
    }

    public void setUsageUnit(String usageUnit) {
        this.usageUnit = usageUnit;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getMinPayment() {
        return minPayment;
    }

    public void setMinPayment(String minPayment) {
        this.minPayment = minPayment;
    }

    public String getMaxPayment() {
        return maxPayment;
    }

    public void setMaxPayment(String maxPayment) {
        this.maxPayment = maxPayment;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }

    public void setAdditionalMessage(String additionalMessage) {
        this.additionalMessage = additionalMessage;
    }

    public String getBilling() {
        return billing;
    }

    public void setBilling(String billing) {
        this.billing = billing;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getAdminBank() {
        return adminBank;
    }

    public void setAdminBank(String adminBank) {
        this.adminBank = adminBank;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }


    //Rina ==> topup
    @SerializedName("transactionDate")
    private String transactionDate;

    @SerializedName("MSISDN")
    private String MSISDN;

    @SerializedName("nominal")
    private String nominal;

    @SerializedName("refIDCustomer")
    private String refIDCustomer;

    @SerializedName("transactionID")
    private String transactionID;

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getRefIDCustomer() {
        return refIDCustomer;
    }

    public void setRefIDCustomer(String refIDCustomer) {
        this.refIDCustomer = refIDCustomer;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

}
