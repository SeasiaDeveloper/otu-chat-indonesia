package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.btprint.btConnection;
import com.eklanku.otuChat.btprint.iObject;
import com.eklanku.otuChat.btprint.iPaperType;
import com.eklanku.otuChat.btprint.iPrint;
import com.eklanku.otuChat.btprint.iPrinters;
import com.eklanku.otuChat.btprint.iSettings;

import java.io.IOException;

public class SettingPrinterActivity extends PreferenceActivity {
    private String strImei, strDevice;
    private ProgressDialog progress_dialog;


    @SuppressWarnings({"deprecation"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        progress_dialog = new ProgressDialog(SettingPrinterActivity.this);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminate(true);
        progress_dialog.setTitle("Memproses");
        progress_dialog.setMessage("Tunggu...");
        Device imei = new Device(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        strImei = imei.telephonyManager().getDeviceId().toString();
        strDevice = Build.MODEL;

        tampilData();

        Preference tesprint = (Preference) findPreference("test_print");
        tesprint.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                        .getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    showAlert("ERROR", "Driver tidak ada!");
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        showAlert("ERROR", "Nyalakan Bluetooth terlebih dahulu");
                    } else {
                        StringBuffer str = new StringBuffer();
                        str.append("\nTest Print\n");
                        str.append("Powered by Powered by OTU Chat Indonesia\n\n");
                        final String message = str.toString();
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
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        tampilData();
    }

    @SuppressWarnings("deprecation")
    private void tampilData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        EditTextPreference ednmpritner = (EditTextPreference) findPreference("mac_printer");
        EditTextPreference listnamaprinter = (EditTextPreference) findPreference("nm_printer");
        PreferenceScreen kodeapp = (PreferenceScreen) findPreference("imei");
        PreferenceScreen device = (PreferenceScreen) findPreference("namahp");

        kodeapp.setSummary(strImei);
        device.setSummary(strDevice);
        listnamaprinter.setSummary(sp.getString("nm_printer", "NA"));
        ednmpritner.setSummary(sp.getString("mac_printer", "NA"));
    }

    @SuppressWarnings({"deprecation"})
    public void writePrinter(String strprint) throws IOException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
            iPrint _print = new iPrint(SettingPrinterActivity.this);
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
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                SettingPrinterActivity.this);
        dlgAlert.setMessage(Message);
        dlgAlert.setTitle("Print");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @SuppressWarnings("deprecation")
    private void showDialogReprint(String Message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                SettingPrinterActivity.this);
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
                        StringBuffer str = new StringBuffer();
                        str.append("Test Print \n");
                        str.append("Powered by OTU Chat Indonesia\n\n");
                        final String message = str.toString();
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
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    @SuppressWarnings("deprecation")
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(SettingPrinterActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
                .setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
