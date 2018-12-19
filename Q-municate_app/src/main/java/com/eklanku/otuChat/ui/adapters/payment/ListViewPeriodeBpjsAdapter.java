package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eklanku.otuChat.R;

import java.util.ArrayList;

public class ListViewPeriodeBpjsAdapter extends BaseAdapter {

    ArrayList<String> _periode;
    String _keterangan;
    Context mContext;
    LayoutInflater inflter;

    public ListViewPeriodeBpjsAdapter(Context context, ArrayList<String> periode, String keterangan){
        this.mContext = context;
        this._periode = periode;
        this._keterangan = keterangan;
        inflter = (LayoutInflater.from(mContext));
    }
    @Override
    public int getCount() {
        return _periode.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.item_periode_bpjs, null);
        TextView txt_periode = (TextView) convertView.findViewById(R.id.tvperiode);
        TextView txt_keterangan = (TextView) convertView.findViewById(R.id.tvketerangan);

        txt_periode.setText(_periode.get(position));
        txt_keterangan.setText(_keterangan);
        return convertView;
    }
}
