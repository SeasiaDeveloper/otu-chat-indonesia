package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class updateProfile extends AppCompatActivity {

    EditText ed_idmember, ed_name, ed_upline, ed_carrier, ed_email, ed_ktp, ed_addr, ed_city, ed_sponsor_name,
            ed_sponsor_hp, ed_bonus, ed_pin;

    String idmember, name, upline, carrier, sponsor_name, sponsor_hp, bonus;

    Dialog loadingDialog;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strApIUse = "OTU", strSecurityCode;

    Button btnUpdate;

    com.eklanku.otuChat.utils.Utils utilsAlert;
    String titleAlert = "Profile";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile_payment);

        utilsAlert = new com.eklanku.otuChat.utils.Utils(updateProfile.this);

        ed_idmember = findViewById(R.id.edt_idmember);
        ed_name = findViewById(R.id.edt_name);
        ed_upline = findViewById(R.id.edt_upline);
        ed_carrier = findViewById(R.id.edt_carreer);
        ed_email = findViewById(R.id.edt_email);
        ed_ktp = findViewById(R.id.edt_ktp);
        ed_addr = findViewById(R.id.edt_addres);
        ed_city = findViewById(R.id.edt_city);
        ed_sponsor_name = findViewById(R.id.edt_sponsor);
        ed_sponsor_hp = findViewById(R.id.edt_sponsor_hp);
        ed_bonus = findViewById(R.id.edt_bonus);
        ed_pin = findViewById(R.id.txt_pin_update);

        btnUpdate = findViewById(R.id.btnupdate);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);


        idmember = getIntent().getExtras().getString("idmember", "");
        name = getIntent().getExtras().getString("name", "");
        upline = getIntent().getExtras().getString("upline", "");
        carrier = getIntent().getExtras().getString("carrier", "");
        sponsor_name = getIntent().getExtras().getString("sponsorname", "");
        sponsor_hp = getIntent().getExtras().getString("sponsorhp", "");
        bonus = getIntent().getExtras().getString("bonus", "");

        ed_idmember.setText(idmember);
        ed_name.setText(name);
        ed_upline.setText(upline);
        ed_carrier.setText(carrier);
        ed_sponsor_name.setText(sponsor_name);
        ed_sponsor_hp.setText(sponsor_hp);
        ed_bonus.setText(bonus);

        requestFocus(ed_email);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfil();

            }
        });
    }

    public void updateProfil() {
        Log.d("OPPO-1", "getProfile: running get profil "+ed_pin.getText().toString());
        loadingDialog = ProgressDialog.show(updateProfile.this, "Harap Tunggu", "Update data profil...");
        loadingDialog.setCanceledOnTouchOutside(true);

        String secCode = ed_pin.getText().toString() + "x@2016ekl";
        strSecurityCode = Utils.md5(secCode);

        Log.d("OPPO-1", "strUserID: "+strUserID);
        Log.d("OPPO-1", "strApIUse: "+strApIUse);
        Log.d("OPPO-1", "strAccessToken: "+strAccessToken);
        Log.d("OPPO-1", "strSecurityCode: "+strSecurityCode);
        Log.d("OPPO-1", "ed_email.getText().toString(): "+ed_email.getText().toString());
        Log.d("OPPO-1", "ed_city.getText().toString(): "+ed_city.getText().toString());
        Log.d("OPPO-1", "ed_addr.getText().toString(): "+ed_addr.getText().toString());
        Log.d("OPPO-1", "ed_ktp.getText().toString(): "+ed_ktp.getText().toString());

        Call<DataProfile> callProfil = mApiInterfacePayment.updateProfile(strUserID, strApIUse, strAccessToken, strSecurityCode,
                ed_email.getText().toString(), ed_city.getText().toString(), ed_addr.getText().toString(), ed_ktp.getText().toString());
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        finish();
                        utilsAlert.globalDialog(updateProfile.this, titleAlert, msg);
                        //Toast.makeText(getBaseContext(), "SUCCESS UPDATE PROFIL [" + msg + "]", Toast.LENGTH_SHORT).show();
                    } else {
                        utilsAlert.globalDialog(updateProfile.this, titleAlert, msg);
                        //Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(updateProfile.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(updateProfile.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
