package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiClientProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfaceProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    Dialog loadingDialog;
    ApiInterfacePayment mApiInterfacePayment;

    String strApIUse = "OTU";

    String mbr_id, name_member, upline, mbr_carier, email, no_ktp, alamat, kota, sponsor_name, hp_sponsor, jml_bonus;
    TextView txtId, txtName,txtUpline, txtCarrier, txtEmail, txtKtp, txtAddress, txtCity, txtSponsorName, txtSponsorHp, txtBonus;
    Button btnUpdate;

    Utils utilsAlert;
    String titleAlert = "Profil";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil_payment);//layout belum

        utilsAlert = new Utils(Profile.this);

        txtId= findViewById(R.id.txt_profile_id);
        txtName= findViewById(R.id.txt_profile_name);
        txtUpline= findViewById(R.id.txt_profile_upline);
        txtCarrier= findViewById(R.id.txt_profile_carrier);
        txtEmail= findViewById(R.id.txt_profile_email);
        txtKtp= findViewById(R.id.txt_profile_ktp);
        txtAddress= findViewById(R.id.txt_profile_address);
        txtCity= findViewById(R.id.txt_profile_city);
        txtSponsorName= findViewById(R.id.txt_profile_sponsor_name);
        txtSponsorHp= findViewById(R.id.txt_profile_sponsor_hp);
        txtBonus= findViewById(R.id.txt_profile_bonus);
        btnUpdate = findViewById(R.id.btnupdate);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        getProfile();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EXupdateProfil();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileonResume();
    }

    public void getProfile() {
        Log.d("OPPO-1", "getProfile: running get profil");
        loadingDialog = ProgressDialog.show(Profile.this, "Harap Tunggu", "Load data profil...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<DataProfile> callProfil = mApiInterfacePayment.getTokenProfile(PreferenceUtil.getNumberPhone(this), strApIUse);
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        String tokenProfile = response.body().getProfileToken();
                        //send token to get data profile
                        exProfile(tokenProfile);
                    } else {
                        utilsAlert.globalDialog(Profile.this, titleAlert, msg);
                        //Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void getProfileonResume() {
        Call<DataProfile> callProfil = mApiInterfacePayment.getTokenProfile(PreferenceUtil.getNumberPhone(this), strApIUse);
        callProfil.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        String tokenProfile = response.body().getProfileToken();
                        //send token to get data profile
                        exProfile(tokenProfile);
                    } else {
                        utilsAlert.globalDialog(Profile.this, titleAlert, msg);
                        //Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void exProfile(String tokenProfile) {
        Log.d("OPPO-1", "getProfile: running exProfil");
        Call<DataDetailProfile> callProfil = mApiInterfacePayment.getProfile(PreferenceUtil.getNumberPhone(this), strApIUse, tokenProfile);
        callProfil.enqueue(new Callback<DataDetailProfile>() {
            @Override
            public void onResponse(Call<DataDetailProfile> call, Response<DataDetailProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    final List<DataDetailProfile> result = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        mbr_id = result.get(0).getMbr_id();
                        name_member = result.get(0).getName_member();
                        upline = result.get(0).getUpline();
                        mbr_carier = result.get(0).getMbr_carier();
                        email = result.get(0).getEmail();
                        no_ktp = result.get(0).getNo_ktp();
                        alamat = result.get(0).getAlamat();
                        kota = result.get(0).getKota();
                        sponsor_name = result.get(0).getSponsor_name();
                        hp_sponsor = result.get(0).getHp_sponsor();
                        jml_bonus = result.get(0).getJml_bonus();

                        Log.d("OPPO-1", "mbr_id: "+mbr_id);
                        Log.d("OPPO-1", "name_member: "+name_member);
                        Log.d("OPPO-1", "upline: "+upline);
                        Log.d("OPPO-1", "mbr_carier: "+mbr_carier);
                        Log.d("OPPO-1", "email: "+email);
                        Log.d("OPPO-1", "no_ktp: "+no_ktp);
                        Log.d("OPPO-1", "alamat: "+alamat);
                        Log.d("OPPO-1", "kota: "+kota);
                        Log.d("OPPO-1", "sponsor_name: "+sponsor_name);
                        Log.d("OPPO-1", "hp_sponsor: "+hp_sponsor);
                        Log.d("OPPO-1", "jml_bonus: "+jml_bonus);

                        txtId.setText(mbr_id);
                        txtName.setText(name_member);
                        txtUpline.setText(upline);
                        txtCarrier.setText(mbr_carier);
                        txtEmail.setText(email);
                        txtKtp.setText(no_ktp);
                        txtAddress.setText(alamat);
                        txtCity.setText(kota);
                        txtSponsorName.setText(sponsor_name);
                        txtSponsorHp.setText(hp_sponsor);
                        txtBonus.setText(jml_bonus);

                    } else {
                        utilsAlert.globalDialog(Profile.this, titleAlert, response.body().getErrNumber());
                        //Toast.makeText(getBaseContext(), "FAILED GET TOKEN ["+result+"]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DataDetailProfile> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
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

    public void EXupdateProfil(){
        if(txtEmail.getText().toString().equals("")&&
                txtKtp.getText().toString().equals("")&&
                txtAddress.getText().toString().equals("")&&
                txtCity.getText().toString().equals("")){

            Intent intent = new Intent(getBaseContext(), updateProfile.class);
            intent.putExtra("idmember", txtId.getText().toString());
            intent.putExtra("name", txtName.getText().toString());
            intent.putExtra("upline", txtUpline.getText().toString());
            intent.putExtra("carrier", txtCarrier.getText().toString());
            intent.putExtra("sponsorname", txtSponsorName.getText().toString());
            intent.putExtra("sponsorhp", txtSponsorHp.getText().toString());
            intent.putExtra("bonus", txtBonus.getText().toString());
            startActivity(intent);
        }else{
            Toast.makeText(getBaseContext(), "Data Sudah di Lengkapi", Toast.LENGTH_SHORT).show();
        }
    }
}
