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
import android.widget.CheckBox;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertSyarat extends AppCompatActivity {

    Button btn_Lnjut;
    CheckBox ck_setuju;

    Dialog loadingDialog;
    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";
    String nama, nominal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_syarat_topup);

        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        if (getIntent().getExtras() != null) {
            nama = getIntent().getExtras().getString("nama", "");
            nominal = getIntent().getExtras().getString("nominal", "");
        }

        //Expandable textview
        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);

        expTv1.setText(getString(R.string.syarat_ketentuan_detail));

        btn_Lnjut = (Button) findViewById(R.id.btnLanjut);
        ck_setuju = (CheckBox) findViewById(R.id.cksetuju);

        ck_setuju.setChecked(false);
        btn_Lnjut.setEnabled(false);
        btn_Lnjut.setBackgroundResource(R.drawable.custom_round);


        ck_setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ck_setuju.isChecked()) {
                    btn_Lnjut.setEnabled(true);
                    btn_Lnjut.setBackgroundResource(R.drawable.custom_round_green_dark);
                } else {
                    btn_Lnjut.setEnabled(false);
                    btn_Lnjut.setBackgroundResource(R.drawable.custom_round);
                }
            }
        });

        btn_Lnjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkToTopupDetail();
            }
        });
    }

    public void linkToTopupDetail() {
        depositOrder();
    }


    private void depositOrder() {
        loadingDialog = ProgressDialog.show(AlertSyarat.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TopupPayResponse> dataCall = mApiInterfacePayment.postDepositOrder(strUserID, strAccessToken, strApIUse, nama, nominal);
        dataCall.enqueue(new Callback<TopupPayResponse>() {
            @Override
            public void onResponse(Call<TopupPayResponse> call, Response<TopupPayResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent intent = new Intent(getBaseContext(), TopupBilling.class);
                        intent.putExtra("bank", nama);
                        intent.putExtra("nominal", response.body().getNominal());
                        intent.putExtra("pesan", error);
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
