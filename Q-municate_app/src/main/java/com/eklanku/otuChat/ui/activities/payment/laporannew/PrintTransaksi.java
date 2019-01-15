package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.btprint.btConnection;
import com.eklanku.otuChat.btprint.iObject;
import com.eklanku.otuChat.btprint.iPaperType;
import com.eklanku.otuChat.btprint.iPrint;
import com.eklanku.otuChat.btprint.iPrinters;
import com.eklanku.otuChat.btprint.iSettings;
import com.eklanku.otuChat.ui.activities.main.Utils;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class PrintTransaksi extends AppCompatActivity {

    TextView tv_tglprint, tv_keteranganprint, tv_jenisvoucher, tv_tujuan, tv_noseri, tv_harga;

    ProgressDialog progress_dialog;
    private SharedPreferences config;
    private String strNamaPrinter, strMacAddress;

    public static void start(Activity activity, String tanggal, String keterangan, String jenisvoucher,
                             String nomortujuan, String nomorseri, String harga) {
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

        config = this.getSharedPreferences("config", 0);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        strMacAddress = prefs.getString("mac_printer", "");
        strNamaPrinter = prefs.getString("nm_printer", "");

        progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminate(true);
        progress_dialog.setTitle("Memproses");
        progress_dialog.setMessage("Tunggu...");

        Button btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparePrint(tv_tglprint.getText().toString(), tv_keteranganprint.getText().toString(), tv_jenisvoucher.getText().toString(),
                        tv_tujuan.getText().toString(), tv_noseri.getText().toString(),
                        tv_harga.getText().toString());
            }
        });
    }

    private void preparePrint(String tanggal, String keterangan, String jenis, String tujuan, String noseri, String harga) {

        /*String dataForPrint = "\n" +
                "\n\n" +
                "" + tanggal.trim() + "\n" +
                "-------------------------------\n" +
                "" + keterangan + "\n" +
                "Jenis Voucher : " + jenis + "\n" +
                "No Tujuan     : " + tujuan + "\n" +
                "No Seri       : " + noseri + "\n" +
                "Harga         : Rp" + ConverterUtils.convertIDR(harga) + "\n" +
                "-------------------------------\n" +
                Utils.center("Terima Kasih dan", 32) + "\n" +
                Utils.center("Selamat Berbelanja Kembali", 32) + "\n" +
                "-------------------------------\n" +
                Utils.center("Layanan Konsumen OTU Chat", 32) + "\n" +
                Utils.center("081-13-888-286", 32) + "\n" +
                Utils.center("081-13-888-286", 32) + "\n" +
                Utils.center("customer.care@otu.co.id", 32) + "\n" +
                "\n\n";*/

        String dataForPrint = "\n" +
                "" + tanggal.trim() + "\n" +
                "-------------------------------\n" +
                "" + keterangan + "\n" +
                "Jenis Voucher : " + jenis + "\n" +
                "No Tujuan     : " + tujuan + "\n" +
                "No Seri       : " + noseri + "\n" +
                "Harga         : " + formatRupiah(Double.parseDouble(harga)) + "\n" +
                "-------------------------------\n" +
                Utils.center("Terima Kasih dan", 32) + "\n" +
                Utils.center("Selamat Berbelanja Kembali", 32) + "\n" +
                "-------------------------------\n" +
                Utils.center("Layanan Konsumen OTU Chat", 32) + "\n" +
                Utils.center("081-13-888-286", 32) + "\n" +
                Utils.center("081-13-888-286", 32) + "\n" +
                Utils.center("customer.care@otu.co.id", 32) + "\n" +
                "\n\n";

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getString("mac_printer", "") != null) {
            print(dataForPrint);
        } else {
            Toast.makeText(this, "Bluetooth printer not set, please go to setting printer menu", Toast.LENGTH_SHORT).show();
        }


    }

    public String formatRupiah(double nominal) {
        String parseRp = "";
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        parseRp = formatRupiah.format(nominal);
        return parseRp;
    }

    private void print(String dataForPrint) {

        final String message = dataForPrint;
        SharedPreferences.Editor editor = config.edit();
        editor.putString("REPRINT", message);
        editor.commit();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showAlert("ERROR", "Driver tidak ada!\nSilahkan REPRINT SLIP");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                showAlert("ERROR", "Nyalakan Bluetooth terlebih dahulu!\nKemudian silahkan REPRINT SLIP");
            } else {
                progress_dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            writePrinter(message);
                        } catch (IOException e) {
                        }
                    }
                }).start();
            }
        }
    }

    public void writePrinter(String strprint) throws IOException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String btAddr = sp.getString("mac_printer", "");
        iSettings _settings = new iSettings();
        _settings.SetPaperType(iPaperType.THERMAL);
        _settings.PrinterType(iPrinters.ZEBRA_CAMEO);
        btConnection con = new btConnection();
        con.Address(btAddr);
        if (!con.isConnected()) {
            con.Connect();
        }
        if (con.isConnected()) {
            iPrint _print = new iPrint(this);
            _print.Connection(con);
            _print.Settings(_settings);
            _print.Start(sp.getString("nm_printer", ""));
            iObject _object = new iObject();
            _object.Text(strprint);
            _print.Add(_object);
            _print.End();
            _print.Print();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            _object = null;
            _print = null;
            progressHandler.sendEmptyMessage(0);
        } else {
            if (con.ErrorMessage().length() > 0) {
                progressHandler.sendEmptyMessage(1);
            }
        }
        con.Disconnect();
        _settings = null;
        con = null;
    }

    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    progress_dialog.dismiss();
                    showDialog("Print Sukses!");
                    break;
                case 1:
                    progress_dialog.dismiss();
                    showDialogReprint("Print GAGAL\nSambungan printer belum terjalin! Coba lagi?");
                    break;
            }
        }
    };

    @SuppressWarnings("deprecation")
    private void showDialog(String Message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(Message);
        dlgAlert.setTitle("Print");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //finish();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @SuppressWarnings("deprecation")
    private void showDialogReprint(String Message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(Message);
        dlgAlert.setTitle("Print Gagal");
        dlgAlert.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                        .getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    showAlert("ERROR", "Driver tidak ada!");
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        // Bluetooth is not enable :)
                        showAlert("ERROR", "Nyalakan Bluetooth terlebih dahulu");
                    } else {
                        String strprint = config.getString("REPRINT", "");
                        final String message = strprint.toString();
                        progress_dialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    writePrinter(message);
                                } catch (IOException e) {
                                }
                            }
                        }).start();
                    }
                }
                dialog.dismiss();
            }
        });
        dlgAlert.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
