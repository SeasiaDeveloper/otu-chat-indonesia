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
    ArrayList<String> point;
    LayoutInflater inflter;

    public ListViewAdapter(Context mContext, ArrayList<String> denom, ArrayList<String> nominal, ArrayList<String> point) {
        this.mContext = mContext;
        this.denom = denom;
        this.nominal = nominal;
        this.point = point;
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
        convertView = inflter.inflate(R.layout.item_product_list, null);
        TextView text_denom = (TextView) convertView.findViewById(R.id.denom);
        TextView text_nominal = (TextView) convertView.findViewById(R.id.nominal);
        TextView text_point = (TextView)  convertView.findViewById(R.id.point);

        text_denom.setText(denom.get(position));
        text_nominal.setText(nominal.get(position));
        text_point.setText(point.get(position));
        return convertView;
    }
}
