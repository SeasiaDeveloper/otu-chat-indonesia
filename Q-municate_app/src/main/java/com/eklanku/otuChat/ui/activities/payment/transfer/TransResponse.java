package com.eklanku.otuChat.ui.activities.payment.transfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eklanku.otuChat.R;;

import java.text.NumberFormat;
import java.util.Locale;

public class TransResponse extends AppCompatActivity {

    Button btnBack;

    //Rina
    String msg, idTujuan, nama, nominal, refID, keterangan;

    TextView txtIdTujuan, txtNamaTujuan, txtNominal, txtrefID, txtketerangan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_response);

        initializeResources();
        idTujuan = getIntent().getExtras().getString("idTujuan", "");
        nama = getIntent().getExtras().getString("nama", "");
        nominal = getIntent().getExtras().getString("nominal", "");
        refID = getIntent().getExtras().getString("refID", "");
        keterangan = getIntent().getExtras().getString("keterangan", "");

        txtIdTujuan = findViewById(R.id.txt_id_tujuan);
        txtNamaTujuan = findViewById(R.id.txt_nama_tujuan);
        txtNominal = findViewById(R.id.txt_nominal);
        txtrefID = findViewById(R.id.txt_reff_id);
        txtketerangan = findViewById(R.id.txt_keterangan);

        loadData();
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

        txtIdTujuan.setText(idTujuan);
        txtNamaTujuan.setText(nama);
        txtNominal.setText(rupiah);
        txtrefID.setText(refID);
        txtketerangan.setText(keterangan);
    }

    private void initializeResources() {
        btnBack = (Button) findViewById(R.id.btn_ok);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
