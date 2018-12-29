package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eklanku.otuChat.R;

import java.util.ArrayList;

public class ListHistoryAdapter extends BaseAdapter {

    ArrayList<String> kode;
    ArrayList<String> tanggal;
    ArrayList<String> jenistransaksi;
    ArrayList<String> nominal;
    ArrayList<String> status;
    private Context mContext;
    LayoutInflater inflter;

    public ListHistoryAdapter(Context mContext, ArrayList<String> _kode, ArrayList<String>
            _tanggal, ArrayList<String> _jenistransaksi, ArrayList<String> _nominal, ArrayList<String> _status) {

        this.mContext = mContext;
        this.kode = _kode;
        this.tanggal = _tanggal;
        this.jenistransaksi = _jenistransaksi;
        this.nominal = _nominal;
        this.status = _status;
        inflter = (LayoutInflater.from(mContext));
    }

    @Override
    public int getCount() {
        return kode.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = inflter.inflate(R.layout.item_history_list, null);
        TextView kodeTransaksi = convertView.findViewById(R.id.tvKode);
        TextView tgl = convertView.findViewById(R.id.tvTanggal);
        TextView jns = convertView.findViewById(R.id.tvJnsTransaksi);
        TextView nom = convertView.findViewById(R.id.tvNominal);
        TextView stat = convertView.findViewById(R.id.tvstatus);

        kodeTransaksi.setText(kode.get(position));
        tgl.setText(tanggal.get(position));
        jns.setText(jenistransaksi.get(position));
        nom.setText(nominal.get(position));
        stat.setText(status.get(position));

        return convertView;
    }
}
