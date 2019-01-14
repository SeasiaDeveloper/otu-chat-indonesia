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
        switch (v.getId()) {
            case R.id.btnMAF:
                jenis = "FINMAF";
                img = "maf";
                break;
            case R.id.btnMCF:
                jenis = "FINMACF";
                img = "mcf";
                break;
            case R.id.btnBAF:
                jenis = "FINBAF";
                img = "baf";
                break;
            case R.id.btnFIF:
                jenis = "FINFIF";
                img = "fif";
                break;
            case R.id.btnColombia:
                jenis = "FINCOLUMBIA";
                img = "columbia";
                break;
            case R.id.btnWOM:
                jenis = "FINWOM";
                img = "wom";
                break;
        }
        cekTransaksiFinance(jenis, img);
    }

    public void cekTransaksiFinance(String jenis, String img) {
        Intent i = new Intent(TransMultifinance_opsi.this, TransMultiFinance.class);
        i.putExtra("jenis", jenis);
        i.putExtra("img", img);
        startActivity(i);
    }
}
