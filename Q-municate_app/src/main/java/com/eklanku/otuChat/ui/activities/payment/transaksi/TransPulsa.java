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
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapterNew;
import com.eklanku.otuChat.ui.views.ARTextView;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransPulsa extends AppCompatActivity {

    SharedPreferences prefs;
    Spinner spnKartu, spnNominal;
    EditText txtNo, etTransaksiKe;
    TextInputLayout layoutNo;
    Button btnBayar;
    String load_id = "X";
    Dialog loadingDialog;
    ArrayAdapter<String> adapter;
    ImageView imgopr;
    TextView tvOpr;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strAplUse = "OTU";
    String code, point;

    Context context;

    TextView tvNomor, tvVoucher, tvProduct, tvTranske;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String titleAlert = "Pulsa";

    ImageView imgOpr;
    TextView txOpr;
    RelativeLayout layOpr;
    ARTextView txLayOpr;

    ListView listPulsa;
    ArrayList<String> id_paket;
    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_pulsa2);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransPulsa.this);

        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnKartu = findViewById(R.id.spnTransPulsaKartu);
        spnNominal = findViewById(R.id.spnTransPulsaNominal);
        txtNo = findViewById(R.id.txtTransPulsaNo);
        etTransaksiKe = findViewById(R.id.txt_transaksi_ke);
        btnBayar = findViewById(R.id.btnTransPulsaBayar);
        layoutNo = findViewById(R.id.txtLayoutTransPulsaNo);

        imgOpr = findViewById(R.id.imgOpr);
        txOpr = findViewById(R.id.txOpr);
        layOpr = findViewById(R.id.layNominal);
        txLayOpr = findViewById(R.id.txLayOpr);
        listPulsa = findViewById(R.id.listPulsa);

        layoutView = findViewById(R.id.linear_pulsa);
        progressBar = findViewById(R.id.progress_pulsa);
        tvEmpty = findViewById(R.id.tv_empty);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnBayar.setEnabled(false);
        btnBayar.setText(getString(R.string.beli));

        tvOpr = findViewById(R.id.textopr);
        imgopr = findViewById(R.id.imgopr);
        etTransaksiKe.setText("1");

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(PreferenceManager.KEY_USERID);
        strAccessToken = user.get(PreferenceManager.KEY_ACCESS_TOKEN);

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnBayar.setOnClickListener(view -> {
            if (!validateIdPel()) {
                return;
            }


            final Dialog dialog = new Dialog(TransPulsa.this);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_alert_dialog);
            dialog.setCancelable(false);
            dialog.setTitle("Peringatan Transaksi!!!");

            btnYes = dialog.findViewById(R.id.btn_yes);
            btnNo = dialog.findViewById(R.id.btn_no);
            tvNomor = dialog.findViewById(R.id.txt_nomor);
            tvVoucher = dialog.findViewById(R.id.txt_voucher);
            tvNomor.setText(txtNo.getText().toString().trim());
            tvVoucher.setText(code);
            btnYes.setText(getString(R.string.lanjutkan));
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cekTransaksi();
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
            return;

        });

        initializeResources();
        getPrefixPulsa();
        //getProductPulsa();

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
            cekPrefixNumber(s);
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateIdPel();
        }
    }

    private boolean validateIdPel() {
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

    private void cekTransaksi() {
        loadingDialog = ProgressDialog.show(TransPulsa.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString().trim(), etTransaksiKe.getText().toString(), "", "", code);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equals("SUCCESS")) {
                        //List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("productCode", "PULSA");//
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
                        inKonfirmasi.putExtra("ep", point);
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransPulsa.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransPulsa.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPulsa.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_TRANSBELI", t.getMessage());
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

    String oprPulsa = "";
    String tempOprPulsa = "";
    boolean statOprPulsa = false;
    boolean otherOpr = true;

    private void initializeResources() {
        listPulsa = findViewById(R.id.listPulsa);

        listPulsa.setOnItemClickListener((parent, view, position, id) -> {
            if (!validateIdPel()) {
                return;
            }

            code = code_product.get(position);
            point = point_ep.get(position);
            String name = code_name.get(position);
            dialogWarning(code, name);
        });

    }

    public void dialogWarning(String kodepaket, String name) {
        final Dialog dialog = new Dialog(TransPulsa.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_alert_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Peringatan Transaksi!!!");


        btnYes = dialog.findViewById(R.id.btn_yes);
        btnNo = dialog.findViewById(R.id.btn_no);
        tvNomor = dialog.findViewById(R.id.txt_nomor);
        tvVoucher = dialog.findViewById(R.id.txt_voucher);

        tvProduct = dialog.findViewById(R.id.txt_product);
        tvTranske = dialog.findViewById(R.id.txt_transke);
        tvProduct.setText(name);
        tvTranske.setText(etTransaksiKe.getText().toString());

        tvNomor.setText(txtNo.getText().toString().trim());
        tvVoucher.setText(kodepaket);

        btnYes.setText(getString(R.string.lanjutkan));
        btnYes.setOnClickListener(view -> {
            cekTransaksi();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*====================================================new service api=====================================*/
    ArrayList<String> listProvider;
    ArrayList<String> listPrefix;

    public void getPrefixPulsa() {
        showProgress(true);
        Call<DataPrefix> dataPrefix = apiInterfacePayment.getPrefixPulsa(strUserID, strAccessToken, strAplUse);
        Log.d("OPPO-1", "getPrefixPulsa: " + strUserID + ", " + strAccessToken + ", " + strAplUse);
        dataPrefix.enqueue(new Callback<DataPrefix>() {
            @Override
            public void onResponse(Call<DataPrefix> call, Response<DataPrefix> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    Log.d("OPPO-1", "status: " + status);
                    String respMessage = response.body().getRespMessage();
                    listProvider = new ArrayList<>();
                    listPrefix = new ArrayList<>();
                    listProvider.clear();
                    listPrefix.clear();
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        List<DataDetailPrefix> data = response.body().getData();
                        for (int i = 0; i < data.size(); i++) {
                            listProvider.add(data.get(i).getProvider());
                            listPrefix.add(data.get(i).getPrefix());
                        }
                        getProductPulsa();
                    } else {
                        showProgress(false);
                        utilsAlert.globalDialog(TransPulsa.this, titleAlert, respMessage);
                    }
                } else {
                    showProgress(false);
                    utilsAlert.globalDialog(TransPulsa.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataPrefix> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransPulsa.this, titleAlert, "2. " + getResources().getString(R.string.error_api));
            }
        });
    }

    ArrayList<String> code_product;
    ArrayList<String> code_name;
    ArrayList<String> point_ep;

    public void cekPrefixNumber(CharSequence s) {
        SpinnerAdapterNew adapter = null;
        ArrayList<String> a, b, c, d;
        a = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>();
        d = new ArrayList<>();
        code_product = new ArrayList<>();
        code_name= new ArrayList<>();
        point_ep = new ArrayList<>();
        listPulsa.setAdapter(null);

        try {
            String nomorHp;
            if (s.length() >= 6) {

                a.clear();
                b.clear();
                c.clear();
                d.clear();
                listPulsa.setAdapter(null);

                String nomorHP1 = s.toString().substring(0, 2);
                String valNomorHp  /*, valNomorHP2 = ""*/;
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
                    Log.d("AYIK", "list->" + nomorHp + ", " + listPrefix.get(i));
                    if (nomorHp.equals(listPrefix.get(i))) {
                        oprPulsa = listProvider.get(i);
                        txOpr.setText(oprPulsa);
                        statOprPulsa = true;
                        otherOpr = false;
                        int id = TransPulsa.this.getResources().getIdentifier("mipmap/" + oprPulsa.toLowerCase(), null, TransPulsa.this.getPackageName());
                        imgOpr.setImageResource(id);
                        break;
                    } else {
                        a.clear();
                        b.clear();
                        c.clear();
                        d.clear();
                        point_ep.clear();
                        code_product.clear();
                        code_name.clear();
                        layOpr.setVisibility(View.GONE);
                        txLayOpr.setVisibility(View.GONE);
                        statOprPulsa = false;
                        tempOprPulsa = "";
                        btnBayar.setEnabled(true);
                        oprPulsa = "";
                        listPulsa.setAdapter(null);
                        imgOpr.setImageResource(0);
                    }
                }

                for (int j = 0; j < listProviderProduct.size(); j++) {
                    if (listProviderProduct.get(j).equalsIgnoreCase(oprPulsa)) {

                        a.add(listName.get(j));
                        b.add(listPrice.get(j));
                        c.add(listEP.get(j));
                        d.add(listProviderProduct.get(j));
                        code_product.add(listCode.get(j));
                        code_name.add(listName.get(j));
                        point_ep.add(listEP.get(j));
                    }
                }

                Log.d("AYIK", "list->" + a.size());

                if (a.size() >= 1 /*&& nomorHp.equals(oprPulsa)*/) {
                    tvEmpty.setVisibility(View.GONE);
                    listPulsa.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);

                    //listPulsa.setVisibility(View.VISIBLE);
                }

                adapter = new SpinnerAdapterNew(getApplicationContext(), a, b, c, d, oprPulsa);
                listPulsa.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (!oprPulsa.equalsIgnoreCase(tempOprPulsa) && statOprPulsa) {
                    //loadProduct(strUserID, strAccessToken, strAplUse, oprPulsa);
                    tempOprPulsa = oprPulsa;
                    statOprPulsa = false;
                    btnBayar.setEnabled(true);
                    layOpr.setVisibility(View.GONE);
                    txLayOpr.setVisibility(View.GONE);
                    otherOpr = true;
                }

                if (otherOpr && oprPulsa.equalsIgnoreCase("")) {
                    Log.d("OPPO-1", "cekPrefix: ");
                    //loadProvider(strUserID, strAccessToken, strAplUse, strProductType);
                    otherOpr = false;
                }

            } else /*if (s.length() < 4)*/ {
                //listPulsa.setAdapter(adapter);
                listPulsa.setAdapter(null);
                a.clear();
                b.clear();
                c.clear();
                d.clear();

                statOprPulsa = false;
                otherOpr = true;
                tempOprPulsa = "";
                txOpr.setText("");
                imgOpr.setImageResource(0);
                layOpr.setVisibility(View.GONE);
                txLayOpr.setVisibility(View.GONE);
                btnBayar.setEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ArrayList<String> listCode;
    ArrayList<String> listPrice;
    ArrayList<String> listName;
    ArrayList<String> listEP;
    ArrayList<String> listisActive;
    ArrayList<String> listType;
    ArrayList<String> listProviderProduct;

    public void getProductPulsa() {
        Call<DataAllProduct> dataPulsa = apiInterfacePayment.getProduct_pulsa(strUserID, strAccessToken, strAplUse);
        dataPulsa.enqueue(new Callback<DataAllProduct>() {
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
                    } else {
                        utilsAlert.globalDialog(TransPulsa.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransPulsa.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransPulsa.this, titleAlert, "2. " + getResources().getString(R.string.error_api));

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
