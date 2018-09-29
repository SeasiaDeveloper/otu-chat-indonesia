package com.eklanku.otuChat.ui.activities.payment.topup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.TopupPayResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.ListViewAdapter;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerBankAdapter;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.models.TopupDetailM;
import com.eklanku.otuChat.ui.activities.payment.models.TopupOrderResponse;
import com.eklanku.otuChat.utils.Utils;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TopupOrder extends AppCompatActivity {

    ListView listView;
    ArrayList<String> nama_paket, id_paket;
    Dialog loadingDialog;

    ApiInterface mApiInterface;
    ListViewAdapter listViewAdapter;
    ArrayList<String> nominal;

    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";

    Spinner spnDataBank;
    EditText edNominal;
    Button btnTopup;

    String nama, norek, an, error, strMsg;
    CheckBox ck_Setuju;
    LinearLayout tx_syarat;

    Utils utilsAlert;
    String titleAlert = "Topup";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_order);

        utilsAlert = new Utils(TopupOrder.this);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        spnDataBank = findViewById(R.id.spinner_data_bank);
        edNominal = findViewById(R.id.edittext_nominal);
        btnTopup = findViewById(R.id.btn_topup);
        ck_Setuju = findViewById(R.id.cksetuju);


        //get userid and token from preference
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        //loadData();
        loadDataBank();
        //initializeResources();

        ck_Setuju.setChecked(false);
        btnTopup.setEnabled(false);
        btnTopup.setBackgroundResource(R.drawable.custom_round);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Expandable textview
        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);
        expTv1.setText(getString(R.string.syarat_ketentuan_detail));


        ck_Setuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ck_Setuju.isChecked()){
                    btnTopup.setBackgroundResource(R.drawable.background_round_corner_green);
                    btnTopup.setEnabled(true);
                }else{
                    btnTopup.setBackgroundResource(R.drawable.custom_round);
                    btnTopup.setEnabled(false);
                }
            }
        });

        btnTopup.setOnClickListener(new BayarButtonListener());

    }

   /* private void initializeResources() {

        listView = (ListView) findViewById(R.id.listPaketTopup);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), TopupBilling.class);
                intent.putExtra("id_paket", id_paket.get(position));
                intent.putExtra("nm_paket", nama_paket.get(position));
                startActivity(intent);
                finish();
            }
        });
    }*/


    private void loadDataBank() {
        loadingDialog = ProgressDialog.show(TopupOrder.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TopupOrderResponse> dataCall = mApiInterfacePayment.postInquiryBankList(strUserID, strAccessToken, strApIUse);
        dataCall.enqueue(new Callback<TopupOrderResponse>() {
            @Override
            public void onResponse(Call<TopupOrderResponse> call, Response<TopupOrderResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        final List<TopupDetailM> result = response.body().getBanklist();
                        SpinnerBankAdapter adapter = new SpinnerBankAdapter(getApplicationContext(), result);
                        spnDataBank.setAdapter(adapter);
                        spnDataBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String isActive = result.get(position).getIsactive();
                                if(isActive.equalsIgnoreCase("Live")){
                                    norek = result.get(position).getNorec();
                                    nama = result.get(position).getBank();
                                    an = result.get(position).getAnbank();


                                }else{
                                    //Toast.makeText(TopupOrder.this, "No Rekening tidak aktif", Toast.LENGTH_SHORT).show();
                                    utilsAlert.globalDialog(TopupOrder.this, titleAlert, "No Rekening tidak aktif");

                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        utilsAlert.globalDialog(TopupOrder.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TopupOrder.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupOrderResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TopupOrder.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    private class BayarButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            /*Intent intent = new Intent(TopupOrder.this, AlertSyarat.class);
            intent.putExtra("nominal", edNominal.getText().toString());
            intent.putExtra("nama", nama);
            startActivity(intent);*/
            depositOrder();

        }
    }


    private void depositOrder() {
        loadingDialog = ProgressDialog.show(TopupOrder.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TopupPayResponse> dataCall = mApiInterfacePayment.postDepositOrder(strUserID, strAccessToken, strApIUse, nama, edNominal.getText().toString());
        dataCall.enqueue(new Callback<TopupPayResponse>() {
            @Override
            public void onResponse(Call<TopupPayResponse> call, Response<TopupPayResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        Intent intent = new Intent(getBaseContext(), TopupBilling.class);
                        intent.putExtra("bank", nama);
                        intent.putExtra("nominal", response.body().getNominal());
                        intent.putExtra("pesan",error );
                        startActivity(intent);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TopupOrder.this, titleAlert, error);
                        //Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TopupOrder.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupPayResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TopupOrder.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }

    /*====================================payment lama==================================================*/
/*
    private void loadData() {
        loadingDialog = ProgressDialog.show(TopupOrder.this, "Harap Tunggu", "Mengambil Data...");
        loadingDialog.setCanceledOnTouchOutside(true);
        Call<TopupOrderResponse> dataCall = mApiInterface.getPaketTopup();
        dataCall.enqueue(new Callback<TopupOrderResponse>() {
            @Override
            public void onResponse(Call<TopupOrderResponse> call, Response<TopupOrderResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status   = response.body().getStatus();
                    String error    = response.body().getError();

                    if ( status.equals("OK") ) {
                        final List<PaketTopup> result = response.body().getPaketTopups();
                        id_paket = new ArrayList<>();
                        nama_paket = new ArrayList<>();
                        nominal = new ArrayList<>();

                        for ( int i=0; i<result.size(); i++ ) {
                            id_paket.add(result.get(i).getId());
                            Double total = Double.valueOf(result.get(i).getHargaPaket());
                            Locale localeID = new Locale("in", "ID");
                            NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                            String rupiah = format.format(total);
                            String r = result.get(i).getNamaPaket().replace(" rb","k");
                            nama_paket.add("Paket " + r);
                            nominal.add(rupiah);
                        }

                        listViewAdapter = new ListViewAdapter(getApplicationContext(),nama_paket,nominal);
                        listView.setAdapter(listViewAdapter);
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, nama_paket);
//                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getBaseContext(), "Terjadi kesalahan:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopupOrderResponse> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_LOADDATA", t.getMessage().toString());
            }
        });
    }
    /*===========================================end payment lama=====================================================*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payment_topup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_topup_confirmation:
                Toast.makeText(this, "Konfirmasi pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_topup_evidence:
                Toast.makeText(this, "Kirim bukti pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
