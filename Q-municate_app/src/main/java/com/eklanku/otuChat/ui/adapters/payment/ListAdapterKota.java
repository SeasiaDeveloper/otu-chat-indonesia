package com.eklanku.otuChat.ui.adapters.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.eklanku.otuChat.ui.activities.payment.models.DataKota;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterKota extends BaseAdapter implements Filterable {

    Context context;
    private ArrayList<DataKota> namakota;
    LayoutInflater inflter;

    private List<DataKota> dataKota;
    private List<DataKota> mainList;

    public ListAdapterKota(Context mContext, ArrayList<DataKota> kota){
        this.context = mContext;
        this.namakota = kota;

    }

    @Override
    public int getCount() {
        return namakota.size();
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
        convertView = inflter.inflate(R.layout.item_kota, null);
        TextView text_kota = (TextView) convertView.findViewById(R.id.tvName);
        text_kota.setText(namakota.get(position).getKota());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    dataKota = mainList;
                } else {
                    ArrayList<DataKota> filteredList = new ArrayList<>();
                    for (DataKota kota : mainList) {
                        if (kota.getKota().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(kota);
                        }
                    }
                    namakota = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = namakota;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                namakota = (ArrayList<DataKota>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
