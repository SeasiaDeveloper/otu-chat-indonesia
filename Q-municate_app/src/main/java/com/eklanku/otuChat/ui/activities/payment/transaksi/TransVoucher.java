package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataNominal;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataNominal;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransVoucher extends AppCompatActivity {

    private static String[] jenis_voucher = {"Cherry Credit", "Garena", "Gemscool", "Googleplay US", "Lyto",
            "Megaxus", "PSN Indonesia", "Qeon", "Steam Wallet IDR",
            "Steam Wallet US", "Wavegame", "ZYNGA","MOBILE LEGEND"};
    private static String[] kode_voucher  = {"VCR", "GR", "GS", "GPUS", "LT",
            "MX", "PSID", "Q", "SID",
            "SUS", "WG", "ZY","ML"};
    private static String load_type       = "game_nominal";

    SharedPreferences prefs;
    Spinner spnVoucher, spnNominal;
    EditText txtNo;
//    TextInputLayout layoutNo;
    Button btnBayar;
    String //id_member,
            load_id = "VCR", selected_nominal;
    ApiInterface mApiInterface;
    Dialog loadingDialog;
    String[] nominal;
    String[] denom;
    String[] point;
    ArrayAdapter<String> adapter;

    SpinnerPpobAdapter spinnerPpobAdapter;
    SpinnerPaymentAdapter spinnerPaymentAdapter;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID,strAccessToken,strAplUse = "OTU";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_voucher);

        ButterKnife.bind(this);

        prefs         = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnVoucher    = (Spinner) findViewById(R.id.spnTransVoucherJenis);
        spnNominal    = (Spinner) findViewById(R.id.spnTransVoucherNominal);
        txtNo         = (EditText) findViewById(R.id.txtTransVoucherNo);
//        layoutNo      = (TextInputLayout) findViewById(R.id.txtLayoutTransVoucherNo);
        btnBayar      = (Button) findViewById(R.id.btnTransVoucherBayar);
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("CEK TAGIHAN");
        txtNo.addTextChangedListener(new txtWatcher(txtNo));
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, jenis_voucher);
//        spnVoucher.setAdapter(adapter);

        spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), jenis_voucher);
        spnVoucher.setAdapter(spinnerPpobAdapter);
        spnVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load_id = kode_voucher[position];
                switch (load_id) {
                    case "ML":
                        txtNo.setHint("Nomor ID Mobile Legend");
                        break;
                    default:
                        txtNo.setHint("Masukkan Nomor Telepon");
                }
                load_data();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !validateIdpel() ) {
                    return;
                }
                cek_transaksi();
            }
        });
    }


    private class txtWatcher implements TextWatcher {

        private View view;

        private txtWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            validateIdpel();
        }
    }

    public void load_data() {
        loadingDialog = ProgressDialog.show(TransVoucher.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "cek_transaksi - transvoucher: "+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, "085334059170");
        dataCall.enqueue(new Callback<LoadDataResponse>() {

            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nominal = new String[0];
                denom = new String[0];
                point = new String[0];

                spinnerPaymentAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                spnNominal.setAdapter(spinnerPaymentAdapter);

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("OK")) {
                        final List<DataNominal> result = response.body().getResult();
                        nominal = new String[result.size()];
                        denom = new String[result.size()];
                        point = new String[result.size()];
                        selected_nominal = result.get(0).getProductKode();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        DecimalFormat decimal = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setCurrencySymbol("");
                        dfs.setMonetaryDecimalSeparator(',');
                        dfs.setGroupingSeparator('.');
                        decimal.setDecimalFormatSymbols(dfs);

                        for (int i = 0; i < result.size(); i++) {

                            Log.e("OPPO debug", "onResponse: " + result.get(i).getProductName());
                            Log.e("OPPO debug", "onResponse: " + result.get(i).getHargaJual());
                            Log.e("OPPO debug", "onResponse: " + result.get(i).getEpoint());

                            denom[i] = result.get(i).getProductName();
                            nominal[i] = format.format(result.get(i).getHargaJual());
                            if (result.get(i).getEpoint() != null) {
                                Log.e("OPPO debug", "onResponse2: " + result.get(i).getEpoint());
                                point[i] = "point : " + decimal.format(result.get(i).getEpoint());
                            } else {
                                point[i] = "point : " + decimal.format(0);
                            }

                        }

                        spinnerPaymentAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                        spnNominal.setAdapter(spinnerPaymentAdapter);
                        spnNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_nominal = result.get(position).getProductKode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {

            }
        });
    }

    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransVoucher.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, selected_nominal, txtNo.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), strAplUse);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error  = response.body().getRespMessage();

                    if ( status.equals("SUCCESS") ) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi       = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("transaksi", trans.get(0).getTransaksi());
                        inKonfirmasi.putExtra("harga", trans.get(0).getHarga());
                        inKonfirmasi.putExtra("id_pel", trans.get(0).getIdPel());
                        inKonfirmasi.putExtra("jenis", trans.get(0).getJenis());
                        inKonfirmasi.putExtra("pin", trans.get(0).getPin());
                        inKonfirmasi.putExtra("cmd_save", trans.get(0).getCmdSave());
                        startActivity(inKonfirmasi);
                    } else {
                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();

        if (id_pel.isEmpty()) {
            Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            //layoutNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

        if (id_pel.length() < 8) {
            Toast.makeText(this, "Masukkan minimal 8 digit nomor", Toast.LENGTH_SHORT).show();
//            layoutNo.setError("Masukkan minimal 8 digit nomor");
            requestFocus(txtNo);
            return false;
        }

        //layoutNo.setErrorEnabled(false);
        return true;
    }
    private void requestFocus(View view) {
        if ( view.requestFocus() ) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /*==========================================payment lama==========================================*/
    /*
    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransVoucher.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Log.d("OPPO-1", "cek_transaksi - transvucher: "+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), load_id, selected_nominal, txtNo.getText().toString(), "game");
//        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli("085334059170", load_id, selected_nominal, txtNo.getText().toString(), "game");
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error  = response.body().getError();

                    if ( status.equals("OK") ) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi       = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("transaksi", trans.get(0).getTransaksi());
                        inKonfirmasi.putExtra("harga", trans.get(0).getHarga());
                        inKonfirmasi.putExtra("id_pel", trans.get(0).getIdPel());
                        inKonfirmasi.putExtra("jenis", trans.get(0).getJenis());
                        inKonfirmasi.putExtra("pin", trans.get(0).getPin());
                        inKonfirmasi.putExtra("cmd_save", trans.get(0).getCmdSave());
                        startActivity(inKonfirmasi);
                    } else {
                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    /*================================end payment lama==========================================*/
}
