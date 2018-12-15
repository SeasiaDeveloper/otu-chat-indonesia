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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransKonfirmasi;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment2.SpinnerAdapter;
import com.eklanku.otuChat.ui.adapters.payment2.SpinnerAdapterNew;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransVouchergame_product extends AppCompatActivity {

    ListView lvProductGame;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;

    Dialog loadingDialog;
    String strUserID, strAccessToken, strAplUse = "OTU";

    Bundle extras;
    Utils utilsAlert;
    String titleAlert = "Voucher Game";
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

    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;

    ImageView imgOPR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_game_product);

        utilsAlert = new Utils(TransVouchergame_product.this);
        extras = getIntent().getExtras();

        lvProductGame = findViewById(R.id.listProductGame);
        btnBayar = findViewById(R.id.btnTransVoucherBayar);
        noPel = findViewById(R.id.txtTransVoucherNo);
        transKe = findViewById(R.id.txtTransKe);
        transKe.setText("1");
        layoutNo = findViewById(R.id.txtLayoutTransPulsaNo);
        imgOPR = findViewById(R.id.imgOpr);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        layoutView = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);

        initializeResources();
        noPel.addTextChangedListener(new txtWatcher(noPel));

        _listnama = new ArrayList<>();
        _listprice = new ArrayList<>();
        _listep = new ArrayList<>();
        _listProvide = new ArrayList<>();
        _listCode = new ArrayList<>();

        _listnama.clear();
        _listprice.clear();
        _listep.clear();
        _listProvide.clear();
        _listCode.clear();

        _listnama = extras.getStringArrayList("listName");
        _listprice = extras.getStringArrayList("listPrice");
        _listep = extras.getStringArrayList("listEP");
        _listProvide = extras.getStringArrayList("listProvider");
        _listCode = extras.getStringArrayList("listCode");
        _namaProvider = extras.getString("jnsGame");
        _img = extras.getString("imgOpr");

        addList();
    }

    public void addList() {
        SpinnerAdapterNew adapter = new SpinnerAdapterNew(getApplicationContext(), _listnama, _listprice, _listep, _listProvide, _namaProvider);
        int id = TransVouchergame_product.this.getResources().getIdentifier("drawable/ic_voucher_game_" + _img.toLowerCase(), null, TransVouchergame_product.this.getPackageName());
        Log.d("OPPO-1", "addList>>>>>>>>>>>>>>: " + id);
        if (id == 0) {
            int id2 = TransVouchergame_product.this.getResources().getIdentifier("drawable/ic_" + _img.toLowerCase(), null, TransVouchergame_product.this.getPackageName());
            imgOPR.setImageResource(id2);
        } else {
            imgOPR.setImageResource(id);
        }

        lvProductGame.setAdapter(adapter);
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


    public void loadProduct(String userID, String accessToken, String aplUse, String voucherGame) {
        try {
            loadingDialog = ProgressDialog.show(TransVouchergame_product.this, "Harap Tunggu", "Mengambil Data...");
            loadingDialog.setCanceledOnTouchOutside(true);

            Call<LoadDataResponseProduct> gameVoucher = apiInterfacePayment.getLoadProduct(userID, accessToken, aplUse, voucherGame);
            gameVoucher.enqueue(new Callback<LoadDataResponseProduct>() {
                @Override
                public void onResponse(Call<LoadDataResponseProduct> call, Response<LoadDataResponseProduct> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        String status = response.body().getStatus();
                        String respMessage = response.body().getRespMessage();
                        if (status.equalsIgnoreCase("SUCCESS")) {

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

                            SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), products);
                            lvProductGame.setAdapter(adapter);
                            /*lvProductGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    code = products.get(position).getCode();
                                }
                            });*/

                        }
                    }
                }

                @Override
                public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransVouchergame_product.this, "Harap Tunggu", "Cek Transaksi...");
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
                        inKonfirmasi.putExtra("ep", ep);
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransVouchergame_product.this, titleAlert, error);
                        // Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransVouchergame_product.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransVouchergame_product.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }


    private void initializeResources() {
        Log.d("OPPO-1", "initializeResources: ");
        lvProductGame = (ListView) findViewById(R.id.listProductGame);
        lvProductGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransVouchergame_product.this);

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

    private boolean validateIdpel() {
        String id_pel = noPel.getText().toString().trim();

        if (id_pel.isEmpty()) {
            //Toast.makeText(this, "Kolom nomor tidak boleh kosong", Toast.LENGTH_SHORT).show();
            noPel.setError("Kolom nomor tidak boleh kosong");
            requestFocus(noPel);
            return false;
        }

      /*  if (id_pel.length() < 8) {
            //Toast.makeText(this, "Masukkan minimal 8 digit nomor", Toast.LENGTH_SHORT).show();
            noPel.setError("Masukkan minimal 8 digit nomor");
            requestFocus(noPel);
            return false;
        }*/

        //layoutNo.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
