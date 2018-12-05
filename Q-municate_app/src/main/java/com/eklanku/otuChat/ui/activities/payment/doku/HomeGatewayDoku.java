package com.eklanku.otuChat.ui.activities.payment.doku;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.topup.TopupOrder;

public class HomeGatewayDoku extends AppCompatActivity {

    Button btnPay, btnTicketing;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_pay_doku);
        btnPay = findViewById(R.id.btnCreditCard);
        btnTicketing = findViewById(R.id.btn_ticketing);

        Window window = this.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeGatewayDoku.this, CCPaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnTicketing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeGatewayDoku.this, TopupOrder.class));
                finish();
            }
        });
    }
}
