package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.eklanku.otuChat.R;;

public class ListViewAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<String> denom;
    ArrayList<String> nominal;
    LayoutInflater inflter;

    public ListViewAdapter(Context mContext, ArrayList<String> denom, ArrayList<String> nominal) {
        this.mContext = mContext;
        this.denom = denom;
        this.nominal = nominal;
        inflter = (LayoutInflater.from(mContext));
    }

    @Override
    public int getCount() {
        return denom.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.item_topup_list, null);
        TextView text_denom = (TextView) convertView.findViewById(R.id.denom);
        TextView text_nominal = (TextView) convertView.findViewById(R.id.nominal);
        text_denom.setText(denom.get(position));
        text_nominal.setText(nominal.get(position));
        return convertView;
    }
}
