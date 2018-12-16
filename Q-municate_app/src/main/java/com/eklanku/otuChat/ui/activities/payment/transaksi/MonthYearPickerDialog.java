package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.eklanku.otuChat.R;

import java.util.Calendar;
import java.util.Locale;

public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("PILIH BULAN");

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);

        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        final Button ok = dialog.findViewById(R.id.cancel);
        final Button cancel = dialog.findViewById(R.id.ok);

        String[] arr = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(arr);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        yearPicker.setMinValue(year - 1);
        yearPicker.setMaxValue(year + 1);
        yearPicker.setValue(year);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog.this.getDialog().cancel();
            }
        });

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }

}