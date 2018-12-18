package com.eklanku.otuChat.ui.activities.payment.tableview.popup;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.tableview.holder.CellViewHolder;
import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.sort.SortState;

public class CellColomnLongPressPopup extends PopupMenu implements PopupMenu.OnMenuItemClickListener {

    private CellViewHolder cellViewHolder;
    private ITableView m_iTableView;
    private Context mContext;
    private int mXPosition;


    final int CUT_ID = 2211;
    final int COPY_ID = 2212;
    final int PASTE_ID = 2213;

    public CellColomnLongPressPopup(CellViewHolder p_iViewHolder, ITableView
            p_jTableView) {
        super(p_iViewHolder.itemView.getContext(), p_iViewHolder.itemView);
        this.cellViewHolder = p_iViewHolder;
        this.m_iTableView = p_jTableView;
        this.mContext = p_iViewHolder.itemView.getContext();
        this.mXPosition = cellViewHolder.getAdapterPosition();

        copyText(p_iViewHolder.itemView.getContext());
        // find the view holder
        // cellViewHolder = (CellViewHolder) m_iTableView.getCellRecyclerView().findViewHolderForAdapterPosition(mXPosition);
        initialize();
    }

    private void initialize() {
        createMenuItem();
        changeMenuItemVisibility();

        this.setOnMenuItemClickListener(this);
    }

    private void createMenuItem() {
        this.getMenu().add(Menu.NONE, COPY_ID, 0, "Copy");
    }

    private void changeMenuItemVisibility() {
        getMenu().getItem(0).setVisible(true);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        return true;
    }

    private void copyText(Context context) {
        //ClipboardManager clipboardManager = (ClipboardManager)getSystemService(context.CLIPBOARD_SERVICE);
    }

}
