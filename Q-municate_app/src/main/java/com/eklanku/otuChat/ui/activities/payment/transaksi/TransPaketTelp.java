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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailPrefix;
import com.eklanku.otuChat.ui.activities.payment.models.DataPrefix;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapterNew;
import com.eklanku.otuChat.ui.views.ARTextView;
import com.eklanku.otuChat.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransPaketTelp extends AppCompatActivity {


    SharedPreferences prefs;
    Spinner spnJenis, spnNama;
    EditText txtNo, txtTransaksi_ke;
    TextInputLayout layoutNo;
    Button btnBayar;
    String //id_member,
            load_id = "BXD", selected_nominal;
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
    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strOpsel, strAplUse = "OTU", strProductType = "KUOTA";
    String code, ep;

    TextView txtnomor, txtvoucher;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String titleAlert = "Paket Telp";

    ImageView imgOpr;
    TextView txOpr;

    RelativeLayout layOprPaket;
    ARTextView laytxOprPaket;
    ListView listPaketdata;
    ArrayList<String> id_paket;
    TextView tvEmpty;

    LinearLayout layoutPulsa;
    ProgressBar progressBar;
    String nominalx, tujuanx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_paket_telp);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransPaketTelp.this);


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
        spnJenis = (Spinner) findViewById(R.id.spnTransPaketJenis);
        spnNama = (Spinner) findViewById(R.id.spnTransPaketNama);
        txtNo = (EditText) findViewById(R.id.txtTransPaketNo);
        txtNo.setText(tujuanx);
        txtTransaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransPaketBayar);
        btnBayar.setText("BELI");

        imgOpr = findViewById(R.id.imgOpr);
        txOpr = findViewById(R.id.txOpr);
        layOprPaket = findViewById(R.id.layOprPaket);
        laytxOprPaket = findViewById(R.id.textView35);
        listPaketdata = findViewById(R.id.listPaketdata);
        tvEmpty = findViewById(R.id.tv_empty);
        layoutPulsa = findViewById(R.id.linear_paket);
        progressBar = findViewById(R.id.progress_paket);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nominal = new String[0];
        denom = new String[0];
        point = new String[0];
        txtTransaksi_ke.setText("1");

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
        //loadProvider(strUserID, strAccessToken, strAplUse, strProductType);

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransPaketTelp.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(txtNo.getText().toString().trim());
                txtvoucher.setText(code);

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
                return;
            }

        });

        initializeResources();
        loadPrefix();

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

            cekPrefixPaket(s);

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

    String oprPaket = "";
    String tempPaket = "";
    boolean statPaket = false;
    boolean otherOpr = true;

    //============================================================NEW API=======================================================
    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransPaketTelp.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString().trim(), "", "", code);
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

                        inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("productCode", "PAKET TELP");//
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
                        utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }


    private void initializeResources() {
        listPaketdata = (ListView) findViewById(R.id.listPaketdata);
        listPaketdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransPaketTelp.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                code = code_product.get(position);
                ep = endpoint.get(position);
                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);

                TextView tvProduct = dialog.findViewById(R.id.txt_product);
                TextView tvTranske = dialog.findViewById(R.id.txt_transke);
                tvProduct.setText(code_name.get(position));
                tvTranske.setText(txtTransaksi_ke.getText().toString());

                txtnomor.setText(txtNo.getText().toString().trim());
                txtvoucher.setText(code);
                btnYes.setText("YA, Lanjutkan");
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cek_transaksi();
                        dialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

    ArrayList<String> listProvider;
    ArrayList<String> listPrefix;

    public void loadPrefix() {
        showProgress(true);
        Call<DataPrefix> prefix_data = apiInterfacePayment.getPrefixTelp(strUserID, strAccessToken, strAplUse);
        prefix_data.enqueue(new Callback<DataPrefix>() {
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
                            listProvider.add(data.get(i).getProvider());//disini untuk replace nya
                            listPrefix.add(data.get(i).getPrefix());
                        }
                        getProductData();
                    } else {
                        showProgress(false);
                        utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, respMessage);
                    }
                } else {
                    showProgress(false);
                    utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataPrefix> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
    }

    ArrayList<String> code_product, code_name, endpoint;

    public void cekPrefixPaket(CharSequence s) {
        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();
        ArrayList<String> c = new ArrayList<>();
        ArrayList<String> d = new ArrayList<>();
        SpinnerAdapterNew adapter = null;
        code_product = new ArrayList<>();
        code_name = new ArrayList<>();
        endpoint = new ArrayList<>();
        listPaketdata.setAdapter(null);
        try {
            String nomorHp = "";
            if (s.length() >= 6) {
                String nomorHP1 = s.toString().substring(0, 2);
                String valNomorHp = "";
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
                        oprPaket = listProvider.get(i);
                        txOpr.setText(oprPaket);
                        statPaket = true;
                        otherOpr = false;
                        String setImgOpr = "";

                        Log.d("OPPO-1", "provider: " + oprPaket + " -> " + listProvider.get(i));

                        if (oprPaket.equalsIgnoreCase("XL TELPON")) {
                            setImgOpr = "xl";
                        } else if (oprPaket.equalsIgnoreCase("TSEL TELPON")) {
                            setImgOpr = "telkomsel";
                        } else if (oprPaket.equalsIgnoreCase("AXIS TELPON")) {
                            setImgOpr = "axis";
                        } else if (oprPaket.equalsIgnoreCase("SMARTFREN TELPON")) {
                            setImgOpr = "smart";
                        } else if (oprPaket.equalsIgnoreCase("TRI TELPON")) {
                            setImgOpr = "three";
                        } else if (oprPaket.equalsIgnoreCase("ISAT TELPON")) {
                            setImgOpr = "indosat";
                        } else if (oprPaket.equalsIgnoreCase("BOLT")) {
                            setImgOpr = "bolt";
                        }
                        int id = TransPaketTelp.this.getResources().getIdentifier("mipmap/" + setImgOpr, null, TransPaketTelp.this.getPackageName());
                        imgOpr.setImageResource(id);
                        break;
                    } else {
                        listPaketdata.setAdapter(null);
                        a.clear();
                        b.clear();
                        c.clear();
                        d.clear();
                        code_product.clear();
                        code_name.clear();
                        endpoint.clear();
                        statPaket = false;
                        tempPaket = "";
                        layOprPaket.setVisibility(View.GONE);
                        laytxOprPaket.setVisibility(View.GONE);
                        btnBayar.setEnabled(true);
                        oprPaket = "";
                        imgOpr.setImageResource(0);
                    }
                }


                for (int j = 0; j < listProviderProduct.size(); j++) {
                    if (listProviderProduct.get(j).equalsIgnoreCase(oprPaket)) {
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

                adapter = new SpinnerAdapterNew(getApplicationContext(), a, b, c, d, oprPaket);
                listPaketdata.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                if (!oprPaket.equalsIgnoreCase(tempPaket) && statPaket) {
                    tempPaket = oprPaket;
                    statPaket = false;
                    btnBayar.setEnabled(true);
                    layOprPaket.setVisibility(View.GONE);
                    laytxOprPaket.setVisibility(View.GONE);
                    otherOpr = true;
                }

                if (otherOpr && oprPaket.equalsIgnoreCase("")) {
                    otherOpr = false;
                }

            } else /*if (s.length() < 4) */ {
                listPaketdata.setAdapter(null);
                a.clear();
                b.clear();
                c.clear();
                d.clear();
                code_product.clear();
                code_name.clear();
                endpoint.clear();
                statPaket = false;
                otherOpr = true;
                tempPaket = "";
                txOpr.setText("");
                imgOpr.setImageResource(0);
                layOprPaket.setVisibility(View.GONE);
                laytxOprPaket.setVisibility(View.GONE);
                btnBayar.setEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<String> listCode;
    ArrayList<String> listName;
    ArrayList<String> listPrice;
    ArrayList<String> listEP;
    ArrayList<String> listIsActive;
    ArrayList<String> listProviderProduct;
    ArrayList<String> listType;

    public void getProductData() {

        Call<DataAllProduct> product_data = apiInterfacePayment.geetProduct_paketTelp(strUserID, strAccessToken, strAplUse);
        product_data.enqueue(new Callback<DataAllProduct>() {
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
                            Log.d("OPPO-1", "onResponse: " + data.get(i).getProvider() + ", " + data.get(i).getName());
                            listPrice.add(data.get(i).getPrice());
                            listEP.add(data.get(i).getEp());
                            listIsActive.add(data.get(i).getIsActive());
                            listType.add(data.get(i).getType());
                            listProviderProduct.add(data.get(i).getProvider());
                        }

                        if (!TextUtils.isEmpty(tujuanx)) {
                            cekPrefixPaket(tujuanx);
                        }
                    } else {
                        utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransPaketTelp.this, titleAlert, "3. " + getResources().getString(R.string.error_api));
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
