package com.eklanku.otuChat.ui.activities.payment.topup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailM;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopupBilling extends AppCompatActivity {

    Button btnComfirm;

    Dialog loadingDialog;

    ApiInterface mApiInterface;

    String id_paket, nm_paket;
    TextView lblNominal;

    ApiInterfacePayment mApiInterfacePayment;

    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";

    TextView txtUserid, txtNominal, txtBank, txtTime, txtKeterangan;
    EditText edMessage;

    String bank, nominal, msg;
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_billing);
        initializeResources();

        bank = getIntent().getExtras().getString("bank", "");
        nominal = getIntent().getExtras().getString("nominal", "");
        msg = getIntent().getExtras().getString("pesan", "");

        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        txtUserid = findViewById(R.id.txt_user_id);
        txtNominal = findViewById(R.id.txt_nominal);
        txtBank = findViewById(R.id.txt_bank);
        txtTime = findViewById(R.id.txt_time);
        edMessage = findViewById(R.id.txt_message);
        txtKeterangan = findViewById(R.id.textKetKonfirmasiTopupBill);

        txtUserid.setText(strUserID);
        txtNominal.setText(nominal);
        txtBank.setText(bank);
        txtTime.setText(sdf.format(new Date()));
        edMessage.setText(msg);

/*
        Double total = 0.0d;
        try {
            if (nominal != null && !nominal.trim().isEmpty())
                total = Double.valueOf(nominal);
        } catch (Exception e) {
            total = 0.0d;
        }
        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        String rupiah = format.format(total);
*/

        txtKeterangan.setText("Langkah menuju sukses melakukan deposit:\n1. transfer sesuai nominal Rp " + nominal + ".\n2. Pastikan no. rek. sesuai dgn a.n. PT. Eklanku Sehati Cemerlang.\n3. Silahkan klik tombol Konfirmasi, setelah transfer dilakukan.");
    }


    private void initializeResources() {
        btnComfirm = (Button) findViewById(R.id.btn_ok);
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //depositOrder();
                finish();
            }
        });
    }


    /*=================================dipindah di topup order====================================*/
    /*
    private void depositOrder() {
        loadingDialog = ProgressDialog.show(TopupBilling.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TopupPayResponse> dataCall = mApiInterfacePayment.postDepositOrder(strUserID, strAccessToken, strApIUse, bank, nominal);
        dataCall.enqueue(new Callback<TopupPayResponse>() {
            @Override
            public void onResponse(Call<TopupPayResponse> call, Response<TopupPayResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        edMessage.setText(error);

                    } else {
                        Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupPayResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }
*/

    /*=======================================payment lama==================================================*/
/*
    private class BayarButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            loadingDialog = ProgressDialog.show(TopupBilling.this, "Harap Tunggu", "Memproses Pengisian Topup");
            loadingDialog.setCanceledOnTouchOutside(true);
            Call<TopupPayResponse> dataCall = mApiInterface.postTopupPay("transfer", id_paket, PreferenceUtil.getNumberPhone(this)));
//            Call<TopupPayResponse> dataCall = mApiInterface.postTopupPay("transfer", id_paket, "085334059170");
            dataCall.enqueue(new Callback<TopupPayResponse>() {
                @Override
                public void onResponse(Call<TopupPayResponse> call, Response<TopupPayResponse> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        String status = response.body().getStatus();
                        String error = response.body().getError();
                        String message = response.body().getMessage();

                        if (status.equals("OK")) {
                            Intent intent = new Intent(getBaseContext(), TopupDetail.class);
                            intent.putExtra("id", message);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TopupPayResponse> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    Log.d("API_LOADDATA", t.getMessage().toString());
                }
            });

        }
    }
/*============================================end payment lama================================================================*/

}
