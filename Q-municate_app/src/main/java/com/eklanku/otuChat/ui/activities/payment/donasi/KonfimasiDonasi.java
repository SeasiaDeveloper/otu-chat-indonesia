package com.eklanku.otuChat.ui.activities.payment.donasi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eklanku.otuChat.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class KonfimasiDonasi extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_donasi);
        Bundle extras = getIntent().getExtras();

        TextView ket = findViewById(R.id.tvket1);
        Button btnOk = findViewById(R.id.btnKonfirmasiDonasi);

        String nominal =  extras.getString("nominal");;
        String respMessage =  extras.getString("pesan");;

        ket.setText(respMessage);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
}
