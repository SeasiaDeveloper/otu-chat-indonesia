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

    public static String BASE_URL_DEV = "8AA40907930C18B8CC916140B8706874BC15F0C2FF2C3E7A603E7C0BDDECFBB22BCBE4DA5A434C1A73000EC9EA24BA5F";
    public static String BASE_URL_PROD = "58E286852585508A48D83B932C7012D46C508559F775FEFB766E9E1AECC88D32";

    public static String XAPIKEY = "43EBE47A09884E05D2CB871AF7A73A6E";

    private static Retrofit retrofit = null;
    private static Context context;

    public ApiClientPayment(Context ctx) {
        context = ctx;
    }

    public static Retrofit getClient() {

        String apiDev = "", apiProd = "";
        String apiKey = "";
        try {
            apiDev = AESUtils.decrypt(BASE_URL_DEV);
            apiProd = AESUtils.decrypt(BASE_URL_PROD);
            apiKey = AESUtils.decrypt(XAPIKEY);

        } catch (Exception e) {
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(apiDev)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
