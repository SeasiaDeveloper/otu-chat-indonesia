package com.eklanku.otuChat.ui.activities.payment.laporannew;

/**
 * Created by AHMAD AYIK RIFAI on 10/3/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

        holder.tvtanggal.setText(itemProduct.getTgl_deposit());
        holder.tvjumlah.setText(itemProduct.getTotal_transfer());
        holder.tvbank.setText(itemProduct.getBank());


        if (itemProduct.getStatus_deposit().equalsIgnoreCase("Gagal")) {
            holder.tvstatus.setTextColor(Color.RED);
        } else {
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }


        if (itemProduct.getStatus_deposit().equalsIgnoreCase("Active")) {
            holder.tvstatus.setText("Sukses");
        }

        holder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(itemProduct.getTgl_deposit(), itemProduct.getJumlah_deposit(), itemProduct.getCodeunix(), itemProduct.getTotal_transfer(),
                        itemProduct.getBank(), itemProduct.getNomor_rekening(), itemProduct.getPemilik(), itemProduct.getStatus_deposit());
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void showDetail(String _tanggal, String _deposit, String _kode_unix, String _total_transfer, String _bank,
                           String _nomor_rekening, String _atas_nama, String _status) {
        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_deposit);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView title = builder.findViewById(R.id.tv_product_type);
        final EditText tanggal = builder.findViewById(R.id.et_tgl);
        final EditText deposit = builder.findViewById(R.id.et_jumlah_deposit);
        final EditText kode_unix = builder.findViewById(R.id.et_kode_unix);
        final EditText total_transfer = builder.findViewById(R.id.et_total_transfer);
        final EditText bank = builder.findViewById(R.id.et_bank);
        final EditText nomor_rekening = builder.findViewById(R.id.et_nomor_rekening);
        final EditText atas_nama = builder.findViewById(R.id.et_atas_nama);
        final EditText status = builder.findViewById(R.id.et_status);

        title.setText("DETAIL HISTORY DEPOSIT");
        tanggal.setText(_tanggal);
        deposit.setText(_deposit);
        kode_unix.setText(_kode_unix);
        total_transfer.setText(_total_transfer);
        bank.setText(_bank);
        nomor_rekening.setText(_nomor_rekening);
        atas_nama.setText(_atas_nama);
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