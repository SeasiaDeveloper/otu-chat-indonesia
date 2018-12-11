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

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.Utils;
import com.eklanku.otuChat.R;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    Dialog loadingDialog;
    ApiInterfacePayment mApiInterfacePayment;

    String strApIUse = "OTU";

    String mbr_id, name_member, upline, mbr_carier, email, no_ktp, alamat, kota, sponsor_name, hp_sponsor, jml_bonus;
    TextView txtId, txtName, txtidUpline, txtCarrier, txtEmail, txtKtp, txtAddress, txtCity, txtSponsorName, txtSponsorHp,
            txtjabatanupline, txtanggallahir, txnomorhpmember, txtanggaldaftar, txbank, txnorek, txpemilikrek;
    Button btnUpdate;

    Utils utilsAlert;
    String titleAlert = "Profil";

    String strUserID, strAccessToken;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil_payment);//layout belum

        utilsAlert = new Utils(Profile.this);

        //==========data member==============
        txtId = findViewById(R.id.txt_profile_id);
        txtName = findViewById(R.id.txt_profile_name);
        txtKtp = findViewById(R.id.txt_profile_ktp);
        txtanggallahir = findViewById(R.id.txt_tgl_lahir);
        txtAddress = findViewById(R.id.txt_profile_address);
        txtCity = findViewById(R.id.txt_profile_city);
        txnomorhpmember = findViewById(R.id.txt_nomor_hp);
        txtEmail = findViewById(R.id.txt_profile_email);
        txtCarrier = findViewById(R.id.txt_profile_carrier);
        txtanggaldaftar = findViewById(R.id.txt_tanggal_daftar);


        //===========data bank member
        txbank = findViewById(R.id.txt_bank);
        txnorek = findViewById(R.id.txt_nomor_rekening);
        txpemilikrek = findViewById(R.id.txt_pemilik_rekening);

        //==========data sponsor
        txtidUpline = findViewById(R.id.txt_id_upline);
        txtSponsorName = findViewById(R.id.txt_profile_sponsor_name);
        txtSponsorHp = findViewById(R.id.txt_profile_sponsor_hp);
        txtjabatanupline = findViewById(R.id.txt_jabatan_upline);
        btnUpdate = findViewById(R.id.btnupdate);

        preferenceManager = new PreferenceManager(this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        exProfile();


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
        exProfileOnResume();
        Log.d("OPPO-1", "onResume: running");
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
                        //exProfile(tokenProfile);
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
                        //exProfile(tokenProfile);
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

    public void exProfile() {
        Log.d("OPPO-1", "getProfile: running exProfil");
        Call<DataDetailProfile> callProfil = mApiInterfacePayment.getProfile(strUserID, strApIUse, strAccessToken);
        callProfil.enqueue(new Callback<DataDetailProfile>() {
            @Override
            public void onResponse(Call<DataDetailProfile> call, Response<DataDetailProfile> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String userID = response.body().getUserID();
                    final List<DataDetailProfile> result = response.body().getData();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {

                        //==========data member==============
                        txtId.setText(userID);
                        txtName.setText(result.get(0).getO_nama_member());
                        txtKtp.setText(result.get(0).getNo_ktp());
                        txtanggallahir.setText(result.get(0).getO_tgl_lahir());
                        txtAddress.setText(result.get(0).getO_alamat());
                        txtCity.setText(result.get(0).getO_kota());
                        txnomorhpmember.setText(result.get(0).getO_hp());
                        txtEmail.setText(result.get(0).getO_mail());
                        txtCarrier.setText(result.get(0).getO_jabatanmember());
                        txtanggaldaftar.setText(result.get(0).getO_tgl_daftar());

                        //===============data bank==============
                        txbank.setText(result.get(0).getO_bank());
                        txnorek.setText(result.get(0).getO_norec());
                        txpemilikrek.setText(result.get(0).getO_pemilikrekening());

                        //==========data sponsor
                        txtidUpline.setText(result.get(0).getO_id_sponsor());
                        txtSponsorName.setText(result.get(0).getO_nama_sponsor());
                        txtSponsorHp.setText(result.get(0).getO_hp_sponsor());
                        txtjabatanupline.setText(result.get(0).getO_jabatan_sponsor());


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
                //utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });

    }

    public void exProfileOnResume() {
        loadingDialog = ProgressDialog.show(Profile.this, "Harap Tunggu", "Load data profil...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "getProfile: running exProfil");
        Call<DataDetailProfile> callProfil = mApiInterfacePayment.getProfile(strUserID, strApIUse, strAccessToken);
        callProfil.enqueue(new Callback<DataDetailProfile>() {
            @Override
            public void onResponse(Call<DataDetailProfile> call, Response<DataDetailProfile> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String userID = response.body().getUserID();
                    final List<DataDetailProfile> result = response.body().getData();
                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {

                        //==========data member==============
                        txtId.setText(userID);
                        txtName.setText(result.get(0).getO_nama_member());
                        txtKtp.setText(result.get(0).getNo_ktp());
                        txtanggallahir.setText(result.get(0).getO_tgl_lahir());
                        txtAddress.setText(result.get(0).getO_alamat());
                        txtCity.setText(result.get(0).getO_kota());
                        txnomorhpmember.setText(result.get(0).getO_hp());
                        txtEmail.setText(result.get(0).getO_mail());
                        txtCarrier.setText(result.get(0).getO_jabatanmember());
                        txtanggaldaftar.setText(result.get(0).getO_tgl_daftar());

                        //===============data bank==============
                        txbank.setText(result.get(0).getO_bank());
                        txnorek.setText(result.get(0).getO_norec());
                        txpemilikrek.setText(result.get(0).getO_pemilikrekening());

                        //==========data sponsor
                        txtidUpline.setText(result.get(0).getO_id_sponsor());
                        txtSponsorName.setText(result.get(0).getO_nama_sponsor());
                        txtSponsorHp.setText(result.get(0).getO_hp_sponsor());
                        txtjabatanupline.setText(result.get(0).getO_jabatan_sponsor());


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
                //utilsAlert.globalDialog(Profile.this, titleAlert, getResources().getString(R.string.error_api));
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

    public void EXupdateProfil() {


        Intent intent = new Intent(getBaseContext(), updateProfile.class);
        intent.putExtra("idmember", txtId.getText().toString());
        intent.putExtra("name", txtName.getText().toString());
        intent.putExtra("ktp", txtKtp.getText().toString());
        intent.putExtra("tgllahir", txtanggallahir.getText().toString());
        intent.putExtra("alamat", txtAddress.getText().toString());
        intent.putExtra("kota", txtCity.getText().toString());
        intent.putExtra("nohp_member", txnomorhpmember.getText().toString());
        intent.putExtra("email", txtEmail.getText().toString());
        intent.putExtra("karirmember", txtCarrier.getText().toString());
        intent.putExtra("tgldaftar", txtanggaldaftar.getText().toString());

        intent.putExtra("bank", txbank.getText().toString());
        intent.putExtra("norec", txnorek.getText().toString());
        intent.putExtra("pemilikrec", txpemilikrek.getText().toString());

        intent.putExtra("idupline", txtidUpline.getText().toString());
        intent.putExtra("namaupline", txtSponsorName.getText().toString());
        intent.putExtra("hpsponsor", txtSponsorHp.getText().toString());
        intent.putExtra("karirsponsor", txtjabatanupline.getText().toString());
        startActivity(intent);

    }
}
