package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.konfirmasitransaksi.TransKonfirmasiPascabayar;
import com.eklanku.otuChat.ui.activities.payment.konfirmasitransaksi.TransKonfirmasiPrabayar;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProvider;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.ui.views.ARTextView;
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

public class TransTv extends AppCompatActivity {

    private static String[] nama_jenis;
    private static String[] kode_jenis;
    private static String load_type = "tvbyr_jenis";

    SharedPreferences prefs;
    RelativeLayout layJenisPembayaran;
    Spinner spnJenis, spnLayanan;
    EditText txtNo, txtno_hp, txtTransaksi_ke;
    TextInputLayout layoutNo;
    TextView tvJenisPembayaran, tvKet;
    ARTextView arvNoPel, arvNohp;
    Button btnBayar;
    String //id_member,
            load_id = "TV", selected_nominal;
    Dialog loadingDialog;
    String[] nominal;

    //rina
    SpinnerPpobAdapter spinnerPpobAdapter;
    String[] point;
    String[] denom;
    SpinnerPaymentAdapter spinnerPaymentAdapter;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID, strAccessToken, strAplUse = "OTU", strOpsel;
    LinearLayout layoutNominal;
    private RadioGroup radioGroup;
    String[] nama_wilayah;

    String StrTV;
    LinearLayout layoutNoKonfirmasi, layoutTransaksiKe;

    TextView txtnomor, txtvoucher;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String nominalx, tujuanx;
    String titleAlert = "Vouchet TV";
    private static long mLastClickTime = 0;
    LinearLayout layoutView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_tv);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransTv.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                nominalx = null;
                tujuanx = null;
            } else {
                nominalx = extras.getString("nominal");
                tujuanx = extras.getString("tujuan");

            }
        } else {
            nominalx = (String) savedInstanceState.getSerializable("nominal");
            tujuanx = (String) savedInstanceState.getSerializable("tujuan");

        }

        layoutNominal = (LinearLayout) findViewById(R.id.layout_nominal);
        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnJenis = (Spinner) findViewById(R.id.spnTransTvJenis);
        spnLayanan = (Spinner) findViewById(R.id.spnTransTvLayanan);
        txtNo = (EditText) findViewById(R.id.txtTransTvNo);
        txtNo.setText(tujuanx);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransTvBayar);
        tvJenisPembayaran = findViewById(R.id.textView30);
        arvNoPel = findViewById(R.id.AtvNomorPelanggan);
        tvKet = findViewById(R.id.tvKet);
        arvNohp = findViewById(R.id.aRvNoHp);
        layJenisPembayaran = findViewById(R.id.layJenisPembayaran);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        txtno_hp = (EditText) findViewById(R.id.txt_no_hp);
        if (PreferenceUtil.getNumberPhone(this).startsWith("+62")) {
            String no = PreferenceUtil.getNumberPhone(this).replace("+62", "0");
            txtno_hp.setText(no);
        } else {
            txtno_hp.setText(PreferenceUtil.getNumberPhone(this));
        }

        txtTransaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        txtTransaksi_ke.setText("1");


        layoutView = findViewById(R.id.linear_pulsa);
        progressBar = findViewById(R.id.progress_pulsa);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        layoutNoKonfirmasi = findViewById(R.id.layout_no_konfirmasi);
        layoutTransaksiKe = findViewById(R.id.layout_transaksi_ke);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_tv);
        radioGroup.clearCheck();
        RadioButton rbToken = findViewById(R.id.radio_tv_voucher);
        rbToken.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                //Toast.makeText(TransPln.this, rb.getText(), Toast.LENGTH_SHORT).show();
                if (rb.getText().toString().equalsIgnoreCase("VOUCHER TV")) {
                    layoutNominal.setVisibility(View.GONE);
                    layJenisPembayaran.setVisibility(View.GONE);
                    tvJenisPembayaran.setVisibility(View.GONE);
                    arvNohp.setVisibility(View.GONE);
                    layoutNo.setVisibility(View.GONE);
                    layoutNoKonfirmasi.setVisibility(View.GONE);
                    layoutTransaksiKe.setVisibility(View.GONE);
                    btnBayar.setVisibility(View.GONE);
                    arvNoPel.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                } else {
                    loadProviderPPOB(strUserID, strAccessToken, "OTU", "TV KABEL");
                    layoutNominal.setVisibility(View.GONE);
                    layoutTransaksiKe.setVisibility(View.GONE);
                    layJenisPembayaran.setVisibility(View.VISIBLE);
                    arvNoPel.setVisibility(View.VISIBLE);
                    StrTV = "TV KABEL";
                    btnBayar.setVisibility(View.VISIBLE);
                    btnBayar.setText("CEK TAGIHAN");
                    layoutNoKonfirmasi.setVisibility(View.VISIBLE);
                    tvJenisPembayaran.setVisibility(View.VISIBLE);
                    layoutNoKonfirmasi.setVisibility(View.VISIBLE);
                    layoutNo.setVisibility(View.VISIBLE);
                    arvNohp.setVisibility(View.VISIBLE);
                }
            }
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }

                Log.d("OPPO-1", "onClick: " + StrTV);
                if (StrTV.equalsIgnoreCase("VOUCHER TV")) {
                    final Dialog dialog = new Dialog(TransTv.this);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.activity_alert_dialog);
                    dialog.setCancelable(false);
                    dialog.setTitle("Peringatan Transaksi!!!");

                    btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                    btnNo = (Button) dialog.findViewById(R.id.btn_no);
                    txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                    txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                    txtnomor.setText(txtNo.getText().toString());
                    txtvoucher.setText(selected_nominal);

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            cek_transaksi_tv_voucher();
                            dialog.dismiss();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    return;

                } else {
                    cek_transaksi();
                }

            }
        });

        load();
    }

    private void load() {
        loadProviderPPOB(strUserID, strAccessToken, "OTU", "TV KABEL");
        layoutNominal.setVisibility(View.GONE);
        StrTV = "TV KABEL";
        btnBayar.setText("CEK TAGIHAN");
        txtno_hp.setVisibility(View.VISIBLE);
        layoutTransaksiKe.setVisibility(View.GONE);
        layoutNoKonfirmasi.setVisibility(View.VISIBLE);
    }

    private void loadProvider(String userID, String accessToken, String aplUse, String productType) {
        loadingDialog = ProgressDialog.show(TransTv.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        //Toast.makeText(this, "load provider", Toast.LENGTH_SHORT).show();
        Call<LoadDataResponseProvider> userCall = mApiInterfacePayment.getLoadProvider(userID, accessToken, aplUse, productType);
        userCall.enqueue(new Callback<LoadDataResponseProvider>() {
            @Override
            public void onResponse(Call<LoadDataResponseProvider> call, Response<LoadDataResponseProvider> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String userID = response.body().getUserID();
                    String accessToken = response.body().getAccessToken();
                    String respMessage = response.body().getRespMessage();
                    String respTime = response.body().getRespTime();
                    String productTypes = response.body().getProductTypes();

                    if (status.equals("SUCCESS")) {
                        List<String> list = new ArrayList<String>();
                        list.clear();
                        final List<DataProvider> products = response.body().getProviders();
                        for (int i = 0; i < products.size(); i++) {
                            String x = products.get(i).getName_provaider();
                            list.add(x);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, list);
                        spnJenis.setAdapter(adapter);
                        spnJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                strOpsel = parent.getItemAtPosition(position).toString();
                                loadProduct(strUserID, strAccessToken, strAplUse, strOpsel);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        utilsAlert.globalDialog(TransTv.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProvider> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private void loadProduct(String userID, String accessToken, String aplUse, String provider) {
        loadingDialog = ProgressDialog.show(TransTv.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<LoadDataResponseProduct> userCall = mApiInterfacePayment.getLoadProduct(userID, accessToken, aplUse, provider);
        userCall.enqueue(new Callback<LoadDataResponseProduct>() {
            @Override
            public void onResponse(Call<LoadDataResponseProduct> call, Response<LoadDataResponseProduct> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String userID = response.body().getUserID();
                    String accessToken = response.body().getAccessToken();
                    String respMessage = response.body().getRespMessage();
                    String respTime = response.body().getRespTime();
                    String provider = response.body().getProvider();

                    if (status.equals("SUCCESS")) {

                        List<String> listPrice = new ArrayList<String>();
                        List<String> listNama = new ArrayList<String>();
                        List<String> listEp = new ArrayList<String>();
                        listPrice.clear();
                        listNama.clear();
                        listEp.clear();
                        final List<DataProduct> products = response.body().getProducts();
                        for (int i = 0; i < products.size(); i++) {
                            String name = products.get(i).getName();
                            String price = products.get(i).getPrice();
                            String ep = products.get(i).getEp();

                            listNama.add(name);
                            listEp.add(ep);
                            listPrice.add(price);
                        }

                        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), products);
                        spnLayanan.setAdapter(adapter);
                        spnLayanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_nominal = products.get(position).getCode();
                                String x = products.get(position).getName();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        utilsAlert.globalDialog(TransTv.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private void loadProviderPPOB(String userID, String accessToken, String aplUse, String productGroup) {
        showProgress(true);

        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                showProgress(false);
                nama_wilayah = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_wilayah);
                spnJenis.setAdapter(adapter);
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
                        spnJenis.setAdapter(adapter);
                        spnJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                load_id = result.get(position).getCode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        utilsAlert.globalDialog(TransTv.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();

        if (id_pel.isEmpty()) {
            layoutNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

        layoutNo.setErrorEnabled(false);
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
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void cek_transaksi() {
        Log.d("OPPO-1", "cek_transaksi: " + load_id + " / " + strUserID + " / " + strAccessToken + " / " + txtNo.getText().toString() + " / " + PreferenceUtil.getNumberPhone(this) + " / " + strAplUse);
        loadingDialog = ProgressDialog.show(TransTv.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);


        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, load_id, txtNo.getText().toString(), txtno_hp.getText().toString(), strAplUse);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();


                    if (status.equals("SUCCESS")) {
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasiPascabayar.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());
                        inKonfirmasi.putExtra("accessToken", strAccessToken);
                        inKonfirmasi.putExtra("status", status);
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());
                        inKonfirmasi.putExtra("respTime", response.body().getRespTime());
                        inKonfirmasi.putExtra("productCode", "TV Pascabayar");
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
                        utilsAlert.globalDialog(TransTv.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private void cek_transaksi_tv_voucher() {
        Log.d("OPPO-1", "cek_transaksi_tv_voucher: ");
        loadingDialog = ProgressDialog.show(TransTv.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString(), txtno_hp.getText().toString(), "", selected_nominal);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasiPrabayar.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("productCode", "TV Prabayar");//
                        inKonfirmasi.putExtra("billingReferenceID", response.body().getTransactionID());//
                        inKonfirmasi.putExtra("customerID", response.body().getMSISDN());//
                        inKonfirmasi.putExtra("customerMSISDN", response.body().getMSISDN());//
                        inKonfirmasi.putExtra("customerName", "");
                        inKonfirmasi.putExtra("period", "");
                        inKonfirmasi.putExtra("policeNumber", "");
                        inKonfirmasi.putExtra("lastPaidPeriod", "");
                        inKonfirmasi.putExtra("tenor", "");
                        inKonfirmasi.putExtra("lastPaidDueDate", "");
                        inKonfirmasi.putExtra("usageUnit", "TOPUP");
                        inKonfirmasi.putExtra("penalty", "");
                        inKonfirmasi.putExtra("payment", response.body().getNominal());//
                        inKonfirmasi.putExtra("minPayment", "");
                        inKonfirmasi.putExtra("minPayment", "");
                        inKonfirmasi.putExtra("additionalMessage", response.body().getAdditionalMessage());
                        inKonfirmasi.putExtra("billing", response.body().getNominal());//
                        inKonfirmasi.putExtra("sellPrice", "");
                        inKonfirmasi.putExtra("adminBank", "0");
                        inKonfirmasi.putExtra("profit", "");
                        inKonfirmasi.putExtra("ep", response.body().getEp());

                        inKonfirmasi.putExtra("transaksi", "-");
                        inKonfirmasi.putExtra("harga", "-");
                        inKonfirmasi.putExtra("id_pel", "-");
                        inKonfirmasi.putExtra("jenis", "-");
                        inKonfirmasi.putExtra("pin", "-");
                        inKonfirmasi.putExtra("cmd_save", "-");
                        startActivity(inKonfirmasi);
                    } else {
                        utilsAlert.globalDialog(TransTv.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransTv.this, titleAlert, getResources().getString(R.string.error_api));
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
            layoutView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            layoutView.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }
}
