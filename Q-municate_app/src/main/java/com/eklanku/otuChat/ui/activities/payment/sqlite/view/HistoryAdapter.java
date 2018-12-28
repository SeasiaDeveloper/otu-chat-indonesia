package com.eklanku.otuChat.ui.activities.payment.sqlite.view;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.sqlite.database.model.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<History> historyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView number;
        public TextView dot;
        public TextView nominal;
        public TextView trans;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.number);
            nominal = view.findViewById(R.id.nominal);
            trans = view.findViewById(R.id.trans);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sqlite_history_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        History history = historyList.get(position);

        holder.number.setText(history.getNumber());
        holder.nominal.setText(history.getNominal());
        holder.trans.setText(history.getTrans());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(history.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
