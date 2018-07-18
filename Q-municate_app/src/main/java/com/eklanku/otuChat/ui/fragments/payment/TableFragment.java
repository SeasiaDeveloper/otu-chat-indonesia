package com.eklanku.otuChat.ui.fragments.payment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.eklanku.otuChat.ui.adapters.payment.MyTableAdapter;
import com.evrencoskun.tableview.TableView;

import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.json.WebServiceHandler;
import com.eklanku.otuChat.ui.activities.payment.tableview.model.CellModel;
import com.eklanku.otuChat.ui.activities.payment.tableview.model.ColumnHeaderModel;
import com.eklanku.otuChat.ui.activities.payment.tableview.model.RowHeaderModel;
import com.eklanku.otuChat.ui.activities.payment.tableview.popup.MyTableViewListener;
import com.eklanku.otuChat.ui.adapters.payment.MyTableAdapter;

@SuppressLint("ValidFragment")
public class TableFragment extends Fragment {

    private static final String LOG_TAG = TableFragment.class.getSimpleName();

    private TableView mTableView;
    private MyTableAdapter mTableAdapter;

    private ProgressDialog mProgressDialog;
    private WebServiceHandler mWebServiceHandler;

    // For TableView
    private List<List<CellModel>> mCellList;
    private List<ColumnHeaderModel> mColumnHeaderList;
    private List<RowHeaderModel> mRowHeaderList;

    private String TableName;

    public String strUserID, strAccessToken;

    public TableFragment(String strUserID, String strAccessToken) {
        // Required empty public constructor\
        this.strUserID = strUserID;
        this.strAccessToken = strAccessToken;

        Log.d("OPPO-1", "TableFragment===================strUserID: "+strUserID+"/"+strAccessToken);
    }

    public String getTableName(){
        return this.TableName;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        this.TableName=bundle.getString("laporan");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.tableview_fragment, container, false);

        mTableView = (TableView) view.findViewById(R.id.my_TableView);

        // Create TableView Adapter
        mTableAdapter = new MyTableAdapter(getContext());
        mTableView.setAdapter(mTableAdapter);

        // Create listener
        mTableView.setTableViewListener(new MyTableViewListener(mTableView));

        // UserInfo data will be getting from a web server.
        mWebServiceHandler = new WebServiceHandler(this);
        mWebServiceHandler.loadData();

        return view;
    }

    public void populatedTableView(List<List<CellModel>> userInfoList) {
        // create Models
        mColumnHeaderList = createColumnHeaderModelList();
        mCellList = userInfoList;
        mRowHeaderList = createRowHeaderList();


        // Set all items to the TableView
        mTableAdapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
    }


    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        if(getTableName().contains("balance")) {
            list.add(new ColumnHeaderModel("Tgl"));
            list.add(new ColumnHeaderModel("Kode"));
            list.add(new ColumnHeaderModel("Kredit"));
            list.add(new ColumnHeaderModel("Debet"));
            list.add(new ColumnHeaderModel("Sisa"));
            list.add(new ColumnHeaderModel("Status"));
            list.add(new ColumnHeaderModel("Ket"));
        }else if(getTableName().contains("trx")){
            //invoice	tgl	vstatus	harga	tujuan	keterangan	vsn	mbr_name	tgl_sukses
            list.add(new ColumnHeaderModel("Tgl"));
            list.add(new ColumnHeaderModel("tujuan"));
            list.add(new ColumnHeaderModel("harga"));
            list.add(new ColumnHeaderModel("status"));
            list.add(new ColumnHeaderModel("ket"));
        }else if(getTableName().contains("deposit")){
            list.add(new ColumnHeaderModel("Tgl"));
            list.add(new ColumnHeaderModel("Bank"));
            list.add(new ColumnHeaderModel("Deposit"));
            list.add(new ColumnHeaderModel("Kode"));
            list.add(new ColumnHeaderModel("Status"));
        }else if(getTableName().contains("penarikan")){
            list.add(new ColumnHeaderModel("Tgl"));
            list.add(new ColumnHeaderModel("Bank"));
            list.add(new ColumnHeaderModel("Jumlah"));
            list.add(new ColumnHeaderModel("Nama"));
            list.add(new ColumnHeaderModel("Nomor Rek."));
        }else if(getTableName().contains("bonus")){
            list.add(new ColumnHeaderModel("Tgl"));
            list.add(new ColumnHeaderModel("Keterangan"));
            list.add(new ColumnHeaderModel("Jenis Bonus"));
            list.add(new ColumnHeaderModel("Status Bonus"));
        }

        return list;
    }

    private List<RowHeaderModel> createRowHeaderList() {
        List<RowHeaderModel> list = new ArrayList<>();
        for (int i = 0; i < mCellList.size(); i++) {
            // In this example, Row headers just shows the index of the TableView List.
            list.add(new RowHeaderModel(String.valueOf(i + 1)));
        }
        return list;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Get data, please wait...");
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {

        if ((mProgressDialog != null) && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }
}
