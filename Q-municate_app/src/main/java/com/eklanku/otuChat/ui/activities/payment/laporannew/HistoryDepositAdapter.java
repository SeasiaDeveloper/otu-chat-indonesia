package com.eklanku.otuChat.ui.activities.payment.laporannew;

/**
 * Created by AHMAD AYIK RIFAI on 10/3/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

        holder.tvtanggal.setText(formatTgl(itemProduct.getTgl_deposit()));
        holder.tvjumlah.setText(formatRupiah(Double.parseDouble(itemProduct.getTotal_transfer())));
        holder.tvbank.setText(itemProduct.getBank());


        if (itemProduct.getStatus_deposit().equalsIgnoreCase("Gagal") || itemProduct.getStatus_deposit().equalsIgnoreCase("Reject")) {
            holder.tvstatus.setTextColor(Color.RED);
        } else if (itemProduct.getStatus_deposit().equalsIgnoreCase("Waiting") || itemProduct.getStatus_deposit().equalsIgnoreCase("Onproses")) {
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else {
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        if (itemProduct.getStatus_deposit().equalsIgnoreCase("Active")) {
            holder.tvstatus.setText("Sukses");
        } else {
            holder.tvstatus.setText(itemProduct.getStatus_deposit());
        }

        holder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(formatTgl(itemProduct.getTgl_deposit()), itemProduct.getJumlah_deposit(), itemProduct.getCodeunix(), itemProduct.getTotal_transfer(),
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
        final TextView tanggal = builder.findViewById(R.id.et_tgl);
        final TextView deposit = builder.findViewById(R.id.et_jumlah_deposit);
        final TextView kode_unix = builder.findViewById(R.id.et_kode_unix);
        final TextView total_transfer = builder.findViewById(R.id.et_total_transfer);
        final TextView bank = builder.findViewById(R.id.et_bank);
        final TextView nomor_rekening = builder.findViewById(R.id.et_nomor_rekening);
        final TextView atas_nama = builder.findViewById(R.id.et_atas_nama);
        final TextView status = builder.findViewById(R.id.et_status);
        final TextView tutup = builder.findViewById(R.id.tv_close);

        if (_status.equalsIgnoreCase("Gagal") || _status.equalsIgnoreCase("Reject")) {
            status.setTextColor(Color.RED);
        } else if (_status.equalsIgnoreCase("Waiting") || _status.equalsIgnoreCase("Onproses")) {
            status.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else {
            status.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }


        title.setText("Detail Deposit");
        tanggal.setText(_tanggal);
        deposit.setText(formatRupiah(Double.parseDouble(_deposit)));
        kode_unix.setText(_kode_unix);
        total_transfer.setText(formatRupiah(Double.parseDouble(_total_transfer)));
        bank.setText(_bank);
        nomor_rekening.setText(_nomor_rekening);
        atas_nama.setText(_atas_nama);

        if (_status.equalsIgnoreCase("Active")) {
            status.setText("Sukses");
        } else {
            status.setText(_status);
        }
        Button btnClose = builder.findViewById(R.id.btn_close);

        tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        ImageView btnPrint = builder.findViewById(R.id.btnprint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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