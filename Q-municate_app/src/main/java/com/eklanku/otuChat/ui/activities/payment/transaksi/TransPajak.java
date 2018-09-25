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
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransPajak extends AppCompatActivity {

    private static String[] nama_wilayah/* = {"PDAM BONDOWOSO",
            "PDAM PONTIANAK",
            "PDAM KAB JEMBER",
            "PDAM KAB MOJOKERTO",
            "PDAM KOTA BANDUNG",
            "PDAM KOTA BANDAR LAMPUNG",
            "PDAM KOTA BOGOR",
            "PDAM KAB SIDOARJO",
            "PDAM SURABAYA",
            "PDAM KAB BANGKALAN",
            "PDAM KAB MALANG",
            "PDAM KAB SITUBONDO",
            "PDAM KOTA PURWOREJO",
            "PDAM KAB GROBOGAN",
            "PDAM AETRA JAKARTA",
            "PDAM PALYJA JAKARTA",
            "PDAM KOTA SURAKARTA",
            "PDAM DENPASAR",
            "PDAM KOTA TANAH GROGOT",
            "PDAM KAB. BALANGAN",
            "PDAM KOTA JAMBI",
            "PDAM KAB. BOJONEGORO",
            "PDAM KAB. BATANG",
            "PDAM KAB. PASURUAN",
            "PDAM KOTA PASURUAN",
            "PDAM KAB. SAMPANG",
            "PDAM KAB. KUBU RAYA",
            "PDAM KAB. TAPIN",
            "PDAM KOTA BANJARBARU",
            "PDAM KOTA MATARAM",
            "PDAM KOTA MANADO",
            "PDAM KOTA PALEMBANG",
            "PDAM KAB BULELENG"}*/;

    private static String[] kode_wilayah/* = {"PDAM_BONDO",
            "PDAM_PON",
            "PDAM_JMBR",
            "PDAM_MJKRT",
            "PDAM_BDG",
            "PDAM_LPG",
            "PDAM_KOBGR",
            "PDAM_SIDO",
            "PDAM_SBY",
            "PDAM_BNKLN",
            "PDAM_MLG",
            "PDAM_STUBN",
            "PDAM_PURRJ",
            "PDAM_GRBG",
            "PDAM_AETRA",
            "PDAM_PLYJA",
            "PDAM_SURKT",
            "PDAM_DENPASAR",
            "PDAM_WAGROGOT",
            "PDAM_BALANGAN",
            "PDAM_WAJAMBI",
            "PDAM_WABJN",
            "PDAM_WABATANG",
            "PDAM_WAPASU",
            "PDAM_WAKOPASU",
            "PDAM_WASAMPANG",
            "PDAM_WAKUBURAYA",
            "PDAM_WATAPIN",
            "PDAM_WAIBANJAR",
            "PDAM_WAGIRIMM",
            "PDAM_WAMANADO",
            "PDAM_WAPLMBNG",
            "PDAM_BULLNG"}*/;

    SharedPreferences prefs;
    Spinner spnWilayah;
    EditText txtNo, txtno_hp;
    TextInputLayout layoutNo;
    Button btnBayar;
    String //id_member,
            load_id = "PDAM_AETRA";
    ApiInterface mApiInterface;
    Dialog loadingDialog;

    //rina
    SpinnerPpobAdapter spinnerPpobAdapter;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID,strAccessToken,strAplUse = "OTU";

    Utils utilsAlert;
    String titleAlert = "Pajak";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pajak_daerah);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransPajak.this);

        prefs      = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnWilayah = (Spinner) findViewById(R.id.spnTransPajak);
        txtNo      = (EditText) findViewById(R.id.txtTransPajakNo);
        layoutNo    = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar   = (Button) findViewById(R.id.btnTransPajakBayar);
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("CEK TAGIHAN");
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        txtno_hp = (EditText) findViewById(R.id.txt_no_hp);
        if(PreferenceUtil.getNumberPhone(this).startsWith("+62")){
            String no = PreferenceUtil.getNumberPhone(this).replace("+62","0");
            txtno_hp.setText(no);
        }else{
            txtno_hp.setText(PreferenceUtil.getNumberPhone(this));
        }

       /* spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), nama_wilayah);
        spnWilayah.setAdapter(spinnerPpobAdapter);
        spnWilayah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load_id = kode_wilayah[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        loadProvider(strUserID, strAccessToken, "OTU", "PAJAK DAERAH");

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

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();

        if (id_pel.isEmpty()) {
            //Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            layoutNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

        /*if (id_pel.length() < 8) {
            Toast.makeText(this, "Masukkan minimal 8 digit nomor", Toast.LENGTH_SHORT).show();
//            layoutNo.setError("Masukkan minimal 8 digit nomor");
            requestFocus(txtNo);
            return false;
        }*/

        //layoutNo.setErrorEnabled(false);
        return true;
    }

    private void loadProvider(String userID, String accessToken, String aplUse, String productGroup) {
        loadingDialog = ProgressDialog.show(TransPajak.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nama_wilayah = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_wilayah);
                spnWilayah.setAdapter(adapter);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> result = response.body().getProductList();
                        nama_wilayah = new String[result.size()];
                        load_id = result.get(0).getCode();

                        for (int i = 0; i < result.size(); i++) {
                            nama_wilayah[i] = result.get(i).getName();
                        }

                        adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_wilayah);
                        spnWilayah.setAdapter(adapter);
                        spnWilayah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                load_id = result.get(position).getCode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        utilsAlert.globalDialog(TransPajak.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPajak.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPajak.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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

    private void requestFocus(View view) {
        if ( view.requestFocus() ) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void cek_transaksi() {
        Log.d("OPPO-1", "cek_transaksi: "+load_id);
        loadingDialog = ProgressDialog.show(TransPajak.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, load_id, txtNo.getText().toString(), txtno_hp.getText().toString(), strAplUse);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus().toString();
                    String error  = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse - error: "+error);

                    if ( status.equals("SUCCESS") ) {

                        Intent inKonfirmasi       = new Intent(getBaseContext(), TransKonfirmasi.class);
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
                        utilsAlert.globalDialog(TransPajak.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPajak.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPajak.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    /*========================================payment lama=====================================================*/
    /*

    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransPdam.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Log.d("OPPO-1", "cek_transaksi - transpdam: "+PreferenceUtil.getNumberPhone(this)));
        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli(PreferenceUtil.getNumberPhone(this)), load_id, "", txtNo.getText().toString(), "pdambyr");
//        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli("085334059170", load_id, "", txtNo.getText().toString(), "pdambyr");
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
    }*/
    /*===========================================end payment lama======================================================*/
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
