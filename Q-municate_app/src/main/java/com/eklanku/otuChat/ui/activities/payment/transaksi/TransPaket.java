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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TransPaket extends AppCompatActivity {

    private static String[] jenis_paket = {"Axis Bronet", "Axis Data", "BOLT Data", "ISat Combo", "ISat Data",
            "ISat Extra", "SmartFren Data", "Three Data", "TSel Data", "TSel SMS", "XL Data", "XL Combo"};
    private static String[] kode_paket = {"BXD", "AXD", "BOLT", "INC", "IND",
            "INE", "SMD", "TD", "SD", "SS", "XH", "XCD"};
    private static String load_type = "paket_nominal";

    SharedPreferences prefs;
    Spinner spnJenis,spnNama;
    EditText txtNo;
    //TextInputLayout layoutNo;
    Button btnBayar;
    String //id_member,
            load_id = "BXD", selected_nominal;
    ApiInterface mApiInterface;
    Dialog loadingDialog;
    String[] nominal;
    ArrayAdapter<String> adapter;

    SpinnerPaymentAdapter spinnerAdapter;
    String[] denom;
    String[] point;
    JSONArray mJsonArray = null;

    ImageView imgopr;
    TextView txtopr;
    boolean cek_opr = false;
    String opr = "";
    SpinnerPpobAdapter spinnerPpobAdapter;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID,strAccessToken,strAplUse = "OTU";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_paket);

        ButterKnife.bind(this);

        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnJenis  = (Spinner) findViewById(R.id.spnTransPaketJenis);
        spnNama = (Spinner) findViewById(R.id.spnTransPaketNama);
        txtNo = (EditText) findViewById(R.id.txtTransPaketNo);
        //layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPaketNo);
        btnBayar = (Button) findViewById(R.id.btnTransPaketBayar);
        //id_member = prefs.getString("auth_id", "");
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("CEK TAGIHAN");
        nominal = new String[0];
        denom = new String[0];
        point = new String[0];

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

       /* txtNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (mJsonArray == null) {
//                    load_datafilter();
//                }
//                btnBayar.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                cekPrefix(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
//        load_datafilter();

        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, jenis_paket);
        spnJenis.setAdapter(adapter);
*/

        spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), jenis_paket);
        spnJenis.setAdapter(spinnerPpobAdapter);
        spnJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load_id = kode_paket[position];
                load_data2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
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
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void cekPrefix(CharSequence s) {
        if (s.length() >= 6) {
            String nohp = "";

            //handling 62 and +62
            if (s.toString().startsWith("62")) {
                nohp = s.toString().substring(0, 2).replace("62", "0") + s.toString().substring(2, 5);
            } else if (s.toString().startsWith("+62")) {
                nohp = s.toString().substring(0, 3).replace("+62", "0") + s.toString().substring(3, 6);
            } else {
                nohp = s.toString().substring(0, 4);
            }

            try {
                for (int i = 0; i < mJsonArray.length(); i++) {
                    String nama = mJsonArray.getJSONObject(i).getString("name");
                    String idx = mJsonArray.getJSONObject(i).getString("idx");
                    String no = mJsonArray.getJSONObject(i).getString("no");

                    Log.d("OPPO-1", "cekPrefix - nama: "+nama);
                    Log.d("OPPO-1", "cekPrefix - idx: "+idx);
                    Log.d("OPPO-1", "cekPrefix - no: "+no);

                    if (no.contains(nohp)) {
                        txtopr.setText(nama);
                        Context c = getApplicationContext();
                        int id = c.getResources().getIdentifier("mipmap/" + nama, null, c.getPackageName());
                        imgopr.setImageResource(id);
                        opr = idx;
                        break;
                    } else {
                        Log.d("OPPO-1", "data tidak ditemukan ");
                        opr = "";
                        txtopr.setText("");
                        imgopr.setImageResource(0);
                        nominal = new String[0];
                        denom = new String[0];
                        point = new String[0];
                        spinnerAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                        spnNama.setAdapter(spinnerAdapter);
                        btnBayar.setEnabled(false);
                    }

                }

                Log.d("OPPO-1", "load data trx lama: " + load_id + " baru: " + opr);
                Log.d("OPPO-1", "cek opr: " + cek_opr);


                if (!load_id.equalsIgnoreCase(opr)) {
                    Log.d("OPPO-1", "load data trx - 2");
                    cek_opr = false;
                }

                if (!opr.equalsIgnoreCase("") && !cek_opr) {
                    Log.d("OPPO-1", "load data trx");
                    cek_opr = true;
                    load_id = opr;
                    btnBayar.setEnabled(true);
                    load_data();
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Koneksi internet gagal:\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (s.length() < 4) {
            txtopr.setText("");
            imgopr.setImageResource(0);
            nominal = new String[0];
            denom = new String[0];
            point = new String[0];
            spinnerAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
            spnNama.setAdapter(spinnerAdapter);
            btnBayar.setEnabled(false);
            cek_opr = false;
        } else if (s.length() >= 8 && s.length() <= 13) {
            btnBayar.setEnabled(true);
        }
        /*else if (s.length() < 8) {
            btnBayar.setEnabled(false);
        } else if (s.length() > 13) {
            btnBayar.setEnabled(false);
        }*/
    }

    private void load_data() {
        loadingDialog = ProgressDialog.show(TransPaket.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "cek_transaksi - transpaket: "+PreferenceUtil.getNumberPhone(this));
        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, PreferenceUtil.getNumberPhone(this));
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nominal = new String[0];
                adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, nominal);
                spnNama.setAdapter(adapter);

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("OK")) {
                        final List<DataNominal> result = response.body().getResult();
                        nominal = new String[result.size()];
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
                            nominal[i] = result.get(i).getProductName() + " | " + format.format(result.get(i).getHargaJual()) + " (" + decimal.format(result.get(i).getEpoint()) + ")";
                        }

                        adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, nominal);
                        spnNama.setAdapter(adapter);
                        spnNama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_nominal = result.get(position).getProductKode();//getH2hCode();
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
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    public void load_data2() {
        loadingDialog = ProgressDialog.show(TransPaket.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "cek_transaksi - transpaket: "+PreferenceUtil.getNumberPhone(this));
        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, PreferenceUtil.getNumberPhone(this));
//        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, "085334059170");
        dataCall.enqueue(new Callback<LoadDataResponse>() {

            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nominal = new String[0];
                denom = new String[0];
                point = new String[0];
                spinnerAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                spnNama.setAdapter(spinnerAdapter);

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
                            denom[i] = result.get(i).getProductName();
                            nominal[i] = format.format(result.get(i).getHargaJual());
                            point[i] = "point : " + decimal.format(result.get(i).getEpoint());
                        }

                        spinnerAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                        spnNama.setAdapter(spinnerAdapter);
                        // adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, nominal);
                        // spnNama.setAdapter(adapter);
                        spnNama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_nominal = result.get(position).getProductKode();//getH2hCode();
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
        loadingDialog = ProgressDialog.show(TransPaket.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString(), "1","" ,"", selected_nominal);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
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

/*====================================payment lama========================================================================*/
/*
    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransPaket.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Log.d("OPPO-1", "cek_transaksi - transpaket: "+PreferenceUtil.getNumberPhone(this)));
        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli(PreferenceUtil.getNumberPhone(this)), load_id, selected_nominal, txtNo.getText().toString(), "paket");
//        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli("085334059170", load_id, selected_nominal, txtNo.getText().toString(), "paket");
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("OK")) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
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
    }*/
/*===========================================================end payment lama==================================================*/
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
            case R.id.action_transaction_evidence:
                Toast.makeText(this, "Kirim bukti pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
