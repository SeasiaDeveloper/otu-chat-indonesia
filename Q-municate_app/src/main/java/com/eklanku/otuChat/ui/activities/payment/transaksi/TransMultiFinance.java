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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.konfirmasitransaksi.TransKonfirmasiPascabayar;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.Utils;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransMultiFinance extends AppCompatActivity {

    private static String[] nama_operator;

    private static String load_type    = "finance_nominal";

    SharedPreferences prefs;
    Spinner spnOperator;
    EditText txtNo, txtno_hp, txtNoHP;
    TextInputLayout layoutNo;
    Button btnBayar;
    String load_id = "FIN", selected_operator;
    Dialog loadingDialog;
    ArrayAdapter<String> adapter;

    SpinnerPpobAdapter spinnerPpobAdapter;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID,strAccessToken,strAplUse = "OTU";

    Utils utilsAlert;
    String titleAlert = "Multi Finance";

    ListView listFinance;
    ArrayList<String> idFinance;

    Bundle extras;
    String jenis, img;

    ImageView imgFinance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_multifinance);
        ButterKnife.bind(this);
        extras = getIntent().getExtras();



        utilsAlert = new Utils(TransMultiFinance.this);

        prefs       = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnOperator = (Spinner) findViewById(R.id.spnTransFinanceOperator);
        txtNo       = (EditText) findViewById(R.id.txtTransFinanceNo);
        layoutNo    = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar    = (Button) findViewById(R.id.btnTransFinanceBayar);
        txtNoHP = findViewById(R.id.txt_no_hp);
        imgFinance = findViewById(R.id.img_finance);
        btnBayar.setText("CEK TAGIHAN");
        txtNo.addTextChangedListener(new txtWatcher(txtNo));
        listFinance = findViewById(R.id.listFinance);

        txtno_hp = (EditText) findViewById(R.id.txt_no_hp);
        if(PreferenceUtil.getNumberPhone(this).startsWith("+62")){
            String no = PreferenceUtil.getNumberPhone(this).replace("+62","0");
            txtno_hp.setText(no);
        }else{
            txtno_hp.setText(PreferenceUtil.getNumberPhone(this));
        }


        jenis = extras.getString("jenis");
        img = extras.getString("img");
        setTitle(jenis);

        int idimg = TransMultiFinance.this.getResources().getIdentifier("drawable/ic_finance_" + img.toLowerCase(), null, TransMultiFinance.this.getPackageName());
        imgFinance.setImageResource(idimg);



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

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

    private void loadProvider(String userID, String accessToken, String aplUse, String productGroup) {
        loadingDialog = ProgressDialog.show(TransMultiFinance.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nama_operator = new String[0];
                idFinance = new ArrayList<>();
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
                            idFinance.add(result.get(i).getCode());
                        }

                        spinnerPpobAdapter = new SpinnerPpobAdapter(getBaseContext(), nama_operator);
                        listFinance.setAdapter(spinnerPpobAdapter);

                    } else {
                        utilsAlert.globalDialog(TransMultiFinance.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransMultiFinance.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransMultiFinance.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();
        txtNo.setError(null);
        if (id_pel.isEmpty()) {
            txtNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

        return true;
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
        loadingDialog = ProgressDialog.show(TransMultiFinance.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, jenis, txtNo.getText().toString(), txtno_hp.getText().toString(), strAplUse);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus().toString();
                    String error  = response.body().getRespMessage();

                    if ( status.equals("SUCCESS") ) {
                        Intent inKonfirmasi       = new Intent(getBaseContext(), TransKonfirmasiPascabayar.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());
                        inKonfirmasi.putExtra("accessToken", strAccessToken);
                        inKonfirmasi.putExtra("status", status);
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());
                        inKonfirmasi.putExtra("respTime", response.body().getRespTime());
                        inKonfirmasi.putExtra("productCode", "Nomor Pelanggan Multifinance");
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
                        inKonfirmasi.putExtra("ep", response.body().getEp());

                        inKonfirmasi.putExtra("transaksi", "-");
                        inKonfirmasi.putExtra("harga", "-");
                        inKonfirmasi.putExtra("id_pel", "-");
                        inKonfirmasi.putExtra("jenis", "-");
                        inKonfirmasi.putExtra("pin", "-");
                        inKonfirmasi.putExtra("cmd_save", "-");
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransMultiFinance.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransMultiFinance.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransMultiFinance.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
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

    private void initializeResources() {
        listFinance = (ListView) findViewById(R.id.listFinance);
        listFinance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }
                selected_operator =  idFinance.get(position);

                cek_transaksi();
            }
        });

    }
}
