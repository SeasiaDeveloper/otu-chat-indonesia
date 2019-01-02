package com.eklanku.otuChat.ui.activities.payment.laporannew;

/**
 * Created by AHMAD AYIK RIFAI on 10/3/2017.
 */

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;


import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
            //tvDot = view.findViewById(R.id.dot);
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
        holder.tvNominal.setText(formatRupiah(Double.parseDouble(itemProduct.getTrxNominal())));
        holder.tvJenis.setText(itemProduct.getTrxJenis());
        holder.tvInvoice.setText(itemProduct.getTrxInvoice());

        if (itemProduct.getTrxStatus().equalsIgnoreCase("Gagal")) {
            holder.tvStatus.setTextColor(Color.RED);
        } else if (itemProduct.getTrxStatus().equalsIgnoreCase("Waiting")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        //holder.tvDot.setText(Html.fromHtml("&#8226;"));

        holder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(itemProduct.getTrxTanggal(), itemProduct.getTrxStatus(), itemProduct.getTrxNominal(), itemProduct.getTrxJenis(),
                        itemProduct.getTrxInvoice(), itemProduct.getTrxTujuan(), itemProduct.getTrxKet(), itemProduct.getTrxVsn());
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void showDetail(String trxTanggal, String trxStatus, String trxNominal, String trxJenis,
                           String trxInvoice, String trxTujuan, String trxKet, String trxVsn) {

        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_trx);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView tvProductType = builder.findViewById(R.id.tv_product_type);
        final TextView etTgl = builder.findViewById(R.id.et_tgl);
        final TextView etStatus = builder.findViewById(R.id.et_status);
        final TextView etNominal = builder.findViewById(R.id.et_harga);
        final TextView etInvoice = builder.findViewById(R.id.et_invoice);
        final TextView etTujuan = builder.findViewById(R.id.et_tujuan);
        final TextView etVsn = builder.findViewById(R.id.et_vsn);
        final TextView etKet = builder.findViewById(R.id.et_keterangan);
        final TextView tutup = builder.findViewById(R.id.tv_close);

        if (trxStatus.equalsIgnoreCase("Gagal")) {
            etStatus.setTextColor(Color.RED);
        } else if (trxStatus.equalsIgnoreCase("Waiting")) {
            etStatus.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else {
            etStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        etTgl.setText(trxTanggal);
        etStatus.setText(trxStatus);
        etNominal.setText(formatRupiah(Double.parseDouble(trxNominal)));
        tvProductType.setText("Detail Transaksi " + trxJenis);
        etInvoice.setText(trxInvoice);
        etTujuan.setText(trxTujuan);
        etVsn.setText(trxVsn);
        etKet.setText(trxKet);

        //Button btnClose = builder.findViewById(R.id.btn_close);
        //Button btnBuy = builder.findViewById(R.id.btn_buy);
        //Button btnPrint = builder.findViewById(R.id.btn_print);

        ImageView btnBuy = builder.findViewById(R.id.btn_buy);
        ImageView btnPrint = builder.findViewById(R.id.btn_print);

        ImageView btnCopyInv = builder.findViewById(R.id.btn_copy_inv);
        ImageView btnCopyTjn = builder.findViewById(R.id.btn_copy_tjn);

        btnCopyInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Salin invoice", etInvoice.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Salin invoice", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopyTjn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Salin tujuan", etTujuan.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Salin tujuan", Toast.LENGTH_SHORT).show();
            }
        });

        tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();

            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
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

}