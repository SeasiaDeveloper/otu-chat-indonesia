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
        myViewHolder.tvTanggal.setText(itemProduct.getTgl_perolehan());
        myViewHolder.tvJnsBonus.setText(itemProduct.getJenis_bonus());
        myViewHolder.tvStatus.setText(itemProduct.getStatus_bonus());
        myViewHolder.tvJumlahBonus.setText(itemProduct.getJml_bonus());
        if (itemProduct.getStatus_bonus().equalsIgnoreCase("Gagal")) {
            myViewHolder.tvStatus.setTextColor(Color.RED);
        } else {
            myViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }
        myViewHolder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(itemProduct.getTgl_perolehan(), itemProduct.getJenis_bonus(), itemProduct.getJml_bonus(),
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
        final EditText tanggal = builder.findViewById(R.id.et_tgl);
        final EditText jenis_bonus = builder.findViewById(R.id.et_jenis_bonus);
        final EditText jumlah_bonus = builder.findViewById(R.id.et_jumlah_bonus);
        final EditText keterangan = builder.findViewById(R.id.et_keterangan);
        final EditText status = builder.findViewById(R.id.et_status);

        title.setText("DETAIL HISTORY BONUS");
        tanggal.setText(_tanggal);
        jenis_bonus.setText(_jenis_bonus);
        jumlah_bonus.setText(_jumlah_bonus);
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
