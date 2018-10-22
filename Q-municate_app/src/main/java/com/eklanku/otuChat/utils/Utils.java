package com.eklanku.otuChat.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.transaksi.PaymentLogin;

public class Utils {

    Context context;
    Button btn_ok;
    TextView txjdlError, txpesanError;

    public Utils(Context context) {
        this.context = context;
    }

    public void globalDialog(Activity activity, String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_global_dialog);
        dialog.setCancelable(false);

        btn_ok = dialog.findViewById(R.id.btn_ok_error);
        txjdlError = dialog.findViewById(R.id.tx_judul_error);
        txpesanError = dialog.findViewById(R.id.tx_pesan_error);

        txjdlError.setText(title);
        txpesanError.setText(message);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (message.equalsIgnoreCase("SERVER BUSY, PLEASE TRY AGAIN LATER")) {
                    activity.startActivity(new Intent(context, PaymentLogin.class));
                    PreferenceUtil.setLoginStatus(context, false);
                }

                activity.finish();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                dialog.dismiss();
                if (message.equalsIgnoreCase("SERVER BUSY, PLEASE TRY AGAIN LATER")) {
                    activity.startActivity(new Intent(context, PaymentLogin.class));
                    PreferenceUtil.setLoginStatus(context, false);
                    activity.finish();
                }
            }
        });
        /*builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();*/
    }
}
