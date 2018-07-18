package com.eklanku.otuChat.ui.activities.payment.laporan;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.ProgressDialog;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.fragments.payment.TableFragment;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailHistosryOTU;
import com.eklanku.otuChat.ui.activities.payment.models.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.fragments.payment.TableFragment;

import java.util.HashMap;
import java.util.List;

public class HistoryBalanceActivity extends AppCompatActivity {

    //Rina
    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID, strAccessToken, strApIUse = "OTU";

    String mutasi_id, sisa_saldo, uang_masuk, uang_keluar, keterangan, tgl_mutasi, mutasi_status;
    String requestID;

    Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporansaldo);


        Button btnBack  = (Button) findViewById(R.id.btnHistoryBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ButterKnife.bind(this);

        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        if (savedInstanceState == null) {
            Bundle bundl = new Bundle();
            bundl.putString("laporan", "balance");

            TableFragment dv = new TableFragment(strUserID, strAccessToken);
            dv.setArguments(bundl);

            getSupportFragmentManager().beginTransaction().add(R.id.activity_container, dv, TableFragment.class.getSimpleName()).commit();
        }

        hideNavigationBar();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void hideNavigationBar() {

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
                .SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
                .SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View
                    .OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View
                    .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    //====================================== get history
    /*public void loadHistoryTransaksi() {
        loadingDialog = ProgressDialog.show(HistoryBalanceActivity.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);


        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx("EKL0003937", strApIUse, "cf35cc06989f9f84ef48c0e18ca4ee91", "3");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    Log.d("OPPO-1", "onResponse: "+response.body().getListData());
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        for (int i = 0; i < result.size(); i++) {
                            mutasi_id = result.get(i).getMutasi_id();
                            sisa_saldo = result.get(i).getSisa_saldo();
                            uang_masuk = result.get(i).getUang_masuk();
                            uang_keluar = result.get(i).getUang_keluar();
                            keterangan = result.get(i).getKeterangan();
                            tgl_mutasi = result.get(i).getTgl_mutasi();
                            mutasi_status = result.get(i).getMutasi_status();

                            Log.d("OPPO-1", "onResponse: "+mutasi_id);
                            Log.d("OPPO-1", "onResponse: "+sisa_saldo);
                            Log.d("OPPO-1", "onResponse: "+uang_masuk);
                            Log.d("OPPO-1", "onResponse: "+uang_keluar);
                            Log.d("OPPO-1", "onResponse: "+keterangan);
                            Log.d("OPPO-1", "onResponse: "+tgl_mutasi);
                            Log.d("OPPO-1", "onResponse: "+mutasi_status);

                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
//                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }*/
}
