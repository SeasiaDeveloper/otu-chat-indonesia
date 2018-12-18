package com.eklanku.otuChat.ui.activities.payment.tableview.popup;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.eklanku.otuChat.ui.activities.payment.tableview.holder.CellViewHolder;
import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.eklanku.otuChat.ui.activities.payment.tableview.holder.ColumnHeaderViewHolder;

public class MyTableViewListener implements ITableViewListener {

    private ITableView mTableView;

    public MyTableViewListener(ITableView pTableView) {
        this.mTableView = pTableView;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder p_jCellView, int p_nXPosition, int p_nYPosition) {

        if (p_jCellView != null && p_jCellView instanceof CellViewHolder) {

            Log.d("OPPO-1", "onCellClicked: "+((CellViewHolder) p_jCellView).cell_textview.getText());
            // Create Long Press Popup
            CellColomnLongPressPopup popup = new CellColomnLongPressPopup(
                    (CellViewHolder) p_jCellView, mTableView);
            // Show
            popup.show();

        }

    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder p_jColumnHeaderView, int p_nXPosition) {

    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder p_jColumnHeaderView, int p_nXPosition) {

        if (p_jColumnHeaderView != null && p_jColumnHeaderView instanceof ColumnHeaderViewHolder) {

            // Create Long Press Popup
            ColumnHeaderLongPressPopup popup = new ColumnHeaderLongPressPopup(
                    (ColumnHeaderViewHolder) p_jColumnHeaderView, mTableView);

            // Show
            popup.show();

        }
    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder p_jRowHeaderView, int p_nYPosition) {

    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder p_jRowHeaderView, int p_nYPosition) {

    }

}
