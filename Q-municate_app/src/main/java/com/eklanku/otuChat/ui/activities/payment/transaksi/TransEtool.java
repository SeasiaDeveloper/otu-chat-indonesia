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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.konfirmasitransaksi.TransKonfirmasiPrabayar;
import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailProviderByType;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProvider;
import com.eklanku.otuChat.ui.activities.payment.models.DataProviderByType;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapterNew;
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

public class TransEtool extends AppCompatActivity {

    SharedPreferences prefs;
    Spinner spnKartu, spnNominal;
    EditText txtNo, txtTrasaksi_ke;
    TextInputLayout layoutNo;
    Button btnBayar;
    String load_id = "X", selected_nominal;
    Dialog loadingDialog;
    ArrayAdapter<String> adapter;
    ImageView imgopr;
    TextView txtopr;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strAplUse = "OTU", strProductType = "ETOOL";
    String strOpsel;
    String code, ep, name, nameetoll;

    TextView txtnomor, txtvoucher;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String titleAlert = "E Tool";

    ListView listNamaPaket;
    ArrayList<String> idNamaPaket, nameEtool;

    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;
    String nominalx, tujuanx;

    private static long mLastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_etool);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransEtool.this);

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
        spnKartu = (Spinner) findViewById(R.id.spnTransEtool);
        spnNominal = (Spinner) findViewById(R.id.spnTransEtoolNominal);
        txtNo = (EditText) findViewById(R.id.txtTransEtoolNo);
        txtNo.setText(tujuanx);
        btnBayar = (Button) findViewById(R.id.btnTransEtoolBayar);
        txtTrasaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("BELI");
        listNamaPaket = findViewById(R.id.listNamaPaket);

        layoutView = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);

        txtTrasaksi_ke.setText("1");
        txtopr = (TextView) findViewById(R.id.textopr);
        imgopr = (ImageView) findViewById(R.id.imgopr);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        txtNo.addTextChangedListener(new txtWatcher(txtNo));


        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransEtool.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(txtNo.getText().toString());
                txtvoucher.setText(code);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

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
                return;

            }
        });

        getproduct_etool();
        initializeResources();


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
        Log.d("OPPO-1", "cek_transaksi: " + code);
        loadingDialog = ProgressDialog.show(TransEtool.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString(), "", "", code);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equals("SUCCESS")) {
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasiPrabayar.class);
                        inKonfirmasi.putExtra("productCode", "ETOOL");//
                        inKonfirmasi.putExtra("billingReferenceID", response.body().getTransactionID());//
                        inKonfirmasi.putExtra("customerMSISDN", response.body().getMSISDN());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("billing", response.body().getNominal());//
                        inKonfirmasi.putExtra("adminBank", "0");
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("ep", ep);
                        inKonfirmasi.putExtra("jenisvoucher", name);
                        inKonfirmasi.putExtra("oprPulsa", nameetoll);

                        /*inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("customerID", response.body().getMSISDN());//
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
                        inKonfirmasi.putExtra("sellPrice", "");
                        inKonfirmasi.putExtra("profit", "");*/
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransEtool.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransEtool.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransEtool.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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
        listNamaPaket = (ListView) findViewById(R.id.listNamaPaket);
        listNamaPaket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                code = code_product.get(position);
                ep = endpoint.get(position);
                name = code_name.get(position);
                nameetoll = d.get(position);
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransEtool.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(txtNo.getText().toString());
                txtvoucher.setText(formatRupiah(Double.parseDouble(listPrice.get(position))));

                TextView tvProduct = dialog.findViewById(R.id.txt_product);
                TextView tvTranske = dialog.findViewById(R.id.txt_transke);
                tvProduct.setText(code_name.get(position));
                tvTranske.setText(txtTrasaksi_ke.getText().toString());

                TextView tvKeterangan = dialog.findViewById(R.id.txt_keterangan);
                TextView total = dialog.findViewById(R.id.txt_total);
                ImageView imgKonfirmasi = dialog.findViewById(R.id.img_konfirmasi);
                TextView biaya = dialog.findViewById(R.id.txt_biaya);

                tvKeterangan.setText(code_name.get(position));
                total.setText(formatRupiah(Double.parseDouble(listPrice.get(position))));
                biaya.setText("Rp0");

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
    //======================================================NEW API================================================
    ArrayList<String> listCode;
    ArrayList<String> listPrice;
    ArrayList<String> listName;
    ArrayList<String> listEP;
    ArrayList<String> listisActive;
    ArrayList<String> listType;
    ArrayList<String> listProviderProduct;

    public void getproduct_etool() {
        showProgress(true);

        Call<DataAllProduct> getproduct_etoll = apiInterfacePayment.getproduct_etoll(strUserID, strAccessToken, strAplUse);
        getproduct_etoll.enqueue(new Callback<DataAllProduct>() {
            @Override
            public void onResponse(Call<DataAllProduct> call, Response<DataAllProduct> response) {
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
                        LoadProviderByTipe();
                    } else {
                        showProgress(false);
                        utilsAlert.globalDialog(TransEtool.this, titleAlert, respMessage);
                    }
                } else {
                    showProgress(false);
                    utilsAlert.globalDialog(TransEtool.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransEtool.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
    }

    ArrayList<String> list;

    public void LoadProviderByTipe() {
        Log.d("OPPO-1", "onResponse: running" + strUserID + ", " + strAccessToken + ", " + strAplUse + ", " + strProductType);
        Call<DataProviderByType> provider_by_type = apiInterfacePayment.getProviderByType(strUserID, strAccessToken, strAplUse, strProductType);
        provider_by_type.enqueue(new Callback<DataProviderByType>() {
            @Override
            public void onResponse(Call<DataProviderByType> call, Response<DataProviderByType> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    Log.d("OPPO-1", "onResponse >> status: " + status);
                    String respMessage = response.body().getRespMessage();
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        list = new ArrayList<String>();
                        list.clear();
                        List<DataDetailProviderByType> data = response.body().getData();
                        for (int i = 0; i < data.size(); i++) {
                            String x = data.get(i).getName_provider();
                            list.add(x);
                        }

                        Log.d("OPPO-1", "onResponse: "+list);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, list);
                        spnKartu.setAdapter(adapter);
                        spnKartu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                initProduct(list.get(position));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        utilsAlert.globalDialog(TransEtool.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransEtool.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataProviderByType> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransEtool.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
    }

    ArrayList<String> code_product, code_name, endpoint;
    ArrayList<String> a, b, c, d;

    public void initProduct(String provider) {
        Log.d("OPPO-1", "initProduct: " + provider);

        a = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>();
        d = new ArrayList<>();
        code_product = new ArrayList<>();
        code_name = new ArrayList<>();
        endpoint = new ArrayList<>();
        a.clear();
        b.clear();
        c.clear();
        d.clear();
        code_product.clear();
        code_name.clear();
        endpoint.clear();
        String product_type = "";
        for (int i = 0; i < listCode.size(); i++) {
            // Log.d("OPPO-1", "initProduct: "+listProviderProduct.get(i)+", "+provider);
            if (listProviderProduct.get(i).equalsIgnoreCase(provider)) {
                a.add(listName.get(i));
                b.add(listPrice.get(i));
                c.add(listEP.get(i));
                d.add(listProviderProduct.get(i));
                code_product.add(listCode.get(i));
                code_name.add(listName.get(i));
                endpoint.add(listEP.get(i));
            }
        }
        SpinnerAdapterNew adapterNew = new SpinnerAdapterNew(getApplicationContext(), a, b, c, d, product_type);
        listNamaPaket.setAdapter(adapterNew);
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
