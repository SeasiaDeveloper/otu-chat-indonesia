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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPaymentAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransVoucherGame extends AppCompatActivity {

    private static String[] jenis_voucher = {"Cherry Credit", "Garena", "Gemscool", "Googleplay US", "Lyto",
            "Megaxus", "PSN Indonesia", "Qeon", "Steam Wallet IDR",
            "Steam Wallet US", "Wavegame", "ZYNGA", "MOBILE LEGEND"};
    private static String[] kode_voucher = {"VCR", "GR", "GS", "GPUS", "LT",
            "MX", "PSID", "Q", "SID",
            "SUS", "WG", "ZY", "ML"};
    private static String load_type = "game_nominal";

    SharedPreferences prefs;
    Spinner spnVoucher, spnNominal;
    EditText txtNo, txtTransaksi_ke;
    TextInputLayout layoutNo;
    Button btnBayar;
    String //id_member,
            load_id = "VCR", selected_nominal;
    ApiInterface mApiInterface;
    Dialog loadingDialog;
    String[] nominal;
    String[] denom;
    String[] point;
    ArrayAdapter<String> adapter;

    SpinnerPpobAdapter spinnerPpobAdapter;
    SpinnerPaymentAdapter spinnerPaymentAdapter;
    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strOpsel, strAplUse = "OTU", strProductType = "GAME";

    String code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_voucher);

        ButterKnife.bind(this);

        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnVoucher = (Spinner) findViewById(R.id.spnTransVoucherJenis);
        spnNominal = (Spinner) findViewById(R.id.spnTransVoucherNominal);
        txtNo = (EditText) findViewById(R.id.txtTransVoucherNo);
        txtTransaksi_ke = (EditText) findViewById(R.id.txt_transaksi_ke);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransVoucherBayar);

        txtTransaksi_ke.setText("1");
        btnBayar.setText("BELI");
        txtNo.addTextChangedListener(new txtWatcher(txtNo));

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, jenis_voucher);
//        spnVoucher.setAdapter(adapter);

       /* spinnerPpobAdapter = new SpinnerPpobAdapter(getApplicationContext(), jenis_voucher);
        spnVoucher.setAdapter(spinnerPpobAdapter);
        spnVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load_id = kode_voucher[position];
                switch (load_id) {
                    case "ML":
                        txtNo.setHint("Nomor ID Mobile Legend");
                        break;
                    default:
                        txtNo.setHint("Masukkan Nomor Telepon");
                }
                load_data();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        loadProvider(strUserID, strAccessToken, strAplUse, strProductType);

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }
                AlertDialog dialog = new AlertDialog.Builder(TransVoucherGame.this)
                        .setTitle("Transaksi")
                        .setMessage("Apakah Anda Yakin Ingin Melanjutkan Transaksi dg Detail \nNo: " + txtNo.getText().toString() + "\nVoucher: " + code)
                        .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cek_transaksi();
                                finish();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return;
            }
        });
    }

    private void loadProvider(String userID, String accessToken, String aplUse, String productType) {
        loadingDialog = ProgressDialog.show(TransVoucherGame.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        // Toast.makeText(this, "load provider", Toast.LENGTH_SHORT).show();
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, list);
                        spnVoucher.setAdapter(adapter);
                        spnVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        Toast.makeText(TransVoucherGame.this, "" + respMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProvider> call, Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }

    private void loadProduct(String userID, String accessToken, String aplUse, String provider) {
        loadingDialog = ProgressDialog.show(TransVoucherGame.this, "Harap Tunggu", "Mengambil Data...");
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

                        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), products);
                        spnNominal.setAdapter(adapter);
                        spnNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                code = products.get(position).getCode();
                                //Toast.makeText(TransVoucherGame.this, "" + code, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        Toast.makeText(TransVoucherGame.this, "" + respMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {
                loadingDialog.dismiss();
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


    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransVoucherGame.this, "Harap Tunggu", "Cek Transaksi...");
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
                        inKonfirmasi.putExtra("productCode", "VOUCHER GAME");//
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

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();

        if (id_pel.isEmpty()) {
            //Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            layoutNo.setError("Kolom nomor tidak boleh kosong");
            requestFocus(txtNo);
            return false;
        }

        if (id_pel.length() < 8) {
            //Toast.makeText(this, "Masukkan minimal 8 digit nomor", Toast.LENGTH_SHORT).show();
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


    /*====================================payment lama===============================================*/
    /*
    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransVoucherGame.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Log.d("OPPO-1", "cek_transaksi - transvucher: " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), load_id, selected_nominal, txtNo.getText().toString(), "game");
//        Call<TransBeliResponse> transBeliCall = mApiInterface.postTransBeli("085334059170", load_id, selected_nominal, txtNo.getText().toString(), "game");
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("OK")) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
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


    public void load_data() {
        loadingDialog = ProgressDialog.show(TransVoucherGame.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Log.d("OPPO-1", "cek_transaksi - transvoucher: " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//        Call<LoadDataResponse> dataCall = mApiInterface.postLoadData(load_type, load_id, "085334059170");
        dataCall.enqueue(new Callback<LoadDataResponse>() {

            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                loadingDialog.dismiss();
                nominal = new String[0];
                denom = new String[0];
                point = new String[0];

                spinnerPaymentAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                spnNominal.setAdapter(spinnerPaymentAdapter);

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("OK")) {
                        final List<DataNominal> result = response.body().getResult();
                        nominal = new String[result.size()];
                        denom = new String[result.size()];
                        point = new String[result.size()];
                        selected_nominal = result.get(0).getProductKode();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        DecimalFormat decimal = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                        dfs.setCurrencySymbol("");
                        dfs.setMonetaryDecimalSeparator(',');
                        dfs.setGroupingSeparator('.');
                        decimal.setDecimalFormatSymbols(dfs);

                        for (int i = 0; i < result.size(); i++) {

                            Log.e("OPPO debug", "onResponse: " + result.get(i).getProductName());
                            Log.e("OPPO debug", "onResponse: " + result.get(i).getHargaJual());
                            Log.e("OPPO debug", "onResponse: " + result.get(i).getEpoint());

                            denom[i] = result.get(i).getProductName();
                            nominal[i] = format.format(result.get(i).getHargaJual());
                            if (result.get(i).getEpoint() != null) {
                                Log.e("OPPO debug", "onResponse2: " + result.get(i).getEpoint());
                                point[i] = "point : " + decimal.format(result.get(i).getEpoint());
                            } else {
                                point[i] = "point : " + decimal.format(0);
                            }

                        }

                        spinnerPaymentAdapter = new SpinnerPaymentAdapter(getApplicationContext(), denom, nominal, point);
                        spnNominal.setAdapter(spinnerPaymentAdapter);
                        spnNominal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_nominal = result.get(position).getProductKode();
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

            }
        });
    }
    /*=======================================end payment lama=========================================*/
}
