package com.eklanku.otuChat.ui.activities.payment.transaksi;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.eklanku.otuChat.R;

public class AlertDialog extends Dialog {

    public AlertDialog(@NonNull Context context, String txt_error) {
        super(context);
        this.setContentView(R.layout.activity_alert_error);

        TextView txtError = (TextView) findViewById(R.id.txt_error);
        txtError.setText(txt_error);
    }

    public AlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }


}
