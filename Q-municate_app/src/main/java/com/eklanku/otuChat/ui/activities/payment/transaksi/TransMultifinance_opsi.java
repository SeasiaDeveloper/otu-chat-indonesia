package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.eklanku.otuChat.R;

public class TransMultifinance_opsi extends AppCompatActivity implements View.OnClickListener {

    ImageButton _btnMAF, _btnMCF, _btnBAF, _btnFIF, _btnColumbia, _btnWOM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_multifinance);

        _btnMAF = findViewById(R.id.btnMAF);
        _btnMCF = findViewById(R.id.btnMCF);
        _btnBAF = findViewById(R.id.btnBAF);
        _btnFIF = findViewById(R.id.btnFIF);
        _btnColumbia = findViewById(R.id.btnColombia);
        _btnWOM = findViewById(R.id.btnWOM);

        _btnMAF.setOnClickListener(this);
        _btnMCF.setOnClickListener(this);
        _btnBAF.setOnClickListener(this);
        _btnFIF.setOnClickListener(this);
        _btnColumbia.setOnClickListener(this);
        _btnWOM.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String jenis = "";
        String img = "";
        String title = "";
        switch (v.getId()) {
            case R.id.btnMAF:
                jenis = "FINMAF";
                img = "maf";
                title = "Mega Auto Finance";
                break;
            case R.id.btnMCF:
                jenis = "FINMACF";
                img = "mcf";
                title = "Mega Central Finance";
                break;
            case R.id.btnBAF:
                jenis = "FINBAF";
                img = "baf";
                title = "Busan Auto Finance";
                break;
            case R.id.btnFIF:
                jenis = "FINFIF";
                img = "fif";
                title = "Federal International Finance";
                break;
            case R.id.btnColombia:
                jenis = "FINCOLUMBIA";
                img = "columbia";
                title = "Columbia";
                break;
            case R.id.btnWOM:
                jenis = "FINWOM";
                img = "wom";
                title = "Wahan Ottomitra Multiartha";
                break;
        }
        cekTransaksiFinance(jenis, img, title);
    }

    public void cekTransaksiFinance(String jenis, String img, String title) {
        Intent i = new Intent(TransMultifinance_opsi.this, TransMultiFinance.class);
        i.putExtra("jenis", jenis);
        i.putExtra("img", img);
        i.putExtra("title", title);
        startActivity(i);
    }
}
