package com.eklanku.otuChat.ui.adapters.payment2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.models2.TopupDetailM;

import java.util.List;

;

public class SpinnerBankAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    List<TopupDetailM> datas;

    public SpinnerBankAdapter(Context applicationContext, List<TopupDetailM> datas) {
        this.context = applicationContext;
        this.datas = datas;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return datas.size();
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
        convertView = inflter.inflate(R.layout.item_bank_list, null);
        TextView text_denom = (TextView) convertView.findViewById(R.id.denom);
        TextView text_nominal = (TextView) convertView.findViewById(R.id.nominal);
        TextView text_point = (TextView) convertView.findViewById(R.id.point);
        ImageView imgBank = (ImageView) convertView.findViewById(R.id.img_bank);

        final TopupDetailM item = datas.get(position);

        //Log.d("AYIK", "loaddatabankadapter:" + item + "\n" + item.getBank());


        if (!item.getIsactive().equalsIgnoreCase("Live")) {
            text_denom.setTextColor(Color.GRAY);
            text_nominal.setTextColor(Color.GRAY);
            text_point.setTextColor(Color.GRAY);
        }

        text_denom.setText("Bank " + item.getBank());
        text_nominal.setText("No. Rekening " + item.getNorec());
        text_point.setText("" + item.getAnbank());

        if (item.getBank().equals("BNI")) {
            imgBank.setImageResource(R.drawable.ic_bni);
        } else if (item.getBank().equals("BCA")) {
            imgBank.setImageResource(R.drawable.ic_bca);
        } else if (item.getBank().equals("MANDIRI")) {
            imgBank.setImageResource(R.drawable.ic_mandiri);
        } else if (item.getBank().equals("BRI")) {
            imgBank.setImageResource(R.drawable.ic_bri);
        }

        return convertView;
    }
}
