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
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.Utils;
import com.eklanku.otuChat.R;;
import java.util.HashMap;

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
    TextView txtNama, txtReferal, txPass, txPin, txEmail;
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
        txPass = findViewById(R.id.txt_password);
        txPin = findViewById(R.id.txt_pin);
        txEmail = findViewById(R.id.txt_email);
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

        if (referrerDataRaw != null && referrerDataRaw.startsWith("EKL")) {
            txtReferal.setText(referrerDataRaw);
        } else {
            txtReferal.setText("EKL0000000");
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptNext();
            }
        });
    }

    private void attemptNext() {

        // Reset errors.
        txEmail.setError(null);
        txPass.setError(null);
        txPin.setError(null);

        // Store values at the time of the login attempt.
        String email = txEmail.getText().toString();
        String password = txPass.getText().toString();
        String pin = txPin.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            txPass.setError(getString(R.string.error_invalid_password));
            focusView = txPass;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txEmail.setError(getString(R.string.error_field_required));
            focusView = txEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txEmail.setError(getString(R.string.error_invalid_email));
            focusView = txEmail;
            cancel = true;
        }

        if (!TextUtils.isEmpty(pin) && !isPinValid(pin)) {
            txPin.setError(getString(R.string.error_invalid_password));
            focusView = txPin;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            String txt = txtReferal.getText().toString().trim();
            if (txt.equals("") || TextUtils.isEmpty(txt)) {
                dialogReferalEmpty("Notice", "Your have not fill Referal ID, want continue");
            } else {
                Register();
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }

    private boolean isPinValid(String pin) {
        //TODO: Replace this with your own logic
        return pin.length() >= 6;
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
                        Register();

                    }
                });
        builder.show();
    }


    public void Register() {
        Log.d("OPPO-1", "strSecurityCode: " + PreferenceUtil.getNumberPhone(this));
        Call<DataProfile> callProfil = mApiInterfacePayment.postRegisterUpline(PreferenceUtil.getNumberPhone(this), txtReferal.getText().toString(), strApIUse,
                txtNama.getText().toString(), txEmail.getText().toString(), txPass.getText().toString(), txPin.getText().toString());

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
                    utilsAlert.globalDialog(Register.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                Log.d("OPPO-1", "onFailure: " + t.getMessage());
                progressDialog.dismiss();
                utilsAlert.globalDialog(Register.this, titleAlert, "3. " + getResources().getString(R.string.error_api));

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Register.this, MainActivity.class));
        finish();
    }

}
