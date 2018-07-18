package com.eklanku.otuChat.ui.activities.payment.topup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailM;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopupDetail extends AppCompatActivity {
    Button btnBack;
    TextView lblContent,lblInfo,lbDeposit,lbUniq;
    String id;
    Dialog loadingDialog;

    Button btnKonfirm;
    ApiInterface mApiInterface;
    ApiInterfacePayment mApiInterfacePayment;

    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_detail);
        initializeResources();

        id = getIntent().getExtras().getString("id", "");
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mApiInterfacePayment = ApiClient.getClient().create(ApiInterfacePayment.class);

        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
       // loadData();
    }


    private void loadData() {
        loadingDialog = ProgressDialog.show(TopupDetail.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TopupDetailResponse> dataCall = mApiInterface.getTopupDetail(id);
        dataCall.enqueue(new Callback<TopupDetailResponse>() {
            @Override
            public void onResponse(Call<TopupDetailResponse> call, Response<TopupDetailResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status   = response.body().getStatus();
                    String error    = response.body().getError();

                    if ( status.equals("OK") ) {
                        List<TopupDetailM> result = response.body().getTopupDetailM();

                        String content;
                        String nominal = result.get(0).getNominal();//) + Double.valueOf(result.get(0).getKeyTransfer());
                       /* NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
                        format.setCurrency(Currency.getInstance("IDR"));
                        String rupiah = format.format(total);*/

                        Double total =0.0d;
                        try {
                            if (nominal!=null && !nominal.trim().isEmpty())
                                total = Double.valueOf(nominal)+ Double.valueOf(result.get(0).getKeyTransfer());
                        } catch (Exception e) {
                            total = 0.0d;
                        }
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                        String rupiah = format.format(total);


                        content = "Permintaan Topup Berhasil, ";
                        content += "No Invoice: " + result.get(0).getInvoiceCode() + ". ";
                        content += "Lakukan pembayaran sebelum : " + result.get(0).getInvoiceDate() + ". ";
                        content += "Total pembayaran sbb:";
                        lblContent.setText(content);
                        lbDeposit.setText(rupiah.substring(2,rupiah.length() - 3));
                        lbUniq.setText(rupiah.substring(rupiah.length() - 3));

                    } else {
                        Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupDetailResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });

    }

    private void initializeResources() {
        //     lvBilling = (RecyclerView) findViewById(R.id.listBillingAccountKonfirmasi);
        lblContent = (TextView) findViewById(R.id.lblTopupDetailContent);
        lblInfo = (TextView) findViewById(R.id.textResponKonfirmasiTopupBill);
        btnBack    = (Button) findViewById(R.id.btnTopupDetailBack);
        btnKonfirm    = (Button) findViewById(R.id.btnKonfirmasiTopup);
        lbDeposit = (TextView) findViewById(R.id.txtTopupDetailDeposit);
        lbUniq = (TextView) findViewById(R.id.txtTopupDetailUniq);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnKonfirm.setOnClickListener(new KonfirmButtonListener());
    }


    private class KonfirmButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            loadingDialog = ProgressDialog.show(TopupDetail.this, "Harap Tunggu", "Memproses Pengisian Topup");
            loadingDialog.setCanceledOnTouchOutside(true);
            Call<TopupKonfirmResponse> dataCall = mApiInterface.postTopupKonfirm(id, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            dataCall.enqueue(new Callback<TopupKonfirmResponse>() {
                @Override
                public void onResponse(Call<TopupKonfirmResponse> call, Response<TopupKonfirmResponse> response) {
                    loadingDialog.dismiss();
                    lblInfo.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String status   = response.body().getStatus();
                        String error    = response.body().getError();
                        String message  = response.body().getMessage();

                        if ( status.equals("OK") ) {
                            //Intent intent = new Intent(getBaseContext(), TopupDetail.class);
                            //intent.putExtra("id", message);
                            //startActivity(intent);
                            Toast.makeText(getBaseContext(), "Sukses:" + message+"\n Tunggu beberapa saat untuk kami verifikasi... terima kasih.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            lblInfo.setText("Error:\n"+error);
                            lblInfo.setVisibility(View.VISIBLE);
                            Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TopupKonfirmResponse> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    Log.d("API_LOADDATA", t.getMessage().toString());
                }
            });

        }
    }


}
