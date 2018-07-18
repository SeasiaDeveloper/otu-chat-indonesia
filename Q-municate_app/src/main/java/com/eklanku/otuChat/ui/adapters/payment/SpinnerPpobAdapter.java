package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eklanku.otuChat.R;;

public class SpinnerPpobAdapter extends BaseAdapter {

    Context context;
    String[] MultiPayment;
    LayoutInflater inflter;

    public SpinnerPpobAdapter(Context applicationContext, String[] MultiPayment) {
        this.context = applicationContext;
        this.MultiPayment = MultiPayment;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return MultiPayment.length;
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
        convertView = inflter.inflate(R.layout.item_product_list_multipayment, null);
        TextView text_multipayment = (TextView) convertView.findViewById(R.id.list_multipayment);
        text_multipayment.setText(MultiPayment[position]);
        return convertView;
    }
}
