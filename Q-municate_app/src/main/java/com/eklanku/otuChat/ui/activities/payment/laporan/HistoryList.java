package com.eklanku.otuChat.ui.activities.payment.laporan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.RiwayatActivity;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailHistosryOTU;
import com.eklanku.otuChat.ui.activities.payment.models.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.payment.sqlite.database.model.History;
import com.eklanku.otuChat.ui.activities.payment.tableview.model.CellModel;
import com.eklanku.otuChat.ui.activities.payment.transaksi.PaymentLogin;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.ListHistoryAdapter;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryList extends AppCompatActivity {

    RecyclerView _listHistory;
    TextView _titleHistory;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;
    HashMap<String, String> user;

    String strUserID, strAccessToken, strApIUse = "OTU";

    ListHistoryAdapter listHistoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferenceManager = new PreferenceManager(HistoryList.this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        _listHistory = findViewById(R.id.listHistory);
        _titleHistory = findViewById(R.id.tvtitlehistory);

    }


    ArrayList<String> kode, tanggal, stat, nominal, jenistransaksi, valTanggal, valStatus;

    public void getListHistory() {
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(strUserID, strApIUse, strAccessToken, "1");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    kode = new ArrayList<>();
                    tanggal = new ArrayList<>();
                    stat = new ArrayList<>();
                    nominal = new ArrayList<>();
                    jenistransaksi = new ArrayList<>();
                    valTanggal = new ArrayList<>();
                    valStatus = new ArrayList<>();

                    kode.clear();
                    tanggal.clear();
                    stat.clear();
                    nominal.clear();
                    jenistransaksi.clear();
                    valTanggal.clear();
                    valStatus.clear();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        for (int i = 0; i < ROW_SIZE; i++) {
                            kode.add(result.get(i).getProduct_kode());
                            tanggal.add(result.get(i).getTgl());
                            stat.add(result.get(i).getVstatus());
                            nominal.add(result.get(i).getHarga());
                            jenistransaksi.add(result.get(i).getType_product());
                            if (!tanggal.get(i).equals("") || !tanggal.get(i).equals("null")) {
                                String parsTgl[] = tanggal.get(i).split(" ");
                                String parsTgl2[] = parsTgl[0].split("-");
                                String tanggal = parsTgl2[2];
                                String bulan = parsTgl2[1];
                                String tahun = parsTgl2[0];
                                valTanggal.add(tanggal + "-" + bulan + "-" + tahun + " " + parsTgl[1]);
                            }
                            //ganti status active jadi success
                            if (stat.get(i).equals("Active")) {
                                valStatus.add("Sukses");
                            } else {
                                valStatus.add(stat.get(i));
                            }

                            nominal.add(null);
                            jenistransaksi.add(null);
                            listHistoryAdapter = new ListHistoryAdapter(HistoryList.this, kode, valTanggal, jenistransaksi,  nominal, valStatus);
                            //_listHistory.setAdapter(listHistoryAdapter);
                        }


                    } else {
                        Toast.makeText(HistoryList.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HistoryList.this, HistoryList.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {

                Toast.makeText(HistoryList.this, HistoryList.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
