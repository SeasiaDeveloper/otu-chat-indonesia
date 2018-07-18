package com.eklanku.otuChat.ui.activities.payment.transfer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;

import java.util.HashMap;

public class TransDeposit extends AppCompatActivity {

    EditText txtNo, txtJml;
    TextInputLayout layoutNo, layoutJml;
    Button btnTransfer;

    Dialog loadingDialog;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String reffID;
    String strApIUse = "OTU";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trans_deposit);
        ButterKnife.bind(this);

        txtNo = (EditText) findViewById(R.id.txtTransDepositTujuan);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransDepositTujuan);
        txtJml = (EditText) findViewById(R.id.txtTransDepositJml);
        layoutJml = (TextInputLayout) findViewById(R.id.txtLayoutTransDepositJml);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        //cek antrian
        cekAntrianTransfer();

        btnTransfer = (Button) findViewById(R.id.btnTransDeposit);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }
                cek_transaksi();
            }
        });


    }

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();
        String id_jml = txtJml.getText().toString().trim();

        if (id_pel.isEmpty()) {
            layoutNo.setError("Kolom no tujuan tidak boleh kosong");
            layoutNo.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            requestFocus(txtNo);
            return false;
        } else if (id_jml.isEmpty()) {
            layoutJml.setError("Kolom nominal tidak boleh kosong");
            layoutJml.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            requestFocus(txtJml);
            return false;
        }

       /* int varLen=10;
        if(id_pel.matches("-?\\d+(.\\d+)?")()){

        }*/
        if (id_pel.length() < 10) {
            layoutNo.setError("Masukkan minimal 10 digit tujuan");
            layoutNo.setBackgroundColor(getResources().getColor(android.R.color.background_light));
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

    /*
    private void cek_transaksi() {
        Intent intent = new Intent(getBaseContext(), TransConfirm.class);
        intent.putExtra("tujuan", txtNo.getText().toString());
        intent.putExtra("nominal", txtJml.getText().toString());
        startActivity(intent);
        finish();
    }*/

    public void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransDeposit.this, "Harap Tunggu", "Memproses Pengisian Topup...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<DetailTransferResponse> transDepositCall = mApiInterfacePayment.postRequestTransfer(strUserID, strAccessToken, strApIUse, txtNo.getText().toString(), txtJml.getText().toString());
        transDepositCall.enqueue(new Callback<DetailTransferResponse>() {
            @Override
            public void onResponse(Call<DetailTransferResponse> call, Response<DetailTransferResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent intent = new Intent(getBaseContext(), TransConfirm.class);
                        intent.putExtra("userID", response.body().getUserID());
                        intent.putExtra("accessToken", response.body().getAccessToken());
                        intent.putExtra("nominal", response.body().getNominal());
                        intent.putExtra("idTujuan", response.body().getIdTujuan());
                        intent.putExtra("namaPemilik", response.body().getNamaPemilik());
                        intent.putExtra("refID", response.body().getRefID());
                        intent.putExtra("respTime", response.body().getRespTime());
                        intent.putExtra("status", response.body().getStatus());
                        intent.putExtra("error", error);

                        Log.d("OPPO-1", "userID: "+response.body().getUserID());
                        Log.d("OPPO-1", "accessToken: "+response.body().getAccessToken());
                        Log.d("OPPO-1", "nominal: "+response.body().getNominal());
                        Log.d("OPPO-1", "idTujuan: "+response.body().getIdTujuan());
                        Log.d("OPPO-1", "namaPemilik: "+response.body().getNamaPemilik());
                        Log.d("OPPO-1", "refID: "+response.body().getRefID());
                        Log.d("OPPO-1", "respTime: "+response.body().getRespTime());
                        Log.d("OPPO-1", "status: "+response.body().getStatus());
                        Log.d("OPPO-1", "error: "+error);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailTransferResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void cekAntrianTransfer(){
        Call<DetailTransferResponse> transDepositCall = mApiInterfacePayment.antrianTransfer(strUserID, strAccessToken, strApIUse);
        transDepositCall.enqueue(new Callback<DetailTransferResponse>() {
            @Override
            public void onResponse(Call<DetailTransferResponse> call, Response<DetailTransferResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent intent = new Intent(getBaseContext(), TransConfirm.class);
                        intent.putExtra("userID", response.body().getUserID());
                        intent.putExtra("accessToken", response.body().getAccessToken());
                        intent.putExtra("nominal", response.body().getNominal());
                        intent.putExtra("idTujuan", response.body().getIdTujuan());
                        intent.putExtra("namaPemilik", response.body().getNamaPemilik());
                        intent.putExtra("refID", response.body().getRefID());
                        intent.putExtra("respTime", response.body().getRespTime());
                        intent.putExtra("status", response.body().getStatus());
                        intent.putExtra("error", error);

                        Log.d("OPPO-1", "userID: "+response.body().getUserID());
                        Log.d("OPPO-1", "accessToken: "+response.body().getAccessToken());
                        Log.d("OPPO-1", "nominal: "+response.body().getNominal());
                        Log.d("OPPO-1", "idTujuan: "+response.body().getIdTujuan());
                        Log.d("OPPO-1", "namaPemilik: "+response.body().getNamaPemilik());
                        Log.d("OPPO-1", "refID: "+response.body().getRefID());
                        Log.d("OPPO-1", "respTime: "+response.body().getRespTime());
                        Log.d("OPPO-1", "status: "+response.body().getStatus());
                        Log.d("OPPO-1", "error: "+error);

                        startActivity(intent);
                        finish();
                    } else {

                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailTransferResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }
}
