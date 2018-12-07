package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models2.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.payment.models2.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest2.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest2.ApiInterfacePayment;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    String strApIUse = "OTU";

    ApiInterfacePayment mApiInterfacePayment;
    Dialog loadingDialog;
    EditText txtNewPass, txtOTP;
    String strToken, strSecurityCode;
    Button btnReset, btnKeyOTP;

    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;

    String getToken = "";

    com.eklanku.otuChat.utils.Utils utilsAlert;
    String titleAlert = "Reset Password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        utilsAlert = new com.eklanku.otuChat.utils.Utils(ResetPassword.this);

        txtNewPass = (EditText) findViewById(R.id.txtResetPassword);
        txtOTP = findViewById(R.id.txtKeyOTP);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnKeyOTP = findViewById(R.id.btnkeyOTP);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendresetPass();
            }
        });

        btnKeyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetKeyOTP();
            }
        });


    }

    public void sendresetPass() {
        String pass = txtNewPass.getText().toString().trim();
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.postResetpass(PreferenceUtil.getNumberPhone(this), strApIUse, getToken, pass, txtOTP.getText().toString());
        callResetPass.enqueue(new Callback<ResetPassResponse>() {
            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "resetPass: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(getBaseContext(), "SUCCESS RESET PASS [" + msg + "]", Toast.LENGTH_SHORT).show();
                        logOutPayment();
                    }

                } else {
                    utilsAlert.globalDialog(ResetPassword.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                utilsAlert.globalDialog(ResetPassword.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public static String getCurrentTime() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Makassar"));
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            //Log.d("AYIK", "time asia :"+ currentDateTime);

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void resetPass() {
        loadingDialog = ProgressDialog.show(ResetPassword.this, "Harap Tunggu", "Reset Password...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.getTokenResetpass(PreferenceUtil.getNumberPhone(this), strApIUse);
        callResetPass.enqueue(new Callback<ResetPassResponse>() {
            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    Log.d("OPPO-1", "getToken: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        getToken = response.body().getResetToken();
                        //sendresetPass(getToken);
                        finish();
                    }

                    /*else {
                        getToken = "null";
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    utilsAlert.globalDialog(ResetPassword.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(ResetPassword.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void logOutPayment() {
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.postLogoutPayment(strUserID, strAccessToken, getCurrentTime());
        callResetPass.enqueue(new Callback<ResetPassResponse>() {
            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    Log.d("OPPO-1", "logOutPayment: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(getBaseContext(), "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setLoginStatus(getApplicationContext(), false);
                        finish();
                    }
                    /*else {
                        Toast.makeText(getBaseContext(), "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    utilsAlert.globalDialog(ResetPassword.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                utilsAlert.globalDialog(ResetPassword.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void GetKeyOTP(){
        loadingDialog = ProgressDialog.show(ResetPassword.this, "Harap Tunggu", "Mendapatkan Kode OTP...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<ResetPINResponse> reset_pass = mApiInterfacePayment.getKeySMS(strUserID, strAccessToken, strApIUse);
        reset_pass.enqueue(new Callback<ResetPINResponse>() {
            @Override
            public void onResponse(Call<ResetPINResponse> call, Response<ResetPINResponse> response) {
                loadingDialog.dismiss();
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();
                    if(status.equalsIgnoreCase("SUCCESS")){
                        Toast.makeText(getBaseContext(), "[" + status + "] Get Key OTP", Toast.LENGTH_SHORT).show();
                    }else{
                        utilsAlert.globalDialog(ResetPassword.this, titleAlert, respMessage);
                    }
                }else{
                    utilsAlert.globalDialog(ResetPassword.this, titleAlert, "1. "+getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<ResetPINResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(ResetPassword.this, titleAlert, "2. "+getResources().getString(R.string.error_api));
                Log.d("OPPO-1", "onFailure: "+t.getMessage());
            }
        });
    }


}
