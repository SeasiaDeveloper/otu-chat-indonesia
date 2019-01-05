package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapterNew;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

public class TransPln extends AppCompatActivity {

    private static String[] arrayProvider = {"PLN TOKEN"}, arrayProduct;
    private static String load_type = "token_nominal";
    private static String[] nama_operator;

    //SharedPreferences prefs;
    Spinner spinnerProvider, spnNominal;
    //LinearLayout laySpnNominal;
    EditText txtNo, txtno_hp, txtTransaksi_ke;
    TextInputLayout layoutNo;
    Button btnBayar;
    String load_id = "PLN", selected_nominal;
    ApiInterfacePayment apiInterfacePayment;
    Dialog loadingDialog;
    String[] nominal;
    ArrayAdapter<String> adapter;

    //rina
    SpinnerPpobAdapter spinnerProviderAdapter;
    SpinnerPaymentAdapter spinnerPaymentAdapter;
    String[] denom;
    String[] point;
    private PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strOpsel, strAplUse = "OTU", strProductType = "PLN TOKEN", selected_operator;
    String code, ep;
    LinearLayout layoutNominal;
    private RadioGroup radioGroup;
    String rbPln;
    LinearLayout layoutTransaksiKe, layoutNoKonfirmasi;

    TextView txtnomor, txtvoucher, txtpilihpembayaran;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String titleAlert = "PLN";

    ListView listPLN;
    ArrayList<String> id_paket;

    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;
    String nominalx, tujuanx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_pln);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransPln.this);

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

        layoutNominal = (LinearLayout) findViewById(R.id.layout_spinner_nominal);
        spinnerProvider = (Spinner) findViewById(R.id.spnTransPln);
        spnNominal = (Spinner) findViewById(R.id.spnTransPlnNominal);
        txtNo = (EditText) findViewById(R.id.txtTransPlnNo);
        txtNo.setText(tujuanx);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransPlnBayar);
        listPLN = (ListView) findViewById(R.id.listPLN);
        txtpilihpembayaran = findViewById(R.id.tvPiliPembayaran);

        setListViewHeightBasedOnChildren(listPLN);

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        txtno_hp = (EditText) findViewById(R.id.txt_no_hp);

        if (PreferenceUtil.getNumberPhone(this).startsWith("+62")) {
            String no = PreferenceUtil.getNumberPhone(this).replace("+62", "0");
            txtno_hp.setText(no);
        } else {
            txtno_hp.setText(PreferenceUtil.getNumberPhone(this));
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*txtTransaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        txtTransaksi_ke.setText("1");*/

        layoutView = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);


        layoutTransaksiKe = findViewById(R.id.layout_transaksi_ke);
        layoutNoKonfirmasi = findViewById(R.id.layout_no_konfirmasi);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group_pln);
        radioGroup.clearCheck();

        RadioButton rbToken = findViewById(R.id.radio_pln_token);
        RadioButton rbTagihan = findViewById(R.id.radio_pln);

        //Toast.makeText(this, "" + nominalx, Toast.LENGTH_SHORT).show();
        if (nominalx != null) {
            if (nominalx.equalsIgnoreCase("token")) {
                rbToken.setChecked(true);
            } else if (nominalx.equalsIgnoreCase("ppob")) {
                rbTagihan.setChecked(true);
            } else {
                rbToken.setChecked(true);
            }
        } else {
            rbToken.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb.getText().toString().equalsIgnoreCase("Token Listrik")) {
                    loadProvider(strUserID, strAccessToken, strAplUse, strProductType);
//                    getProductPLNToken(); ========================>next ini yang akan di jalankan
                    layoutNominal.setVisibility(View.VISIBLE);
                    rbPln = "Token Listrik";
                    btnBayar.setText("BELI");
                    btnBayar.setVisibility(View.GONE);
                    txtno_hp.setVisibility(View.VISIBLE);
                    layoutTransaksiKe.setVisibility(View.VISIBLE);
                    layoutNoKonfirmasi.setVisibility(View.VISIBLE);
                    listPLN.setVisibility(View.VISIBLE);
                    txtpilihpembayaran.setVisibility(View.VISIBLE);
                } else {
                    loadProviderPPOB(strUserID, strAccessToken, "OTU", "PLN");
                    rbPln = "PLN PASCA";
                    layoutNominal.setVisibility(View.GONE);
                    btnBayar.setText("CEK TAGIHAN");
                    btnBayar.setVisibility(View.VISIBLE);
                    txtno_hp.setVisibility(View.VISIBLE);
                    layoutTransaksiKe.setVisibility(View.GONE);
                    layoutNoKonfirmasi.setVisibility(View.VISIBLE);
                    listPLN.setVisibility(View.GONE);
                    txtpilihpembayaran.setVisibility(View.GONE);
                }
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

        load();
        initializeResources();
    }

    private void load() {
        getProductPLNToken();
        layoutNominal.setVisibility(View.VISIBLE);
        rbPln = "Token Listrik";
        btnBayar.setText("BELI");
        btnBayar.setVisibility(View.GONE);
        txtno_hp.setVisibility(View.VISIBLE);
        layoutTransaksiKe.setVisibility(View.VISIBLE);
        layoutNoKonfirmasi.setVisibility(View.VISIBLE);
        listPLN.setVisibility(View.VISIBLE);
        txtpilihpembayaran.setVisibility(View.VISIBLE);
    }


    private void loadProvider(String userID, String accessToken, String aplUse, String productGroup) {
        loadingDialog = ProgressDialog.show(TransPln.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<LoadDataResponse> dataCall = apiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + status + "/" + error);
                    Log.d("OPPO-1", "onResponse: " + userID + "/" + accessToken + "/" + aplUse);
                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> products = response.body().getProductList();

                        List<String> list = new ArrayList<String>();
                        list.clear();

                        for (int i = 0; i < products.size(); i++) {
                            String x = products.get(i).getName();
                            list.add(x);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, list);
                        spinnerProvider.setAdapter(adapter);
                        spinnerProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        utilsAlert.globalDialog(TransPln.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransPln.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPln.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
                Log.d("OPPO-1", "onFailure: " + t.getMessage());
            }
        });
    }

    private void loadProduct(String userID, String accessToken, String aplUse, String provider) {
        loadingDialog = ProgressDialog.show(TransPln.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<LoadDataResponseProduct> userCall = apiInterfacePayment.getLoadProduct(userID, accessToken, aplUse, provider);
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
                        id_paket = new ArrayList<>();
                        listPrice.clear();
                        listNama.clear();
                        listEp.clear();
                        final List<DataProduct> products = response.body().getProducts();
                        for (int i = 0; i < products.size(); i++) {
                            String name = products.get(i).getName();
                            String price = products.get(i).getPrice();
                            String ep = products.get(i).getEp();
                            id_paket.add(products.get(i).getCode());
                            listNama.add(name);
                            listEp.add(ep);
                            listPrice.add(price);
                        }

                        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), products);
                        listPLN.setAdapter(adapter);

                    } else {
                        utilsAlert.globalDialog(TransPln.this, titleAlert, respMessage);
                        //Toast.makeText(TransPln.this, "" + respMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private void loadProviderPPOB(String userID, String accessToken, String aplUse, String productGroup) {

        Call<LoadDataResponse> dataCall = apiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
//                loadingDialog.dismiss();
                nama_operator = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_operator);
                spinnerProvider.setAdapter(adapter);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> result = response.body().getProductList();
                        nama_operator = new String[result.size()];
                        selected_operator = result.get(0).getCode();

                        // Toast.makeText(TransPln.this, "SUCCESS " + nama_operator + " " + selected_operator, Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < result.size(); i++) {
                            nama_operator[i] = result.get(i).getName();
                        }

                        adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_operator);
                        spinnerProvider.setAdapter(adapter);
                        spinnerProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_operator = result.get(position).getCode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        Toast.makeText(TransPln.this, "onFailed 1 " + titleAlert + " " + error, Toast.LENGTH_SHORT).show();
                        utilsAlert.globalDialog(TransPln.this, titleAlert, error);
                    }
                } else {
                    Toast.makeText(TransPln.this, "onFailed 2", Toast.LENGTH_SHORT).show();
                    utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                Toast.makeText(TransPln.this, "onFailure " + t.getMessage(), Toast.LENGTH_SHORT).show();
                utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
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
        txtNo.setError(null);

        if (id_pel.isEmpty()) {
            txtNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void cek_transaksi() {
        Log.d("OPPO-1", "cek_transaksi: ");
        loadingDialog = ProgressDialog.show(TransPln.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, "plnpost", txtNo.getText().toString(), txtno_hp.getText().toString(), strAplUse);

        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: " + error);

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
                        inKonfirmasi.putExtra("ep", response.body().getEp());

                        inKonfirmasi.putExtra("transaksi", "-");
                        inKonfirmasi.putExtra("harga", "-");
                        inKonfirmasi.putExtra("id_pel", "-");
                        inKonfirmasi.putExtra("jenis", "-");
                        inKonfirmasi.putExtra("pin", "-");
                        inKonfirmasi.putExtra("cmd_save", "-");
                        startActivity(inKonfirmasi);
                    } else {
                        utilsAlert.globalDialog(TransPln.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private void cek_transaksi_token() {
        Log.d("OPPO-1", "cek_transaksi_pasca: " + txtno_hp.getText().toString());
        loadingDialog = ProgressDialog.show(TransPln.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString(), txtno_hp.getText().toString(), "", code);


        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("productCode", "PLN TOKEN");//
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
                        inKonfirmasi.putExtra("ep", ep);

                        inKonfirmasi.putExtra("transaksi", "-");
                        inKonfirmasi.putExtra("harga", "-");
                        inKonfirmasi.putExtra("id_pel", "-");
                        inKonfirmasi.putExtra("jenis", "-");
                        inKonfirmasi.putExtra("pin", "-");
                        inKonfirmasi.putExtra("cmd_save", "-");
                        startActivity(inKonfirmasi);
                    } else {
                        utilsAlert.globalDialog(TransPln.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPln.this, titleAlert, getResources().getString(R.string.error_api));
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_transaction_evidence:
                Toast.makeText(this, "Kirim bukti pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initializeResources() {
        Log.d("OPPO-1", "initializeResources: ");
        listPLN = (ListView) findViewById(R.id.listPLN);
        listPLN.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }

                if (rbPln.equalsIgnoreCase("Token Listrik")) {
                    final Dialog dialog = new Dialog(TransPln.this);

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.activity_alert_dialog);
                    dialog.setCancelable(false);
                    dialog.setTitle("Peringatan Transaksi!!!");
                    ep = listEP.get(position);
                    code = listCode.get(position);

                    btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                    btnNo = (Button) dialog.findViewById(R.id.btn_no);
                    txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                    txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                    txtnomor.setText(txtNo.getText().toString());
                    txtvoucher.setText(code);

                    TextView tvProduct = dialog.findViewById(R.id.txt_product);
                    TextView tvTranske = dialog.findViewById(R.id.txt_transke);
                    tvProduct.setText(listName.get(position));
                    //tvTranske.setText(txtTransaksi_ke.getText().toString());

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cek_transaksi_token();
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
                    Window window = dialog.getWindow();
                    assert window != null;
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    return;
                } else {
                    cek_transaksi();
                }
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        Log.d("OPPO-1", "setListViewHeightBasedOnChildren: " + totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1)));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    //==============================================NEW API================================================
    ArrayList<String> listCode;
    ArrayList<String> listPrice;
    ArrayList<String> listName;
    ArrayList<String> listEP;
    ArrayList<String> listisActive;
    ArrayList<String> listType;
    ArrayList<String> listProviderProduct;

    public void getProductPLNToken() {
        showProgress(false);
        Call<DataAllProduct> getproduk_plntoken = apiInterfacePayment.getproduct_plntoken(strUserID, strAccessToken, strAplUse);
        getproduk_plntoken.enqueue(new Callback<DataAllProduct>() {
            @Override
            public void onResponse(Call<DataAllProduct> call, Response<DataAllProduct> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    listCode = new ArrayList<>();
                    listPrice = new ArrayList<>();
                    listName = new ArrayList<>();
                    listEP = new ArrayList<>();
                    listisActive = new ArrayList<>();
                    listType = new ArrayList<>();
                    listProviderProduct = new ArrayList<>();
                    listCode.clear();
                    listPrice.clear();
                    listName.clear();
                    listEP.clear();
                    listisActive.clear();
                    listType.clear();
                    listProviderProduct.clear();
                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        List<DataProduct> data = response.body().getData();
                        for (int i = 0; i < data.size(); i++) {
                            listCode.add(data.get(i).getCode());
                            listPrice.add(data.get(i).getPrice());
                            listName.add(data.get(i).getName());
                            listEP.add(data.get(i).getEp());
                            listisActive.add(data.get(i).getIsActive());
                            listType.add(data.get(i).getType());
                            listProviderProduct.add(data.get(i).getProvider());
                        }
                        Log.d("OPPO-1", "onResponse: " + listCode);
                        SpinnerAdapterNew adapter = new SpinnerAdapterNew(getApplicationContext(), listName, listPrice, listEP, listProviderProduct, "PLN TOKEN");
                        listPLN.setAdapter(adapter);
                    } else {
                        utilsAlert.globalDialog(TransPln.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransPln.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransPln.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
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
