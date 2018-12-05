package com.eklanku.otuChat.ui.adapters.payment2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.models2.DataProvider;

import java.util.List;

public class SpinnerGameAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflter;
    List<DataProvider> products;
    ImageView imgGame;
    String jns_provider;

    public SpinnerGameAdapter(Context context, List<DataProvider> namaVoucher, String jns) {
        this.context = context;
        this.products = namaVoucher;
        this.jns_provider = jns;
        inflter = (LayoutInflater.from(context));

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

        convertView = inflter.inflate(R.layout.item_game_list, null);
        TextView text_nama = (TextView) convertView.findViewById(R.id.nama);
        ImageView img_game = (ImageView) convertView.findViewById(R.id.img_game);

        final DataProvider item = products.get(position);

        text_nama.setText(item.getName_provaider());

        if(jns_provider.equals("GAME")){
            if(item.getName_provaider().equals("GEMSCOOL")){
                img_game.setImageResource(R.drawable.ic_game_gamescoll);
            }else if(item.getName_provaider().equals("MEGAXUS")){
                img_game.setImageResource(R.drawable.ic_game_megasus);
            }else if(item.getName_provaider().equals("MOBILE LEGENDS")){
                img_game.setImageResource(R.drawable.ic_game_mobilelegend);
            }else if(item.getName_provaider().equals("LYTO")){
                img_game.setImageResource(R.drawable.ic_game_lyto);
            }else if(item.getName_provaider().equals("MOL")){
                img_game.setImageResource(R.drawable.ic_game_mol);
            }else if(item.getName_provaider().equals("GOOGLE PLAY ID")){
                img_game.setImageResource(R.drawable.ic_game_googleplay);
            }else if(item.getName_provaider().equals("GARENA")){
                img_game.setImageResource(R.drawable.ic_game_garena);
            }else if(item.getName_provaider().equals("CHERRY")){
                img_game.setImageResource(R.drawable.ic_game_cherry);
            }
        }else if(jns_provider.equals("PULSA")){
            if(item.getName_provaider().equals("Telkomsel")){
                img_game.setImageResource(R.drawable.ic_pulsa_tsel);
            }else if(item.getName_provaider().equals("XL")){
                img_game.setImageResource(R.drawable.ic_pulsa_xl);
            }else if(item.getName_provaider().equals("Indosat")){
                img_game.setImageResource(R.drawable.ic_pulsa_indosat);
            }else if(item.getName_provaider().equals("Three")){
                img_game.setImageResource(R.drawable.ic_pulsa_three);
            }else if(item.getName_provaider().equals("Smart")){
                img_game.setImageResource(R.drawable.ic_pulsa_smartfren);
            }else if(item.getName_provaider().equals("Axis")){
                img_game.setImageResource(R.drawable.ic_pulsa_axis);
            }
        }else if(jns_provider.equals("PAKET DATA")){
            if(item.getName_provaider().equals("XL DATA")){
                img_game.setImageResource(R.drawable.ic_pulsa_xl);
            }else if(item.getName_provaider().equals("AXIS DATA")){
                img_game.setImageResource(R.drawable.ic_pulsa_axis);
            }else if(item.getName_provaider().equals("SMARTFREN DATA")){
                img_game.setImageResource(R.drawable.ic_pulsa_smartfren);
            }else if(item.getName_provaider().equals("TRI DATA")){
                img_game.setImageResource(R.drawable.ic_pulsa_three);
            }else if(item.getName_provaider().equals("ISAT DATA")){
                img_game.setImageResource(R.drawable.ic_pulsa_indosat);
            }else if(item.getName_provaider().equals("BOLT")){

            }else if(item.getName_provaider().equals("TSEL DATA")){
                img_game.setImageResource(R.drawable.ic_pulsa_tsel);
            }
        }else if(jns_provider.equals("PAKET SMS")){
            if(item.getName_provaider().equals("TSEL SMS")){
                img_game.setImageResource(R.drawable.ic_pulsa_tsel);
            }else if(item.getName_provaider().equals("ISAT SMS")){
                img_game.setImageResource(R.drawable.ic_pulsa_indosat);
            }
        }

        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
