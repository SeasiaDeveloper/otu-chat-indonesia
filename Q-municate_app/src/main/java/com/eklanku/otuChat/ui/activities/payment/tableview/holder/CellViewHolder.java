package com.eklanku.otuChat.ui.activities.payment.tableview.holder;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.payment.tableview.model.CellModel;
import com.eklanku.otuChat.R;;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.eklanku.otuChat.ui.activities.payment.tableview.model.CellModel;
import com.google.android.gms.vision.text.Line;

/**
 * Created by evrencoskun on 1.12.2017.
 */

public class CellViewHolder extends AbstractViewHolder {
    public final TextView cell_textview;
    public final LinearLayout cell_container;


    public CellViewHolder(View itemView) {
        super(itemView);
        cell_textview = itemView.findViewById(R.id.cell_data);
        cell_container = itemView.findViewById(R.id.cell_container);


    }

    public void setCellModel(CellModel p_jModel, int pColumnPosition) {

        // Change textView align by column
        cell_textview.setGravity(ColumnHeaderViewHolder.COLUMN_TEXT_ALIGNS[pColumnPosition] |
                Gravity.LEFT);


        // Set text
        cell_textview.setText(String.valueOf(p_jModel.getData()));

        // It is necessary to remeasure itself.
        cell_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        cell_textview.requestLayout();


    }

    @Override
    public void setSelected(SelectionState p_nSelectionState) {
        super.setSelected(p_nSelectionState);
        if (p_nSelectionState == SelectionState.SELECTED) {
            cell_textview.setTextColor(ContextCompat.getColor(cell_textview.getContext(), R.color.white));
        } else {
            cell_textview.setTextColor(ContextCompat.getColor(cell_textview.getContext(), R.color
                    .unselected_text_color));
        }
    }

    String text = "";

    public String getDataOnTable() {
        cell_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = cell_textview.getText().toString();
                Log.d("OPPO-1", "onClick 1 : " + cell_textview.getText());

            }
        });
        return text;
    }


    public LinearLayout getCell_container() {
        return cell_container;
    }

}
