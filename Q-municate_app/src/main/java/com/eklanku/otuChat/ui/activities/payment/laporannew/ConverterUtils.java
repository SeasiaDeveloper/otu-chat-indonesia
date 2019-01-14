package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ConverterUtils {

    public static String convertIDR(String jumlah) {
        double price = Double.parseDouble(jumlah);

        Locale localeID = new Locale("in", "ID");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeID);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        String pattern = "Rp #,##0.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);

        return String.valueOf(decimalFormat.format(price));
    }

    public static String convertText(int maxChar, String dataText) {
        int dataTextSize = dataText.length();
        int addSpace = maxChar - dataTextSize;

        String text1 = dataText;

        for (int i = 0; i < addSpace; i++) {
            text1 += " ";
        }

        Log.e("ConverterUtils", "dataText size : " + dataTextSize);
        Log.e("ConverterUtils", "text1 size    : " + text1.length());
        return text1;
    }

}
