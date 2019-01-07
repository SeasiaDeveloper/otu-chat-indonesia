package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PrintTransaksi extends AppCompatActivity {


    public static void start(Activity activity){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://demo.eklanku.com/invoice/GenerateInvoice/gen_pdf_dynamic?trxID=1113221"));
        activity.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
