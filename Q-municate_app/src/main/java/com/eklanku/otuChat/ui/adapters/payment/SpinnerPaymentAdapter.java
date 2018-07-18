package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.eklanku.otuChat.R;;

public class SpinnerPaymentAdapter extends BaseAdapter {
    Context context;
    String num[];
    String[] denom;
    String[] nominal;
    String[] point;
    LayoutInflater inflter;

    public SpinnerPaymentAdapter(Context applicationContext, String[] denom, String[] nominal, String[] point) {
        this.context = applicationContext;
        this.denom = denom;
        this.nominal = nominal;
        this.point = point;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return denom.length;
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
        convertView = inflter.inflate(R.layout.item_product_list, null);
        TextView text_denom = (TextView) convertView.findViewById(R.id.denom);
        TextView text_nominal = (TextView) convertView.findViewById(R.id.nominal);
        TextView text_point = (TextView) convertView.findViewById(R.id.point);
        text_denom.setText(denom[position]);
        text_nominal.setText(nominal[position]);
        text_point.setText(point[position]);
        return convertView;
    }
}
