package com.eklanku.otuChat.ui.activities.rest2;

import com.eklanku.otuChat.ui.activities.payment.models2.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models2.DataBannerPayment;
import com.eklanku.otuChat.ui.activities.payment.models2.DataCekMemberTransfer;
import com.eklanku.otuChat.ui.activities.payment.models2.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models2.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models2.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.payment.models2.DataPrefix;
import com.eklanku.otuChat.ui.activities.payment.models2.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models2.DataProviderByType;
import com.eklanku.otuChat.ui.activities.payment.models2.DataRequestDokuCC;
import com.eklanku.otuChat.ui.activities.payment.models2.DataSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models2.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.JsonResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.LaporanSaldoResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.LaporanTrxResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.LoadBanner;
import com.eklanku.otuChat.ui.activities.payment.models2.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models2.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models2.LoginResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.RegisterResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.TopupBillingResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.TopupDetailResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.TopupOrderResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.TransBeliResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterfacePayment {

    @FormUrlEncoded
    @POST("users/checkreff")
    Call<LoginResponse> postCheckReff(
            @Field("reff") String reff
    );


    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> postLogout(
           /* @Field("password") String password,
            @Field("phone") String phone*/

            @Field("userID") String password,
            @Field("accessToken") String accessToken,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("users/detail")
    Call<RegisterResponse> postDetail(
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("users/register")
    Call<RegisterResponse> postRegister(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("reff") String reff
    );

    @FormUrlEncoded
    @POST("users/resetpin")
    Call<RegisterResponse> postResetPIN(
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("users/resetpass")
    Call<RegisterResponse> postResetPass(
            @Field("phone") String phone
    );

    @GET("users/account")
    Call<LoginResponse> getAccount(@Query("id") String id);

    @FormUrlEncoded
    @POST("pembayaran/loaddata")
    Call<LoadDataResponse> postLoadData(
            @Field("load_type") String load_type,
            @Field("load_id") String load_id,
            @Field("no_hp") String no_hp
    );

    @FormUrlEncoded
    @POST("topup/provaider")
    Call<LoadDataResponseProvider> postLoadDataPLN(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("productType") String productType
    );

    @FormUrlEncoded
    @POST("topup/provaider")
    Call<LoadDataResponseProvider> postLoadDataProductPLN(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("productType") String productType
    );

    @GET("pembayaran/loadfilteropr")
    Call<JsonResponse> getLoadFilterOpr();

    @FormUrlEncoded
    @POST("ppob/productGroup")
    Call<LoadDataResponse> getLoadProviderPPOB(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("productGroup") String productGroup
    );


    @FormUrlEncoded
    @POST("topup/product")
    Call<LoadDataResponseProduct> getLoadProduct(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("provider") String provider
    );

    @FormUrlEncoded
    @POST("pembayaran/transbeli")
    Call<TransBeliResponse> postTransBeli(
            @Field("id") String id,
            @Field("jenis") String jenis,
            @Field("nominal") String nominal,
            @Field("id_pel") String id_pel,
            @Field("nm") String nm
    );

    @FormUrlEncoded
    @POST("ppob/payment")
    Call<TransBeliResponse> postTransConfirm(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("billingReferenceID") String billingReferenceID,
            @Field("aplUse") String apIUse
    );

    @FormUrlEncoded
    @POST("laporan/transaksi")
    Call<LaporanSaldoResponse> postLapTransaksi(@Field("id") String id);

    @GET("topup/order")
    Call<TopupOrderResponse> getPaketTopup();

    @GET("topup/billing")
    Call<TopupBillingResponse> getBillingAccount();

    @FormUrlEncoded
    @POST("topup/pay")
    Call<TopupPayResponse> postTopupPay(
            @Field("metode_bayar") String metode_bayar,
            @Field("id_paket") String id_paket,
            //  @Field("id_member") String id_member,
            @Field("no_hp") String no_hp
    );

    @FormUrlEncoded
    @POST("users/deposit")
    Call<DataDeposit> getUserDeposit(
            @Field("no_hp") String no_hp
    );

    @FormUrlEncoded
    @POST("users/transdeposit")
    Call<TopupKonfirmResponse> getTransDeposit(
            @Field("no_hp") String no_hp,
            @Field("tujuan") String tujuan,
            @Field("jml") String jml,
            @Field("pin") String pin
    );

    @FormUrlEncoded
    @POST("laporan/history")
    Call<LaporanSaldoResponse> getHistorySaldo(
            @Field("no_hp") String no_hp
    );

    @FormUrlEncoded
    @POST("laporan/historytrx")
    Call<LaporanTrxResponse> getHistoryTrx(
            @Field("no_hp") String no_hp
    );

    @FormUrlEncoded
    @POST("topup/confirm")
    Call<TopupKonfirmResponse> postTopupKonfirm(
            @Field("id_order") String id_order,
            //  @Field("id_member") String id_member,
            @Field("no_hp") String no_hp
    );

    @GET("topup/detail")
    Call<TopupDetailResponse> getTopupDetail(@Query("id") String id);

    //rina
    @FormUrlEncoded
    @POST("ppob/product")
    Call<LoginResponse> postLoginEkl(

            @Field("userID") String userid,
            @Field("accessToken") String accesstoken,
            @Field("aplUse") String apiuse
    );


    @FormUrlEncoded
    @POST("ppob/inquiry")
    Call<TransBeliResponse> postPpobInquiry(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("productCode") String productGroup,
            @Field("customerID") String customerID,
            @Field("customerMSISDN") String customerMSISDN,
            @Field("aplUse") String aplUse
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

    //banner


    @FormUrlEncoded
    @POST("Prabayar/provider_name")
    Call<LoadDataResponse> postPpobProduct(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("productGroup") String productGroup,
            @Field("aplUse") String aplUse
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
    @POST("Prabayar/product_sms")
    Call<DataAllProduct> getProduct_sms(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("Prabayar/order")
    Call<TransBeliResponse> postTopup(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("MSISDN") String MSISDN,
            @Field("sequence") String sequence,
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

}
