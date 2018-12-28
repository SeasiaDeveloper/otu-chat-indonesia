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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.payment.sqlite.database.DatabaseHelper;
import com.eklanku.otuChat.ui.activities.payment.sqlite.database.model.History;
import com.eklanku.otuChat.ui.activities.payment.transfer.TransConfirm;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.R;;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    String usageUnit, respMessage, point;
    String userID, accessToken;

    ApiInterfacePayment mApiInterfacePayment;
    TextView txtJenis, txtReffID, txtCustomerID, txtCustomerName, txtCustomerMSISDN, txtTanggal, txtPayment, txtAdminBank, txtBilling, txtStatus, txrespMessage, txpoint;

    TextView lbIdCustomer, lbNama, titik2idcust, titik2nama;
    ImageView imgStatus;
    View vIDcust, vName, vPhone;

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    LinearLayout layoutNama, layoutCust;

    //HISTORY
    private DatabaseHelper db;
    private List<History> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_konfirmasi);
        ButterKnife.bind(this);

        //HISTORY
        db = new DatabaseHelper(this);

        extras = getIntent().getExtras();
        prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        lblContent = (TextView) findViewById(R.id.lblKonfirmasiContent);
        lbIdCustomer = (TextView) findViewById(R.id.idcustomer);
        titik2idcust = (TextView) findViewById(R.id.titik2idcust);
        lbNama = (TextView) findViewById(R.id.lbNama);
        titik2nama = (TextView) findViewById(R.id.titik2nama);
        btnSave = (Button) findViewById(R.id.btnKonfirmasiSave);

        imgStatus = findViewById(R.id.img_status);
        layoutNama = findViewById(R.id.layout_name);
        layoutCust = findViewById(R.id.layout_cust);

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
        txrespMessage = findViewById(R.id.txt_notice);
        txpoint = findViewById(R.id.txt_point);



        vIDcust = findViewById(R.id.vIdcustomer);
        vName = findViewById(R.id.vName);
        vPhone = findViewById(R.id.vphone);

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
        status = extras.getString("status");
        usageUnit = extras.getString("usageUnit");
        respMessage = extras.getString("respMessage");
        point = extras.getString("ep");

        // SETTEXT DISINI NA
        txtJenis.setText(jenis);
        txtReffID.setText(billingReferenceID.trim());
        txtCustomerID.setText(customerID);
        txtCustomerName.setText(customerName);
        txtCustomerMSISDN.setText(customerMSISDN);
        txtTanggal.setText(tanggal);
        txtPayment.setText(formatRP(Double.parseDouble(payment)));
        txtAdminBank.setText(formatRP(Double.parseDouble(adminBank)));
        txtBilling.setText(formatRP(Double.parseDouble(billing)));
        txtStatus.setText(status);
        txrespMessage.setText(respMessage);
        txpoint.setText(point);

        if (usageUnit.equalsIgnoreCase("TOPUP")) {
            usageUnit = "TOPUP";
            vIDcust.setVisibility(View.GONE);
            vName.setVisibility(View.GONE);

            lbIdCustomer.setVisibility(View.GONE);
            layoutNama.setVisibility(View.GONE);
            layoutCust.setVisibility(View.GONE);
            lbNama.setVisibility(View.GONE);
            titik2nama.setVisibility(View.GONE);
            titik2idcust.setVisibility(View.GONE);
            txtCustomerID.setVisibility(View.GONE);
            txtCustomerName.setVisibility(View.GONE);
            btnSave.setText("OK");
            lblContent.setText("Terimakasih Telah Berbelanja di Eklanku - Otu Chat");

        } else {
            vIDcust.setVisibility(View.VISIBLE);
            vName.setVisibility(View.VISIBLE);
            layoutNama.setVisibility(View.VISIBLE);
            layoutCust.setVisibility(View.VISIBLE);
            lbIdCustomer.setVisibility(View.VISIBLE);
            lbNama.setVisibility(View.VISIBLE);
            titik2nama.setVisibility(View.VISIBLE);
            titik2idcust.setVisibility(View.VISIBLE);
            txtCustomerID.setVisibility(View.VISIBLE);
            txtCustomerName.setVisibility(View.VISIBLE);
            btnSave.setText("Proses Pembayaran");
            lblContent.setText("Anda yakin akan melanjutkan transaksi? " +
                    "\nSilahkan klik tombol Proses untuk melanjutkan");
        }

        if (status.equals("SUCCESS")) {
            imgStatus.setImageResource(R.drawable.ic_doku_success);
        } else {
            imgStatus.setImageResource(R.drawable.ic_doku_failed);
        }


        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usageUnit.equalsIgnoreCase("TOPUP")) {
                    finish();
                } else {
                    dialogWarning();
                }

            }
        });
    }

    //HISTORY
    private void createHistory(String number, String nominal, String trans) {
        // inserting number in db and getting
        // newly inserted number id
        long id = db.insertHistory(number, nominal, trans);

        // get the newly inserted number from db
        History n = db.getHistory(id);

        if (n != null) {
            // adding new number to array list at 0 position
            historyList.add(0, n);

            // refreshing the list
            //mAdapter.notifyDataSetChanged();

            //toggleEmptyNotesHistory();
        }
    }


    private void confirmTransaksi() {
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

    public String formatRP(double nominal){
        String valNom = "";
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator('.');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        valNom = kursIndonesia.format(nominal);
        return valNom;
    }


    public void dialogWarning() {
        final Dialog dialog = new Dialog(TransKonfirmasi.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_alert_dialog_konfirmasi);
        dialog.setCancelable(false);
        dialog.setTitle("Peringatan Transaksi!!!");


        Button btnYes = dialog.findViewById(R.id.btn_yes);
        Button btnNo = dialog.findViewById(R.id.btn_no);
        TextView tvNama = dialog.findViewById(R.id.txt_namapemiliktagihan);
        TextView tvtagihan = dialog.findViewById(R.id.txt_tagihan);

        tvNama.setText(customerName);
        tvtagihan.setText(formatRP(Double.parseDouble(billing)));

        btnYes.setText(getString(R.string.lanjutkan));
        btnYes.setOnClickListener(view -> {
            confirmTransaksi();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return;
    }
}
