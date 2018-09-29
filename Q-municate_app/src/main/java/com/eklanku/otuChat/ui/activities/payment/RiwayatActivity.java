package com.eklanku.otuChat.ui.activities.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryBalanceActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryBonusActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryDespositActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryPenarikanActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryTrxActivity;
import com.eklanku.otuChat.ui.activities.payment.transaksi.PaymentLogin;
import com.eklanku.otuChat.utils.PreferenceUtil;

import butterknife.ButterKnife;

public class RiwayatActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mLinRiwayatSaldo;
    LinearLayout mLinRiwayatTransaksi;
    LinearLayout mLinRiwayatDeposit;
    LinearLayout mLinRiwayatPenarikan;
    LinearLayout mLinRiwayatBonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        mLinRiwayatSaldo = (LinearLayout) findViewById(R.id.linRiwayatSaldo);
        mLinRiwayatTransaksi = (LinearLayout) findViewById(R.id.linRiwayatTransaksi);
        mLinRiwayatDeposit = (LinearLayout) findViewById(R.id.linRiwayatDeposit);
        mLinRiwayatPenarikan = (LinearLayout) findViewById(R.id.linRiwayatPenrikan);
        mLinRiwayatBonus = (LinearLayout) findViewById(R.id.linRiwayatBonus);

        mLinRiwayatSaldo.setOnClickListener(this);
        mLinRiwayatTransaksi.setOnClickListener(this);
        mLinRiwayatDeposit.setOnClickListener(this);
        mLinRiwayatPenarikan.setOnClickListener(this);
        mLinRiwayatBonus.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linRiwayatSaldo:
                if (menuDialog()) {
                    startActivity(new Intent(RiwayatActivity.this, HistoryBalanceActivity.class));
                }
                break;
            case R.id.linRiwayatTransaksi:
                if (menuDialog()) {
                    startActivity(new Intent(RiwayatActivity.this, HistoryTrxActivity.class));
                }
                break;
            case R.id.linRiwayatDeposit:
                if (menuDialog()) {
                    startActivity(new Intent(RiwayatActivity.this, HistoryDespositActivity.class));
                }
                break;
            case R.id.linRiwayatPenrikan:
                if (menuDialog()) {
                    startActivity(new Intent(RiwayatActivity.this, HistoryPenarikanActivity.class));
                }
                break;
            case R.id.linRiwayatBonus:
                if (menuDialog()) {
                    startActivity(new Intent(RiwayatActivity.this, HistoryBonusActivity.class));
                }
                break;
        }
    }
    private boolean menuDialog() {
        if (!PreferenceUtil.isLoginStatus(RiwayatActivity.this)) {
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(RiwayatActivity.this)
                    .setTitle("PERINGATAN!!!")
                    .setMessage("Untuk meningkatkan Kemanan Silahkan Login terlebih dahulu")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(RiwayatActivity.this, PaymentLogin.class));
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            return false;
        } else return true;
    }

    
}