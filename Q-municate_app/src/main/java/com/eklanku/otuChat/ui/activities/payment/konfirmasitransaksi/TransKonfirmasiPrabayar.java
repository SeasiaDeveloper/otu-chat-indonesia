package com.eklanku.otuChat.ui.activities.payment.konfirmasitransaksi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.laporannew.PrintTransaksi;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransESaldo_product;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransEtool;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketData;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketTelp;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPulsa;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransSMS;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransVouchergame_product;

import java.text.NumberFormat;
import java.util.Locale;

public class TransKonfirmasiPrabayar extends AppCompatActivity {

    TextView tvKeterangan, tvReffId, tvNohp, tvTanggal, tvHarga, tvAdminBank, tvTotal, tvKeteranganTrans, tvPoint;
    ImageView imgKonfirmasi, btnprint, btnbuy;
    Button btnok;
    Bundle extras;
    String jenisvoucher, oprpulsa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_konfirmasi_prabayar);
        extras = getIntent().getExtras();

        tvKeterangan = findViewById(R.id.txtketerangan);
        tvReffId = findViewById(R.id.txt_billing_reff_id);
        tvNohp = findViewById(R.id.txt_nomor_handphone);
        tvTanggal = findViewById(R.id.txt_tanggal);
        tvHarga = findViewById(R.id.txt_harga);
        tvAdminBank = findViewById(R.id.txt_admin_bank);
        tvTotal = findViewById(R.id.txt_total);
        tvKeteranganTrans = findViewById(R.id.txt_keterangantrans);
        tvPoint = findViewById(R.id.txt_point);
        imgKonfirmasi = findViewById(R.id.imgkonfirmasi);

        tvKeterangan.setText(extras.getString("productCode"));
        tvReffId.setText(extras.getString("billingReferenceID"));
        tvNohp.setText(extras.getString("customerMSISDN"));
        tvTanggal.setText(formatTgl(extras.getString("respTime")));
        tvHarga.setText(formatRupiah(Double.parseDouble(extras.getString("billing"))));
        tvAdminBank.setText(formatRupiah(Double.parseDouble(extras.getString("adminBank"))));
        tvTotal.setText(formatRupiah(Double.parseDouble(extras.getString("billing"))));
        tvKeteranganTrans.setText(extras.getString("respMessage"));
        tvPoint.setText(extras.getString("ep"));
        jenisvoucher = extras.getString("jenisvoucher");
        oprpulsa = extras.getString("oprPulsa");

        int id = TransKonfirmasiPrabayar.this.getResources().getIdentifier("mipmap/" + oprpulsa.toLowerCase(), null, TransKonfirmasiPrabayar.this.getPackageName());
        imgKonfirmasi.setImageResource(id);

        btnok = findViewById(R.id.btnOK);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnprint = findViewById(R.id.btn_print);
        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ket = "Slip Pembelian "
                        + extras.getString("productCode").substring(0, 1).toUpperCase()
                        + extras.getString("productCode").substring(1).toLowerCase() + " "
                        + oprpulsa.substring(0, 1).toUpperCase()
                        + oprpulsa.substring(1).toLowerCase();
                PrintTransaksi.start(TransKonfirmasiPrabayar.this, formatTgl(extras.getString("respTime")), ket, jenisvoucher,
                        extras.getString("customerMSISDN"), "-", formatRupiah(Double.parseDouble(extras.getString("billing"))));

            }
        });

        btnbuy = findViewById(R.id.btn_buy);
        btnbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy(extras.getString("productCode"), "0", extras.getString("customerMSISDN"), oprpulsa);
            }
        });
    }

    public void buy(String trxJenis, String trxNominal, String trxTujuan, String trxProvideName) {
        Intent i = null;

        if (trxJenis.equalsIgnoreCase("PULSA")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransPulsa.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            TransKonfirmasiPrabayar.this.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("KUOTA PAKET DATA")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransPaketData.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            TransKonfirmasiPrabayar.this.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("ETOOL")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransEtool.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            TransKonfirmasiPrabayar.this.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("VOUCHER GAME")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransVouchergame_product.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            i.putExtra("jenis", trxProvideName);
            TransKonfirmasiPrabayar.this.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("TELPONS")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransPaketTelp.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            TransKonfirmasiPrabayar.this.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("KUOTA SMS")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransSMS.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            TransKonfirmasiPrabayar.this.startActivity(i);
        }else if (trxJenis.equalsIgnoreCase("ESALDO")) {
            i = new Intent(TransKonfirmasiPrabayar.this, TransESaldo_product.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            i.putExtra("jenis", trxProvideName);
            TransKonfirmasiPrabayar.this.startActivity(i);
        }

        /*else if (trxJenis.equalsIgnoreCase("PPOB PDAM")) {
            i = new Intent(context, TransPdam.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
            //============================================================
        } else if (trxJenis.equalsIgnoreCase("PPOB TELKOM")) {
            i = new Intent(context, TransTelkom.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB PLN")) {
            i = new Intent(context, TransPln.class);
            i.putExtra("nominal", "ppob");
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PLNTOKEN")) {
            i = new Intent(context, TransPln.class);
            i.putExtra("nominal", "token");
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("BPJSKES")) {
            i = new Intent(context, TransBpjs.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB TV KABEL")) {
            i = new Intent(context, TransTv.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("WIFI ID")) {
            i = new Intent(context, TransWi.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("OJEK ONLINE")) {
            i = new Intent(context, TransESaldo_product.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            i.putExtra("jenis", trxProvideName);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB TELCO PASCA")) {
            i = new Intent(context, TransTagihan.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } */
        else {
            Toast.makeText(TransKonfirmasiPrabayar.this, "Coming soon...", Toast.LENGTH_SHORT).show();
        }
    }

    public String formatTgl(String tgl) {
        String format = "";
        if (!tgl.equals("") || !tgl.equals("null")) {
            String parsTgl[] = tgl.split(" ");
            String parsTgl2[] = parsTgl[0].split("-");

            String tanggal = parsTgl2[2];
            String bulan = parsTgl2[1];
            String tahun = parsTgl2[0];

            format = tanggal + "-" + bulan + "-" + tahun + " " + parsTgl[1];
        } else {
            format = "";
        }

        return format;
    }

    public String formatRupiah(double nominal) {
        String parseRp = "";
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        parseRp = formatRupiah.format(nominal);
        return parseRp;
    }

}
