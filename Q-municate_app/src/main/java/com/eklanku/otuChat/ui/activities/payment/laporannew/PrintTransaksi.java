package com.eklanku.otuChat.ui.activities.payment.laporannew;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.eklanku.otuChat.R;

public class PrintTransaksi extends AppCompatActivity {

    TextView tv_tglprint, tv_keteranganprint, tv_jenisvoucher, tv_tujuan, tv_noseri, tv_harga;

    public static void start(Activity activity, String tanggal, String keterangan, String jenisvoucher,
                             String nomortujuan, String nomorseri, String harga){
        Intent i = new Intent(activity, PrintTransaksi.class);
        i.putExtra("tanggal", tanggal);
        i.putExtra("keterangan", keterangan);
        i.putExtra("jenisvoucher", jenisvoucher);
        i.putExtra("nomortujuan", nomortujuan);
        i.putExtra("nomorseri", nomorseri);
        i.putExtra("harga", harga);
        activity.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_transaksi);
        tv_tglprint = findViewById(R.id.tvtglprint);
        tv_keteranganprint = findViewById(R.id.tvKeteranganPrint);
        tv_jenisvoucher = findViewById(R.id.tvJenisVoucher);
        tv_tujuan = findViewById(R.id.tvTujuan);
        tv_noseri = findViewById(R.id.tvNomorSeri);
        tv_harga = findViewById(R.id.tvHarga);

        tv_tglprint.setText(getIntent().getStringExtra("tanggal"));
        tv_keteranganprint.setText(getIntent().getStringExtra("keterangan"));
        tv_jenisvoucher.setText(getIntent().getStringExtra("jenisvoucher"));
        tv_tujuan.setText(getIntent().getStringExtra("nomortujuan"));
        tv_noseri.setText(getIntent().getStringExtra("nomorseri"));
        tv_harga.setText(getIntent().getStringExtra("harga"));
    }
}
