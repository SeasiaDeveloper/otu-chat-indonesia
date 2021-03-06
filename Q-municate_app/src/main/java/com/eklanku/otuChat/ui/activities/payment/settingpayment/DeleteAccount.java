package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccount extends AppCompatActivity {

    Dialog loadingDialog;
    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";
    String strSecurityCode;

    EditText txtPinDelete;
    Button btnDelete;

    com.eklanku.otuChat.utils.Utils utilsAlert;
    String titleAlert = "Hapus Akun";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delete_account);

        utilsAlert = new com.eklanku.otuChat.utils.Utils(DeleteAccount.this);

        //get userid and token from preference
        preferenceManager = new PreferenceManager(DeleteAccount.this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        txtPinDelete = (EditText) findViewById(R.id.txtPinDelete);
        btnDelete = (Button) findViewById(R.id.btn_non_aktif_account);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    public void deleteAccount() {
        loadingDialog = ProgressDialog.show(DeleteAccount.this, "Harap Tunggu", "Non Aktifkan Account...");
        loadingDialog.setCanceledOnTouchOutside(true);


        String secCode = txtPinDelete.getText().toString() + "x@2016ekl";
        strSecurityCode = Utils.md5(secCode);

        Log.d("OPPO-1", "deleteAccount: "+strSecurityCode);

        Call<DataProfile> dataCall = mApiInterfacePayment.deleteAccount(strUserID, strAccessToken, strApIUse, strSecurityCode);
        dataCall.enqueue(new Callback<DataProfile>() {

            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: "+status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(DeleteAccount.this, msg, Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setMemberStatus(DeleteAccount.this, false);
                        logOutPayment();
                    } else {
                        utilsAlert.globalDialog(DeleteAccount.this, titleAlert, msg);
                        //Toast.makeText(DeleteAccount.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(DeleteAccount.this, titleAlert, "1. "+getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(DeleteAccount.this, titleAlert, "2. "+getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void logOutPayment() {
        Log.d("OPPO-1", strUserID + ", " + strAccessToken + ", " + getCurrentTime());
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.postLogoutPayment(strUserID, strAccessToken, getCurrentTime());
        callResetPass.enqueue(new Callback<ResetPassResponse>() {

            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(DeleteAccount.this, "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        //utilsAlert.globalDialog(DeleteAccount.this, titleAlert, msg);

                        PreferenceUtil.setLoginStatus(DeleteAccount.this, false);

                        Intent register = new Intent(getBaseContext(), Register.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        register.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(register);
                        finish();
                        MainActivity.mainActivity.finish();
                    } else {
                        utilsAlert.globalDialog(DeleteAccount.this, titleAlert, msg);
                        //Toast.makeText(DeleteAccount.this, "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    utilsAlert.globalDialog(DeleteAccount.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(DeleteAccount.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                utilsAlert.globalDialog(DeleteAccount.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(DeleteAccount.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payment_transaction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_transaction_confirmation:
                Toast.makeText(this, "Konfirmasi pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_transaction_evidence:
                Toast.makeText(this, "Kirim bukti pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
