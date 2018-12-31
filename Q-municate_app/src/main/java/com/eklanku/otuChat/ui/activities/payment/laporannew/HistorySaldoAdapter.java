package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;

import java.util.List;

public class HistorySaldoAdapter extends RecyclerView.Adapter<HistorySaldoAdapter.MyViewHolder> {

    private Context context;
    private List<ItemHistorySaldo> cartList;

    public HistorySaldoAdapter(Context context, List<ItemHistorySaldo> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_history_saldo_list, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final ItemHistorySaldo itemProduct = cartList.get(i);

        myViewHolder.tvTanggal.setText(itemProduct.getTgl_mutasi());
        myViewHolder.tvInvoice.setText(itemProduct.getMutasi_id());
        myViewHolder.tvSisaSaldo.setText(itemProduct.getSisa_saldo());

        if(itemProduct.getMutasi_status().equalsIgnoreCase("Gagal")){
            myViewHolder.tvStatus.setTextColor(Color.RED);
        }else{
            myViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        if(itemProduct.getMutasi_status().equalsIgnoreCase("Active")){
            myViewHolder.tvStatus.setText("Sukses");
        }

        myViewHolder.viewTrx.setOnClickListener(new View.OnClickListener() {
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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTanggal, tvInvoice, tvStatus, tvSisaSaldo;
        private View viewTrx;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTrx = itemView;
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvInvoice = itemView.findViewById(R.id.tvMutasi_id);
            tvSisaSaldo = itemView.findViewById(R.id.tvSisaSaldo);
            tvStatus = itemView.findViewById(R.id.tvstatus);
        }
    }
}
