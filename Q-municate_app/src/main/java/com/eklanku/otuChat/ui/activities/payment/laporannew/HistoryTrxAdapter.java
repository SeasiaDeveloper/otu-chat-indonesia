package com.eklanku.otuChat.ui.activities.payment.laporannew;

/**
 * Created by AHMAD AYIK RIFAI on 10/3/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;


import java.util.List;

public class HistoryTrxAdapter extends RecyclerView.Adapter<HistoryTrxAdapter.MyViewHolder> {
    private Context context;
    private List<ItemHistoryTrx> cartList;
    private String TAG = HistoryTrxAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvKode, tvTanggal, tvStatus, tvNominal, tvJenis, tvDot, tvInvoice;
        private View viewTrx;

        public MyViewHolder(View view) {
            super(view);
            viewTrx = view;
            tvKode = view.findViewById(R.id.tvKode);
            tvTanggal = view.findViewById(R.id.tvTanggal);
            tvStatus = view.findViewById(R.id.tvstatus);
            tvNominal = view.findViewById(R.id.tvNominal);
            tvJenis = view.findViewById(R.id.tvJnsTransaksi);
            tvDot = view.findViewById(R.id.dot);
            tvInvoice = view.findViewById(R.id.tvInvoice);

        }
    }

    public HistoryTrxAdapter(Context context, List<ItemHistoryTrx> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemHistoryTrx itemProduct = cartList.get(position);

        holder.tvKode.setText(itemProduct.getTrxKode());
        holder.tvTanggal.setText(itemProduct.getTrxTanggal());
        holder.tvStatus.setText(itemProduct.getTrxStatus());
        holder.tvNominal.setText(itemProduct.getTrxNominal());
        holder.tvJenis.setText(itemProduct.getTrxJenis());
        holder.tvInvoice.setText(itemProduct.getTrxInvoice());

        if(itemProduct.getTrxStatus().equalsIgnoreCase("Gagal")){
            holder.tvStatus.setTextColor(Color.RED);
        }else{
            holder.tvStatus.setTextColor(Color.GREEN);
        }

        holder.tvDot.setText(Html.fromHtml("&#8226;"));

        holder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "DETAIL", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


}