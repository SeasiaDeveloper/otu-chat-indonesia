package com.eklanku.otuChat.ui.activities.payment.donasi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models.DataSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailM;
import com.eklanku.otuChat.ui.activities.payment.models.TopupOrderResponse;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerBankAdapter;
import com.eklanku.otuChat.utils.Utils;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonasiTransferSaldo extends AppCompatActivity {

    EditText edNominal, edtujuan;
    Button btnDonasi;
    ApiInterfacePayment mApiInterfacePayment;
    Spinner spnDataBank;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken, strApIUse = "OTU";

    String nama, norek, an, live;
    Utils utilsAlert;
    String titleAlert = "Donasi Via Transfer";

    Dialog loadingDialog;
    TextView tvSaldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi_saldo);
        edNominal = findViewById(R.id.edittext_nominal);
        edtujuan = findViewById(R.id.edittext_tujuan);
        btnDonasi = findViewById(R.id.btn_donasi);
        tvSaldo = findViewById(R.id.tvjmlsaldo);
        edtujuan.setEnabled(false);

        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(DonasiTransferSaldo.this);
        utilsAlert = new Utils(DonasiTransferSaldo.this);

        //get userid and token from preferencei
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
        LoadSaldoBonus();
        setTitle("Donasi via Saldo Eklanku");

        btnDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateIdPel()) {
                    return;
                }
                final Dialog dialog = new Dialog(DonasiTransferSaldo.this);

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

    }

    public void transferDonasi() {
        loadingDialog = ProgressDialog.show(DonasiTransferSaldo.this, "Harap Tunggu", "Load Donasi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TopupPayResponse> dataCall = mApiInterfacePayment.donasi_manual(strUserID, strAccessToken, strApIUse, "SALDO", edNominal.getText().toString());
        dataCall.enqueue(new Callback<TopupPayResponse>() {
            @Override
            public void onResponse(Call<TopupPayResponse> call, Response<TopupPayResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String respMessage = response.body().getResponMessage2();
                    Log.d("OPPO-1", "onResponse: "+respMessage);
                    if (status.equals("SUCCESS")) {
                        Intent intent = new Intent(getBaseContext(), KonfimasiDonasi.class);
                        intent.putExtra("nominal", edNominal.getText().toString());
                        intent.putExtra("pesan", respMessage);
                        startActivity(intent);
                        finish();
                    } else {
                        utilsAlert.globalDialog(DonasiTransferSaldo.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(DonasiTransferSaldo.this, titleAlert, getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<TopupPayResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(DonasiTransferSaldo.this, titleAlert, getResources().getString(R.string.error_api));
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


    public void LoadSaldoBonus() {
        loadingDialog = ProgressDialog.show(DonasiTransferSaldo.this, "Harap Tunggu", "Load Saldo...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<DataSaldoBonus> userCall = mApiInterfacePayment.getSaldodetail(strUserID, strApIUse, strAccessToken);
        userCall.enqueue(new Callback<DataSaldoBonus>() {
            @Override
            public void onResponse(Call<DataSaldoBonus> call, Response<DataSaldoBonus> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    String id_member = "", sisa_uang = "", carier_member = "", bonus_member = "";
                    Log.d("OPPO-1", "OnLoad userID " + strUserID + " response.isSuccessful()) " + response.isSuccessful());
                    if (status.equals("SUCCESS")) {

                        final List<DataDetailSaldoBonus> products = response.body().getBalance();
                        for (int i = 0; i < products.size(); i++) {
                            id_member = products.get(i).getId_member();
                            sisa_uang = products.get(i).getSisa_uang();
                            carier_member = products.get(i).getCarier_member();
                            bonus_member = products.get(i).getBonus_member();
                        }

                        Double total = 0.0d;
                        try {
                            if (sisa_uang != null && !sisa_uang.trim().isEmpty())
                                total = Double.valueOf(sisa_uang);
                        } catch (Exception e) {
                            total = 0.0d;
                        }
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                        String rupiah = format.format(total);

                        tvSaldo.setText(rupiah);


                    } else {
                        Toast.makeText(DonasiTransferSaldo.this, "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DonasiTransferSaldo.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataSaldoBonus> call, Throwable t) {
                loadingDialog.dismiss();
                Log.d("OPPO-1", "onFailure: " + t.getMessage());
                Toast.makeText(DonasiTransferSaldo.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
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
}
