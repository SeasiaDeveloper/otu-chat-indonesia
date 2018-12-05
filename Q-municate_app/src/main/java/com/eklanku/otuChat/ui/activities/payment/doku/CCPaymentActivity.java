package com.eklanku.otuChat.ui.activities.payment.doku;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.interfaces.iPaymentCallback;
import com.doku.sdkocov2.model.PaymentItems;
import com.eklanku.otuChat.Manifest;
import com.eklanku.otuChat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CCPaymentActivity extends AppCompatActivity {

    //EditText et_ccn, et_cvv, et_noc, et_ed, et_email, et_mp;
    //Button btnPayment;
    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhwKdO2PuW3JWc4fFA05UzcrA0JTj2Bgc0meP9nE2Qea2CvCQbhtVrEPmrl4G98W9w2sUsjwRidPub/eYEwz3G2W8GGXi0OU7JGzcLmH5lZT9yujtjPS8ISCpB78JlzmVka1B3zO8N32JR6uoZkR/4u49O6r+YJFHaL9fVO0UD+3vYfUrWqbGeohglBWiLVzo0fDQGq6Pnzy8SCpaUSBWYTzm08m5ni0q+z8wYkqRRle4HmIsKNlivWC5OWxH2cEmI1hyhhFK1tLYnbzX8fWvEmTz4jUMWcPepnhVBYLEKpL3TlGH7c0xaHYkEejw0jNuy/jVuLOSS1NiQTJdVvdpRwIDAQAB";
    String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCHAp07Y+5bclZzh8UDTlTNysDQlOPYGBzSZ4/2cTZB5rYK8JBuG1WsQ+auXgb3xb3DaxSyPBGJ0+5v95gTDPcbZbwYZeLQ5TskbNwuYfmVlP3K6O2M9LwhIKkHvwmXOZWRrUHfM7w3fYlHq6hmRH/i7j07qv5gkUdov19U7RQP7e9h9StapsZ6iGCUFaItXOjR8NAaro+fPLxIKlpRIFZhPObTybmeLSr7PzBiSpFGV7geYiwo2WK9YLk5bEfZwSYjWHKGEUrW0tidvNfx9a8SZPPiNQxZw96meFUFgsQqkvdOUYftzTFodiQR6PDSM27L+NW4s5JLU2JBMl1W92lHAgMBAAECggEAeWwLdsd4LnxVbhAUStXfBTotUSonBEkjWsPTQOQu1PQmkw4qByzET2q+A+ICyEHqWd9rPKUzbM7S6ZY3CiMl9lj34sV3SUJwf2D8YSaybioSWrREOPo+XFjgTFwuxvf+IYc97/y6cPmRRokGqfs/PRvgrFIr9zORko5SsbGK0otsufoNxWUlV3Aak92x4yBFC/xBEuqHA8fCN0SIhzNL97/zzfq3g+K1RA+y2OW8m0lWpxIKEl9dMvI2paqkC2ZRj/oMwPJlh1relTWlUzODyl3b/nr3DIe+3HRCEVHhZ1CKVIDEtg0okSTrQ7Lc5F2s6YxUjsNw5lv/uM3OzwOI8QKBgQDU9xvNAyWYsIWuo1bYJcvLXLjoVTl2UZVz6sce7KQ2/XQ9cw7cGtaKDKoU/wxMvRBaDz3hnrl2NO5Z0z5Cac8khrapvBMawppTkhEF7KvY5FlfB+I1wOKebJvd01c/DZVfMJgVoCvv1+c9jqvfdn68O/d/vgJceRtbx0Lfkg//rwKBgQCiStFpowkQuJVKfKJKElQ1Nd6h4u7Wlqg1M7FaGdqvTkI3RTDknbkfINkRQaY/xYIHVYxlT13FO7UDmnw2B742WOQK/o8+gRbAKG1mbsr48t5yyWFlEbUww04kJ0XXe8wGfi0c2V0tFsPPHwQHV233GWfBYFSV/w4xwidvXPU96QKBgQCmeIc9yXDxG0NUURAIo/ra+5761eu2Bm36D5MZJEf1SEg95JvACCaeAOpOwVO/BKcsju+DVwyITzXl90+aoJUwazGMGp0gdPAn1W0DIY7vWwhVVZdJB37d8e4hBxwTCK4zm4u2k97ke/OhVc4aPskwoPuF2mSEHpL5OhiXJNjmtwKBgGWVq2sSEBSVVW1ggj0XJ/p+k7KFV7aeav+SMcsSL95Xf/8UojwXtc6UQwsYKrX0LSXmGJE5kppoey+cPW+cfSWLkoKHQKaBKvpw07mwVABsFW2IPByFbwLs3TaoFoGBDf5qcFaEgFw+yaYV5fGqejyXfGiAobGaCwp+WyzL5P4xAoGAB3CpqnQ4Rk5RWYJYC51oztV/JuLhWbC7VzuSmMHZmeEfBdmD9E6Gs0Sumo3h5FkAFQ2blYlgljhQgaMyTmz36Pj18rtY4sWZqvdbVboUnZ8MIWxpDDmao5obiViJxf/eRCLVX7D2/VcAUH589P9ARIOvq8El0C+ql8ASd0z4rpM=";
    String sharedKey = "DRFCrfw5n216";
    String mallId = "6663";
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
    JSONObject respongetTokenSDK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_payment_credit_card);

       /* et_ccn = findViewById(R.id.txt_ccn);
        et_cvv = findViewById(R.id.txt_cvv);
        et_noc = findViewById(R.id.txt_noc);
        et_ed = findViewById(R.id.txt_ed);
        et_email = findViewById(R.id.txt_email);
        et_mp = findViewById(R.id.txt_mp);
        btnPayment = findViewById(R.id.btnpp);

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveToken();
            }
        });*/

        retrieveToken();
    }


    public void retrieveToken() {
        String transactionID = transactionID();
        String sessionID = sessionID();
        DirectSDK directSDK = new DirectSDK();
        PaymentItems paymentItems = new PaymentItems();
        paymentItems.setDataAmount("15000.00");
        paymentItems.setDataBasket("[{\"name\":\"donasi\",\"amount\":\"15000.00\",\"quantity\":\"1\"}]");
        paymentItems.setDataCurrency("360");
        try {
            paymentItems.setDataWords(SHA1("15000.00" + mallId +
                    sharedKey + transactionID + 360 + getImei()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        paymentItems.setDataMerchantChain("NA");
        paymentItems.setDataSessionID(String.valueOf(sessionID()));
        paymentItems.setDataTransactionID(transactionID);
        paymentItems.setDataMerchantCode(mallId);
        paymentItems.setDataImei(getImei());
        paymentItems.setMobilePhone("089695489743");
        paymentItems.isProduction(false); //set ‘true’ for production and ‘false’ for development
        paymentItems.setPublicKey(publicKey); //PublicKey can be obtained from the DOKU Back Office
        directSDK.setCart_details(paymentItems);
        directSDK.setPaymentChannel(1);

        //handle response
        directSDK.getResponse(new iPaymentCallback() {
            @Override
            public void onSuccess(final String text) {
                try {
                    respongetTokenSDK = new JSONObject(text);
                    Log.d("OPPO-1", "onSuccess: " + respongetTokenSDK.toString());
                    Log.d("OPPO-1", "onSuccess: " + respongetTokenSDK.getString("res_response_code"));

                    if (respongetTokenSDK.getString("res_response_code").equalsIgnoreCase("0000")) {
                        Log.d("OPPO-1", "onSuccess: " + respongetTokenSDK.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String text) {
                Log.d("OPPO-1", "onError: " + text);
            }

            @Override
            public void onException(Exception eSDK) {
                eSDK.printStackTrace();
                Log.d("OPPO-1", "onException: " + eSDK.getMessage().toString());
            }
        }, getApplicationContext());
    }

    public String amount(int amount) {
        String vAmount = "";
        vAmount = new DecimalFormat("##.##").format(amount);
        return vAmount;
    }

    public String getImei() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String imei = telephonyManager.getDeviceId();
            Log.d("OPPO-1", "getImei: " + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    public static final String DATA = "0123456789";

    public String transactionID() {
        String vTransactionID = "";
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(DATA.length());
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        vTransactionID = "dp_ccn_" + sdf.format(new Date()) + "_" + randomStringBuilder.toString();
        Log.d("OPPO-1", "vTransactionID: " + vTransactionID);
        return vTransactionID;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }

        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public String sessionID() {
        //menggunakan format timestamp
        String vSessionId = "";
        Long tsLong = System.currentTimeMillis() / 1000;
        vSessionId = tsLong.toString();
        Log.d("OPPO-1", "sessionID: "+vSessionId);
        return vSessionId;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
