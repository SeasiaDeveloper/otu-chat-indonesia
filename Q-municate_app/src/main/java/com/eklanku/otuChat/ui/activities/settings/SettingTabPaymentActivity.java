package com.eklanku.otuChat.ui.activities.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.DeleteAccount;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Profile;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPIN;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPassword;

public class SettingTabPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_tab_payment);

        Button btnProfile = findViewById(R.id.btn_profile);
        Button btnResetPIN = findViewById(R.id.btn_reset_pin);
        Button btnPass = findViewById(R.id.btn_reset_pass);
        Button btnNonaktifAccount = findViewById(R.id.btn_non_aktif_account);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingTabPaymentActivity.this, Profile.class));
               // finish();
            }
        });
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingTabPaymentActivity.this, ResetPassword.class));
               // finish();
            }
        });
        btnResetPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingTabPaymentActivity.this, ResetPIN.class));
              //  finish();
            }
        });

        btnNonaktifAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingTabPaymentActivity.this, DeleteAccount.class);
                startActivity(i);
                finish();
            }
        });
    }
}
