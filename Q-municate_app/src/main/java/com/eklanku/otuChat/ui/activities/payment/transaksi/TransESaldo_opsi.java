package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransESaldo_opsi extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnGojek, btnGrab, btnOvo;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strAplUse = "OTU";

    Utils utilsAlert;

    String titleAlert = "E-Saldo";

    ProgressBar progressBar;

    LinearLayout layoutView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_e_saldo);

        utilsAlert = new Utils(TransESaldo_opsi.this);

        btnGojek = findViewById(R.id.btnSaldoGojek);
        btnGrab = findViewById(R.id.btnSaldoGrab);
        btnOvo = findViewById(R.id.btnsaldoOVO);
        progressBar = findViewById(R.id.progress);
        layoutView = findViewById(R.id.linear_layout);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(PreferenceManager.KEY_USERID);
        strAccessToken = user.get(PreferenceManager.KEY_ACCESS_TOKEN);

        btnGojek.setOnClickListener(this);
        btnGrab.setOnClickListener(this);
        btnOvo.setOnClickListener(this);

        //get all product from new API
        getProductPulsa();

    }


    ArrayList<String> listCode;
    ArrayList<String> listPrice;
    ArrayList<String> listName;
    ArrayList<String> listEP;
    ArrayList<String> listisActive;
    ArrayList<String> listType;
    ArrayList<String> listProviderProduct;

    public void getProductPulsa() {
        showProgress(true);
        Call<DataAllProduct> dataESaldo = apiInterfacePayment.getProductESaldo(strUserID, strAccessToken, strAplUse);
        dataESaldo.enqueue(new Callback<DataAllProduct>() {
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

                    } else {
                        utilsAlert.globalDialog(TransESaldo_opsi.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransESaldo_opsi.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                utilsAlert.globalDialog(TransESaldo_opsi.this, titleAlert, "2. " + getResources().getString(R.string.error_api));

            }
        });
    }

    public void detailProduct(String provider, String imgOpr){
        ArrayList<String> a, b, c, d, e;
        a = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>();
        d = new ArrayList<>();
        e = new ArrayList<>();
        a.clear();
        b.clear();
        c.clear();
        d.clear();
        e.clear();
        for(int i = 0; i < listCode.size(); i++){
            if (listProviderProduct.get(i).equalsIgnoreCase(provider)) {
                a.add(listName.get(i));
                b.add(listPrice.get(i));
                c.add(listEP.get(i));
                d.add(listProviderProduct.get(i));
                e.add(listCode.get(i));
            }
        }
        Intent product = new Intent(getApplicationContext(), TransESaldo_product.class);
        product.putExtra("listName", a);
        product.putExtra("listPrice", b);
        product.putExtra("listEP", c);
        product.putExtra("listProvider", d);
        product.putExtra("listCode", e);
        product.putExtra("jnsEsaldo", provider);
        product.putExtra("imgOpr", imgOpr);

        startActivity(product);

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


    @Override
    public void onClick(View v) {
        String jns_ESaldo = "";
        String imgOpr = "";
        switch (v.getId()) {
            case R.id.btnSaldoGojek:
                jns_ESaldo = "GO-JEK";
                imgOpr = "gojek";
                break;
            case R.id.btnSaldoGrab:
                jns_ESaldo = "GRAB";
                imgOpr = "grab";
                break;
            case R.id.btnsaldoOVO:
                jns_ESaldo = "OVO CASH";
                imgOpr = "ovo";
                break;
        }
        detailProduct(jns_ESaldo, imgOpr);
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
