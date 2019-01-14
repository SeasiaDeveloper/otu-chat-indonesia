package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.konfirmasitransaksi.TransKonfirmasiPascabayar;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailPeriodeBPJS;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.DataPeriodeBPJS;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.ListViewPeriodeBpjsAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.Utils;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransBpjs extends AppCompatActivity {

    private static String[] nama_bpjs = {"BPJS Kesehatan"};

    private static String[] kode_bpjs = {"BPJSKES"};

    SharedPreferences prefs;
    Spinner spnBpjs;
    EditText txtNo, txtno_hp;
    TextInputLayout layoutNo;
    Button btnBayar;
    String load_id = "BPJSKES";
    Dialog loadingDialog;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken, strAplUse = "OTU", strProductGroup = "BPJS";

    SpinnerPpobAdapter spinnerPpobAdapter;
    String selected_operator;
    String nominalx, tujuanx;

    private static String[] nama_operator;

    Utils utilsAlert;
    String titleAlert = "BPJS";

    EditText etPickADate;
    private int mYear, mMonth, mDay;

    String monthYearStr;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");

    Spinner spnPeriodeBPJS;
    TextView txketperiode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_bpjs);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransBpjs.this);

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
        spnBpjs = (Spinner) findViewById(R.id.spnTransBpjs);
        txtNo = (EditText) findViewById(R.id.txtTransBpjsNo);
        txtNo.setText(tujuanx);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransBpjsBayar);
        spnPeriodeBPJS = findViewById(R.id.spnPeriodeBPJS);
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("CEK TAGIHAN");
        txtNo.addTextChangedListener(new txtWatcher(txtNo));
        txketperiode = findViewById(R.id.tvketPeriode);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtno_hp = (EditText) findViewById(R.id.txt_no_hp);
        if (PreferenceUtil.getNumberPhone(this).startsWith("+62")) {
            String no = PreferenceUtil.getNumberPhone(this).replace("+62", "0");
            txtno_hp.setText(no);
        } else {
            txtno_hp.setText(PreferenceUtil.getNumberPhone(this));
        }

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        loadProvider(strUserID, strAccessToken, strAplUse, strProductGroup);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }
                cek_transaksi();
            }
        });

        /*Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        etPickADate = (EditText) findViewById(R.id.et_datePicker);
        etPickADate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                monthYear();
            }
        });

        List<String> monthsList = new ArrayList<String>();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            String month = months[i];
            //System.out.println("month = " + month);
            monthsList .add(months[i]);
            Log.d("AYIK", "month->"+ month);
        }*/

        getPeriodeBpjs();
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

        if(id_pel.length()>16){
            txtNo.setError("Panjang maksimal nomor pelanggan adalah 16 digit");
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
        Log.d("OPPO-1", "cek_transaksi: "+selected_operator);
        loadingDialog = ProgressDialog.show(TransBpjs.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiryBPJS(strUserID, strAccessToken, selected_operator, txtNo.getText().toString(), txtno_hp.getText().toString(), "OTU", idper);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus().toString();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasiPascabayar.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());
                        inKonfirmasi.putExtra("accessToken", strAccessToken);
                        inKonfirmasi.putExtra("status", status);
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());
                        inKonfirmasi.putExtra("respTime", response.body().getRespTime());
                        inKonfirmasi.putExtra("productCode", "Nomor Peserta BPJS Kesehatan");
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

                        Log.d("OPPO-1", "getBillingReferenceID() " + response.body().getBillingReferenceID());

                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransBpjs.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransBpjs.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransBpjs.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private void loadProvider(String userID, String accessToken, String aplUse, String productGroup) {
        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                //loadingDialog.dismiss();
                nama_operator = new String[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_operator);
                spnBpjs.setAdapter(adapter);

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> result = response.body().getProductList();
                        nama_operator = new String[result.size()];
                        selected_operator = result.get(0).getCode();

                        for (int i = 0; i < result.size(); i++) {
                            nama_operator[i] = result.get(i).getName();
                        }

                        adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_text, nama_operator);
                        spnBpjs.setAdapter(adapter);
                        spnBpjs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selected_operator = result.get(position).getCode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        utilsAlert.globalDialog(TransBpjs.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransBpjs.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                utilsAlert.globalDialog(TransBpjs.this, titleAlert, getResources().getString(R.string.error_api));
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

    private void monthYear() {
        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
        pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                monthYearStr = year + "-" + (month + 1) + "-" + i2;

                long interval = Math.abs(interval(monthYearStr));
                if (interval <= 400) {
                    etPickADate.setText((formatMonthYear(monthYearStr)));
                } else {
                    Toast.makeText(TransBpjs.this, "Melebihi batas waktu yang ditentukan (Maksimal 1 Tahun)", Toast.LENGTH_SHORT).show();
                }

            }
        });
        pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
    }

    String formatMonthYear(String str) {
        Date date = null;
        try {
            date = input.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(date);
    }

    private long interval(String event) {

        long x = 0;
        String eventDate = event;
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = date.parse(eventDate);
            Date currentDate = new Date();
            currentDate = date.parse(date.format(currentDate));
            long difference = date1.getTime() - currentDate.getTime();
            long diff = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
            x = diff;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return x;

    }

    ArrayList<String> idperiode;
    ArrayList<String> blnperiode;
    String keteranganperiode;
    String idper, bln, ket;

    public void getPeriodeBpjs(){
        Call<DataPeriodeBPJS> periode = mApiInterfacePayment.getperiodebpjs(strUserID, strAccessToken, strAplUse);
        periode.enqueue(new Callback<DataPeriodeBPJS>() {
            @Override
            public void onResponse(Call<DataPeriodeBPJS> call, Response<DataPeriodeBPJS> response) {
                idperiode = new ArrayList<>();
                blnperiode = new ArrayList<>();
                idperiode.clear();
                blnperiode.clear();
                ListViewPeriodeBpjsAdapter adapter = new ListViewPeriodeBpjsAdapter(getBaseContext(), blnperiode, keteranganperiode);
                spnPeriodeBPJS.setAdapter(adapter);
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    keteranganperiode = response.body().getRespMessage();
                    txketperiode.setText("* "+keteranganperiode);
                    if(status.equalsIgnoreCase("SUCCESS")){
                        Log.d("OPPO-1", "oSuccess");
                        List<DataDetailPeriodeBPJS> periode = response.body().getPeriode();
                        for(int i = 0; i < periode.size(); i++){
                            idperiode.add(periode.get(i).getId());
                            blnperiode.add(periode.get(i).getPeriode());
                        }

                        Log.d("OPPO-1", "onResponse: "+idperiode);
                        Log.d("OPPO-1", "onResponse: "+blnperiode);

                        adapter = new ListViewPeriodeBpjsAdapter(getBaseContext(), blnperiode, keteranganperiode);
                        spnPeriodeBPJS.setAdapter(adapter);
                        spnPeriodeBPJS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                idper = periode.get(position).getId();
                                bln = periode.get(position).getPeriode();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(Call<DataPeriodeBPJS> call, Throwable t) {
                Log.d("OPPO-1", "onFailure: "+t.getMessage());
            }
        });
    }



}

