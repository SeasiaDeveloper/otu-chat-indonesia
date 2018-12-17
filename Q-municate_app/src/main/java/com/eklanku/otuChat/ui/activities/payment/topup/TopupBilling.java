package com.eklanku.otuChat.ui.activities.payment.topup;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.utils.Utils;;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TopupBilling extends AppCompatActivity {

    Button btnComfirm;

    Dialog loadingDialog;


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

    Utils utilsAlert;
    String titleAlert = "Topup";

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
        utilsAlert = new Utils(TopupBilling.this);
        txtKeterangan.setText("Langkah menuju sukses melakukan deposit:\n1. transfer sesuai nominal Rp " + nominal + ".\n2. Pastikan no. rek. sesuai dgn a.n. PT. Eklanku Indonesia Cemerlang.\n3. Silahkan klik tombol Konfirmasi, setelah transfer dilakukan.");
    }


    private void initializeResources() {
        btnComfirm = (Button) findViewById(R.id.btn_ok);
        btnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //depositOrder();
                utilsAlert.globalDialog(TopupBilling.this, titleAlert, "Terimakasih Telah Melakukan Pengisian Saldo");
                //finish();
            }
        });
    }

}
