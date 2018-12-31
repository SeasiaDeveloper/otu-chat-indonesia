package com.eklanku.otuChat.ui.activities.payment.laporannew;

/**
 * Created by AHMAD AYIK RIFAI on 10/3/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;

import java.util.List;

public class HistoryDepositAdapter extends RecyclerView.Adapter<HistoryDepositAdapter.MyViewHolder> {
    private Context context;
    private List<ItemHistoryDeposit> cartList;
    private String TAG = HistoryDepositAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvjumlah, tvstatus, tvunix, tvbank, tvtanggal, tvDot, tvInvoice;
        private View viewTrx;

        public MyViewHolder(View view) {
            super(view);
            viewTrx = view;
            tvjumlah = view.findViewById(R.id.tvjumlahdeposit);
            tvstatus = view.findViewById(R.id.tvstatus);
            tvbank = view.findViewById(R.id.tvBank);
            tvtanggal = view.findViewById(R.id.tvTanggal);

        }
    }

    public HistoryDepositAdapter(Context context, List<ItemHistoryDeposit> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_deposit_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemHistoryDeposit itemProduct = cartList.get(position);

        holder.tvtanggal.setText(itemProduct.getTanggal());
        holder.tvjumlah.setText(itemProduct.getJmldeposit());
        holder.tvbank.setText(itemProduct.getBank());


        if(itemProduct.getStatus().equalsIgnoreCase("Gagal")){
            holder.tvstatus.setTextColor(Color.RED);
        }else{
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }


        if(itemProduct.getStatus().equalsIgnoreCase("Active")){
            holder.tvstatus.setText("Sukses");
        }

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