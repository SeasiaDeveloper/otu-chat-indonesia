package com.eklanku.otuChat.ui.activities.payment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.laporannew.HistoryActivity;
import com.eklanku.otuChat.ui.activities.payment.transaksi.PaymentLogin;
import com.eklanku.otuChat.utils.PreferenceUtil;


public class RiwayatActivity extends AppCompatActivity implements View.OnClickListener {

    Button mLinRiwayatSaldo;
    Button mLinRiwayatTransaksi;
    Button mLinRiwayatDeposit;
    Button mLinRiwayatPenarikan;
    Button mLinRiwayatBonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        //Toast.makeText(this, "HISTORI", Toast.LENGTH_SHORT).show();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLinRiwayatSaldo = findViewById(R.id.linRiwayatSaldo);
        mLinRiwayatTransaksi = findViewById(R.id.linRiwayatTransaksi);
        mLinRiwayatDeposit = findViewById(R.id.linRiwayatDeposit);
        mLinRiwayatPenarikan = findViewById(R.id.linRiwayatPenarikan);
        mLinRiwayatBonus = findViewById(R.id.linRiwayatBonus);

        mLinRiwayatSaldo.setOnClickListener(this);
        mLinRiwayatTransaksi.setOnClickListener(this);
        mLinRiwayatDeposit.setOnClickListener(this);
        mLinRiwayatPenarikan.setOnClickListener(this);
        mLinRiwayatBonus.setOnClickListener(this);

        Log.d("OPPO-1", "oke");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.linRiwayatSaldo:
                if (menuDialog()) {
                    //startActivity(new Intent(RiwayatActivity.this, HistoryBalanceActivity.class));
                    Intent intent = new Intent(RiwayatActivity.this, HistoryActivity.class);
                    intent.putExtra("jenisHistory", "saldo");
                    startActivity(intent);
                }
                break;
            case R.id.linRiwayatTransaksi:
                //Toast.makeText(this, "TRX", Toast.LENGTH_SHORT).show();
                if (menuDialog()) {
                    /*startActivity(new Intent(RiwayatActivity.this, HistoryActivity.class));*/
                    Intent intent = new Intent(RiwayatActivity.this, HistoryActivity.class);
                    intent.putExtra("jenisHistory", "transaksi");
                    startActivity(intent);
                }
                break;
            case R.id.linRiwayatDeposit:
                if (menuDialog()) {
                    //startActivity(new Intent(RiwayatActivity.this, HistoryDespositActivity.class));
                    Intent intent = new Intent(RiwayatActivity.this, HistoryActivity.class);
                    intent.putExtra("jenisHistory", "deposit");
                    startActivity(intent);
                }
                break;
            case R.id.linRiwayatPenarikan:
                if (menuDialog()) {
                   // startActivity(new Intent(RiwayatActivity.this, HistoryPenarikanActivity.class));
                    Intent intent = new Intent(RiwayatActivity.this, HistoryActivity.class);
                    intent.putExtra("jenisHistory", "penarikan");
                    startActivity(intent);
                }
                break;
            case R.id.linRiwayatBonus:
                if (menuDialog()) {
                    //startActivity(new Intent(RiwayatActivity.this, HistoryBonusActivity.class));
                    Intent intent = new Intent(RiwayatActivity.this, HistoryActivity.class);
                    intent.putExtra("jenisHistory", "bonus");
                    startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}