package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    Dialog loadingDialog;
    ApiInterfacePayment mApiInterfacePayment;

    String strApIUse = "OTU", strUserID, strAccessToken;
    PreferenceManager preferenceManager;

    String mbr_id, name_member, upline, mbr_carier, email, no_ktp, alamat, kota, sponsor_name, hp_sponsor, jml_bonus;
    TextView txtNama, txtReferal;
    Button btnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("AYIK", "register:onCreate");
        setContentView(R.layout.activity_register_payment);//layout belum

        txtNama = findViewById(R.id.txt_nama);
        txtReferal = findViewById(R.id.txt_referal);
        btnNext = findViewById(R.id.btn_next);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(Register.this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resgister();
            }
        });
    }


    public void Resgister() {
        loadingDialog = ProgressDialog.show(Register.this, "Harap Tunggu", "Proses Register...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "Resgister: " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Log.d("OPPO-1", "Resgister: " + strApIUse);
        Call<DataProfile> callProfil = mApiInterfacePayment.getTokenRegister(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), strApIUse);
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status);
                    Log.d("OPPO-1", "onResponsemsg: " + msg);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        String tokenRegister = response.body().getRegisterToken();
                        //send token to get data profile
                        exRegister(tokenRegister);

                    } else {
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void exRegister(String tokenRegister) {
        Call<DataProfile> callProfil = mApiInterfacePayment.postRegisterUpline(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), txtReferal.getText().toString(), strApIUse, tokenRegister, txtNama.getText().toString());
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        //run main activity
                        PreferenceUtil.setMemberStatus(Register.this, true);
                        //MainActivity.start(Register.this);
                        startActivity(new Intent(Register.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });

    }

    //cek apakah member sudah terdaftar atau belum
    private void isMember() {
        Call<DataProfile> isMember = mApiInterfacePayment.isMember(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), strApIUse);
        isMember.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String errNumber = response.body().getErrNumber();
                    if (errNumber.equalsIgnoreCase("0")) {
                        //Log.d("OPPO-1", "onResponse: " + status + " , " + msg);
                        MainActivity.start(Register.this);
                        Log.d("AYIK", "isMember:success");
                    } else {
                        Log.d("AYIK", "isMember:failed token");
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("AYIK", "isMember:failed api error");
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
                Log.d("AYIK", "isMember:failed failure");
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

                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(Register.this, "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setLoginStatus(Register.this, false);
                    } else {
                        Toast.makeText(Register.this, "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(Register.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                Toast.makeText(Register.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                //Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public static String getCurrentTime() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Makassar"));
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // PreferenceUtil.setMemberStatus(Register.this,true);
//        MainActivity.start(Register.this);
        startActivity(new Intent(Register.this, MainActivity.class));
        finish();
    }

    /* @Override
    protected void onResume() {
        super.onResume();
        Log.d("AYIK", "register:onResume");
        isMember();
    }*/
}
