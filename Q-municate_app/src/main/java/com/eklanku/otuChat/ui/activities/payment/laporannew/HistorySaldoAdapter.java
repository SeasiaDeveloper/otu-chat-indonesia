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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

        myViewHolder.tvTanggal.setText(formatTgl(itemProduct.getTgl_mutasi()));
        myViewHolder.tvInvoice.setText(itemProduct.getMutasi_id());
       // myViewHolder.tvStatus.setText(itemProduct.getMutasi_status());

        myViewHolder.tvSisaSaldo.setText(formatRupiah(Double.parseDouble(itemProduct.getSisa_saldo())));

        if (itemProduct.getMutasi_status().equalsIgnoreCase("Gagal")) {
            myViewHolder.tvStatus.setTextColor(Color.RED);
        } else {
            myViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        if (itemProduct.getMutasi_status().equalsIgnoreCase("Active")) {
            myViewHolder.tvStatus.setText("Sukses");
        } else {
            myViewHolder.tvStatus.setText(itemProduct.getMutasi_status());
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
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
                           String _status) {
        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_saldo);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView title = builder.findViewById(R.id.tv_product_type);
        final TextView mutasi_id = builder.findViewById(R.id.et_mutasi_id);
        final TextView tanggal = builder.findViewById(R.id.et_tgl);
        final TextView sisa_saldo = builder.findViewById(R.id.et_sisa_saldo);
        final TextView kredit = builder.findViewById(R.id.et_uang_masuk);
        final TextView debet = builder.findViewById(R.id.et_uang_keluar);
        final TextView keterangan = builder.findViewById(R.id.et_keterangan);
        final TextView status = builder.findViewById(R.id.et_status);
        final TextView tutup = builder.findViewById(R.id.tv_close);

        if (_status.equalsIgnoreCase("Gagal")) {
            status.setTextColor(Color.RED);
        } else {
            status.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }


        title.setText("Detail Saldo");
        mutasi_id.setText(_mutasi_id);
        tanggal.setText(formatTgl(_tanggal));
        sisa_saldo.setText(formatRupiah(Double.parseDouble(_sisa_saldo)));
        kredit.setText(formatRupiah(Double.parseDouble(_kredit)));
        debet.setText(formatRupiah(Double.parseDouble(_debet)));
        keterangan.setText(_keterangan);

        if(_status.equalsIgnoreCase("Active")){
            status.setText("Sukses");
        }else{
            status.setText(_status);
        }

        Button btnClose = builder.findViewById(R.id.btn_close);

        tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        builder.show();
        Window window = builder.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public String formatRupiah(double nominal) {
        String parseRp = "";
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        parseRp = formatRupiah.format(nominal);
        return parseRp;
    }

    public String formatTgl(String tgl){
        String format = "";
        if(!tgl.equals("") || !tgl.equals("null")){
            String parsTgl[] = tgl.split(" ");
            String parsTgl2[] = parsTgl[0].split("-");

            String tanggal = parsTgl2[2];
            String bulan = parsTgl2[1];
            String tahun = parsTgl2[0];

            format = tanggal+"-"+bulan+"-"+tahun+" "+parsTgl[1];
        }else{
            format = "";
        }

        return format;
    }
}
