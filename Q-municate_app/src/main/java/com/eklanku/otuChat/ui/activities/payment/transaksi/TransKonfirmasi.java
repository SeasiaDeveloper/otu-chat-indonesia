package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.R;;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransKonfirmasi extends AppCompatActivity {
    Bundle extras;
    SharedPreferences prefs;
    TextView lblContent;
    Button btnSave;
    Dialog loadingDialog;
    String //id_member,
            transaksi, //harga,
            jenis, id_pel, pin, cmd_save;

    String productCode, billingReferenceID, customerID, customerName, customerMSISDN, tanggal, payment, adminBank, billing, status;
    String usageUnit;
    String userID, accessToken;

    ApiInterfacePayment mApiInterfacePayment;
    TextView txtJenis, txtReffID, txtCustomerID, txtCustomerName, txtCustomerMSISDN, txtTanggal, txtPayment, txtAdminBank, txtBilling, txtStatus;

    TextView lbIdCustomer, lbNama, titik2idcust, titik2nama;

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_konfirmasi);
        ButterKnife.bind(this);

        extras = getIntent().getExtras();
        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        lblContent = (TextView) findViewById(R.id.lblKonfirmasiContent);
        lbIdCustomer = (TextView) findViewById(R.id.idcustomer);
        titik2idcust = (TextView) findViewById(R.id.titik2idcust);
        lbNama = (TextView) findViewById(R.id.lbNama);
        titik2nama = (TextView) findViewById(R.id.titik2nama);
        btnSave = (Button) findViewById(R.id.btnKonfirmasiSave);

        txtJenis = findViewById(R.id.txt_jenis);
        txtReffID = findViewById(R.id.txt_billing_reff_id);
        txtCustomerID = findViewById(R.id.txt_customer_id);
        txtCustomerName = findViewById(R.id.txt_customer_name);
        txtCustomerMSISDN = findViewById(R.id.txt_customer_msisdn);
        txtTanggal = findViewById(R.id.txt_tanggal);
        txtPayment = findViewById(R.id.txt_payment);
        txtAdminBank = findViewById(R.id.txt_admin_bank);
        txtBilling = findViewById(R.id.txt_billing);
        txtStatus = findViewById(R.id.txt_status);

        jenis = extras.getString("productCode");
        id_pel = extras.getString("id_pel");
        pin = extras.getString("pin");
        cmd_save = extras.getString("cmd_save");
        transaksi = extras.getString("transaksi");

        //Rina
        productCode = extras.getString("productCode");
        billingReferenceID = extras.getString("billingReferenceID");
        customerID = extras.getString("customerID");
        customerName = extras.getString("customerName");
        customerMSISDN = extras.getString("customerMSISDN");
        tanggal = sdf.format(new Date());
        payment = extras.getString("payment");
        adminBank = extras.getString("adminBank");
        billing = extras.getString("billing");
        accessToken = extras.getString("accessToken");
        userID = extras.getString("userID");
        status =  extras.getString("status");
        usageUnit = extras.getString("usageUnit");
        //

        // SETTEXT DISINI NA
        txtJenis.setText(jenis);
        txtReffID.setText(billingReferenceID);
        txtCustomerID.setText(customerID);
        txtCustomerName.setText(customerName);
        txtCustomerMSISDN.setText(customerMSISDN);
        txtTanggal.setText(tanggal);
        txtPayment.setText(payment);
        txtAdminBank.setText(adminBank);
        txtBilling.setText(billing);
        txtStatus.setText(status);

        if(usageUnit.equalsIgnoreCase("TOPUP")){
            usageUnit = "TOPUP";
            lbIdCustomer.setVisibility(View.GONE);
            lbNama.setVisibility(View.GONE);
            titik2nama.setVisibility(View.GONE);
            titik2idcust.setVisibility(View.GONE);
            txtCustomerID.setVisibility(View.GONE);
            txtCustomerName.setVisibility(View.GONE);
            btnSave.setText("OK");
            lblContent.setText("Transaksi Sukses\nTerimakasih Telah Berbelanja di Eklanku");
        }else{
            lbIdCustomer.setVisibility(View.VISIBLE);
            lbNama.setVisibility(View.VISIBLE);
            titik2nama.setVisibility(View.VISIBLE);
            titik2idcust.setVisibility(View.VISIBLE);
            txtCustomerID.setVisibility(View.VISIBLE);
            txtCustomerName.setVisibility(View.VISIBLE);
            btnSave.setText("Confirmation");
            lblContent.setText("Anda yakin akan melanjutkan transaksi? " +
                    "\nSilahkan klik tombol Proses untuk melanjutkan");
        }


        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usageUnit.equalsIgnoreCase("TOPUP")){
                    finish();
                }else{
                    konfirm_transaksi();
                }

            }
        });
    }


    private void konfirm_transaksi() {
        loadingDialog = ProgressDialog.show(TransKonfirmasi.this, "Harap Tunggu", "Konfirmasi Pembayaran...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transKonfirmCall = mApiInterfacePayment.postTransConfirm(userID, accessToken, billingReferenceID, "OTU");
        transKonfirmCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    Log.d("OPPO-1", "onResponse: " + status);

                    if (status.equals("SUCCESS")) {
                        Intent inThankYou = new Intent(getBaseContext(), TransThankyou.class);
                        startActivity(inThankYou);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSCONFIRM", t.getMessage().toString());
            }
        });
    }

    /*======================================================konfirm transakasi lama================================================*/
    /*
    private void konfirm_transaksi() {
        loadingDialog = ProgressDialog.show(TransKonfirmasi.this, "Harap Tunggu", "Konfirmasi Pembayaran...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transKonfirmCall = mApiInterface.postTransConfirm(PreferenceUtil.getNumberPhone(this)), jenis, id_pel, pin, cmd_save);
        transKonfirmCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();

                    if (status.equals("OK")) {
                        Intent inThankYou = new Intent(getBaseContext(), TransThankyou.class);
                        startActivity(inThankYou);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSCONFIRM", t.getMessage().toString());
            }
        });
    }
    /*=======================================================*/
    @Override
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
