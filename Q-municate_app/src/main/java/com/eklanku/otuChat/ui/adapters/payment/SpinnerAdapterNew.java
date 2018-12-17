package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.Utils;

import java.util.ArrayList;

public class SpinnerAdapterNew extends BaseAdapter {
    Context context;
    private ArrayList<String> nama, price, ep, provider;
    LayoutInflater inflter;
    String _nama;

    public SpinnerAdapterNew(Context applicationContext, ArrayList<String> listnama, ArrayList<String> listprice, ArrayList<String> listep, ArrayList<String> listProvide, String namaProvider) {
        this.context = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));
        this.nama = listnama;
        this.price = listprice;
        this.ep = listep;
        this.provider = listProvide;
        this._nama = namaProvider;

        Log.d(">>>>>>>>>>>>>>+", "SpinnerAdapterNew: "+listep);

    }

    @Override
    public int getCount() {
        return nama.size();
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

        Log.d(">>>>>>>>>>>>>>+", "getView: "+provider.get(position)+", "+price.get(position)+", "+position);
        //if(provider.get(position).equalsIgnoreCase(_nama)){
            /*Log.d(">>>>>>>>>>>>>>+", "getView>>: "+provider.get(position));
            Log.d(">>>>>>>>>>>>>>+", "getView>>: "+price.get(position));*/
            double x = Double.valueOf(price.get(position));
            long y = (long) x;

            text_denom.setText(nama.get(position));
            text_nominal.setText("Rp" + Utils.DoubleToCurency(y));
            text_point.setText("point: " + ep.get(position));
       // }


        return convertView;
    }
}
