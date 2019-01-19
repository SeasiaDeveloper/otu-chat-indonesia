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
import android.util.Log;
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

public class PrintTransaksiBPJS extends AppCompatActivity {

    TextView _tvInvoice, _tvTanggalBPJS, _tvNoResi, _tvNoPelanggan, _tvNama, _tvJmlPeserta, _tvTelp, _tvNoref,
            _tvJmlPremi, _tvTagihan, _tvBiayaAdmin, _tvTotalTagihan, _tvTerbilang, _tvFooter, _tvtglprint, _tvKeteranganPrint;


    ProgressDialog progress_dialog;
    private SharedPreferences config;
    private String strNamaPrinter, strMacAddress;
    String jnstrx;

    public static void startBPJSKes(Activity activity, String jnstrx, String invoice, String tanggalcetak, String judul, String tanggalTransaksi, String nomorResi,
                                    String nomorPelanggan, String namaPeserta, String jumlahPeserta, String nomorTelepon, String nomorReferensi,
                                    String jumlahPremi, String jumlahTagihan, String biayaAdmin, String totalTagihan, String terbilang, String footer1) {

        Intent i = new Intent(activity, PrintTransaksiBPJS.class);
        i.putExtra("jenistrx", jnstrx);
        i.putExtra("invoice", invoice);
        i.putExtra("tanggalcetak", tanggalcetak);
        i.putExtra("judul", judul);
        i.putExtra("tanggalTransaksi", tanggalTransaksi);
        i.putExtra("nomorResi", nomorResi);
        i.putExtra("nomorPelanggan", nomorPelanggan);
        i.putExtra("namaPeserta", namaPeserta);
        i.putExtra("jumlahPeserta", jumlahPeserta);
        i.putExtra("nomorTelepon", nomorTelepon);
        i.putExtra("nomorReferensi", nomorReferensi);
        i.putExtra("jumlahPremi", jumlahPremi);
        i.putExtra("jumlahTagihan", jumlahTagihan);
        i.putExtra("biayaAdmin", biayaAdmin);
        i.putExtra("totalTagihan", totalTagihan);
        i.putExtra("terbilang", terbilang);
        i.putExtra("footer1", footer1);
        activity.startActivity(i);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_transaksi_bpjs);

        jnstrx = getIntent().getStringExtra("jenistrx");

        _tvInvoice = findViewById(R.id.tvInvoice);
        _tvTanggalBPJS = findViewById(R.id.tvTanggalBPJS);
        _tvNoResi = findViewById(R.id.tvNoResi);
        _tvNoPelanggan = findViewById(R.id.tvNoPelanggan);
        _tvNama = findViewById(R.id.tvNama);
        _tvJmlPeserta = findViewById(R.id.tvJmlPeserta);
        _tvTelp = findViewById(R.id.tvTelp);
        _tvNoref = findViewById(R.id.tvNoref);
        _tvJmlPremi = findViewById(R.id.tvJmlPremi);
        _tvTagihan = findViewById(R.id.tvTagihan);
        _tvBiayaAdmin = findViewById(R.id.tvBiayaAdmin);
        _tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        _tvTerbilang = findViewById(R.id.tvTerbilang);
        _tvFooter = findViewById(R.id.tvFooter);
        _tvtglprint = findViewById(R.id.tvtglprint);
        _tvKeteranganPrint = findViewById(R.id.tvKeteranganPrint);

        _tvInvoice.setText(getIntent().getStringExtra("invoice"));
        _tvTanggalBPJS.setText(getIntent().getStringExtra("tanggalTransaksi"));
        _tvNoResi.setText(getIntent().getStringExtra("nomorResi"));
        _tvNoPelanggan.setText(getIntent().getStringExtra("nomorPelanggan"));
        _tvNama.setText(getIntent().getStringExtra("namaPeserta"));
        _tvJmlPeserta.setText(getIntent().getStringExtra("jumlahPeserta"));
        _tvTelp.setText(getIntent().getStringExtra("nomorTelepon"));
        _tvNoref.setText(getIntent().getStringExtra("nomorReferensi"));
        _tvJmlPremi.setText(getIntent().getStringExtra("jumlahPremi"));
        _tvTagihan.setText(getIntent().getStringExtra("jumlahTagihan"));
        _tvBiayaAdmin.setText(getIntent().getStringExtra("biayaAdmin"));
        _tvTotalTagihan.setText(getIntent().getStringExtra("totalTagihan"));
        _tvTerbilang.setText(getIntent().getStringExtra("terbilang"));
        _tvFooter.setText(getIntent().getStringExtra("footer1"));
        _tvtglprint.setText(getIntent().getStringExtra("tanggalcetak"));
        _tvKeteranganPrint.setText(getIntent().getStringExtra("judul"));


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

                strukBPJSKes(getIntent().getStringExtra("invoice").toString(),
                        getIntent().getStringExtra("tanggalcetak").toString(),
                        getIntent().getStringExtra("judul").toString(),
                        getIntent().getStringExtra("tanggalTransaksi").toString(),
                        getIntent().getStringExtra("nomorResi").toString(),
                        getIntent().getStringExtra("nomorPelanggan").toString(),
                        getIntent().getStringExtra("namaPeserta").toString(),
                        getIntent().getStringExtra("jumlahPeserta").toString(),
                        getIntent().getStringExtra("nomorTelepon").toString(),
                        getIntent().getStringExtra("nomorReferensi").toString(),
                        getIntent().getStringExtra("jumlahPremi").toString(),
                        getIntent().getStringExtra("jumlahTagihan").toString(),
                        getIntent().getStringExtra("biayaAdmin").toString(),
                        getIntent().getStringExtra("totalTagihan").toString(),
                        getIntent().getStringExtra("terbilang").toString(),
                        getIntent().getStringExtra("footer1").toString()
                );
            }

        });
    }

    public void strukBPJSKes(String invoice, String tanggalcetak, String judul, String tanggalTransaksi, String nomorResi,
                             String nomorPelanggan, String namaPeserta, String jumlahPeserta, String nomorTelepon, String nomorReferensi,
                             String jumlahPremi, String jumlahTagihan, String biayaAdmin, String totalTagihan, String terbilang, String footer1) {
        Toast.makeText(this, "Printing", Toast.LENGTH_SHORT).show();
        String dataForPrint =
                "" + tanggalcetak.trim() + "\n" +
                "-------------------------------\n" +
                "" + judul + "\n\n" +
                invoice + "\n\n" +
                "Tanggal   : " + tanggalTransaksi + "\n" +
                "Resi      : " + nomorResi + "\n" +
                "IdPel     : " + nomorPelanggan + "\n" +
                "Nama      : " + namaPeserta + "\n" +
                "Peserta   : " + jumlahPeserta + "\n" +
                "Telepon   : " + nomorTelepon + "\n" +
                "No Ref    : " + nomorReferensi + "\n" +
                "Jml Premi : " + jumlahPremi + "\n" +
                "Tagihan   : " + formatRupiah(Double.valueOf(jumlahTagihan)) + "\n" +
                "Admin     : " + formatRupiah(Double.valueOf(biayaAdmin)) + "\n" +
                "-------------------------------\n" +
                "Total     : " + formatRupiah(Double.valueOf(totalTagihan)) + "\n" +
                Utils.center("(" + terbilang + ")", 32) + "\n\n" +
                Utils.center(footer1, 32) + "\n" +
                "-------------------------------\n" +
                Utils.center("Terima Kasih dan", 32) + "" +
                Utils.center("Selamat Berbelanja Kembali", 32) + "" +
                "-------------------------------\n" +
                Utils.center("Layanan Konsumen OTU Chat", 32) + "" +
                Utils.center("081-13-888-286", 32) + "" +
                Utils.center("081-13-888-286", 32) + "" +
                Utils.center("customer.care@otu.co.id", 32);


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
        String btAddr;
        /*if (sp.getString("mac_printer", "") == null || sp.getString("mac_printer", "").equals("")) {
            Toast.makeText(this, "Mac address printer kosong", Toast.LENGTH_SHORT).show();
        }*/
        btAddr = sp.getString("mac_printer", "");
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
            _object.FontSize(5);
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
                finish();
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
