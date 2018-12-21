package com.eklanku.otuChat.ui.activities.payment.json;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.fragments.payment.TableFragment;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailHistosryOTU;
import com.eklanku.otuChat.ui.activities.payment.models.DataHistoryOTU;
import com.eklanku.otuChat.ui.activities.payment.tableview.model.CellModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebServiceHandler {

    private TableFragment mainFragment;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID, strAccessToken, strApIUse = "OTU";

    String mutasi_id, sisa_saldo, uang_masuk, uang_keluar, keterangan, tgl_mutasi, mutasi_status;
    String id_member, invoice, tgl, vstatus, harga, tujuan, vsn, mbr_name, tgl_sukses;
    String jumlah_deposit, status_deposit, codeunix, bank, tgl_deposit;

    public WebServiceHandler(TableFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    public void loadData() {
        if(this.mainFragment.getTableName().contains("balance")) {
            loaddata_balance();
        }else if(this.mainFragment.getTableName().contains("trx")){
            loaddata_trx();
        }else if(this.mainFragment.getTableName().contains("deposit")){
            loaddata_deposit();
        }else if(this.mainFragment.getTableName().contains("penarikan")){
            loaddata_penarikan();
        }else{
            loaddata_bonus();
        }

    }

    private void loaddata_balance(){
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        mainFragment.showProgressDialog();
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(mainFragment.strUserID, strApIUse, mainFragment.strAccessToken, "3");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                mainFragment.hideProgressDialog();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();
                        List<List<CellModel>> list = new ArrayList<>();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        for (int i = 0; i < ROW_SIZE; i++) {

                            mutasi_id = result.get(i).getMutasi_id();
                            keterangan = result.get(i).getKeterangan();
                            tgl_mutasi = result.get(i).getTgl_mutasi();
                            mutasi_status = result.get(i).getMutasi_status().toString();

                            String valDate = "";
                            if(!tgl_mutasi.equals("") || !tgl_mutasi.equals("null")){
                                String parsTgl[] = tgl_mutasi.split(" ");
                                String parsTgl2[] = parsTgl[0].split("-");

                                String tanggal = parsTgl2[2];
                                String bulan = parsTgl2[1];
                                String tahun = parsTgl2[0];

                                valDate = tanggal+"-"+bulan+"-"+tahun+" "+parsTgl[1];
                            }


                            //ganti status active jadi success
                            String vMutasi_Status = "";
                            if(mutasi_status.equals("Active")){
                                vMutasi_Status = "sukses";
                            }else{
                                vMutasi_Status = mutasi_status;
                            }

                            //

                            List<CellModel> cellList = new ArrayList<>();
                            cellList.add(new CellModel("0", valDate));
                            cellList.add(new CellModel("1", mutasi_id));
                            cellList.add(new CellModel("2", format.format(result.get(i).getUang_masuk())));
                            cellList.add(new CellModel("3", format.format(result.get(i).getUang_keluar())));
                            cellList.add(new CellModel("4", format.format(result.get(i).getSisa_saldo())));
                            cellList.add(new CellModel("5", vMutasi_Status));
                            cellList.add(new CellModel("6", keterangan));
                            list.add(cellList);
                        }

                        TextView txtInfo = (TextView) mainFragment.getActivity().findViewById(R.id.txtHistoryInfo);
                        if (ROW_SIZE > 0) {
                            mainFragment.populatedTableView(list);
                            txtInfo.setVisibility(View.GONE);
                        } else
                            txtInfo.setText("Data history saldo Anda tidak ada...");

                    } else {
                        Toast.makeText(mainFragment.getContext(), "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                mainFragment.hideProgressDialog();
                Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    private void loaddata_trx(){
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        Log.d("OPPO-1", "loaddata_balance: "+mainFragment.strUserID);
        Log.d("OPPO-1", "loaddata_balance: "+mainFragment.strAccessToken);
        mainFragment.showProgressDialog();
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(mainFragment.strUserID, strApIUse, mainFragment.strAccessToken, "1");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                mainFragment.hideProgressDialog();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();
                        List<List<CellModel>> list = new ArrayList<>();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        for (int i = 0; i < ROW_SIZE; i++) {

                            id_member = result.get(i).getId_member();
                            invoice = result.get(i).getInvoice();
                            tgl = result.get(i).getTgl();
                            vstatus = result.get(i).getVstatus();
                            //harga = result.get(i).getHarga();
                            tujuan = result.get(i).getTujuan();
                            keterangan = result.get(i).getKeterangan();
                            vsn = result.get(i).getVsn();
                            mbr_name = result.get(i).getMbr_name();
                            tgl_sukses = result.get(i).getTgl_sukses();

                            String valDate = "";
                            if(!tgl.equals("") || !tgl.equals("null")){
                                String parsTgl[] = tgl.split(" ");
                                String parsTgl2[] = parsTgl[0].split("-");

                                String tanggal = parsTgl2[2];
                                String bulan = parsTgl2[1];
                                String tahun = parsTgl2[0];

                                valDate = tanggal+"-"+bulan+"-"+tahun+" "+parsTgl[1];
                            }

                            //ganti status active jadi success
                            String vMutasi_Status = "";
                            if(vstatus.equals("Active")){
                                vMutasi_Status = "sukses";
                            }else{
                                vMutasi_Status = vstatus;
                            }

                            List<CellModel> cellList = new ArrayList<>();
                            cellList.add(new CellModel("0", valDate));
                            cellList.add(new CellModel("1", tujuan));
                            cellList.add(new CellModel("2", format.format(result.get(i).getHarga())));
                            cellList.add(new CellModel("3", vMutasi_Status));
                            cellList.add(new CellModel("4", keterangan));
                            list.add(cellList);
                        }

                        TextView txtInfo = (TextView) mainFragment.getActivity().findViewById(R.id.txtHistoryInfo);
                        if (ROW_SIZE > 0) {
                            mainFragment.populatedTableView(list);
                            txtInfo.setVisibility(View.GONE);
                        } else
                            txtInfo.setText("Data history transaksi Anda tidak ada...");

                    } else {
                        Toast.makeText(mainFragment.getContext(), "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                mainFragment.hideProgressDialog();
                Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }


    private void loaddata_deposit(){
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        mainFragment.showProgressDialog();
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(mainFragment.strUserID, strApIUse, mainFragment.strAccessToken, "2");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                mainFragment.hideProgressDialog();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();
                        List<List<CellModel>> list = new ArrayList<>();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        for (int i = 0; i < ROW_SIZE; i++) {

                            //jumlah_deposit = result.get(i).getJumlah_deposit();
                            status_deposit = result.get(i).getStatus_deposit();
                            codeunix = result.get(i).getCodeunix();
                            bank = result.get(i).getBank();
                            tgl_deposit = result.get(i).getTgl_deposit();

                            String valDate = "";
                            if(!tgl_deposit.equals("") || !tgl_deposit.equals("null")){
                                String parsTgl[] = tgl_deposit.split(" ");
                                String parsTgl2[] = parsTgl[0].split("-");

                                String tanggal = parsTgl2[2];
                                String bulan = parsTgl2[1];
                                String tahun = parsTgl2[0];

                                valDate = tanggal+"-"+bulan+"-"+tahun+" "+parsTgl[1];
                            }

                            //ganti status active jadi success
                            String vMutasi_Status = "";
                            if(status_deposit.equals("Active")){
                                vMutasi_Status = "sukses";
                            }else{
                                vMutasi_Status = status_deposit;
                            }

                            List<CellModel> cellList = new ArrayList<>();
                            cellList.add(new CellModel("0", valDate));
                            cellList.add(new CellModel("1", bank));
                            cellList.add(new CellModel("2", format.format(result.get(i).getJumlah_deposit())));
                            cellList.add(new CellModel("3", codeunix));
                            cellList.add(new CellModel("4", vMutasi_Status));



                            list.add(cellList);
                        }

                        TextView txtInfo = (TextView) mainFragment.getActivity().findViewById(R.id.txtHistoryInfo);
                        if (ROW_SIZE > 0) {
                            mainFragment.populatedTableView(list);
                            txtInfo.setVisibility(View.GONE);
                        } else
                            txtInfo.setText("Data history deposit Anda tidak ada...");

                    } else {
                        Toast.makeText(mainFragment.getContext(), "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                mainFragment.hideProgressDialog();
                Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    private void loaddata_penarikan(){
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        Log.d("OPPO-1", "loaddata_balance: "+mainFragment.strUserID);
        Log.d("OPPO-1", "loaddata_balance: "+mainFragment.strAccessToken);
        mainFragment.showProgressDialog();
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(mainFragment.strUserID, strApIUse, mainFragment.strAccessToken, "4");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                mainFragment.hideProgressDialog();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();
                        List<List<CellModel>> list = new ArrayList<>();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        for (int i = 0; i < ROW_SIZE; i++) {

                            //ganti status active jadi success
                            String vMutasi_Status = "";
                            if(result.get(i).getStatus_penarikan().equals("Active")){
                                vMutasi_Status = "sukses";
                            }else{
                                vMutasi_Status = result.get(i).getStatus_penarikan();
                            }

                            String valDate = "";
                            if(!result.get(i).getTgl_penarikan().equals("") || !result.get(i).getTgl_penarikan().equals("null")){
                                String parsTgl[] = result.get(i).getTgl_penarikan().split(" ");
                                String parsTgl2[] = parsTgl[0].split("-");

                                String tanggal = parsTgl2[2];
                                String bulan = parsTgl2[1];
                                String tahun = parsTgl2[0];

                                valDate = tanggal+"-"+bulan+"-"+tahun+" "+parsTgl[1];
                            }

                            List<CellModel> cellList = new ArrayList<>();
                            cellList.add(new CellModel("0", valDate));
                            cellList.add(new CellModel("1", result.get(i).getBank()));
                            cellList.add(new CellModel("2", format.format(result.get(i).getJml_penarikan())));
                            cellList.add(new CellModel("3", result.get(i).getAtas_nama()));
                            cellList.add(new CellModel("4", vMutasi_Status));


                            list.add(cellList);
                        }

                        TextView txtInfo = (TextView) mainFragment.getActivity().findViewById(R.id.txtHistoryInfo);
                        if (ROW_SIZE > 0) {
                            mainFragment.populatedTableView(list);
                            txtInfo.setVisibility(View.GONE);
                        } else
                            txtInfo.setText("Data history penarikan Anda tidak ada...");

                    } else {
                        Toast.makeText(mainFragment.getContext(), "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                mainFragment.hideProgressDialog();
                Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    private void loaddata_bonus(){
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        mainFragment.showProgressDialog();
        Call<DataHistoryOTU> dataCall = mApiInterfacePayment.getHistoryTrx(mainFragment.strUserID, strApIUse, mainFragment.strAccessToken, "5");
        dataCall.enqueue(new Callback<DataHistoryOTU>() {
            @Override
            public void onResponse(Call<DataHistoryOTU> call, Response<DataHistoryOTU> response) {
                mainFragment.hideProgressDialog();

                if (!response.equals(null) && response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<DataDetailHistosryOTU> result = response.body().getListData();
                        int ROW_SIZE = result.size();
                        List<List<CellModel>> list = new ArrayList<>();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);

                        for (int i = 0; i < ROW_SIZE; i++) {
                            //ganti status active jadi success
                            String vMutasi_Status = "";
                            if(result.get(i).getStatus_bonus().equals("Active")){
                                vMutasi_Status = "sukses";
                            }else{
                                vMutasi_Status = result.get(i).getStatus_bonus();
                            }

                            String valDate = "";
                            if(!result.get(i).getTgl_perolehan().equals("") || !result.get(i).getTgl_perolehan().equals("null")){

                                String parsTgl[] = result.get(i).getTgl_perolehan().split(" ");
                                String parsTgl2[] = parsTgl[0].split("-");
                                String tanggal = parsTgl2[2];
                                String bulan = parsTgl2[1];
                                String tahun = parsTgl2[0];

                                valDate = tanggal+"-"+bulan+"-"+tahun+" "+parsTgl[1];
                            }

                            List<CellModel> cellList = new ArrayList<>();
                            cellList.add(new CellModel("0", valDate));
                            cellList.add(new CellModel("1", result.get(i).getKeterangan()));
                            cellList.add(new CellModel("2", result.get(i).getJenis_bonus()));
                            cellList.add(new CellModel("3", vMutasi_Status));
                            cellList.add(new CellModel("4", result.get(i).getJml_bonus()));
                            list.add(cellList);
                        }

                        TextView txtInfo = (TextView) mainFragment.getActivity().findViewById(R.id.txtHistoryInfo);
                        if (ROW_SIZE > 0) {
                            mainFragment.populatedTableView(list);
                            txtInfo.setVisibility(View.GONE);
                        } else
                            txtInfo.setText("Data history bonus Anda tidak ada...");

                    } else {
                        Toast.makeText(mainFragment.getContext(), "Terjadi kesalahan:\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataHistoryOTU> call, Throwable t) {
                mainFragment.hideProgressDialog();
                Toast.makeText(mainFragment.getContext(), mainFragment.getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

}
