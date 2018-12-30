package com.eklanku.otuChat.ui.activities.payment.laporannew;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailHistosryOTU;
import com.eklanku.otuChat.ui.activities.payment.models.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewHistoryTrxActivity extends AppCompatActivity {

    TextView _titleHistory;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;
    HashMap<String, String> user;

    String strUserID, strAccessToken, strApIUse = "OTU";

    //=====================
    private RecyclerView recyclerView;
    private HistoryTrxAdapter adapter;
    private ArrayList<ItemHistoryTrx> trxList;

    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_new_trx);

        layoutView = findViewById(R.id.layout);
        progressBar = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);

        preferenceManager = new PreferenceManager(NewHistoryTrxActivity.this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        /*_listHistory = findViewById(R.id.listHistory);*/
        _titleHistory = findViewById(R.id.tvtitlehistory);

        //=====================================
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        trxList = new ArrayList<>();

        getListHistory();

    }

    public void getListHistory() {
        showProgress(true);
        trxList.clear();
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(strUserID, strApIUse, strAccessToken, "1");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String trxKode, trxTanggal, trxStatus, trxNominal, trxJenis, trxInvoice;

                    //Toast.makeText(NewHistoryTrxActivity.this, "SUKSES", Toast.LENGTH_SHORT).show();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                       /* Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);*/

                        for (int i = 0; i < ROW_SIZE; i++) {
                            trxKode = result.get(i).getProduct_kode();
                            trxTanggal = result.get(i).getTgl();
                            trxStatus = result.get(i).getVstatus();
                            trxNominal = result.get(i).getHarga();
                            trxJenis = result.get(i).getType_product();
                            trxInvoice = result.get(i).getInvoice();

                            Log.d("AYIK", "history trx->" + trxKode + " " + trxJenis + " " + trxNominal);

                            ItemHistoryTrx trx = new ItemHistoryTrx(trxKode, trxTanggal, trxStatus, trxNominal, trxJenis, trxInvoice);
                            trxList.add(trx);

                            //ganti status active jadi success
                            /*if (stat.get(i).equals("Active")) {
                                valStatus.add("Sukses");
                            } else {
                                valStatus.add(stat.get(i));
                            }*/

                        }

                        if (ROW_SIZE >= 1) {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                        }

                        adapter = new HistoryTrxAdapter(NewHistoryTrxActivity.this, trxList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(NewHistoryTrxActivity.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(NewHistoryTrxActivity.this, NewHistoryTrxActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {

                showProgress(false);
                Toast.makeText(NewHistoryTrxActivity.this, NewHistoryTrxActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
            layoutView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            layoutView.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

}
