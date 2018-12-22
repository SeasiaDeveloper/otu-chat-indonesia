package com.eklanku.otuChat.ui.activities.rest;

import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataBannerPayment;
import com.eklanku.otuChat.ui.activities.payment.models.DataCekMemberTransfer;
import com.eklanku.otuChat.ui.activities.payment.models.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.payment.models.DataKota;
import com.eklanku.otuChat.ui.activities.payment.models.DataPeriodeBPJS;
import com.eklanku.otuChat.ui.activities.payment.models.DataPrefix;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataProviderByType;
import com.eklanku.otuChat.ui.activities.payment.models.DataRequestDokuCC;
import com.eklanku.otuChat.ui.activities.payment.models.DataSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadBanner;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.LoginResponse;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupOrderResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterfacePayment {

    @FormUrlEncoded
    @POST("topup/product")
    Call<LoadDataResponseProduct> getLoadProduct(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("provider") String provider
    );

    //deposit
    @FormUrlEncoded
    @POST("Deposit/bank")
    Call<TopupOrderResponse> postInquiryBankList(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    //konfirm transfer
    @FormUrlEncoded
    @POST("transfer/order")
    Call<TopupKonfirmResponse> postTransferOrder(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("refID") String refID,
            @Field("pin") String pin
    );

    //get deposit
    @FormUrlEncoded
    @POST("saldo")
    Call<DataDeposit> getSaldo(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken
    );


    @FormUrlEncoded
    @POST("resetpass/getToken")
    Call<ResetPassResponse> getTokenResetpass(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse
    );


    @FormUrlEncoded
    @POST("resetpin")
    Call<ResetPINResponse> postResetPin(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken
    );

    //cancel transfer
    @FormUrlEncoded
    @POST("transfer/cancel")
    Call<TopupKonfirmResponse> cancelTransfer(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("refID") String refID
    );

    //antrian transfer
    @FormUrlEncoded
    @POST("transfer/cektransfer")
    Call<DetailTransferResponse> antrianTransfer(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );


    //edit profile
    @FormUrlEncoded
    @POST("profile/updateProfile")
    Call<DataProfile> updateProfile(
            @Field("userID") String userID,
            @Field("aplUse") String accessToken,
            @Field("accessToken") String aplUse,
            @Field("pin") String pin,
            @Field("email") String email,
            @Field("city") String city,
            @Field("address") String address,
            @Field("identitas") String identitas
    );

    //banner
    @FormUrlEncoded
    @POST("banner")
    Call<LoadBanner> getBanner(
            @Field("userID") String userID,
            @Field("aplUse") String accessToken
    );

    @FormUrlEncoded
    @POST("Prabayar/provider_name")
    Call<LoadDataResponseProvider> getLoadProvider(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("productType") String productType
    );


    //===================================================new api=====================================

    @FormUrlEncoded
    @POST("Member/logout")
    Call<ResetPassResponse> postLogoutPayment(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("token") String token
    );


    @FormUrlEncoded
    @Headers({"X-API-KEY: 222"})
    @POST("Member/login")
    Call<LoginResponse> postLogin(

            @Field("userID") String password,
            @Field("token") String token,
            @Field("securityCode") String securityCode,
            @Field("passwd") String passwd
    );

    @FormUrlEncoded
    @Headers({"X-API-KEY: 222"})
    @POST("Prabayar/prefix_pulsa")
    Call<DataPrefix> getPrefixPulsa(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/prefix_sms")
    Call<DataPrefix> getPrefixSMS(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/prefix_data")
    Call<DataPrefix> getPrefixData(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/prefix_telpon")
    Call<DataPrefix> getPrefixTelp(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/product_pulsa")
    Call<DataAllProduct> getProduct_pulsa(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/product_data")
    Call<DataAllProduct> geetProduct_paketdata(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/product_telepon")
    Call<DataAllProduct> geetProduct_paketTelp(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/product_sms")
    Call<DataAllProduct> getProduct_sms(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/order")
    Call<TransBeliResponse> postTopupOld(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("MSISDN") String MSISDN,
            @Field("sequence") String sequence,
            @Field("buyerPhone") String buyerPhone,
            @Field("refIDCustomer") String refIDCustomer,
            @Field("productCode") String productCode
    );

    @FormUrlEncoded
    @POST("Prabayar/order")
    Call<TransBeliResponse> postTopup(//menghilangkan sequence
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("MSISDN") String MSISDN,
            @Field("buyerPhone") String buyerPhone,
            @Field("refIDCustomer") String refIDCustomer,
            @Field("productCode") String productCode
    );

    //get deposit
    @FormUrlEncoded
    @POST("Member/get_saldo_bonus")
    Call<DataSaldoBonus> getSaldodetail(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken
    );


    @FormUrlEncoded
    @POST("Prabayar/product_pln_token")
    Call<DataAllProduct> getproduct_plntoken(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/product_etoll")
    Call<DataAllProduct> getproduct_etoll(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );


    @FormUrlEncoded
    @POST("Prabayar/provider_name")
    Call<DataProviderByType> getProviderByType(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("productType") String productType
    );

    @FormUrlEncoded
    @POST("Prabayar/product_game")
    Call<DataAllProduct> getproduct_game(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @GET("Konten/banner")
    Call<DataBannerPayment> getBanner(
    );

    //deposit billing
    @FormUrlEncoded
    @POST("Deposit/request")
    Call<TopupPayResponse> postDepositOrder(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("bank") String bank,
            @Field("nominal") String nominal
    );

    //history
    @FormUrlEncoded
    @POST("Riwayat/riwayat_by_id")
    Call<DataHistoryOTU> getHistoryTrx(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken,
            @Field("requestID") String requestID
    );

    //get token register
    @FormUrlEncoded
    @POST("Member/register_token")
    Call<DataProfile> getTokenRegister(
            @Field("userID") String userID,//nomor hp
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Member/go_register")
    Call<DataProfile> register(
            @Field("userID") String userID,
            @Field("uplineID") String uplineID,
            @Field("aplUse") String aplUse,
            @Field("registerToken") String registerToken,
            @Field("nama") String nama,
            @Field("mbr_email") String mbr_email,
            @Field("mbr_pswd") String mbr_pswd
    );

    //is member
    @FormUrlEncoded
    @POST("Member/get_member")
    Call<DataProfile> isMember(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse
    );

    //get token profile
    @FormUrlEncoded
    @POST("Member/profile_token")
    Call<DataProfile> getTokenProfile(
            @Field("userID") String userID,//nomor hp
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Member/profile")
    Call<DataDetailProfile> getProfile(
            @Field("userID") String userID,//nomor hp
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken
    );

    //API request doku
    @FormUrlEncoded
    @POST("Deposit/request")
    Call<DataRequestDokuCC> requestDoku(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("bank") String bank,
            @Field("nominal") String nominal,
            @Field("doku-token") String doku_token,
            @Field("deviceid") String deviceid,
            @Field("doku-pairing-code") String doku_pairing_code,
            @Field("doku-invoice-no") String doku_invoice_no,
            @Field("payment_chanel") String payment_chanel,
            @Field("words") String words
    );

    @FormUrlEncoded
    @POST("Transfer/cek_member")
    Call<DataCekMemberTransfer> getCekMemberTransfer(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("destination") String destination,
            @Field("accessToken") String accessToken
    );

    //request transfer
    @FormUrlEncoded
    @POST("Transfer/transfer_saldo")
    Call<DetailTransferResponse> postRequestTransfer(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("tujuan") String tujuan,
            @Field("nominal") String nominal,
            @Field("pin") String pin
    );

    //delete account
    @FormUrlEncoded
    @POST("Member/send_desible_account")
    Call<DataProfile> deleteAccount(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("pin") String pin

    );

    //register
    @FormUrlEncoded
    @POST("Member/go_register")
    Call<DataProfile> postRegisterUpline(
            @Field("userID") String userID,//nomor hp
            @Field("uplineID") String uplineID,
            @Field("aplUse") String aplUse,
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password,
            @Field("pin") String pin
    );

    //reset pass
    @FormUrlEncoded
    @POST("member/reset_pass")
    Call<ResetPassResponse> Resetpass(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken,
            @Field("newpass") String newpass,
            @Field("pin") String pin
    );

    @FormUrlEncoded
    @POST("Member/reset_pin")
    Call<ResetPINResponse> apprResetPin(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("otp") String keySMS,
            @Field("newpin") String newpin
    );

    @FormUrlEncoded
    @POST("Member/kirim_key")
    Call<ResetPINResponse> getKeySMS(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    //update profil
    @FormUrlEncoded
    @POST("Member/update_profile")
    Call<DataProfile> updateProfil(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken,
            @Field("email") String email,
            @Field("city") String city,
            @Field("address") String address,
            @Field("identitas") String identitas,
            @Field("tgl_lahir") String tgl_lahir,
            @Field("pin") String pin
    );

    //get provider type
    @FormUrlEncoded
    @POST("Pascabayar/product_name")
    Call<LoadDataResponse> postPpobProduct(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("productGroup") String productGroup,
            @Field("aplUse") String aplUse
    );

    //get product e saldo
    @FormUrlEncoded
    @POST("Prabayar/product_esaldo")
    Call<DataAllProduct> getProductESaldo(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    //inquiry
    @FormUrlEncoded
    @POST("Pascabayar/inquiry")
    Call<TransBeliResponse> postPpobInquiry(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("productCode") String productGroup,
            @Field("customerID") String customerID,
            @Field("customerMSISDN") String customerMSISDN,
            @Field("aplUse") String aplUse
    );

    //inquiry
    @FormUrlEncoded
    @POST("Pascabayar/inquiry")
    Call<TransBeliResponse> postPpobInquiryBPJS(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("productCode") String productGroup,
            @Field("customerID") String customerID,
            @Field("customerMSISDN") String customerMSISDN,
            @Field("aplUse") String aplUse,
            @Field("periode") String periode
    );


    @FormUrlEncoded
    @POST("Pascabayar/payment")
    Call<TransBeliResponse> postTransConfirm(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("billingReferenceID") String billingReferenceID,
            @Field("aplUse") String apIUse
    );

    //get product wifi id
    @FormUrlEncoded
    @POST("Prabayar/product_wifi_id")
    Call<DataAllProduct> getProduct_wifiid(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Member/kota_provinsi")
    Call<DataKota> getKota(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    //get periode bpjs
    @FormUrlEncoded
    @POST("Pascabayar/bulan_bpjs")
    Call<DataPeriodeBPJS> getperiodebpjs(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Donasi/request")
    Call<TopupPayResponse> donasi_manual(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("bank") String bank,
            @Field("nominal") String nominal
    );


}
