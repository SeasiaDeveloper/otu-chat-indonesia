package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
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
    EditText txtNewPass;
    String strToken, strSecurityCode;
    Button btnReset;

    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;

    String getToken = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);
        txtNewPass = (EditText) findViewById(R.id.txtResetPassword);
        btnReset = (Button) findViewById(R.id.btnReset);

        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });

        Log.d("AYIK", "time asia/makassar :"+ getCurrentTime());


    }

    public void sendresetPass(String token) {
        String pass = txtNewPass.getText().toString().trim();
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.postResetpass(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), strApIUse, getToken, pass);
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
                    /*else {
                        Toast.makeText(getBaseContext(), "FAILED RESET PASS [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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
        loadingDialog = ProgressDialog.show(ResetPassword.this, "Harap Tunggu", "Load Reset Password...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.getTokenResetpass(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), strApIUse);
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
                        sendresetPass(getToken);
                        finish();
                    }

                    /*else {
                        getToken = "null";
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

}
