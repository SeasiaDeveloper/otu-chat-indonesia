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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryPenarikanAdapter extends RecyclerView.Adapter<HistoryPenarikanAdapter.MyViewHolder> {
    private Context context;
    private List<ItemHistoryPenarikan> cartList;
    private String TAG = HistoryPenarikanAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvjumlah, tvstatus, tvtanggal;
        private View viewTrx;

        public MyViewHolder(View view) {
            super(view);
            viewTrx = view;
            tvjumlah = view.findViewById(R.id.tvjumlahpenarikan);
            tvstatus = view.findViewById(R.id.tvstatus);
            tvtanggal = view.findViewById(R.id.tvTanggal);

        }
    }

    public HistoryPenarikanAdapter(Context context, List<ItemHistoryPenarikan> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_penarikan_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemHistoryPenarikan itemProduct = cartList.get(position);

        holder.tvtanggal.setText(itemProduct.getTgl_penarikan());
        holder.tvjumlah.setText(formatRupiah(Double.parseDouble(itemProduct.getJml_penarikan())));


        if(itemProduct.getStatus_penarikan().equalsIgnoreCase("Reject")){
            holder.tvstatus.setTextColor(Color.RED);
        }else if (itemProduct.getStatus_penarikan().equalsIgnoreCase("Waiting")) {
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else {
            holder.tvstatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }


        if(itemProduct.getStatus_penarikan().equalsIgnoreCase("Active")){
            holder.tvstatus.setText("Sukses");
        }else{
            holder.tvstatus.setText(itemProduct.getStatus_penarikan());
        }
        holder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(itemProduct.getTgl_penarikan(), itemProduct.getJml_penarikan(), itemProduct.getBank(),
                        itemProduct.getAtas_nama(), itemProduct.getNomer_rekening(), itemProduct.getStatus_penarikan());
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void showDetail(String _tanggal, String _jumlah_penarikan, String _bank, String _atas_nama,
                           String _nomor_rekening, String _status){
        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_penarikan);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView title = builder.findViewById(R.id.tv_product_type);
        final TextView tanggal = builder.findViewById(R.id.et_tgl);
        final TextView jumlah_penarikan = builder.findViewById(R.id.et_jumlah_penarikan);
        final TextView bank = builder.findViewById(R.id.et_bank);
        final TextView atas_nama = builder.findViewById(R.id.et_atas_nama);
        final TextView nomor_rekening = builder.findViewById(R.id.et_nomor_rekening);
        final TextView status = builder.findViewById(R.id.et_status_penarikan);
        final TextView tutup = builder.findViewById(R.id.tv_close);


        if(_status.equalsIgnoreCase("Reject")){
            status.setTextColor(Color.RED);
        }else if (_status.equalsIgnoreCase("Waiting")) {
            status.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else {
            status.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }


        if(_status.equalsIgnoreCase("Active")){
            status.setText("Sukses");
        }else{
            status.setText(_status);
        }

        title.setText("Detail Penarikan");
        tanggal.setText(_tanggal);
        jumlah_penarikan.setText(formatRupiah(Double.parseDouble(_jumlah_penarikan)));
        bank.setText(_bank);
        atas_nama.setText(_atas_nama);
        nomor_rekening.setText(_nomor_rekening);

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

}