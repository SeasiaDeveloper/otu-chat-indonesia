package com.eklanku.otuChat.ui.activities.rest;

import com.eklanku.otuChat.ui.activities.payment.models.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.JsonResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LaporanSaldoResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LaporanTrxResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoginResponse;
import com.eklanku.otuChat.ui.activities.payment.models.RegisterResponse;
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


public interface ApiInterfaceProfile {

    @FormUrlEncoded
    @POST("users/checkreff")
    Call<LoginResponse> postCheckReff(
            @Field("reff") String reff
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<LoginResponse> postLogin(
           /* @Field("password") String password,
            @Field("phone") String phone*/

            @Field("userID") String password,
            @Field("token") String token,
            @Field("securityCode") String securityCode
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

    @GET("pembayaran/loadfilteropr")
    Call<JsonResponse> getLoadFilterOpr();

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
    @POST("pembayaran/transconfirm")
    Call<TransBeliResponse> postTransConfirm(
            @Field("id") String id,
            @Field("jenis") String jenis,
            // @Field("nohp") String nohp,
            @Field("id_pel") String id_pel,
            @Field("pin") String pin,
            @Field("cmd_save") String cmd_save
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






}
