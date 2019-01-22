package com.eklanku.otuChat.ui.activities.rest;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.eklanku.otuChat.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClientPayment {

    public static String BASE_URL_DEV = "cOpe/g2A8hqDgyEJwaoa1Caze/h3RgRFibqfLSQllQUV91SmPeRuDQ==";
    public static String BASE_URL_PROD = "cOpe/g2A8hrXPBPth94IezwI7RxxWadxw2ZuY6De9DM=";


    public static String XAPIKEY = "V2vxhAvT2xs=";

    private static Retrofit retrofit = null;
    private static Context context;

    public ApiClientPayment(Context ctx) {
        context = ctx;
    }

    public static Retrofit getClient() {

        String apiDev = "", apiProd = "";
        String apiKey = "";
        try {
            apiKey = new Enc(context).decrypt(XAPIKEY);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (retrofit == null) {
            String finalApiKey = apiKey;
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder()
                            .header("X-API-KEY", finalApiKey);
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                }
            })
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder().setLenient().create();

            try {
                apiDev = new Enc(context).decrypt(BASE_URL_DEV);
                apiProd = new Enc(context).decrypt(BASE_URL_PROD);

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            retrofit = new Retrofit.Builder()
                    .baseUrl(apiProd)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
