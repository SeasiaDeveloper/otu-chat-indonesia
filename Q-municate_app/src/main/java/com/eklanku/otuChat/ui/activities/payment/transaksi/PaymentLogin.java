package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.LoginResponse;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPassword;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;


public class PaymentLogin extends AppCompatActivity {

    TextInputLayout
            layoutPass;
    EditText txtPass;
    TextView lblDaftar,
            lblInfo;
    Button btnLogin;
    Dialog loadingDialog;

    private PreferenceManager preferenceManager;
    private ApiInterfacePayment apiInterfacePayment;
    private String strUserID, strToken, strSecurityCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_login);

        ButterKnife.bind(this);

        initializeResources();

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);
    }

    private void initializeResources() {
        layoutPass = (TextInputLayout) findViewById(R.id.txtLayoutLoginPassword);
        txtPass = (EditText) findViewById(R.id.txtLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        lblDaftar = (TextView) findViewById(R.id.lblLoginRegister);
        lblInfo = (TextView) findViewById(R.id.textResponPaymentLogin);

        btnLogin.setOnClickListener(new loginButtonListener());
        lblDaftar.setOnClickListener(new RegisterLabelListener());
    }

    private class loginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (
                    !validatePassword()) {
                return;
            }

            loadingDialog = ProgressDialog.show(PaymentLogin.this, "Harap Tunggu", "Melakukan proses login");
            loadingDialog.setCanceledOnTouchOutside(true);

            String pass = txtPass.getText().toString().trim();
            String encryptPass = Utils.md5(pass + "x@2564D");
            strToken = getCurrentTime();

            strSecurityCode = Utils.md5(strToken + Utils.md5(encryptPass));

            Log.d("OPPO-1", "userid:" + PreferenceUtil.getNumberPhone(PaymentLogin.this) + ", security:" + strSecurityCode + ", token:" + strToken);
            // RETROFIT=============================================
            Call<LoginResponse> userCall = apiInterfacePayment.postLogin(PreferenceUtil.getNumberPhone(PaymentLogin.this), strToken, strSecurityCode, pass);
            userCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    loadingDialog.dismiss();
                    lblInfo.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        String status = response.body().getStatus();
                        String userID = response.body().getUserID();
                        String accessToken = response.body().getAccessToken();
                        String respMessage = response.body().getRespMessage();
                        if (status.equals("SUCCESS")) {
                            Log.d("OPPO-1", "onResponse: " + userID + " , " + accessToken);
                            preferenceManager.createUserPayment(userID, accessToken, true);
                            PreferenceUtil.setLoginStatus(getApplicationContext(), true);
                            finish();
                        } else {
                            Log.d("OPPO-1", "onResponse: " + userID + " , " + accessToken);
                            lblInfo.setText("Error:\n" + respMessage);
                            lblInfo.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("OPPO-1", "onResponse: " + response.body());
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    loadingDialog.dismiss();
                    Log.d("OPPO-1", "onResponse: ========== " + t.getMessage());
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    lblInfo.setText(getResources().getString(R.string.error_api));
                    lblInfo.setVisibility(View.VISIBLE);
                }
            });
            // RETROFIT=============================================
        }
    }

    private class RegisterLabelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent inResetPass = new Intent(getBaseContext(), ResetPassword.class);
            startActivity(inResetPass);
            finish();
        }
    }

    private boolean validatePassword() {
        String pass = txtPass.getText().toString().trim();

        if (pass.isEmpty()) {
            layoutPass.setError("Kolom password tidak boleh kosong");
            requestFocus(txtPass);
            return false;
        }

        layoutPass.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

}


