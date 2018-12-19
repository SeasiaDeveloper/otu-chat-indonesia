package com.eklanku.otuChat.ui.activities.payment.donasi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.doku.CCPaymentActivity;


public class DonasiOpsi extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_donasi);

        ImageButton btndonasiManual = findViewById(R.id.btnDonasimanual);
        ImageButton btndonasiSaldo = findViewById(R.id.btnDonasisaldoekl);
        ImageButton btndonasiDoku = findViewById(R.id.btnDonasidoku);

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

            }
        });

        btndonasiDoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DonasiOpsi.this, CCPaymentActivity.class);
                startActivity(i);
            }
        });
    }
}
