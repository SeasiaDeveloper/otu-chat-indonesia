package com.eklanku.otuChat.ui.activities.payment.donasi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.doku.CCPaymentActivity;
import com.ms.square.android.expandabletextview.ExpandableTextView;


public class DonasiOpsi extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_donasi);

        ImageButton btndonasiManual = findViewById(R.id.btnDonasimanual);
        ImageButton btndonasiSaldo = findViewById(R.id.btnDonasisaldoekl);
        ImageButton btndonasiDoku = findViewById(R.id.btnDonasidoku);
        CheckBox ck_setuju = (CheckBox) findViewById(R.id.cksetuju);

        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);
        expTv1.setText(getString(R.string.syarat_ketentuan_donasi_detail));
        ck_setuju.setChecked(false);
        btndonasiSaldo.setImageResource(R.drawable.ic_donasi_saldo_grey);
        btndonasiDoku.setImageResource(R.drawable.ic_donasi_cc_grey);
        btndonasiSaldo.setEnabled(false);
        btndonasiDoku.setEnabled(false);

        ck_setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ck_setuju.isChecked()) {
                    btndonasiSaldo.setEnabled(true);
                    btndonasiDoku.setEnabled(true);
                    btndonasiSaldo.setImageResource(R.drawable.ic_donasi_saldo);
                    btndonasiDoku.setImageResource(R.drawable.ic_credit_card);
                } else {
                    btndonasiSaldo.setEnabled(false);
                    btndonasiDoku.setEnabled(false);
                    btndonasiSaldo.setImageResource(R.drawable.ic_donasi_saldo_grey);
                    btndonasiDoku.setImageResource(R.drawable.ic_donasi_cc_grey);
                }
            }
        });


        btndonasiManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DonasiOpsi.this, DonasiManualTransfer.class);
                startActivity(i);
            }
        });

        btndonasiSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(DonasiOpsi.this, DonasiTransferSaldo.class);
                startActivity(i);
            }
        });

        btndonasiDoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DonasiOpsi.this, CCDonasiActivity.class);
                startActivity(i);
            }
        });
    }
}
