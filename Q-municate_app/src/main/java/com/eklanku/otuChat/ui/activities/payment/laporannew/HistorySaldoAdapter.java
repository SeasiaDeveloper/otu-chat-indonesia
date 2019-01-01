package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
                showDetail(itemProduct.getMutasi_id(), itemProduct.getTgl_mutasi(), itemProduct.getSisa_saldo(),
                        itemProduct.getUang_masuk(), itemProduct.getUang_keluar(), itemProduct.getKeterangan(), itemProduct.getMutasi_status());
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

    public void showDetail(String _mutasi_id, String _tanggal, String _sisa_saldo, String _kredit, String _debet, String _keterangan,
                           String _status){
        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_saldo);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView title = builder.findViewById(R.id.tv_product_type);
        final EditText mutasi_id = builder.findViewById(R.id.et_mutasi_id);
        final EditText tanggal = builder.findViewById(R.id.et_tgl);
        final EditText sisa_saldo = builder.findViewById(R.id.et_sisa_saldo);
        final EditText kredit = builder.findViewById(R.id.et_uang_masuk);
        final EditText debet = builder.findViewById(R.id.et_uang_keluar);
        final EditText keterangan = builder.findViewById(R.id.et_keterangan);
        final EditText status = builder.findViewById(R.id.et_status);

        title.setText("DETAIL HISTORY SALDO");
        mutasi_id.setText(_mutasi_id);
        tanggal.setText(_tanggal);
        sisa_saldo.setText(_sisa_saldo);
        kredit.setText(_kredit);
        debet.setText(_debet);
        keterangan.setText(_keterangan);
        status.setText(_status);

        Button btnClose = builder.findViewById(R.id.btn_close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        builder.show();
        Window window = builder.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
