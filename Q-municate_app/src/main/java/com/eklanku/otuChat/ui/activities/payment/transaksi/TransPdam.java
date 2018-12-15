package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataListPPOB;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerPpobAdapter;
import com.eklanku.otuChat.utils.Utils;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.util.HashMap;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransPdam extends AppCompatActivity {

    private static String[] nama_wilayah/* = {"PDAM BONDOWOSO",
            "PDAM PONTIANAK",
            "PDAM KAB JEMBER",
            "PDAM KAB MOJOKERTO",
            "PDAM KOTA BANDUNG",
            "PDAM KOTA BANDAR LAMPUNG",
            "PDAM KOTA BOGOR",
            "PDAM KAB SIDOARJO",
            "PDAM SURABAYA",
            "PDAM KAB BANGKALAN",
            "PDAM KAB MALANG",
            "PDAM KAB SITUBONDO",
            "PDAM KOTA PURWOREJO",
            "PDAM KAB GROBOGAN",
            "PDAM AETRA JAKARTA",
            "PDAM PALYJA JAKARTA",
            "PDAM KOTA SURAKARTA",
            "PDAM DENPASAR",
            "PDAM KOTA TANAH GROGOT",
            "PDAM KAB. BALANGAN",
            "PDAM KOTA JAMBI",
            "PDAM KAB. BOJONEGORO",
            "PDAM KAB. BATANG",
            "PDAM KAB. PASURUAN",
            "PDAM KOTA PASURUAN",
            "PDAM KAB. SAMPANG",
            "PDAM KAB. KUBU RAYA",
            "PDAM KAB. TAPIN",
            "PDAM KOTA BANJARBARU",
            "PDAM KOTA MATARAM",
            "PDAM KOTA MANADO",
            "PDAM KOTA PALEMBANG",
            "PDAM KAB BULELENG"}*/;

    SharedPreferences prefs;
    Spinner spnWilayah;
    EditText txtNo, txtno_hp;
    TextInputLayout layoutNo;
    Button btnBayar;
    String //id_member,
            load_id = "PDAM_AETRA";
    Dialog loadingDialog;

    //rina
    SpinnerPpobAdapter spinnerPpobAdapter;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID, strAccessToken, strAplUse = "OTU";

    Utils utilsAlert;
    String titleAlert = "PDAM";
    ListView listwilayah;
    ArrayList<String> id_wilayah;
    private EditText etWilayah;
    //ArrayAdapter<String> adapter;

    private ArrayList<ItemPdam> cartList;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_pdam);

        utilsAlert = new Utils(TransPdam.this);

        ButterKnife.bind(this);
        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        spnWilayah = (Spinner) findViewById(R.id.spnTransPdamWilayah);
        txtNo = (EditText) findViewById(R.id.txtTransPdamNo);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransPulsaNo);
        btnBayar = (Button) findViewById(R.id.btnTransPdamBayar);
        listwilayah = findViewById(R.id.listWilayah);
        etWilayah = findViewById(R.id.autoComplete);
        EditText txtNoHP = findViewById(R.id.txt_no_hp);
        btnBayar.setText("CEK TAGIHAN");
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);
        cartList = new ArrayList<>();
        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

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


        loadProvider(strUserID, strAccessToken, "OTU", "PDAM");

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }
                cek_transaksi();
            }
        });

        initializeResources();
        listwilayah.setVisibility(View.GONE);
        addTextListener();

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

    private void loadProvider(String userID, String accessToken, String aplUse, String productGroup) {

        Call<LoadDataResponse> dataCall = mApiInterfacePayment.postPpobProduct(userID, accessToken, productGroup, aplUse);
        dataCall.enqueue(new Callback<LoadDataResponse>() {
            @Override
            public void onResponse(Call<LoadDataResponse> call, Response<LoadDataResponse> response) {
                //loadingDialog.dismiss();
                nama_wilayah = new String[0];
                id_wilayah = new ArrayList<>();

                cartList.clear();
                cartList = new ArrayList<>();

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("SUCCESS")) {
                        final List<DataListPPOB> result = response.body().getProductList();
                        nama_wilayah = new String[result.size()];
                        load_id = result.get(0).getCode();


                        for (int i = 0; i < result.size(); i++) {
                            nama_wilayah[i] = result.get(i).getName();
                            id_wilayah.add(result.get(i).getCode());

                            ItemPdam item = new ItemPdam();
                            item.setId(result.get(i).getCode());
                            item.setName(result.get(i).getName());

                            cartList.add(item);

                        }


                        mAdapter = new CustomAdapter(cartList, TransPdam.this);
                        listwilayah.setAdapter(mAdapter);

                    } else {
                        utilsAlert.globalDialog(TransPdam.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransPdam.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoadDataResponse> call, Throwable t) {
                utilsAlert.globalDialog(TransPdam.this, titleAlert, getResources().getString(R.string.error_api));
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void cek_transaksi() {
        Log.d("OPPO-1", "cek_transaksi: " + load_id);
        loadingDialog = ProgressDialog.show(TransPdam.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TransBeliResponse> transBeliCall = mApiInterfacePayment.postPpobInquiry(strUserID, strAccessToken, load_id, txtNo.getText().toString(), txtno_hp.getText().toString(), strAplUse);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus().toString();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse - error: " + error);

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

                        finish();
                    } else {
                        utilsAlert.globalDialog(TransPdam.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(TransPdam.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransPdam.this, titleAlert, getResources().getString(R.string.error_api));
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
        listwilayah = (ListView) findViewById(R.id.listWilayah);
    }

    public void addTextListener() {

        etWilayah.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();
                if (query.length() == 0) {
                    listwilayah.setVisibility(View.GONE);
                } else {
                    listwilayah.setVisibility(View.VISIBLE);
                }

                final ArrayList<ItemPdam> filteredList = new ArrayList<>();

                for (ItemPdam model : cartList) {
                    final String text1 = model.getName().toLowerCase();

                    if (text1.contains(query)) {
                        filteredList.add(model);
                    }
                }

                mAdapter = new CustomAdapter(filteredList, TransPdam.this);
                listwilayah.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    public class ItemPdam {
        String id;
        String name;

        public ItemPdam() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class CustomAdapter extends ArrayAdapter<ItemPdam> implements View.OnClickListener {

        private ArrayList<ItemPdam> dataSet;
        Context mContext;
        public View layout;

        // View lookup cache
        private class ViewHolder {
            TextView txtName;
            TextView txtId;
        }

        public CustomAdapter(ArrayList<ItemPdam> data, Context context) {
            super(context, R.layout.item_pdam, data);
            this.dataSet = data;
            this.mContext = context;

        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            ItemPdam dataModel = (ItemPdam) object;

            Toast.makeText(mContext, "" + dataModel.getId(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemPdam dataModel = getItem(position);
            ViewHolder viewHolder;

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_pdam, parent, false);
                layout = convertView;
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.txtId = (TextView) convertView.findViewById(R.id.tvId);


                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            viewHolder.txtName.setText(dataModel.getName());
            viewHolder.txtId.setText(dataModel.getId());

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!validateIdpel()) {
                        return;
                    }
                    load_id = viewHolder.txtId.getText().toString();
                    cek_transaksi();
                }
            });

            return convertView;
        }

    }
}
