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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class HistoryActivity extends AppCompatActivity {
    Bundle extras;
    TextView _titleHistory;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;
    HashMap<String, String> user;

    String strUserID, strAccessToken, strApIUse = "OTU";

    //=====================
    private RecyclerView recyclerView;
    private HistoryTrxAdapter adapterTransaksi;
    private HistoryDepositAdapter adapterDeposit;
    private HistorySaldoAdapter adapterSaldo;
    private HistoryPenarikanAdapter adapterPenarikan;
    private HistoryBonusAdapter adapterBonus;
    private ArrayList<ItemHistoryTrx> trxListTransaksi;
    private ArrayList<ItemHistoryDeposit> trxListDeposit;
    private ArrayList<ItemHistorySaldo> trxListSaldo;
    private ArrayList<ItemHistoryPenarikan> trxListPenarikan;
    private ArrayList<ItemHistoryBonus> trxListBonus;

    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;

    EditText etCari;
    String jenisHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_new_trx);
        extras = getIntent().getExtras();
        layoutView = findViewById(R.id.layout);
        progressBar = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);

        etCari = findViewById(R.id.et_cari);

        preferenceManager = new PreferenceManager(HistoryActivity.this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        /*_listHistory = findViewById(R.id.listHistory);*/
        _titleHistory = findViewById(R.id.tvtitlehistory);

        //=====================================
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        trxListTransaksi = new ArrayList<>();
        trxListDeposit = new ArrayList<>();
        trxListSaldo = new ArrayList<>();
        trxListPenarikan = new ArrayList<>();
        trxListBonus = new ArrayList<>();

        jenisHistory = extras.getString("jenisHistory");

        if (jenisHistory.equalsIgnoreCase("transaksi")) {
            _titleHistory.setText("HISTORY TRANSAKSI");
            etCari.setVisibility(View.VISIBLE);
            getListHistory();
        } else if (jenisHistory.equalsIgnoreCase("saldo")) {
            _titleHistory.setText("HISTORY SALDO");
            etCari.setVisibility(View.GONE);
            getListHistorySaldo();
        } else if (jenisHistory.equalsIgnoreCase("deposit")) {
            _titleHistory.setText("HISTORY DEPOSIT");
            etCari.setVisibility(View.GONE);
            getListHistoryDeposit();
        } else if (jenisHistory.equalsIgnoreCase("penarikan")) {
            _titleHistory.setText("HISTORY PENARIKAN");
            etCari.setVisibility(View.GONE);
            getListHistoryPenarikan();
        } else if (jenisHistory.equalsIgnoreCase("bonus")) {
            _titleHistory.setText("HISTORY BONUS");
            etCari.setVisibility(View.GONE);
            getListHistoryBonus();
        }

        addTextListener();

    }

    public void getListHistory() {
        showProgress(true);
        trxListTransaksi.clear();
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
                    String trxMbrId, trxTujuan, trxKet, trxVsn, trxMbrName, trxTglSukses, trxProviderName;
                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                        for (int i = 0; i < ROW_SIZE; i++) {
                            trxKode = result.get(i).getProduct_kode();
                            trxTanggal = result.get(i).getTgl();
                            trxStatus = result.get(i).getVstatus();
                            trxNominal = result.get(i).getHarga();
                            trxJenis = result.get(i).getType_product();
                            trxInvoice = result.get(i).getInvoice();

                            trxMbrId = result.get(i).getId_member();
                            trxTujuan = result.get(i).getTujuan();
                            trxKet = result.get(i).getKeterangan();
                            trxVsn = result.get(i).getVsn();
                            trxMbrName = result.get(i).getMbr_name();
                            trxTglSukses = result.get(i).getTgl_sukses();

                            trxProviderName = result.get(i).getProvider_name();

                            if (TextUtils.isEmpty(trxKet)) {
                                trxKet = "-";
                            }

                            if (TextUtils.isEmpty(trxVsn)) {
                                trxVsn = "-";
                            }

                            ItemHistoryTrx trx = new ItemHistoryTrx(trxKode, trxTanggal, trxStatus, trxNominal, trxJenis, trxInvoice,
                                    trxMbrId, trxTujuan, trxKet, trxVsn, trxMbrName, trxTglSukses, trxProviderName);
                            trxListTransaksi.add(trx);

                        }

                        if (ROW_SIZE >= 1) {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                        }

                        adapterTransaksi = new HistoryTrxAdapter(HistoryActivity.this, trxListTransaksi);
                        recyclerView.setAdapter(adapterTransaksi);
                        adapterTransaksi.notifyDataSetChanged();

                    } else {
                        Toast.makeText(HistoryActivity.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {

                showProgress(false);
                Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }


    public void getListHistoryDeposit() {
        showProgress(true);
        trxListDeposit.clear();
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(strUserID, strApIUse, strAccessToken, "2");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String tanggal, deposit, kodeunix, total_transfer, bank, nomor_rekening, atas_nama, status_deposit;

                    //Toast.makeText(HistoryActivity.this, "SUKSES", Toast.LENGTH_SHORT).show();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                       /* Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);*/

                        for (int i = 0; i < ROW_SIZE; i++) {
                            tanggal = result.get(i).getTgl_deposit();
                            deposit = result.get(i).getJumlah_deposit();
                            kodeunix = result.get(i).getCodeunix();
                            total_transfer = result.get(i).getTotal_transfer();
                            bank = result.get(i).getBank();
                            nomor_rekening = result.get(i).getNomer_rekening();
                            atas_nama = result.get(i).getNama_pemilik();
                            status_deposit = result.get(i).getStatus_deposit();

                            ItemHistoryDeposit trx = new ItemHistoryDeposit(tanggal, status_deposit, kodeunix,
                                    total_transfer, bank, deposit, nomor_rekening, atas_nama);
                            trxListDeposit.add(trx);
                        }

                        if (ROW_SIZE >= 1) {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                        }

                        adapterDeposit = new HistoryDepositAdapter(HistoryActivity.this, trxListDeposit);
                        recyclerView.setAdapter(adapterDeposit);
                        adapterDeposit.notifyDataSetChanged();

                    } else {
                        Toast.makeText(HistoryActivity.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                showProgress(false);
                Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    public void getListHistorySaldo() {
        showProgress(true);
        trxListSaldo.clear();
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(strUserID, strApIUse, strAccessToken, "3");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String tanggal_mutasi, mutasi_id, mutasi_status, sisa_saldo, uang_masuk, uang_keluar, keterangan;

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                        Log.d("OPPO-1", "onResponse: " + ROW_SIZE);

                        for (int i = 0; i < ROW_SIZE; i++) {
                            mutasi_id = result.get(i).getMutasi_id();
                            tanggal_mutasi = result.get(i).getTgl_mutasi();
                            mutasi_status = result.get(i).getMutasi_status();
                            sisa_saldo = result.get(i).getSisa_saldo().toString();
                            uang_masuk = result.get(i).getUang_masuk().toString();
                            uang_keluar = result.get(i).getUang_keluar().toString();
                            keterangan = result.get(i).getKeterangan();

                            ItemHistorySaldo trx = new ItemHistorySaldo(mutasi_id, tanggal_mutasi, mutasi_status,
                                    sisa_saldo, uang_masuk, uang_keluar, keterangan);
                            trxListSaldo.add(trx);
                        }

                        if (ROW_SIZE >= 1) {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                        }

                        adapterSaldo = new HistorySaldoAdapter(HistoryActivity.this, trxListSaldo);
                        recyclerView.setAdapter(adapterSaldo);
                        adapterSaldo.notifyDataSetChanged();

                    } else {
                        Toast.makeText(HistoryActivity.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                showProgress(false);
                Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    public void getListHistoryPenarikan() {
        showProgress(true);
        trxListPenarikan.clear();
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(strUserID, strApIUse, strAccessToken, "4");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String tgl_penarikan, status_penarikan, jml_penarikan, bank, atas_nama, nomer_rekening;

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                        for (int i = 0; i < ROW_SIZE; i++) {
                            tgl_penarikan = result.get(i).getTgl_penarikan();
                            status_penarikan = result.get(i).getStatus_penarikan();
                            jml_penarikan = result.get(i).getJml_penarikan();
                            bank = result.get(i).getBank();
                            atas_nama = result.get(i).getAtas_nama();
                            nomer_rekening = result.get(i).getNomer_rekening();

                            ItemHistoryPenarikan trx = new ItemHistoryPenarikan(tgl_penarikan, status_penarikan, jml_penarikan, bank, atas_nama, nomer_rekening);
                            trxListPenarikan.add(trx);
                        }

                        if (ROW_SIZE >= 1) {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                        }

                        adapterPenarikan = new HistoryPenarikanAdapter(HistoryActivity.this, trxListPenarikan);
                        recyclerView.setAdapter(adapterPenarikan);
                        adapterPenarikan.notifyDataSetChanged();

                    } else {
                        Toast.makeText(HistoryActivity.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                showProgress(false);
                Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    public void getListHistoryBonus() {
        showProgress(true);
        trxListBonus.clear();
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(strUserID, strApIUse, strAccessToken, "5");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String tgl_perolehan, keterangan, jenis_bonus, status_bonus, jml_bonus;

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();

                        for (int i = 0; i < ROW_SIZE; i++) {
                            tgl_perolehan = result.get(i).getTgl_perolehan();
                            keterangan = result.get(i).getKeterangan();
                            jenis_bonus = result.get(i).getJenis_bonus();
                            jml_bonus = result.get(i).getJml_bonus();
                            status_bonus = result.get(i).getStatus_bonus();
                            Log.d("OPPO-1", "onResponse: " + status_bonus);
                            ItemHistoryBonus trx = new ItemHistoryBonus(tgl_perolehan, keterangan, jenis_bonus, status_bonus, jml_bonus);
                            trxListBonus.add(trx);
                        }

                        if (ROW_SIZE >= 1) {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                        }

                        adapterBonus = new HistoryBonusAdapter(HistoryActivity.this, trxListBonus);
                        recyclerView.setAdapter(adapterBonus);
                        adapterBonus.notifyDataSetChanged();

                    } else {
                        Toast.makeText(HistoryActivity.this, "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                showProgress(false);
                Toast.makeText(HistoryActivity.this, HistoryActivity.this.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    public void addTextListener() {

        etCari.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList<ItemHistoryTrx> filteredList = new ArrayList<>();

                for (ItemHistoryTrx model : trxListTransaksi) {
                    final String text1 = model.getTrxTujuan().toLowerCase();

                    if (text1.contains(query)) {
                        filteredList.add(model);
                    }
                }

                adapterTransaksi = new HistoryTrxAdapter(HistoryActivity.this, filteredList);
                recyclerView.setAdapter(adapterTransaksi);
                adapterTransaksi.notifyDataSetChanged();
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
