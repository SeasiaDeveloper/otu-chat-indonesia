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

public class PrintTransaksiFinanceMAF extends AppCompatActivity {

    TextView _tvtglprint, _tvKeteranganPrint, _tvInvoice, _tvTanggaltrx, _tvNoResi, _tvNamaKredit, _tvCabang, _tvNokontrak,
            _tvNama, _tvAngke, _tvJthtempo, _tvTagihan, _tvTotalTagihan, _tvTerbilang, _tvFooter, _;


    ProgressDialog progress_dialog;
    private SharedPreferences config;
    private String strNamaPrinter, strMacAddress;
    String jnstrx;

    public static void startFinanceMAF(Activity activity, String jnstrx, String judul, String tglprint, String invoice, String tgltrx, String noresi, String namakredit,
                                       String cabang, String nokontrak, String nama, String angke, String jthtempo, String tagihan, String totaltagihan, String terbilang, String footer) {

        Intent i = new Intent(activity, PrintTransaksiFinanceMAF.class);
        i.putExtra("jnstrx", jnstrx);
        i.putExtra("judul", judul);
        i.putExtra("tglprint", tglprint);
        i.putExtra("invoice", invoice);
        i.putExtra("tgltrx", tgltrx);
        i.putExtra("noresi", noresi);
        i.putExtra("namakredit", namakredit);
        i.putExtra("cabang", cabang);
        i.putExtra("nokontrak", nokontrak);
        i.putExtra("nama", nama);
        i.putExtra("angke", angke);
        i.putExtra("jthtempo", jthtempo);
        i.putExtra("tagihan", tagihan);
        i.putExtra("totaltagihan", totaltagihan);
        i.putExtra("terbilang", terbilang);
        i.putExtra("footer", footer);
        activity.startActivity(i);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_transaksi_finance_maf);

        jnstrx = getIntent().getStringExtra("jenistrx");

        _tvtglprint = findViewById(R.id.tvtglprint);
        _tvKeteranganPrint = findViewById(R.id.tvKeteranganPrint);
        _tvInvoice = findViewById(R.id.tvInvoice);
        _tvTanggaltrx = findViewById(R.id.tvTanggaltrx);
        _tvNoResi = findViewById(R.id.tvNoResi);
        _tvNamaKredit = findViewById(R.id.tvNamaKredit);
        _tvCabang = findViewById(R.id.tvCabang);
        _tvNokontrak = findViewById(R.id.tvNokontrak);
        _tvNama = findViewById(R.id.tvNama);
        _tvAngke = findViewById(R.id.tvAngke);
        _tvJthtempo = findViewById(R.id.tvJthtempo);
        _tvTagihan = findViewById(R.id.tvTagihan);
        _tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        _tvTerbilang = findViewById(R.id.tvTerbilang);
        _tvFooter  = findViewById(R.id.tvFooter);

        _tvtglprint.setText(getIntent().getStringExtra("tglprint"));
        _tvKeteranganPrint.setText(getIntent().getStringExtra("judul"));
        _tvInvoice.setText(getIntent().getStringExtra("invoice"));
        _tvTanggaltrx.setText(getIntent().getStringExtra("tgltrx"));
        _tvNoResi.setText(getIntent().getStringExtra("noresi"));
        _tvNamaKredit.setText(getIntent().getStringExtra("namakredit"));
        _tvCabang.setText(getIntent().getStringExtra("cabang"));
        _tvNokontrak.setText(getIntent().getStringExtra("nokontrak"));
        _tvNama.setText(getIntent().getStringExtra("nama"));
        _tvAngke.setText(getIntent().getStringExtra("angke"));
        _tvJthtempo.setText(getIntent().getStringExtra("jthtempo"));
        _tvTagihan.setText(getIntent().getStringExtra("tagihan"));
        _tvTotalTagihan.setText(getIntent().getStringExtra("totaltagihan"));
        _tvTerbilang.setText(getIntent().getStringExtra("terbilang"));
        _tvFooter.setText(getIntent().getStringExtra("footer"));


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

                strukMAF(getIntent().getStringExtra("judul").toString(),
                        getIntent().getStringExtra("tglprint").toString(),
                        getIntent().getStringExtra("invoice").toString(),
                        getIntent().getStringExtra("tgltrx").toString(),
                        getIntent().getStringExtra("noresi").toString(),
                        getIntent().getStringExtra("namakredit").toString(),
                        getIntent().getStringExtra("cabang").toString(),
                        getIntent().getStringExtra("nokontrak").toString(),
                        getIntent().getStringExtra("nama").toString(),
                        getIntent().getStringExtra("angke").toString(),
                        getIntent().getStringExtra("jthtempo").toString(),
                        getIntent().getStringExtra("tagihan").toString(),
                        getIntent().getStringExtra("totaltagihan").toString(),
                        getIntent().getStringExtra("terbilang").toString(),
                        getIntent().getStringExtra("footer").toString()

                );
            }

        });
    }

    public void strukMAF(String judul, String tglprint, String invoice, String tgltrx, String noresi, String namakredit,
                             String cabang, String nokontrak, String nama, String angke, String jthtempo, String tagihan, String totaltagihan, String terbilang, String footer) {
        Toast.makeText(this, "Printing", Toast.LENGTH_SHORT).show();
        String dataForPrint = 
                "" + tglprint.trim() + "\n" +
                "-------------------------------\n" +
                "" + judul + "\n\n" +
                invoice + "\n\n" +
                "Tanggal   : " + tgltrx + "\n" +
                "Resi      : " + noresi + "\n" +
                "Nm kredit : " + namakredit + "\n" +
                "Cabang    : " + cabang + "\n" +
                "No Kontrak: " + nokontrak + "\n" +
                "Nama      : " + nama + "\n" +
                "Ang ke    : " + angke + "\n" +
                "Jth Tempo : " + jthtempo + "\n" +
                "Tagihan   : " + formatRupiah(Double.valueOf(tagihan)) + "\n" +
                "-------------------------------\n" +
                "Total     : " + formatRupiah(Double.valueOf(totaltagihan)) + "\n" +
                Utils.center("(" + terbilang + ")", 32) + "\n\n" +
                Utils.center(footer, 32) + "\n" +
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
