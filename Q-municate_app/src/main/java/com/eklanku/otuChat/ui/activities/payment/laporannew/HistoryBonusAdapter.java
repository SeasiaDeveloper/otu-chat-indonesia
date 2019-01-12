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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryBonusAdapter extends RecyclerView.Adapter<HistoryBonusAdapter.MyViewHolder> {

    private Context context;
    private List<ItemHistoryBonus> cartList;

    public HistoryBonusAdapter(Context context, List<ItemHistoryBonus> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_history_bonus_list, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final ItemHistoryBonus itemProduct = cartList.get(i);
        myViewHolder.tvTanggal.setText(formatTgl(itemProduct.getTgl_perolehan()));
        myViewHolder.tvJnsBonus.setText(itemProduct.getJenis_bonus());
        myViewHolder.tvStatus.setText(itemProduct.getStatus_bonus());
        myViewHolder.tvJumlahBonus.setText(formatRupiah(Double.parseDouble(itemProduct.getJml_bonus())));
        if (itemProduct.getStatus_bonus().equalsIgnoreCase("Reject")) {
            myViewHolder.tvStatus.setTextColor(Color.RED);
        } else {
            myViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }
        myViewHolder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(formatTgl(itemProduct.getTgl_perolehan()), itemProduct.getJenis_bonus(), itemProduct.getJml_bonus(),
                        itemProduct.getKeterangan(), itemProduct.getStatus_bonus());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTanggal, tvJnsBonus, tvStatus, tvJumlahBonus;
        private View viewTrx;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewTrx = itemView;
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJnsBonus = itemView.findViewById(R.id.tvJenis_cb);
            tvJumlahBonus = itemView.findViewById(R.id.tvJml_cb);
            tvStatus = itemView.findViewById(R.id.tvstatus);
        }
    }

    public void showDetail(String _tanggal, String _jenis_bonus, String _jumlah_bonus, String _keterangan, String _status) {
        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_bonus);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView title = builder.findViewById(R.id.tv_product_type);
        final TextView tanggal = builder.findViewById(R.id.et_tgl);
        final TextView jenis_bonus = builder.findViewById(R.id.et_jenis_bonus);
        final TextView jumlah_bonus = builder.findViewById(R.id.et_jumlah_bonus);
        final TextView keterangan = builder.findViewById(R.id.et_keterangan);
        final TextView status = builder.findViewById(R.id.et_status);
        final TextView tutup = builder.findViewById(R.id.tv_close);

        if (_status.equalsIgnoreCase("Reject")) {
            status.setTextColor(Color.RED);
        } else {
            status.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }
        title.setText("Detail Bonus");
        tanggal.setText(_tanggal);
        jenis_bonus.setText(_jenis_bonus);
        jumlah_bonus.setText(formatRupiah(Double.parseDouble(_jumlah_bonus)));
        keterangan.setText(_keterangan);
        status.setText(_status);

        ImageView btnClose = builder.findViewById(R.id.btn_close);

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
