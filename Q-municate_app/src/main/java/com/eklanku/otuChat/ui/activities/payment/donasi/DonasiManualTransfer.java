package com.eklanku.otuChat.ui.activities.payment.donasi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailM;
import com.eklanku.otuChat.ui.activities.payment.models.TopupOrderResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerBankAdapter;
import com.eklanku.otuChat.utils.Utils;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonasiManualTransfer extends AppCompatActivity {

    EditText edNominal;
    Button btnDonasi;
    ApiInterfacePayment mApiInterfacePayment;
    Spinner spnDataBank;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strApIUse = "OTU";

    String nama, norek, an, live;
    Utils utilsAlert;
    String titleAlert = "Donasi Via Transfer";

    Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi_manual);
        edNominal = findViewById(R.id.edittext_nominal);
        btnDonasi = findViewById(R.id.btn_donasi);
        spnDataBank = findViewById(R.id.spinner_data_bank);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(DonasiManualTransfer.this);
        utilsAlert = new Utils(DonasiManualTransfer.this);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
        setListbank();

        btnDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdPel()) {
                    return;
                }
                final Dialog dialog = new Dialog(DonasiManualTransfer.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog_donasi);
                dialog.setCancelable(false);
                dialog.setTitle("Donasi OTU CHAT");

                Button btnYes = dialog.findViewById(R.id.btn_yes);
                Button btnNo = dialog.findViewById(R.id.btn_no);
                TextView tvNominal = dialog.findViewById(R.id.txt_donasi);

                tvNominal.setText(edNominal.getText().toString());

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferDonasi();
                        dialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return;
            }
        });

        setTitle("Donasi via Transfer");
    }


    public void setListbank() {
        loadingDialog = ProgressDialog.show(DonasiManualTransfer.this, "Harap Tunggu", "Mengambil Data Bank...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TopupOrderResponse> dataCall = mApiInterfacePayment.postInquiryBankList(strUserID, strAccessToken, strApIUse);
        dataCall.enqueue(new Callback<TopupOrderResponse>() {
            @Override
            public void onResponse(Call<TopupOrderResponse> call, Response<TopupOrderResponse> response) {
                //Log.d("AYIK", "loaddatabank:" + response.toString() + "\n" + strUserID + "\n" + strAccessToken + "\n" + strApIUse);
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<TopupDetailM> result = response.body().getBanklist();

                        SpinnerBankAdapter adapter = new SpinnerBankAdapter(getApplicationContext(), result);
                        //Log.d("AYIK", "loaddatabanklist:" + result.size() + "\n" + result.toString());

                        spnDataBank.setAdapter(adapter);
                        spnDataBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String isActive = result.get(position).getIsactive();
                                if (isActive.equalsIgnoreCase("Live")) {
                                    norek = result.get(position).getNorec();
                                    nama = result.get(position).getBank();
                                    an = result.get(position).getAnbank();
                                    live = result.get(position).getIsactive();

                                    Log.d("AYIK", "loaddatabank:" + live);

                                } else {
                                    Toast.makeText(DonasiManualTransfer.this, "No Rekening tidak aktif", Toast.LENGTH_SHORT).show();
                                    //utilsAlert.globalDialog(TopupOrder.this, titleAlert, "No Rekening tidak aktif");

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        utilsAlert.globalDialog(DonasiManualTransfer.this, titleAlert, error);
                    }
                } else {
                    utilsAlert.globalDialog(DonasiManualTransfer.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TopupOrderResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(DonasiManualTransfer.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    public void transferDonasi() {
        loadingDialog = ProgressDialog.show(DonasiManualTransfer.this, "Harap Tunggu", "Load Donasi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TopupPayResponse> dataCall = mApiInterfacePayment.donasi_manual(strUserID, strAccessToken, strApIUse, nama, edNominal.getText().toString());
        dataCall.enqueue(new Callback<TopupPayResponse>() {
            @Override
            public void onResponse(Call<TopupPayResponse> call, Response<TopupPayResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent intent = new Intent(getBaseContext(), KonfimasiDonasi.class);
                        intent.putExtra("nominal", edNominal.getText().toString());
                        intent.putExtra("pesan", respMessage);
                        startActivity(intent);
                        finish();
                    } else {
                        utilsAlert.globalDialog(DonasiManualTransfer.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(DonasiManualTransfer.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TopupPayResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(DonasiManualTransfer.this, titleAlert, getResources().getString(R.string.error_api));
                Log.d("API_LOADDATA", t.getMessage());
            }
        });
    }

    private boolean validateIdPel() {
        String id_pel = edNominal.getText().toString().trim();
        edNominal.setError(null);

        if (id_pel.isEmpty()) {
            edNominal.setError("Kolom nomor tidak boleh kosong");
            requestFocus(edNominal);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
