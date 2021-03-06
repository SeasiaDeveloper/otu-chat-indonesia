package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.DataKota;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.ListAdapterKota;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.UserCustomData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class updateProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText txtId, txtName, txtidUpline, txtCarrier, txtEmail, txtKtp, txtAddress, txtCity, txtSponsorName, txtSponsorHp,
            txtjabatanupline, txtanggallahir, txnomorhpmember, txtanggaldaftar, txbank, txnorek, txpemilikrek, ed_pin;

    String idmember, name, ktp, tgllahir, alamat, kota, nohp_member, email, karirmember, tgldaftar, bank, norec, pemilikrec, idupline, namaupline, hpsponsor, karirsponsor;

    Dialog loadingDialog;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strApIUse = "OTU", strSecurityCode;

    Button btnUpdate;

    com.eklanku.otuChat.utils.Utils utilsAlert;
    String titleAlert = "Profile";

    Bundle extras;
    ImageView img;

    //AUTOCOMPLETE
    AutoCompleteTextView textCity;
    ArrayList<String> cityList;
    String[] cityArr;
    ArrayAdapter<String> cityAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_payment);

        utilsAlert = new com.eklanku.otuChat.utils.Utils(updateProfile.this);
        extras = getIntent().getExtras();

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
        //x = findViewById(R.id.autoCompleteTextView);

        img = findViewById(R.id.profile_image);
        //===========data bank member
        txbank = findViewById(R.id.txt_bank);
        txnorek = findViewById(R.id.txt_nomor_rekening);
        txpemilikrek = findViewById(R.id.txt_pemilik_rekening);

        //==========data sponsor
        txtidUpline = findViewById(R.id.txt_id_upline);
        txtSponsorName = findViewById(R.id.txt_profile_sponsor_name);
        txtSponsorHp = findViewById(R.id.txt_profile_sponsor_hp);
        txtjabatanupline = findViewById(R.id.txt_jabatan_upline);

        ed_pin = findViewById(R.id.txtPinUpdateProfil);
        btnUpdate = findViewById(R.id.btnupdate);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        //set data profil
        idmember = extras.getString("idmember");
        name = extras.getString("name");
        ktp = extras.getString("ktp");
        tgllahir = extras.getString("tgllahir");
        alamat = extras.getString("alamat");
        kota = extras.getString("kota");
        nohp_member = extras.getString("nohp_member");
        email = extras.getString("email");
        karirmember = extras.getString("karirmember");
        tgldaftar = extras.getString("tgldaftar");
        bank = extras.getString("bank");
        norec = extras.getString("norec");
        pemilikrec = extras.getString("pemilikrec");
        idupline = extras.getString("idupline");
        namaupline = extras.getString("namaupline");
        hpsponsor = extras.getString("hpsponsor");
        karirsponsor = extras.getString("karirsponsor");

        //==========data member==============
        txtId.setText(idmember);
        txtName.setText(name);
        txtKtp.setText(ktp);
        txtanggallahir.setText(tgllahir);

        txtanggallahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //statusdate = 1;
                getDate(v);

            }
        });

        txtAddress.setText(alamat);

        txnomorhpmember.setText(nohp_member);
        txtEmail.setText(email);
        txtCarrier.setText(karirmember);
        txtanggaldaftar.setText(tgldaftar);

        //===============data bank==============
        txbank.setText(bank);
        txnorek.setText(norec);
        txpemilikrek.setText(pemilikrec);

        //==========data sponsor
        txtidUpline.setText(idupline);
        txtSponsorName.setText(namaupline);
        txtSponsorHp.setText(hpsponsor);
        txtjabatanupline.setText(karirsponsor);

        txtId.setEnabled(false);
        txtCarrier.setEnabled(false);
        txtanggaldaftar.setEnabled(false);
        txtidUpline.setEnabled(false);
        txtSponsorName.setEnabled(false);
        txtSponsorHp.setEnabled(false);
        txtjabatanupline.setEnabled(false);
        txbank.setEnabled(false);
        txnorek.setEnabled(false);
        txpemilikrek.setEnabled(false);
        txnomorhpmember.setEnabled(false);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(updateProfile.this, "" + textCity.getText().toString(), Toast.LENGTH_SHORT).show();
                updateProfil();

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(updateProfile.this, "Silahkan ke menu Setting Tab Chat untuk update foto profile", Toast.LENGTH_SHORT).show();
            }
        });


        textCity = findViewById(R.id.autoCompleteTextView);
        textCity.setText(kota);
        cityList = new ArrayList<>();

        checkVisibilityUserIcon();
        getKota();

    }

    private void loadLogoActionBar(String logoUrl) {
        ImageLoader.getInstance().loadImage(logoUrl, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        img.setImageBitmap(loadedBitmap);
                    }
                });
    }

    private void checkVisibilityUserIcon() {
        UserCustomData userCustomData = com.quickblox.q_municate_core.utils.Utils.customDataToObject(AppSession.getSession().getUser().getCustomData());
        if (!TextUtils.isEmpty(userCustomData.getAvatarUrl())) {
            loadLogoActionBar(userCustomData.getAvatarUrl());
        } else {
            img.setImageResource(R.drawable.placeholder_user);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        txtanggallahir.setText(dateFormat.format(cal.getTime()));

    }

    public void getDate(View v) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "Date");
    }

    public void updateProfil() {
        Log.d("OPPO-1", "getProfile: running get profil " + ed_pin.getText().toString());
        loadingDialog = ProgressDialog.show(updateProfile.this, "Harap Tunggu", "Update data profil...");
        loadingDialog.setCanceledOnTouchOutside(true);

        String secCode = ed_pin.getText().toString() + "x@2016ekl";
        strSecurityCode = Utils.md5(secCode);

        Call<DataProfile> callProfil = mApiInterfacePayment.updateProfil(strUserID, strApIUse, strAccessToken, txtEmail.getText().toString(), textCity.getText().toString(),
                txtAddress.getText().toString(), txtKtp.getText().toString(), txtanggallahir.getText().toString(), strSecurityCode);
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

    //api get kota
    /* ArrayList<String> listKota;*/

    public void getKota() {

        Call<DataKota> datakota = mApiInterfacePayment.getKota(strUserID, strAccessToken, strApIUse);
        datakota.enqueue(new Callback<DataKota>() {
            @Override
            public void onResponse(Call<DataKota> call, Response<DataKota> response) {
                if (response.isSuccessful()) {
                    /*listKota = new ArrayList<>();
                    listKota.clear();*/
                    cityList.clear();

                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        ArrayList<DataKota> kota = response.body().getData();
                        for (int i = 0; i < kota.size(); i++) {
                            cityList.add(kota.get(i).getKota());
                            Log.d("AYIK", "->" + i + ":" + kota.get(i).getKota());
                        }

                        Log.d("AYIK", "->" + cityList.size());

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                (updateProfile.this, android.R.layout.simple_list_item_1, cityList);
                        textCity.setThreshold(1); //will start working from first character
                        textCity.setAdapter(adapter);

                        /*ListAdapterKota adapter = new ListAdapterKota(updateProfile.this, cityList);
                        //Getting the instance of AutoCompleteTextView
                        x = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                        x.setThreshold(1);//will start working from first character
                        x.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                        x.setTextColor(Color.RED);*/

                    } else {
                        utilsAlert.globalDialog(updateProfile.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(updateProfile.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataKota> call, Throwable t) {
                utilsAlert.globalDialog(updateProfile.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }


}
