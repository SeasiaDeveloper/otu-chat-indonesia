package com.eklanku.otuChat.ui.adapters.payment2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models2.DataProduct;

import java.util.ArrayList;
import java.util.List;

;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    private ArrayList<String> nama, price, ep;
    LayoutInflater inflter;
    List<DataProduct> products;

    public SpinnerAdapter(Context applicationContext, List<DataProduct> products) {
        this.context = applicationContext;
        this.products = products;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return products.size();
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

        final DataProduct item = products.get(position);

        double x = Double.valueOf(item.getPrice());
        long y = (long) x;

        text_denom.setText(item.getName());
        text_nominal.setText("Rp" + Utils.DoubleToCurency(y));
        text_point.setText("point: " + item.getEp());
        return convertView;
    }
}
