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
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailPrefix;
import com.eklanku.otuChat.ui.activities.payment.models.DataPrefix;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProvider;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapterNew;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerGameAdapter;
import com.eklanku.otuChat.utils.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;


public class TransSMS extends AppCompatActivity {

    SharedPreferences prefs;
    Spinner spnKartu, spnNominal;
    EditText txtNo, txtTransaksi_ke;
    TextInputLayout layoutNo;
    Button btnBayar;
    String load_id = "X", selected_nominal;
    Dialog loadingDialog;
    ArrayAdapter<String> adapter;
    ImageView imgopr;
    TextView txtopr;
    LinearLayout layoutPulsa;
    ProgressBar progressBar;
    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strAplUse = "OTU", strProductType = "SMS";
    String strOpsel;
    String code, ep;

    TextView txtnomor, txtvoucher;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String titleAlert = "SMS";

    ListView listSMS;
    ArrayList<String> id_sms;

    TextView tvEmpty;

    String nominalx, tujuanx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_sms2);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransSMS.this);

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

        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnKartu = (Spinner) findViewById(R.id.spnTransSMS);
        spnNominal = (Spinner) findViewById(R.id.spnTransSMSNominal);
        txtNo = (EditText) findViewById(R.id.txtTransSMSNo);
        txtNo.setText(tujuanx);
        txtTransaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        btnBayar = (Button) findViewById(R.id.btnTransSMSBayar);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        listSMS = findViewById(R.id.listSMS);
        tvEmpty = findViewById(R.id.tv_empty);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("BELI");

        layoutPulsa = findViewById(R.id.linear_sms);
        progressBar = findViewById(R.id.progress_sms);

        txtopr = (TextView) findViewById(R.id.textopr);
        imgopr = (ImageView) findViewById(R.id.imgOpr);

        txtTransaksi_ke.setText("1");

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        initializeResources();
        loadPrefix();
        // getProductSMS();
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

            cekPrefix(s);
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

    private void loadProvider(String userID, String accessToken, String aplUse, String productType) {
        loadingDialog = ProgressDialog.show(TransSMS.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<LoadDataResponseProvider> userCall = apiInterfacePayment.getLoadProvider(userID, accessToken, aplUse, productType);
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

                        SpinnerGameAdapter spinnerGameAdapter = new SpinnerGameAdapter(getApplicationContext(), products, "PAKET SMS");
                        spnKartu.setAdapter(spinnerGameAdapter);
                        spnKartu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                strOpsel = products.get(position).getName_provaider();
                                loadProduct(strUserID, strAccessToken, strAplUse, strOpsel);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        utilsAlert.globalDialog(TransSMS.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransSMS.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProvider> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransSMS.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private void loadProduct(String userID, String accessToken, String aplUse, String provider) {
        loadingDialog = ProgressDialog.show(TransSMS.this, "Harap Tunggu", "Mengambil Data...");
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
                        id_sms = new ArrayList<>();
                        listPrice.clear();
                        listNama.clear();
                        listEp.clear();
                        final List<DataProduct> products = response.body().getProducts();
                        for (int i = 0; i < products.size(); i++) {
                            String name = products.get(i).getName();
                            String price = products.get(i).getPrice();
                            String ep = products.get(i).getEp();
                            id_sms.add(products.get(i).getCode());
                            listNama.add(name);
                            listEp.add(ep);
                            listPrice.add(price);
                        }

                        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), products);
                        listSMS.setAdapter(adapter);

                    } else {
                        utilsAlert.globalDialog(TransSMS.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransSMS.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransSMS.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private void cek_transaksi() {
        Log.d("OPPO-1", "cek_transaksi: " + code);
        loadingDialog = ProgressDialog.show(TransSMS.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString().trim(), "", "", code);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equals("SUCCESS")) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("productCode", "PAKET SMS");//
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
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransSMS.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransSMS.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransSMS.this, titleAlert, getResources().getString(R.string.error_api));
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
        listSMS = (ListView) findViewById(R.id.listSMS);

        listSMS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }

                code = code_product.get(position);
                ep = endpoint.get(position);
                final Dialog dialog = new Dialog(TransSMS.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(txtNo.getText().toString());
                txtvoucher.setText(formatRupiah(Double.parseDouble(b.get(position))));

                TextView tvProduct = dialog.findViewById(R.id.txt_product);
                TextView tvTranske = dialog.findViewById(R.id.txt_transke);
                tvProduct.setText(code_name.get(position));
                tvTranske.setText(txtTransaksi_ke.getText().toString());

                TextView tvKeterangan = dialog.findViewById(R.id.txt_keterangan);
                TextView total = dialog.findViewById(R.id.txt_total);
                ImageView imgKonfirmasi = dialog.findViewById(R.id.img_konfirmasi);
                TextView biaya = dialog.findViewById(R.id.txt_biaya);

                tvKeterangan.setText(code_name.get(position));
                total.setText(formatRupiah(Double.parseDouble(b.get(position))));
                biaya.setText("Rp0");
                String setImgOpr = "";
                if (oprSMS.equalsIgnoreCase("ISAT SMS")) {
                    setImgOpr = "indosat";
                } else if (oprSMS.equalsIgnoreCase("TSEL SMS")) {
                    setImgOpr = "telkomsel";
                }
                int idimg = TransSMS.this.getResources().getIdentifier("mipmap/" + setImgOpr.toLowerCase(), null, TransSMS.this.getPackageName());
                imgKonfirmasi.setImageResource(idimg);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cek_transaksi();
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

            }
        });
    }

    public String formatRupiah(double nominal) {
        String parseRp = "";
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        parseRp = formatRupiah.format(nominal);
        return parseRp;
    }


    //=========================================================NEW API=================================================================

    ArrayList<String> listProvider;
    ArrayList<String> listPrefix;

    public void loadPrefix() {
        showProgress(true);
        Call<DataPrefix> prefix_sms = apiInterfacePayment.getPrefixSMS(strUserID, strAccessToken, strAplUse);
        prefix_sms.enqueue(new Callback<DataPrefix>() {
            @Override
            public void onResponse(Call<DataPrefix> call, Response<DataPrefix> response) {
                if (response.isSuccessful()) {
                    listProvider = new ArrayList<>();
                    listPrefix = new ArrayList<>();
                    listProvider.clear();
                    listPrefix.clear();
                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        List<DataDetailPrefix> data = response.body().getData();
                        for (int i = 0; i < data.size(); i++) {
                            listProvider.add(data.get(i).getProvider());
                            listPrefix.add(data.get(i).getPrefix());
                        }
                        getProductSMS();
                    } else {
                        showProgress(false);
                        utilsAlert.globalDialog(TransSMS.this, titleAlert, respMessage);
                    }
                } else {
                    showProgress(false);
                    utilsAlert.globalDialog(TransSMS.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataPrefix> call, Throwable t) {
                Log.d("OPPO-1", "onFailure: " + t.getMessage());
                showProgress(false);
                utilsAlert.globalDialog(TransSMS.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
    }


    String oprSMS = "";
    String tempOprPulsa = "";
    boolean statOprPulsa = false;
    boolean otherOpr = true;
    ArrayList<String> code_product, code_name, endpoint;
    ArrayList<String> a, b, c, d;

    public void cekPrefix(CharSequence s) {

        a = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>();
        d = new ArrayList<>();
        code_product = new ArrayList<>();
        code_name = new ArrayList<>();
        endpoint = new ArrayList<>();
        SpinnerAdapterNew adapter = null;
        listSMS.setAdapter(null);


        try {
            String nomorHp = "";
            if (s.length() >= 6) {
                String nomorHP1 = s.toString().substring(0, 2);
                String valNomorHp = "", valNomorHP2 = "";
                if (nomorHP1.startsWith("+6")) {
                    valNomorHp = nomorHP1.replace("+6", "0");
                    nomorHp = valNomorHp + s.toString().substring(3, 6);
                } else if (nomorHP1.startsWith("62")) {
                    valNomorHp = nomorHP1.replace("62", "0");
                    nomorHp = valNomorHp + s.toString().substring(2, 5);
                } else {
                    nomorHp = s.toString().substring(0, 4);
                }

                for (int i = 0; i < listPrefix.size(); i++) {
                    if (nomorHp.equals(listPrefix.get(i))) {
                        oprSMS = listProvider.get(i);
                        statOprPulsa = true;
                        otherOpr = false;
                        String setImgOpr = "";
                        if (oprSMS.equalsIgnoreCase("ISAT SMS")) {
                            setImgOpr = "indosat";
                        } else if (oprSMS.equalsIgnoreCase("TSEL SMS")) {
                            setImgOpr = "telkomsel";
                        }
                        int id = TransSMS.this.getResources().getIdentifier("mipmap/" + setImgOpr.toLowerCase(), null, TransSMS.this.getPackageName());
                        imgopr.setImageResource(id);
                        break;
                    } else {
                        a.clear();
                        b.clear();
                        c.clear();
                        d.clear();
                        code_product.clear();
                        code_name.clear();
                        endpoint.clear();
                        statOprPulsa = false;
                        tempOprPulsa = "";
                        btnBayar.setEnabled(true);
                        oprSMS = "";
                        imgopr.setImageResource(0);
                        listSMS.setAdapter(null);
                    }
                }


                for (int j = 0; j < listProviderProduct.size(); j++) {
                    if (listProviderProduct.get(j).equalsIgnoreCase(oprSMS)) {
                        a.add(listName.get(j));
                        b.add(listPrice.get(j));
                        c.add(listEP.get(j));
                        d.add(listProviderProduct.get(j));
                        code_product.add(listCode.get(j));
                        code_name.add(listName.get(j));
                        endpoint.add(listEP.get(j));
                    }
                }

                if (a.size() >= 1 /*&& nomorHp.equals(oprPulsa)*/) {
                    tvEmpty.setVisibility(View.GONE);
//                    listPulsa.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
//                    listPulsa.setVisibility(View.GONE);
                }

                adapter = new SpinnerAdapterNew(getApplicationContext(), a, b, c, d, oprSMS);
                listSMS.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (!oprSMS.equalsIgnoreCase(tempOprPulsa) && statOprPulsa) {
                    tempOprPulsa = oprSMS;
                    statOprPulsa = false;
                    btnBayar.setEnabled(true);
                    otherOpr = true;
                }

                if (otherOpr && oprSMS.equalsIgnoreCase("")) {
                    otherOpr = false;
                }

            } else /*if (s.length() < 4) */ {
                listSMS.setAdapter(null);
                a.clear();
                b.clear();
                c.clear();
                d.clear();
                code_product.clear();
                code_name.clear();
                endpoint.clear();
                listSMS.setAdapter(adapter);
                statOprPulsa = false;
                otherOpr = true;
                tempOprPulsa = "";
                imgopr.setImageResource(0);
                btnBayar.setEnabled(false);
            } /*else if (s.length() >= 8 && s.length() <= 13) {
                listSMS.setAdapter(adapter);
                btnBayar.setEnabled(false);
                imgopr.setImageResource(0);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ArrayList<String> listCode;
    ArrayList<String> listPrice;
    ArrayList<String> listName;
    ArrayList<String> listEP;
    ArrayList<String> listIsActive;
    ArrayList<String> listType;
    ArrayList<String> listProviderProduct;

    public void getProductSMS() {
        Call<DataAllProduct> product_sms = apiInterfacePayment.getProduct_sms(strUserID, strAccessToken, strAplUse);
        product_sms.enqueue(new Callback<DataAllProduct>() {
            @Override
            public void onResponse(Call<DataAllProduct> call, Response<DataAllProduct> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    listCode = new ArrayList<>();
                    listName = new ArrayList<>();
                    listPrice = new ArrayList<>();
                    listEP = new ArrayList<>();
                    listIsActive = new ArrayList<>();
                    listProviderProduct = new ArrayList<>();
                    listType = new ArrayList<>();
                    listCode.clear();
                    listName.clear();
                    listPrice.clear();
                    listEP.clear();
                    listIsActive.clear();
                    listType.clear();
                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        List<DataProduct> data = response.body().getData();
                        for (int i = 0; i < data.size(); i++) {
                            listCode.add(data.get(i).getCode());
                            listName.add(data.get(i).getName());
                            listPrice.add(data.get(i).getPrice());
                            listEP.add(data.get(i).getEp());
                            listIsActive.add(data.get(i).getIsActive());
                            listType.add(data.get(i).getType());
                            listProviderProduct.add(data.get(i).getProvider());
                        }
                        if (!TextUtils.isEmpty(tujuanx)) {
                            cekPrefix(tujuanx);
                        }
                    } else {
                        utilsAlert.globalDialog(TransSMS.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransSMS.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransSMS.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            layoutPulsa.setVisibility(show ? View.GONE : View.VISIBLE);
            layoutPulsa.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutPulsa.setVisibility(show ? View.GONE : View.VISIBLE);
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
            layoutPulsa.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

}