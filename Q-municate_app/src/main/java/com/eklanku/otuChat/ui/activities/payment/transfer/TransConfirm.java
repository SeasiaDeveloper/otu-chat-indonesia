package com.eklanku.otuChat.ui.activities.payment.transfer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.R;;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransConfirm extends AppCompatActivity {

    Button btnConfirm, btnCancel;
    Dialog loadingDialog;


    String tujuan;
    TextView lblNominal, lblTujuan;

    //AYIK
    TextView txtReffID, txtUserID, txtNominal, txtIDTujuan, txtNamaTujuan, txtTime;
    EditText txtPIN;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";

    //Rina
    String userID, accessToken, nominal, idTujuan, namaPemilik, refID, respTime, status, respMessage;

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    String strSecurityCode;

    com.eklanku.otuChat.utils.Utils utilsAlert;
    String titleAlert = "Transfer";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirm);

        initializeResources();

        utilsAlert = new com.eklanku.otuChat.utils.Utils(TransConfirm.this);

        tujuan = getIntent().getExtras().getString("tujuan", "");
        nominal = getIntent().getExtras().getString("nominal", "");

        userID = getIntent().getExtras().getString("userID", "");
        accessToken = getIntent().getExtras().getString("accessToken", "");
        nominal = getIntent().getExtras().getString("nominal", "");
        idTujuan = getIntent().getExtras().getString("idTujuan", "");
        namaPemilik = getIntent().getExtras().getString("namaPemilik", "");
        refID = getIntent().getExtras().getString("refID", "");
        respTime = getIntent().getExtras().getString("respTime", "");
        status = getIntent().getExtras().getString("status", "");
        respMessage = getIntent().getExtras().getString("respMessage", "");

        lblNominal = (TextView) findViewById(R.id.lblTransferNominal);
        lblTujuan = (TextView) findViewById(R.id.lblTransferTujuan);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        lblNominal = (TextView) findViewById(R.id.lblTransferNominal);
        lblTujuan = (TextView) findViewById(R.id.lblTransferTujuan);
        txtPIN = (EditText) findViewById(R.id.txtTransDepositPIN);

        //AYIK
        txtReffID = findViewById(R.id.txt_reff_id);
        txtUserID = findViewById(R.id.txt_user_id);
        txtNominal = findViewById(R.id.txt_nominal);
        txtIDTujuan = findViewById(R.id.txt_id_tujuan);
        txtNamaTujuan = findViewById(R.id.txt_nama_tujuan);
        txtTime = findViewById(R.id.txt_time);

        txtPIN = findViewById(R.id.txt_pin);

        txtReffID.setText(refID);
        txtUserID.setText(userID);
        txtNominal.setText(nominal);
        txtIDTujuan.setText(idTujuan);
        txtNamaTujuan.setText(namaPemilik);
        txtTime.setText(sdf.format(new Date()));


    }

    private void loadData() {
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

        lblNominal.setText(rupiah);
    }

    private void initializeResources() {
        btnConfirm = (Button) findViewById(R.id.btn_ok);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(TransConfirm.this)
                        .setTitle("PERINGATAN!!!")
                        .setMessage("Apakah Anda Yakin Ingin Melakukan Transfer dg Detail \nTujuan: " + idTujuan + "\nNama: " + namaPemilik + "\nNominal: " + nominal)
                        .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                transfer();
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

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(TransConfirm.this)
                        .setTitle("PERINGATAN!!!")
                        .setMessage("Apakah Anda Yakin Ingin Membatalkan Transfer dg Detail \nTujuan: " + idTujuan + "\nNama: " + namaPemilik + "\nNominal: " + nominal)
                        .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelTransfer();
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


    public void transfer() {
        loadingDialog = ProgressDialog.show(TransConfirm.this, "Harap Tunggu", "Memproses Pengisian Transfer");
        loadingDialog.setCanceledOnTouchOutside(true);

        String secCode = txtPIN.getText().toString() + "x@2016ekl";
        strSecurityCode = Utils.md5(secCode);

        Log.d("OPPO-1", "strUserID transfer: " + strUserID);
        Log.d("OPPO-1", "strAccessToken: " + strAccessToken);
        Log.d("OPPO-1", "strApIUse: " + strApIUse);
        Log.d("OPPO-1", "refID: " + refID);
        Log.d("OPPO-1", "strSecurityCode: " + strSecurityCode);


        Call<TopupKonfirmResponse> transDepositCall = mApiInterfacePayment.postTransferOrder(strUserID, strAccessToken, strApIUse, refID, strSecurityCode);
        transDepositCall.enqueue(new Callback<TopupKonfirmResponse>() {
            @Override
            public void onResponse(Call<TopupKonfirmResponse> call, Response<TopupKonfirmResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equals("SUCCESS")) {
                        String msg = response.body().getRespMessage();
                        Intent intent = new Intent(getBaseContext(), TransResponse.class);
                        intent.putExtra("refID", refID);
                        intent.putExtra("idTujuan", idTujuan);
                        intent.putExtra("nama", namaPemilik);
                        intent.putExtra("nominal", nominal);
                        intent.putExtra("keterangan", "Transfer SUKSES");
                        intent.putExtra("msg", msg);

                        startActivity(intent);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransConfirm.this, titleAlert, error);
                       // Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransConfirm.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupKonfirmResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransConfirm.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });

    }

    public void cancelTransfer() {
        loadingDialog = ProgressDialog.show(TransConfirm.this, "Harap Tunggu", "Memproses Pembatalan Transfer");
        loadingDialog.setCanceledOnTouchOutside(true);

        String secCode = txtPIN.getText().toString() + "x@2016ekl";
        strSecurityCode = Utils.md5(secCode);

        Log.d("OPPO-1", "strUserID cancel: " + strUserID);
        Log.d("OPPO-1", "strAccessToken: " + strAccessToken);
        Log.d("OPPO-1", "strApIUse: " + strApIUse);
        Log.d("OPPO-1", "refID: " + refID);
        Log.d("OPPO-1", "strSecurityCode: " + strSecurityCode);


        Call<TopupKonfirmResponse> transDepositCall = mApiInterfacePayment.cancelTransfer(strUserID, strAccessToken, strApIUse, refID);
        transDepositCall.enqueue(new Callback<TopupKonfirmResponse>() {
            @Override
            public void onResponse(Call<TopupKonfirmResponse> call, Response<TopupKonfirmResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse: " + status);
                    if (status.equals("SUCCESS")) {
                        String msg = response.body().getRespMessage();
                        Intent intent = new Intent(getBaseContext(), TransResponse.class);
                        intent.putExtra("refID", refID);
                        intent.putExtra("idTujuan", idTujuan);
                        intent.putExtra("nama", namaPemilik);
                        intent.putExtra("nominal", nominal);
                        intent.putExtra("keterangan", "Transfer telah DIBATALKAN");
                        intent.putExtra("msg", msg);

                        startActivity(intent);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransConfirm.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransConfirm.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupKonfirmResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransConfirm.this, titleAlert, getResources().getString(R.string.error_api));
                //.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });

    }


    /*
    private class BayarButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            loadingDialog = ProgressDialog.show(TransConfirm.this, "Harap Tunggu", "Memproses Pengisian Topup");
            loadingDialog.setCanceledOnTouchOutside(true);

<<<<<<< .mine
            Log.d("OPPO-1", "onClick: " + PreferenceUtil.getNumberPhone(this)) + "/" + tujuan + "/" + nominal + "/" + txtPIN.getText().toString());
            Call<TopupKonfirmResponse> transDepositCall = mApiInterfacePayment.postRequestTransfer(strUserID, strAccessToken, strApIUse, tujuan,nominal);
            transDepositCall.enqueue(new Callback<TopupKonfirmResponse>() {
                @Override
                public void onResponse(Call<TopupKonfirmResponse> call, Response<TopupKonfirmResponse> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful()) {
                        String status = response.body().getStatus();
                        String error  = response.body().getError();

<<<<<<< .mine
            Call<TopupKonfirmResponse> transDepositCall = mApiInterface.getTransDeposit(PreferenceUtil.getNumberPhone(this)), tujuan, nominal, txtPIN.getText().toString());
                        if ( status.equals("SUCCESS") ) {
                            Intent intent = new Intent(getBaseContext(), TransResponse.class);

                            intent.putExtra("userID", response.body().getUserID());
                            intent.putExtra("accessToken", response.body().getAccessToken());
                            intent.putExtra("nominal", response.body().getNominal());
                            intent.putExtra("idTujuan", response.body().getIdTujuan());
                            intent.putExtra("namaPemilik", response.body().getNamaPemilik());
                            intent.putExtra("refID", response.body().getRefID());
                            intent.putExtra("respTime", response.body().getRespTime());
                            intent.putExtra("status", response.body().getStatus());
                            intent.putExtra("error", error);
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
                public void onFailure(Call<TopupKonfirmResponse> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    Log.d("API_TRANSBELI", t.getMessage().toString());
                }
            });
        }
    }

*/
    /*=========================================payment lama===================================================*/
}
