package com.eklanku.otuChat.ui.activities.rest;

import com.eklanku.otuChat.ui.activities.payment.models.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.payment.models.JsonResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LaporanSaldoResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LaporanTrxResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoginResponse;
import com.eklanku.otuChat.ui.activities.payment.models.RegisterResponse;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupBillingResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupOrderResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    Call<LoginResponse> postLogin(
           /* @Field("password") String password,
            @Field("phone") String phone*/

            @Field("userID") String password,
            @Field("token") String token,
            @Field("securityCode") String securityCode,
            @Field("passwd") String passwd
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
    @POST("topup/provaider")
    Call<LoadDataResponseProvider> getLoadProvider(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("productType") String productType
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
    @POST("ppob/product")
    Call<LoadDataResponse> postPpobProduct(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("productGroup") String productGroup,
            @Field("aplUse") String aplUse
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

    @FormUrlEncoded
    @POST("topup")
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

    //deposit
    @FormUrlEncoded
    @POST("deposit")
    Call<TopupOrderResponse> postInquiryBankList(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse
    );

    //deposit billing
    @FormUrlEncoded
    @POST("deposit/order")
    Call<TopupPayResponse> postDepositOrder(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("bank") String bank,
            @Field("nominal") String nominal
    );

    //request transfer
    @FormUrlEncoded
    @POST("transfer")
    Call<DetailTransferResponse> postRequestTransfer(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("tujuan") String tujuan,
            @Field("nominal") String nominal
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

    //history
    @FormUrlEncoded
    @POST("history")
    Call<DataHistoryOTU> getHistoryTrx(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken,
            @Field("requestID") String requestID
    );

    //reset pass
    @FormUrlEncoded
    @POST("resetpass")
    Call<ResetPassResponse> postResetpass(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("resetToken") String securityCode,
            @Field("newpass") String newpass
    );

    @FormUrlEncoded
    @POST("resetpass/getToken")
    Call<ResetPassResponse> getTokenResetpass(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("logout")
    Call<ResetPassResponse> postLogoutPayment(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("resetpin")
    Call<ResetPINResponse> postResetPin(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse,
            @Field("accessToken") String accessToken
    );

    @FormUrlEncoded
    @POST("resetpin/process")
    Call<ResetPINResponse> apprResetPin(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("keySMS") String keySMS,
            @Field("newpin") String newpin
    );

    //get token profile
    @FormUrlEncoded
    @POST("profile/getToken")
    Call<DataProfile> getTokenProfile(
            @Field("userID") String userID,//nomor hp
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("profile")
    Call<DataDetailProfile> getProfile(
            @Field("userID") String userID,//nomor hp
            @Field("aplUse") String aplUse,
            @Field("profileToken") String profileToken
    );

    //get token register
    @FormUrlEncoded
    @POST("register/getToken")
    Call<DataProfile> getTokenRegister(
            @Field("userID") String userID,//nomor hp
            @Field("aplUse") String aplUse
    );

    @FormUrlEncoded
    @POST("register")
    Call<DataProfile> postRegisterUpline(
            @Field("userID") String userID,//nomor hp
            @Field("uplineID") String uplineID,
            @Field("aplUse") String aplUse,
            @Field("registerToken") String registerToken,
            @Field("nama") String nama
    );

    //is member
    @FormUrlEncoded
    @POST("getMember")
    Call<DataProfile> isMember(
            @Field("userID") String userID,
            @Field("aplUse") String aplUse
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

    //delete account
    @FormUrlEncoded
    @POST("removeAccount")
    Call<DataProfile> deleteAccount(
            @Field("userID") String userID,
            @Field("accessToken") String accessToken,
            @Field("aplUse") String aplUse,
            @Field("pin") String pin

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
}
