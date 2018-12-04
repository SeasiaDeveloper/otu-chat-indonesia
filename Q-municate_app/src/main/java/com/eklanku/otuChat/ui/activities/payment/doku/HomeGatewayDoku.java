package com.eklanku.otuChat.ui.activities.payment.doku;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eklanku.otuChat.R;

public class HomeGatewayDoku extends AppCompatActivity {

    Button btnPay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_pay_doku);
        btnPay = findViewById(R.id.btnCreditCard);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeGatewayDoku.this, CCPaymentActivity.class);
                startActivity(intent);
            }
        });
    }
}
