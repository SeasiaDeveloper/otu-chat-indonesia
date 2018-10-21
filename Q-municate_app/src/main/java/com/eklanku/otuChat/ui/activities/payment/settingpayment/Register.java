package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.eklanku.otuChat.utils.Utils;

import com.eklanku.otuChat.R;;

import com.quickblox.q_municate_core.models.AppSession;

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
    ProgressDialog progressDialog;
    ApiInterfacePayment mApiInterfacePayment;

    String strApIUse = "OTU", strUserID, strAccessToken;
    PreferenceManager preferenceManager;

    String mbr_id, name_member, upline, mbr_carier, email, no_ktp, alamat, kota, sponsor_name, hp_sponsor, jml_bonus;
    TextView txtNama, txtReferal;
    Button btnNext;

    //rina
    public String firstLaunch, referrerDate, referrerDataRaw, referrerDataDecoded;

    Utils utilsAlert;
    String titleAlert = "Register";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_payment);//layout belum
        utilsAlert = new Utils(Register.this);

        progressDialog = new ProgressDialog(this);

        txtNama = findViewById(R.id.txt_nama);
        txtReferal = findViewById(R.id.txt_referal);
        btnNext = findViewById(R.id.btn_next);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(Register.this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        HashMap<String, String> userReferal = preferenceManager.getUserDetailsPayment();
        firstLaunch = userReferal.get(preferenceManager.KEY_REFF_LAUNCH);
        referrerDate = userReferal.get(preferenceManager.KEY_REFF_DATE);
        referrerDataRaw = userReferal.get(preferenceManager.KEY_REFF_RAW);
        referrerDataDecoded = userReferal.get(preferenceManager.KEY_REFF_DECODE);

        if (referrerDataRaw != null) {
            txtReferal.setText(referrerDataRaw);
        } else {
            txtReferal.setText("EKL0000000");
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = txtReferal.getText().toString().trim();
                if (txt.equals("") || TextUtils.isEmpty(txt)) {
                    dialogReferalEmpty("Notice", "Your have not fill Referal ID, want continue");
                } else {
                    Resgister();
                }
            }
        });
    }

    public void dialogReferalEmpty(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();

                    }
                });
        builder.setPositiveButton("Iya",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        Resgister();

                    }
                });
        builder.show();
    }


    public void Resgister() {
        progressDialog.setMessage("Proses Register...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //okHttpClient

        Call<DataProfile> callProfil = mApiInterfacePayment.getTokenRegister(PreferenceUtil.getNumberPhone(this), strApIUse);
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                //loadingDialog.dismiss();
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equalsIgnoreCase("SUCCESS")) {
                        String tokenRegister = response.body().getRegisterToken();
                        //send token to get data profile
                        exRegister(tokenRegister);

                    } else {
                        progressDialog.dismiss();
                        utilsAlert.globalDialog(Register.this, titleAlert, msg);
                    }
                } else {
                    progressDialog.dismiss();
                    utilsAlert.globalDialog(Register.this, titleAlert, getResources().getString(R.string.error_api));
                }

            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                //loadingDialog.dismiss();
                progressDialog.dismiss();
                utilsAlert.globalDialog(Register.this, titleAlert, getResources().getString(R.string.error_api));

            }
        });
    }

    public void exRegister(String tokenRegister) {
        Call<DataProfile> callProfil = mApiInterfacePayment.postRegisterUpline(PreferenceUtil.getNumberPhone(this), txtReferal.getText().toString(), strApIUse, tokenRegister, txtNama.getText().toString());
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                //loadingDialog.dismiss();
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equalsIgnoreCase("SUCCESS")) {

                        Toast.makeText(Register.this, "Register berhasil", Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setMemberStatus(Register.this, true);
                        startActivity(new Intent(Register.this, MainActivity.class));
                        finish();
                    } else {
                        utilsAlert.globalDialog(Register.this, titleAlert, msg);

                    }
                } else {
                    utilsAlert.globalDialog(Register.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                progressDialog.dismiss();
                utilsAlert.globalDialog(Register.this, titleAlert, getResources().getString(R.string.error_api));

            }
        });

    }

    //cek apakah member sudah terdaftar atau belum
    private void isMember() {
        Call<DataProfile> isMember = mApiInterfacePayment.isMember(AppSession.getSession().getUser().getLogin(), "OTU");

//        Call<DataProfile> isMember = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(this)), strApIUse);
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
