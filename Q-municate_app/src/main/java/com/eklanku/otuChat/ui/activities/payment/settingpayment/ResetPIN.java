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
import com.eklanku.otuChat.ui.activities.payment.models.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPIN extends AppCompatActivity {

    EditText txtNewPin, txtkeySMS;
    Button btnReset, btnGetKey;

    PreferenceManager preferenceManager;
    ApiInterfacePayment mApiInterfacePayment;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";
    Dialog loadingDialog;

    Utils utilsAlert;
    String titleAlert = "Reset PIN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);

        utilsAlert = new Utils(ResetPIN.this);

        txtNewPin = (EditText) findViewById(R.id.txtResetPIN);
        txtkeySMS = (EditText) findViewById(R.id.txtResetKeySMS);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnGetKey = (Button) findViewById(R.id.btnGetKey);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        btnGetKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getKey();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apprResetPIN();
            }
        });

    }

    public void getKey() {
        loadingDialog = ProgressDialog.show(ResetPIN.this, "Harap Tunggu", "Load Reset PIN...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "getKey: "+strUserID);
        Log.d("OPPO-1", "getKey: "+strApIUse);
        Log.d("OPPO-1", "getKey: "+strAccessToken);
        Call<ResetPINResponse> callResetPass = mApiInterfacePayment.postResetPin(strUserID, strApIUse, strAccessToken);
        callResetPass.enqueue(new Callback<ResetPINResponse>() {
            @Override
            public void onResponse(Call<ResetPINResponse> call, Response<ResetPINResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "getKey: "+status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        //jalankan approve
                    }else{
                        utilsAlert.globalDialog(ResetPIN.this, titleAlert, msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPINResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(ResetPIN.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void apprResetPIN(){
        loadingDialog = ProgressDialog.show(ResetPIN.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        String pin = txtNewPin.getText().toString().trim();
        String key = txtkeySMS.getText().toString().trim();
        Call<ResetPINResponse> callResetPass = mApiInterfacePayment.apprResetPin(strUserID, strAccessToken, strApIUse, key, pin);
        callResetPass.enqueue(new Callback<ResetPINResponse>() {
            @Override
            public void onResponse(Call<ResetPINResponse> call, Response<ResetPINResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "apprResetPIN: "+status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        //utilsAlert.globalDialog(ResetPIN.this, titleAlert, msg);
                        Toast.makeText(getBaseContext(), status+":"+msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        utilsAlert.globalDialog(ResetPIN.this, titleAlert, msg);
                        //Toast.makeText(getBaseContext(), status+":"+msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPINResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(ResetPIN.this, titleAlert, getResources().getString(R.string.error_api));
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

}
