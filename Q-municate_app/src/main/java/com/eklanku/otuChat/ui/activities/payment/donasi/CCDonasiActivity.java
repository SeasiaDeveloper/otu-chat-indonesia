package com.eklanku.otuChat.ui.activities.payment.donasi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.interfaces.iPaymentCallback;
import com.doku.sdkocov2.model.LayoutItems;
import com.doku.sdkocov2.model.PaymentItems;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.VolleyController;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//import retrofit2.Response;

public class CCDonasiActivity extends AppCompatActivity {

    //EditText et_ccn, et_cvv, et_noc, et_ed, et_email, et_mp;
    //Button btnPayment;
    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhwKdO2PuW3JWc4fFA05UzcrA0JTj2Bgc0meP9nE2Qea2CvCQbhtVrEPmrl4G98W9w2sUsjwRidPub/eYEwz3G2W8GGXi0OU7JGzcLmH5lZT9yujtjPS8ISCpB78JlzmVka1B3zO8N32JR6uoZkR/4u49O6r+YJFHaL9fVO0UD+3vYfUrWqbGeohglBWiLVzo0fDQGq6Pnzy8SCpaUSBWYTzm08m5ni0q+z8wYkqRRle4HmIsKNlivWC5OWxH2cEmI1hyhhFK1tLYnbzX8fWvEmTz4jUMWcPepnhVBYLEKpL3TlGH7c0xaHYkEejw0jNuy/jVuLOSS1NiQTJdVvdpRwIDAQAB";
    String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCHAp07Y+5bclZzh8UDTlTNysDQlOPYGBzSZ4/2cTZB5rYK8JBuG1WsQ+auXgb3xb3DaxSyPBGJ0+5v95gTDPcbZbwYZeLQ5TskbNwuYfmVlP3K6O2M9LwhIKkHvwmXOZWRrUHfM7w3fYlHq6hmRH/i7j07qv5gkUdov19U7RQP7e9h9StapsZ6iGCUFaItXOjR8NAaro+fPLxIKlpRIFZhPObTybmeLSr7PzBiSpFGV7geYiwo2WK9YLk5bEfZwSYjWHKGEUrW0tidvNfx9a8SZPPiNQxZw96meFUFgsQqkvdOUYftzTFodiQR6PDSM27L+NW4s5JLU2JBMl1W92lHAgMBAAECggEAeWwLdsd4LnxVbhAUStXfBTotUSonBEkjWsPTQOQu1PQmkw4qByzET2q+A+ICyEHqWd9rPKUzbM7S6ZY3CiMl9lj34sV3SUJwf2D8YSaybioSWrREOPo+XFjgTFwuxvf+IYc97/y6cPmRRokGqfs/PRvgrFIr9zORko5SsbGK0otsufoNxWUlV3Aak92x4yBFC/xBEuqHA8fCN0SIhzNL97/zzfq3g+K1RA+y2OW8m0lWpxIKEl9dMvI2paqkC2ZRj/oMwPJlh1relTWlUzODyl3b/nr3DIe+3HRCEVHhZ1CKVIDEtg0okSTrQ7Lc5F2s6YxUjsNw5lv/uM3OzwOI8QKBgQDU9xvNAyWYsIWuo1bYJcvLXLjoVTl2UZVz6sce7KQ2/XQ9cw7cGtaKDKoU/wxMvRBaDz3hnrl2NO5Z0z5Cac8khrapvBMawppTkhEF7KvY5FlfB+I1wOKebJvd01c/DZVfMJgVoCvv1+c9jqvfdn68O/d/vgJceRtbx0Lfkg//rwKBgQCiStFpowkQuJVKfKJKElQ1Nd6h4u7Wlqg1M7FaGdqvTkI3RTDknbkfINkRQaY/xYIHVYxlT13FO7UDmnw2B742WOQK/o8+gRbAKG1mbsr48t5yyWFlEbUww04kJ0XXe8wGfi0c2V0tFsPPHwQHV233GWfBYFSV/w4xwidvXPU96QKBgQCmeIc9yXDxG0NUURAIo/ra+5761eu2Bm36D5MZJEf1SEg95JvACCaeAOpOwVO/BKcsju+DVwyITzXl90+aoJUwazGMGp0gdPAn1W0DIY7vWwhVVZdJB37d8e4hBxwTCK4zm4u2k97ke/OhVc4aPskwoPuF2mSEHpL5OhiXJNjmtwKBgGWVq2sSEBSVVW1ggj0XJ/p+k7KFV7aeav+SMcsSL95Xf/8UojwXtc6UQwsYKrX0LSXmGJE5kppoey+cPW+cfSWLkoKHQKaBKvpw07mwVABsFW2IPByFbwLs3TaoFoGBDf5qcFaEgFw+yaYV5fGqejyXfGiAobGaCwp+WyzL5P4xAoGAB3CpqnQ4Rk5RWYJYC51oztV/JuLhWbC7VzuSmMHZmeEfBdmD9E6Gs0Sumo3h5FkAFQ2blYlgljhQgaMyTmz36Pj18rtY4sWZqvdbVboUnZ8MIWxpDDmao5obiViJxf/eRCLVX7D2/VcAUH589P9ARIOvq8El0C+ql8ASd0z4rpM=";
    String sharedKey = "DRFCrfw5n216";
    String mallId = "4042";
    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
    JSONObject respongetTokenSDK;

    LinearLayout layoutStatus;
    Button btnOk;
    ImageView imgStatus;
    TextView tvStatus;
    String strUserID, strAccessToken, aplUse = "OTU";
    PreferenceManager preferenceManager;

    ApiInterfacePayment apiInterfacePayment;
    ApiClientPayment apiClientPayment;

    String words = "";
    EditText edNom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doku_payment_credit_card);

        layoutStatus = findViewById(R.id.layout_status);
        btnOk = findViewById(R.id.btn_ok);
        imgStatus = findViewById(R.id.img_status);
        tvStatus = findViewById(R.id.tv_status);

        layoutStatus.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        preferenceManager = new PreferenceManager(this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        dialogNominal();
    }


    public void retrieveToken(String amount) {
        String invoiceID = transactionID();
        String sessionID = sessionID();
        Log.d("OPPO-1", "GET TOKEN " + sessionID);
        try {
            words = SHA1(amount + ".00" + mallId +
                    sharedKey + invoiceID + 360 + getImei());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("OPPO-1", "basket >> " + "[{\"name\":\"deposit\",\"amount\":\"" + amount + ".00\",\"quantity\":\"1\",\"subtotal\":\"" + amount + ".00\"}]");

        DirectSDK directSDK = new DirectSDK();
        PaymentItems paymentItems = new PaymentItems();
        paymentItems.setDataAmount(amount + ".00");
        paymentItems.setDataBasket("[{\"name\":\"donasi\",\"amount\":\"" + amount + ".00\",\"quantity\":\"1\",\"subtotal\":\"" + amount + ".00\"}]");
        paymentItems.setDataCurrency("360");
        paymentItems.setDataWords(words);
        Log.d("OPPO-1", "word->2 " + words);
        paymentItems.setDataMerchantChain("NA");
        paymentItems.setDataSessionID(sessionID);
        paymentItems.setDataTransactionID(invoiceID);
        paymentItems.setDataMerchantCode(mallId);
        paymentItems.setDataImei(getImei());
        paymentItems.setMobilePhone(PreferenceUtil.getNumberPhone(this));
        paymentItems.isProduction(true); //set ‘true’ for production and ‘false’ for development
        paymentItems.setPublicKey(publicKey); //PublicKey can be obtained from the DOKU Back Office
        directSDK.setCart_details(paymentItems);
        directSDK.setPaymentChannel(1);

        LayoutItems layoutItems = new LayoutItems();
        layoutItems.setToolbarColor("#3ab54a");
        layoutItems.setToolbarTextColor("#FFFFFF");
        directSDK.setLayout(layoutItems);
        //handle response

        directSDK.getResponse(new iPaymentCallback() {
            @Override
            public void onSuccess(final String text) {
                try {
                    respongetTokenSDK = new JSONObject(text);

                    String tokenId = "", pairingCode = "", responseMessage = "", responseCode = "", deviceId = "", amount = "",
                            tokenCode = "", transactionId = "", dataEmail = "", name = "", paymentChannel = "", dataMobilePhone = "";
                    Log.d("OPPO-1", "onSuccess: " + respongetTokenSDK.toString());

                    if (respongetTokenSDK.getString("res_response_code").equalsIgnoreCase("0000")) {
                        JSONObject object = new JSONObject(respongetTokenSDK.toString());
                        tokenId = object.getString("res_token_id");
                        pairingCode = object.getString("res_pairing_code");
                        responseMessage = object.getString("res_response_msg");
                        responseCode = object.getString("res_response_code");
                        deviceId = object.getString("res_device_id");
                        amount = object.getString("res_amount");
                        tokenCode = object.getString("res_token_code");
                        transactionId = object.getString("res_transaction_id");
                        dataEmail = object.getString("res_data_email");
                        name = object.getString("res_name");
                        paymentChannel = object.getString("res_payment_channel");
                        dataMobilePhone = object.getString("res_data_mobile_phone");

                        /*layoutStatus.setVisibility(View.VISIBLE);
                        btnOk.setVisibility(View.VISIBLE);
                        imgStatus.setImageResource(R.drawable.ic_doku_success);
                        tvStatus.setText(responseMessage);*/

                        Log.d("OPPO-1", "word->3 " + words);
                        next(strUserID, strAccessToken, aplUse, "kartu kredit", amount.replace(".00", ""), tokenId, deviceId, pairingCode, transactionId, paymentChannel, words);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String text) {
                String tokenId = "", pairingCode = "", responseMessage = "", responseCode = "", deviceId = "", amount = "",
                        tokenCode = "", transactionId = "", dataEmail = "", name = "", paymentChannel = "", dataMobilePhone = "";
                // Log.d("OPPO-1", "onError->" + text);
                layoutStatus.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                imgStatus.setImageResource(R.drawable.ic_doku_failed);
                try {
                    JSONObject object = new JSONObject(text);
                    responseCode = object.getString("res_response_code");
                    responseMessage = object.getString("res_response_msg");
                    tvStatus.setText(responseMessage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Exception eSDK) {
                eSDK.printStackTrace();
                layoutStatus.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                imgStatus.setImageResource(R.drawable.ic_doku_failed);
                tvStatus.setText(eSDK.getMessage());
            }
        }, getApplicationContext());
    }

    private void next(String userID, String accessToken, String aplUse, String bank, String nominal, String dokuToken,
                      String dokuDeviceId, String dokuPairingCode, String dokuInvoiceNo, String paymentChannel, String word) {

        Log.d("OPPO-1", "next-userID : " + userID);
        Log.d("OPPO-1", "next-accessToken: " + accessToken);
        Log.d("OPPO-1", "next-aplUse: " + aplUse);
        Log.d("OPPO-1", "next-bank: " + bank);
        Log.d("OPPO-1", "next-nominal: " + nominal);
        Log.d("OPPO-1", "next-dokuToken: " + dokuToken);
        Log.d("OPPO-1", "next-dokuDeviceId : " + dokuDeviceId);
        Log.d("OPPO-1", "next-dokuPairingCode: " + dokuPairingCode);
        Log.d("OPPO-1", "next-dokuInvoiceNo : " + dokuInvoiceNo);
        Log.d("OPPO-1", "next-paymentChannel: " + paymentChannel);
        Log.d("OPPO-1", "next-word: " + word);
        Log.d("OPPO-1", "word->4 " + words);

        //VOLLEY====================================================================================
        StringRequest request = null;
        String url = getResources().getString(R.string.url_volley);
        try {
            request = new StringRequest(Request.Method.POST, url + "Donasi/request",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject dataObj = new JSONObject(response);
                                Log.d("OPPO-1", "response->" + dataObj.toString());
                                if (dataObj.getString("errNumber").equalsIgnoreCase("0")) {
                                    if (dataObj.getString("res_response_code").equalsIgnoreCase("0000")) {
                                        layoutStatus.setVisibility(View.VISIBLE);
                                        btnOk.setVisibility(View.VISIBLE);
                                        imgStatus.setImageResource(R.drawable.ic_doku_success);
                                        tvStatus.setText(dataObj.getString("res_response_msg"));
                                    } else {
                                        layoutStatus.setVisibility(View.VISIBLE);
                                        btnOk.setVisibility(View.VISIBLE);
                                        imgStatus.setImageResource(R.drawable.ic_doku_failed);
                                        tvStatus.setText(dataObj.getString("res_response_msg"));
                                    }
                                } else {
                                    layoutStatus.setVisibility(View.VISIBLE);
                                    btnOk.setVisibility(View.VISIBLE);
                                    imgStatus.setImageResource(R.drawable.ic_doku_failed);
                                    tvStatus.setText(dataObj.getString("respMessage"));
                                }


                            } catch (JSONException e) {
                                layoutStatus.setVisibility(View.VISIBLE);
                                btnOk.setVisibility(View.VISIBLE);
                                imgStatus.setImageResource(R.drawable.ic_doku_failed);
                                tvStatus.setText("Kesalahan parse, silahkan coba lagi");
                                Toast.makeText(CCDonasiActivity.this, "Kesalahan parse, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String msgError;
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        msgError = new String(error.networkResponse.data);
                        String message = Utils.responseMessage(statusCode, msgError);
                        Log.d("OPPO-1", "onErrorResponse: " + msgError + "/" + message);
                        Toast.makeText(CCDonasiActivity.this, "" + statusCode + ", " + message, Toast.LENGTH_SHORT).show();
                        layoutStatus.setVisibility(View.VISIBLE);
                        btnOk.setVisibility(View.VISIBLE);
                        imgStatus.setImageResource(R.drawable.ic_doku_failed);
                        tvStatus.setText("" + statusCode + ", " + message);
                    } else {
                        Toast.makeText(CCDonasiActivity.this, "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
                        layoutStatus.setVisibility(View.VISIBLE);
                        btnOk.setVisibility(View.VISIBLE);
                        imgStatus.setImageResource(R.drawable.ic_doku_failed);
                        tvStatus.setText("Tidak ada koneksi");
                    }

                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", userID);
                    params.put("accessToken", accessToken);
                    params.put("aplUse", aplUse);
                    params.put("bank", "KARTU KREDIT");
                    params.put("nominal", nominal);
                    params.put("doku-token", dokuToken);
                    params.put("deviceid", dokuDeviceId);
                    params.put("doku-pairing-code", dokuPairingCode);
                    params.put("doku-invoice-no", dokuInvoiceNo);
                    params.put("payment_chanel", paymentChannel);
                    params.put("words", word);
                    params.put("parseBasket", nominal);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("X-API-KEY", "222");
                    return header;
                }

            };

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        // avoid retry
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyController.getInstance().addToRequestQueue(request);
    }


    private void dialogNominal() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_cc_donasi);
        dialog.setCancelable(false);

        EditText edNom = dialog.findViewById(R.id.ed_nominal);
        final Button btnOk = dialog.findViewById(R.id.btn_ok);
        final Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        final EditText edTujuan = dialog.findViewById(R.id.ed_tujuan_donasi);

        edTujuan.setEnabled(false);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edNom.getText().toString().equalsIgnoreCase("")) {
                    edNom.setError("Kolom nominal tidak boleh kosong");
                    requestFocus(edNom);
                } else if (edNom.getText().toString().equalsIgnoreCase("0")) {
                    edNom.setError("Nilai nominal tidak boleh 0");
                    requestFocus(edNom);
                } else {
                    retrieveToken(edNom.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.cancel();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void dialogSuccess(String tokenId, String pairingCode, String responseMessage, String responseCode, String deviceId,
                               String amount, String tokenCode, String transactionId, String dataEmail, String name,
                               String paymentChannel, String dataMobilePhone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CCDonasiActivity.this);
        builder.setIcon(R.drawable.ic_doku_success);
        builder.setMessage(responseMessage)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
        builder.create();
    }

    private void dialogFailed(String tokenId, String pairingCode, String responseMessage, String responseCode, String deviceId,
                              String amount, String tokenCode, String transactionId, String dataEmail, String name,
                              String paymentChannel, String dataMobilePhone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CCDonasiActivity.this);
        builder.setIcon(R.drawable.ic_doku_failed);
        builder.setMessage(responseMessage)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
        builder.create();
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
            //Log.d("OPPO-1", "getImei: " + imei);
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

    //public static final String DATA = "0123456789";

    public String transactionID() {
        String vTransactionID;
        vTransactionID = "DEP_CCN_" + sdf.format(new Date()) + "" /*+ "_" + getRandomString()*/;
        //Log.d("OPPO-1", "vTransactionID: " + vTransactionID);
        return vTransactionID;
    }

    protected String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

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
        //Log.d("OPPO-1", "sessionID: " + vSessionId);
        return vSessionId;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}