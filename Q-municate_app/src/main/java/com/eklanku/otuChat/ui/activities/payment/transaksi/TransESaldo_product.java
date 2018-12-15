package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment2.SpinnerAdapterNew;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransESaldo_product extends AppCompatActivity {

    ListView lvProductESaldo;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;

    Dialog loadingDialog;
    String strUserID, strAccessToken, strAplUse = "OTU";

    Bundle extras;
    Utils utilsAlert;
    String titleAlert = "E Saldo";
    String code, ep;

    EditText noPel, transKe;
    Button btnBayar;
    Button btnYes, btnNo;
    TextView txtnomor, txtvoucher;

    ArrayList<String> id_paket;

    TextInputLayout layoutNo;

    ArrayList<String> _listnama;
    ArrayList<String> _listprice;
    ArrayList<String> _listep;
    ArrayList<String> _listProvide;
    ArrayList<String> _listCode;
    String _namaProvider;
    String _img;
    ImageView imgOPR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_e_saldo_product);

        utilsAlert = new Utils(TransESaldo_product.this);
        extras = getIntent().getExtras();

        lvProductESaldo = findViewById(R.id.listEsaldo);
        btnBayar = findViewById(R.id.btnTransVoucherBayar);
        noPel = findViewById(R.id.txtTransVoucherNo);
        transKe = findViewById(R.id.txtTransKe);
        transKe.setText("1");
        layoutNo = findViewById(R.id.txtLayoutTransPulsaNo);
        imgOPR =findViewById(R.id.imgOpr);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        initializeResources();
        noPel.addTextChangedListener(new txtWatcher(noPel));

        _listnama = new ArrayList<>();
        _listprice = new ArrayList<>();
        _listep = new ArrayList<>();
        _listProvide = new ArrayList<>();
        _listCode= new ArrayList<>();

        _listnama.clear();
        _listprice.clear();
        _listep.clear();
        _listProvide.clear();
        _listCode.clear();

        _listnama = extras.getStringArrayList("listName");
        _listprice = extras.getStringArrayList("listPrice");
        _listep =  extras.getStringArrayList("listEP");
        _listProvide =  extras.getStringArrayList("listProvider");
        _listCode =  extras.getStringArrayList("listCode");
        _namaProvider = extras.getString("jnsEsaldo");
        _img = extras.getString("imgOpr");

        addList();
    }

    private void initializeResources() {
        Log.d("OPPO-1", "initializeResources: ");
        lvProductESaldo= (ListView) findViewById(R.id.listEsaldo);
        lvProductESaldo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransESaldo_product.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                code = _listCode.get(position);
                ep = _listep.get(position);
                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(noPel.getText().toString());
                txtvoucher.setText(code);

                TextView tvProduct = dialog.findViewById(R.id.txt_product);
                TextView tvTranske = dialog.findViewById(R.id.txt_transke);
                tvProduct.setText(_listnama.get(position));
                tvTranske.setText(transKe.getText().toString());

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

    private boolean validateIdpel() {
        String id_pel = noPel.getText().toString().trim();

        if (id_pel.isEmpty()) {
            //Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            noPel.setError("Kolom nomor tidak boleh kosong");
            requestFocus(noPel);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

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
    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransESaldo_product.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, noPel.getText().toString(), transKe.getText().toString(), "", "", code);
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
                        inKonfirmasi.putExtra("productCode", "E-SALDO");//
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
                        utilsAlert.globalDialog(TransESaldo_product.this, titleAlert, error);
                        // Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransESaldo_product.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransESaldo_product.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }


    public void addList(){
        SpinnerAdapterNew adapter = new SpinnerAdapterNew(getApplicationContext(), _listnama, _listprice, _listep, _listProvide, _namaProvider);
        int id = TransESaldo_product.this.getResources().getIdentifier("drawable/ic_e_saldo_" + _img.toLowerCase(), null, TransESaldo_product.this.getPackageName());
        imgOPR.setImageResource(id);
        lvProductESaldo.setAdapter(adapter);
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
}
