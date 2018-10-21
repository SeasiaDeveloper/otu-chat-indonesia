package com.eklanku.otuChat.ui.activities.payment.transfer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataCekMemberTransfer;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailCekMemberTransfer;
import com.eklanku.otuChat.ui.activities.payment.models.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPINResponse;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPulsa;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DetailTransferResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupKonfirmResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.Utils;

import java.util.HashMap;
import java.util.List;

public class TransDeposit extends AppCompatActivity {

    EditText txtNo, txtJml, txtNamaTujuan, txtPin;
    TextInputLayout layoutNo, layoutJml;
    Button btnTransfer;

    Dialog loadingDialog;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String reffID;
    String strApIUse = "OTU";

    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    TextView txtnomor, txtvoucher;
    Button btnYes, btnNo, btnCekMember;
    LinearLayout layoutTrf;

    Utils utilsAlert;
    String titleAlert = "Deposit";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trans_deposit);
        ButterKnife.bind(this);

        utilsAlert = new Utils(TransDeposit.this);

        txtNo = (EditText) findViewById(R.id.txtTransDepositTujuan);
        layoutNo = (TextInputLayout) findViewById(R.id.txtLayoutTransDepositTujuan);
        txtJml = (EditText) findViewById(R.id.txtTransDepositJml);
        layoutJml = (TextInputLayout) findViewById(R.id.txtLayoutTransDepositJml);
        txtNamaTujuan = findViewById(R.id.txtNamaTujuan);
        btnCekMember = findViewById(R.id.btnCekMember);
        layoutTrf = findViewById(R.id.layout_trf);
        layoutTrf.setVisibility(View.GONE);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //cek antrian
        //cekAntrianTransfer();

        btnCekMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!validateIdpel()) {
                    return;
                }*/
                loadNamaTujuanTransfer();
            }
        });


        btnTransfer = (Button) findViewById(R.id.btnTransDeposit);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdpel()) {
                    return;
                }

                final Dialog dialog = new Dialog(TransDeposit.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_topup);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transfer!!!");

                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(txtNo.getText().toString());
                txtvoucher.setText(txtJml.getText().toString());

                btnYes.setText("YA, Lanjutkan");
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cek_transaksi();
                        dialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                /*dialog.setPositiveButton("YA, LANJUTKAN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cek_transaksi();
                    }
                });

                dialog.setNegativeButton("BATALKAN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/

                // ((Button)dialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.button_border);

                dialog.show();
                return;

            }
        });


    }

    private boolean validateIdpel() {
        String id_pel = txtNo.getText().toString().trim();
        String id_jml = txtJml.getText().toString().trim();
        String nomorHP = PreferenceUtil.getNumberPhone(this);
        String ValNoHP = "";
        if (PreferenceUtil.getNumberPhone(this).startsWith("+62")) {
            ValNoHP = PreferenceUtil.getNumberPhone(this).replace("+62", "0");
        } else {
            ValNoHP = (PreferenceUtil.getNumberPhone(this));
        }

        if (id_pel.isEmpty()) {
            txtNo.setError("Kolom no tujuan tidak boleh kosong");
            //layoutNo.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            requestFocus(txtNo);
            return false;
        } else if (id_jml.isEmpty()) {
            txtNo.setError("Kolom nominal tidak boleh kosong");
            //layoutJml.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            requestFocus(txtJml);
            return false;
        } else if (id_pel.toUpperCase().equals(strUserID)) {
            txtNo.setError("ID EKL tujuan transfer TIDAK boleh sama dengan ID pribadi");
            return false;
        } else if (id_pel.equals(ValNoHP)) {
            txtNo.setError("Nomor HP tujuan transfer TIDAK boleh sama dengan ID pribadi");
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
        layoutJml.setErrorEnabled(false);
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
        loadingDialog = ProgressDialog.show(TransDeposit.this, "Harap Tunggu", "Memproses Transfer Deposit...");
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

                        Log.d("OPPO-1", "userID: " + response.body().getUserID());
                        Log.d("OPPO-1", "accessToken: " + response.body().getAccessToken());
                        Log.d("OPPO-1", "nominal: " + response.body().getNominal());
                        Log.d("OPPO-1", "idTujuan: " + response.body().getIdTujuan());
                        Log.d("OPPO-1", "namaPemilik: " + response.body().getNamaPemilik());
                        Log.d("OPPO-1", "refID: " + response.body().getRefID());
                        Log.d("OPPO-1", "respTime: " + response.body().getRespTime());
                        Log.d("OPPO-1", "status: " + response.body().getStatus());
                        Log.d("OPPO-1", "error: " + error);

                        startActivity(intent);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransDeposit.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailTransferResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void cekAntrianTransfer() {
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

                        Log.d("OPPO-1", "userID: " + response.body().getUserID());
                        Log.d("OPPO-1", "accessToken: " + response.body().getAccessToken());
                        Log.d("OPPO-1", "nominal: " + response.body().getNominal());
                        Log.d("OPPO-1", "idTujuan: " + response.body().getIdTujuan());
                        Log.d("OPPO-1", "namaPemilik: " + response.body().getNamaPemilik());
                        Log.d("OPPO-1", "refID: " + response.body().getRefID());
                        Log.d("OPPO-1", "respTime: " + response.body().getRespTime());
                        Log.d("OPPO-1", "status: " + response.body().getStatus());
                        Log.d("OPPO-1", "error: " + error);

                        startActivity(intent);
                        finish();
                    } else {
                        //utilsAlert.globalDialog(TransDeposit.this, titleAlert, error);
                    }
                } else {
                    //utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailTransferResponse> call, Throwable t) {
                //utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadNamaTujuanTransfer() {
        loadingDialog = ProgressDialog.show(TransDeposit.this, "Harap Tunggu", "Cek Member...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<DataCekMemberTransfer> transDepositCall = mApiInterfacePayment.getCekMemberTransfer(strUserID, strApIUse, txtNo.getText().toString(), strAccessToken);
        transDepositCall.enqueue(new Callback<DataCekMemberTransfer>() {
            @Override
            public void onResponse(Call<DataCekMemberTransfer> call, Response<DataCekMemberTransfer> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailCekMemberTransfer> ckmember = response.body().getDatarespon();
                        for (int i = 0; i < ckmember.size(); i++) {
                            txtNamaTujuan.setText(ckmember.get(i).getNama_member());
                            String status_member = ckmember.get(i).getStatus_member();
                            if (status_member.equals("Active")) {
                                layoutTrf.setVisibility(View.VISIBLE);
                            } else {
                                layoutTrf.setVisibility(View.GONE);
                                Toast.makeText(TransDeposit.this, "Member tidak aktif", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        layoutTrf.setVisibility(View.GONE);
                        Toast.makeText(TransDeposit.this, "Gagal cek member, periksa kembali kode member", Toast.LENGTH_SHORT).show();
                        //utilsAlert.globalDialog(TransDeposit.this, titleAlert, error);
                    }
                } else {
                    layoutTrf.setVisibility(View.GONE);
                    //utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataCekMemberTransfer> call, Throwable t) {
                loadingDialog.dismiss();
                layoutTrf.setVisibility(View.GONE);
                //utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    public void getKey() {
        loadingDialog = ProgressDialog.show(TransDeposit.this, "Harap Tunggu", "Load Get key...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<ResetPINResponse> callResetPass = mApiInterfacePayment.postResetPin(strUserID, strApIUse, strAccessToken);
        callResetPass.enqueue(new Callback<ResetPINResponse>() {
            @Override
            public void onResponse(Call<ResetPINResponse> call, Response<ResetPINResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "getKey: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        //jalankan approve
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPINResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransDeposit.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }
}
