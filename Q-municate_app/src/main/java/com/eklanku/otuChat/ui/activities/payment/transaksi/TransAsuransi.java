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
import android.widget.Spinner;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.DataNominal;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransAsuransi extends AppCompatActivity {

    private static String[] nama_operator;
    private static String load_type = "asuransi_nominal";

    SharedPreferences prefs;
    Spinner spnOperator;
    EditText txtNo, txtno_hp;
    Button btnBayar;
    String load_id = "ASR", selected_operator;
    ApiInterface mApiInterface;
    ApiInterfacePayment mApiInterfacePayment;

    Dialog loadingDialog;
    ArrayAdapter<String> adapter;

    SpinnerPpobAdapter spinnerPpobAdapter;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    TextInputLayout layoutNo;

    Utils utilsAlert;
    String titleAlert = "Asuransi";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_asuransi);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransAsuransi.this);

        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnOperator = (Spinner) findViewById(R.id.spnTransAsuransiOperator);
        txtNo = (EditText) findViewById(R.id.txtTransAsuransiNo);
        layoutNo    = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransAsuransiBayar);
        txtNo.addTextChangedListener(new txtWatcher(txtNo));
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("CEK TAGIHAN");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtno_hp = (EditText) findViewById(R.id.txt_no_hp);
        if(PreferenceUtil.getNumberPhone(this).startsWith("+62")){
            String no = PreferenceUtil.getNumberPhone(this).replace("+62","0");
            txtno_hp.setText(no);
        }else{
            txtno_hp.setText(PreferenceUtil.getNumberPhone(this));
        }

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        //load data produk
//        load_data();
        loadProvider(strUserID, strAccessToken, "OTU", "ASURANSI");

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
//            Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            layoutNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

        if (id_pel.length() < 8) {
//            Toast.makeText(this, "Masukkan minimal 8 digit nomor", Toast.LENGTH_SHORT).show();
            layoutNo.setError("Masukkan minimal 8 digit nomor");
            requestFocus(txtNo);
            return false;
        }

        layoutNo.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void loadProvider(String userID, String accessToken, String aplUse, String productGroup) {
        loadingDialog = ProgressDialog.show(TransAsuransi.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nama_operator = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_operator);
                spnOperator.setAdapter(adapter);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> result = response.body().getProductList();
                        nama_operator = new String[result.size()];
                        selected_operator = result.get(0).getCode();

                        for (int i = 0; i < result.size(); i++) {
                            nama_operator[i] = result.get(i).getName();
                        }

                        adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_operator);
                        spnOperator.setAdapter(adapter);
                        spnOperator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_operator = result.get(position).getCode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        utilsAlert.globalDialog(TransAsuransi.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransAsuransi.this, titleAlert, getResources().getString(R.string.error_api));
                    // Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransAsuransi.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransAsuransi.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, selected_operator, txtNo.getText().toString(), txtno_hp.getText().toString(), "OTU");
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus().toString();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());
                        inKonfirmasi.putExtra("accessToken", strAccessToken);
                        inKonfirmasi.putExtra("status", status);
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());
                        inKonfirmasi.putExtra("respTime", response.body().getRespTime());
                        inKonfirmasi.putExtra("productCode", response.body().getProductCode());
                        inKonfirmasi.putExtra("billingReferenceID", response.body().getBillingReferenceID());
                        inKonfirmasi.putExtra("customerID", response.body().getCustomerID());
                        inKonfirmasi.putExtra("customerMSISDN", response.body().getCustomerMSISDN());
                        inKonfirmasi.putExtra("customerName", response.body().getCustomerName());
                        inKonfirmasi.putExtra("period", response.body().getPeriod());
                        inKonfirmasi.putExtra("policeNumber", response.body().getPoliceNumber());
                        inKonfirmasi.putExtra("lastPaidPeriod", response.body().getLastPaidPeriod());
                        inKonfirmasi.putExtra("tenor", response.body().getTenor());
                        inKonfirmasi.putExtra("lastPaidDueDate", response.body().getLastPaidDueDate());
                        inKonfirmasi.putExtra("usageUnit", response.body().getUsageUnit());
                        inKonfirmasi.putExtra("penalty", response.body().getPenalty());
                        inKonfirmasi.putExtra("payment", response.body().getPayment());
                        inKonfirmasi.putExtra("minPayment", response.body().getMinPayment());
                        inKonfirmasi.putExtra("minPayment", response.body().getMaxPayment());
                        inKonfirmasi.putExtra("additionalMessage", response.body().getAdditionalMessage());
                        inKonfirmasi.putExtra("billing", response.body().getBilling());
                        inKonfirmasi.putExtra("sellPrice", response.body().getSellPrice());
                        inKonfirmasi.putExtra("adminBank", response.body().getAdminBank());
                        inKonfirmasi.putExtra("profit", response.body().getProfit());

                        inKonfirmasi.putExtra("transaksi", "-");
                        inKonfirmasi.putExtra("harga", "-");
                        inKonfirmasi.putExtra("id_pel", "-");
                        inKonfirmasi.putExtra("jenis", "-");
                        inKonfirmasi.putExtra("pin", "-");
                        inKonfirmasi.putExtra("cmd_save", "-");
                        startActivity(inKonfirmasi);
                    } else {
                        utilsAlert.globalDialog(TransAsuransi.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransAsuransi.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransAsuransi.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    /*private void load_data() {
        loadingDialog = ProgressDialog.show(TransAsuransi.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(strUserID, strAccessToken, "ASURANSI", "OTU");
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nama_operator = new String[0];
                spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), nama_operator);
                spnOperator.setAdapter(spinnerPpobAdapter);

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> result = response.body().getProductList();
                        nama_operator = new String[result.size()];
                        selected_operator = result.get(0).getCode();

                        for (int i = 0; i < result.size(); i++) {
                            nama_operator[i] = result.get(i).getName();
                        }

                        spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), nama_operator);
                        spnOperator.setAdapter(spinnerPpobAdapter);
                        spnOperator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_operator = result.get(position).getCode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        utilsAlert.globalDialog(TransAsuransi.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransAsuransi.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransAsuransi.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }*/

    /*=========================================================payment lama======================================*/
    /*
    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransAsuransi.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);
        String nominal="";

        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli(PreferenceUtil.getNumberPhone(this)), selected_operator, nominal, txtNo.getText().toString(), "asuransibyr");
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus().toString();
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

    private void load_data() {
        loadingDialog = ProgressDialog.show(TransAsuransi.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
        Log.d("OPPO-1", "load_data: "+strUserID+" / "+strAccessToken);


        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id,PreferenceUtil.getNumberPhone(this)));
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nama_operator=new String[0];
//                adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, nama_operator);
                spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), nama_operator);
                spnOperator.setAdapter(spinnerPpobAdapter);

                if (response.isSuccessful()) {
                    String status   = response.body().getStatus();
                    String error    = response.body().getError();


                    if ( status.equals("OK") ) {
                        final List<DataNominal> result = response.body().getResult();
                        nama_operator                        = new String[result.size()];
                        selected_operator                    = result.get(0).getProductKode();

                        for ( int i=0; i<result.size(); i++ ) {
                            nama_operator[i] = result.get(i).getProductName();//+" | "+ format.format(result.get(i).getHargaJual())+" ("+ decimal.format(result.get(i).getEpoint())+")";
                        }

                        spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), nama_operator);
                        spnOperator.setAdapter(spinnerPpobAdapter);
                        spnOperator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_operator = result.get(position).getProductKode();
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
    }*/
    /*=======================================================end payment lama======================================================*/
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
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
