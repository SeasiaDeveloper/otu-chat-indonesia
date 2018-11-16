package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProvider;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerGameAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.ui.views.ARTextView;
import com.eklanku.otuChat.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataNominal;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProvider;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProvider;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TransPaketData extends AppCompatActivity {

    private static String[] jenis_paket = {"Axis Bronet", "Axis Data", "BOLT Data", "ISat Combo", "ISat Data",
            "ISat Extra", "SmartFren Data", "Three Data", "TSel Data", "TSel SMS", "XL Data", "XL Combo"};
    private static String[] kode_paket = {"BXD", "AXD", "BOLT", "INC", "IND",
            "INE", "SMD", "TD", "SD", "SS", "XH", "XCD"};
    private static String load_type = "paket_nominal";

    SharedPreferences prefs;
    Spinner spnJenis, spnNama;
    EditText txtNo, txtTransaksi_ke;
    TextInputLayout layoutNo;
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
    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strOpsel, strAplUse = "OTU", strProductType = "KUOTA";
    String code;

    TextView txtnomor, txtvoucher;
    Button btnYes, btnNo;

    Utils utilsAlert;
    String titleAlert = "Paket Data";

    String[] prefix_indosat = {"0814", "0815", "0816", "0855", "0856", "0857", "0858"};
    String[] prefix_telkomsel = {"0811", "0812", "0813", "0821", "0822", "0823", "0851", "0852", "0853"};
    String[] prefix_tri = {"0896", "0897", "0898", "0899", "0895"};
    String[] prefix_xl = {"0817", "0818", "0819", "0877", "0879", "0878", "0859"};//, "0831", "0838"
    String[] prefix_bolt = {"0999", "0998"};
    String[] prefix_smartfren = {"0881", "0882", "0883", "0884", "0885", "0885", "0887", "0888", "0889"};
    String[] prefix_axis = {"0838", "0831", "0832", "0833"};

    ImageView imgOpr;
    TextView txOpr;

    RelativeLayout layOprPaket;
    ARTextView laytxOprPaket;
    ListView listPaketdata;
    ArrayList<String> id_paket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_paket);

        ButterKnife.bind(this);

        utilsAlert = new Utils(TransPaketData.this);

        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnJenis = (Spinner) findViewById(R.id.spnTransPaketJenis);
        spnNama = (Spinner) findViewById(R.id.spnTransPaketNama);
        txtNo = (EditText) findViewById(R.id.txtTransPaketNo);
        txtTransaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransPaketBayar);
        btnBayar.setText("BELI");

        imgOpr = findViewById(R.id.imgOpr);
        txOpr = findViewById(R.id.txOpr);
        layOprPaket = findViewById(R.id.layOprPaket);
        laytxOprPaket = findViewById(R.id.textView35);
        listPaketdata = findViewById(R.id.listPaketdata);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nominal = new String[0];
        denom = new String[0];
        point = new String[0];
        txtTransaksi_ke.setText("1");

        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
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

                final Dialog dialog = new Dialog(TransPaketData.this);

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
//            Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            txtNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

       /* if (id_pel.length() < 8) {
//            Toast.makeText(this, "Masukkan minimal 8 digit nomor", Toast.LENGTH_SHORT).show();
            txtNo.setError("Masukkan minimal 8 digit nomor");
            requestFocus(txtNo);
            return false;
        }*/

       // layoutNo.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void loadProvider(String userID, String accessToken, String aplUse, String productType) {
        loadingDialog = ProgressDialog.show(TransPaketData.this, "Harap Tunggu", "Mengambil Data...");
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
                            Log.d("OPPO-1", "onResponse: " + x);
                            list.add(x);
                        }

                        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, list);
                        SpinnerGameAdapter spinnerGameAdapter = new SpinnerGameAdapter(getApplicationContext(), products, "PAKET DATA");
                        spnJenis.setAdapter(spinnerGameAdapter);
                        spnJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        utilsAlert.globalDialog(TransPaketData.this, titleAlert, respMessage);
                        //Toast.makeText(TransPaketData.this, "" + respMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPaketData.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProvider> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPaketData.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }

    private void loadProduct(String userID, String accessToken, String aplUse, String provider) {
        loadingDialog = ProgressDialog.show(TransPaketData.this, "Harap Tunggu", "Mengambil Data...");
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
                        listPaketdata.setAdapter(adapter);
                        /*spnNama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                code = products.get(position).getCode();
                                //Toast.makeText(TransPaketData.this, "" + code, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });*/

                    } else {
                        utilsAlert.globalDialog(TransPaketData.this, titleAlert, respMessage);
                        //Toast.makeText(TransPaketData.this, "" + respMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPaketData.this, titleAlert, getResources().getString(R.string.error_api));
                    // Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPaketData.this, titleAlert, getResources().getString(R.string.error_api));
            }
        });
    }


    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransPaketData.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, txtNo.getText().toString(), txtTransaksi_ke.getText().toString(), "", "", code);
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
                        inKonfirmasi.putExtra("productCode", "PAKET DATA");//
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
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransPaketData.this, titleAlert, error);
                        // Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPaketData.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPaketData.this, titleAlert, getResources().getString(R.string.error_api));
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

    public void cekPrefix(CharSequence s) {
        try {
            if (s.length() >= 6) {
                String nomorHP1 = s.toString().substring(0,2);
                String nomorHp = "";
                String valNomorHp = "", valNomorHP2 = "";
                if(nomorHP1.startsWith("+6")){
                    valNomorHp = nomorHP1.replace("+6","0");
                    nomorHp = valNomorHp + s.toString().substring(3,6);
                }else if(nomorHP1.startsWith("62")){
                    valNomorHp = nomorHP1.replace("62","0");
                    nomorHp = valNomorHp + s.toString().substring(2,5);
                }else{
                    nomorHp = s.toString().substring(0, 4);
                }
                if (Arrays.asList(prefix_xl).contains(nomorHp)) {
                    oprPaket = "XL DATA";
                    imgOpr.setImageResource(R.mipmap.xl);
                    txOpr.setText("XL DATA");
                    statPaket = true;
                    otherOpr = false;
                } else if (Arrays.asList(prefix_axis).contains(nomorHp)) {
                    oprPaket = "AXIS DATA";
                    imgOpr.setImageResource(R.mipmap.axis);
                    txOpr.setText("AXIS DATA");
                    statPaket = true;
                    otherOpr = false;
                } else if (Arrays.asList(prefix_smartfren).contains(nomorHp)) {
                    oprPaket = "SMARTFREN DATA";
                    imgOpr.setImageResource(R.mipmap.smart);
                    txOpr.setText("SMARTFREN DATA");
                    statPaket = true;
                    otherOpr = false;
                } else if (Arrays.asList(prefix_tri).contains(nomorHp)) {
                    oprPaket = "TRI DATA";
                    imgOpr.setImageResource(R.mipmap.three);
                    txOpr.setText("TRI DATA");
                    statPaket = true;
                    otherOpr = false;
                } else if (Arrays.asList(prefix_indosat).contains(nomorHp)) {
                    oprPaket = "ISAT DATA";
                    imgOpr.setImageResource(R.mipmap.indosat);
                    txOpr.setText("ISAT DATA");
                    statPaket = true;
                    otherOpr = false;
                } else if (Arrays.asList(prefix_bolt).contains(nomorHp)) {
                    oprPaket = "BOLT";
                    imgOpr.setImageResource(R.mipmap.bolt);
                    txOpr.setText("BOLT");
                    statPaket = true;
                    otherOpr = false;
                } else if (Arrays.asList(prefix_telkomsel).contains(nomorHp)) {
                    oprPaket = "TSEL DATA";
                    imgOpr.setImageResource(R.mipmap.telkomsel);
                    txOpr.setText("TSEL DATA");
                    statPaket = true;
                    otherOpr = false;
                } else {
                    statPaket = false;
                    tempPaket = "";
                    layOprPaket.setVisibility(View.VISIBLE);
                    laytxOprPaket.setVisibility(View.VISIBLE);
                    btnBayar.setEnabled(true);
                    oprPaket = "";
                }

                if (!oprPaket.equalsIgnoreCase(tempPaket) && statPaket) {
                    loadProduct(strUserID, strAccessToken, strAplUse, oprPaket);
                    statPaket = false;
                    tempPaket = oprPaket;
                    layOprPaket.setVisibility(View.GONE);
                    laytxOprPaket.setVisibility(View.GONE);
                    btnBayar.setEnabled(true);
                    otherOpr = true;
                }

                if(otherOpr && oprPaket.equalsIgnoreCase("")){
                    Log.d("OPPO-1", "cekPrefix: ");
                    loadProvider(strUserID, strAccessToken, strAplUse, strProductType);
                    otherOpr = false;
                }

            } else if (s.length() < 4) {
                spnNama.setAdapter(adapter);
                tempPaket = "";
                btnBayar.setEnabled(false);
                statPaket = false;
                otherOpr = true;
                imgOpr.setImageResource(0);
                txOpr.setText("");
                layOprPaket.setVisibility(View.GONE);
                laytxOprPaket.setVisibility(View.GONE);
            } else if (s.length() >= 8 && s.length() <= 13) {
                btnBayar.setEnabled(false);
                layOprPaket.setVisibility(View.GONE);
                laytxOprPaket.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeResources() {
        listPaketdata = (ListView) findViewById(R.id.listPaketdata);
        listPaketdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransPaketData.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                code = id_paket.get(position);
                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(txtNo.getText().toString());
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
                return;
            }
        });

    }
}
