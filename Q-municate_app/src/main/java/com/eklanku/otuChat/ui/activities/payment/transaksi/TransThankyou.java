package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eklanku.otuChat.R;;

import butterknife.ButterKnife;

public class TransThankyou extends AppCompatActivity {

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_thankyou);
        ButterKnife.bind(this);

        btnBack = (Button) findViewById(R.id.btnThankYouBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
