package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataTransBeli;
import com.eklanku.otuChat.ui.activities.payment.models.LoadDataResponseProduct;
import com.eklanku.otuChat.ui.activities.payment.models.TransBeliResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.adapters.payment.SpinnerAdapter;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransVouchergame_product extends AppCompatActivity{

    ListView lvProductGame;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;
    ApiInterface mApiInterface;

    Dialog loadingDialog;
    String strUserID, strAccessToken, strAplUse = "OTU";

    Bundle extras;
    Utils utilsAlert;
    String titleAlert = "Voucher Game";
    String code;

    EditText noPel, transKe;
    Button btnBayar;
    Button btnYes, btnNo;
    TextView txtnomor, txtvoucher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_game_product);

        utilsAlert = new Utils(TransVouchergame_product.this);
        extras = getIntent().getExtras();

        lvProductGame = findViewById(R.id.listProductGame);
        btnBayar = findViewById(R.id.btnTransVoucherBayar);
        noPel = findViewById(R.id.txtTransVoucherNo);
        transKe = findViewById(R.id.txtTransKe);
        transKe.setText("1");

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        String jnsGame = extras.getString("jnsGame");

        Log.d("OPPO-1", "onCreate: "+jnsGame);

        loadProduct(strUserID, strAccessToken, strAplUse, jnsGame);

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!validateIdpel()) {
//                    return;
//                }

                final Dialog dialog = new Dialog(TransVouchergame_product.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_alert_dialog);
                dialog.setCancelable(false);
                dialog.setTitle("Peringatan Transaksi!!!");

                btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                btnNo = (Button) dialog.findViewById(R.id.btn_no);
                txtnomor = (TextView) dialog.findViewById(R.id.txt_nomor);
                txtvoucher = (TextView) dialog.findViewById(R.id.txt_voucher);
                txtnomor.setText(noPel.getText().toString());
                txtvoucher.setText(code);

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cek_transaksi();
                        dialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return;
            }
        });

    }


    public void loadProduct(String userID, String accessToken, String aplUse, String voucherGame){
        try{
            loadingDialog = ProgressDialog.show(TransVouchergame_product.this, "Harap Tunggu", "Mengambil Data...");
            loadingDialog.setCanceledOnTouchOutside(true);

            Call<LoadDataResponseProduct> gameVoucher = apiInterfacePayment.getLoadProduct(userID, accessToken, aplUse, voucherGame);
            gameVoucher.enqueue(new Callback<LoadDataResponseProduct>() {
                @Override
                public void onResponse(Call<LoadDataResponseProduct> call, Response<LoadDataResponseProduct> response) {
                    loadingDialog.dismiss();
                    if(response.isSuccessful()){
                        String status = response.body().getStatus();
                        String respMessage = response.body().getRespMessage();
                        if(status.equalsIgnoreCase("SUCCESS")){

                            List<String> listPrice = new ArrayList<String>();
                            List<String> listNama = new ArrayList<String>();
                            List<String> listEp = new ArrayList<String>();
                            listPrice.clear();
                            listNama.clear();
                            listEp.clear();
                            final List<DataProduct> products = response.body().getProducts();
                            for (int i = 0; i < products.size(); i++) {
                                String name = products.get(i).getName();
                                String price = products.get(i).getPrice();
                                String ep = products.get(i).getEp();

                                listNama.add(name);
                                listEp.add(ep);
                                listPrice.add(price);
                            }

                            SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), products);
                            lvProductGame.setAdapter(adapter);
                            lvProductGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    code = products.get(position).getCode();
                                }
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<LoadDataResponseProduct> call, Throwable t) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void cek_transaksi() {
        loadingDialog = ProgressDialog.show(TransVouchergame_product.this, "Harap Tunggu", "Cek Transaksi...");
        loadingDialog.setCanceledOnTouchOutside(true);

        Call<TransBeliResponse> transBeliCall = apiInterfacePayment.postTopup(strUserID, strAccessToken, strAplUse, noPel.getText().toString(), noPel.getText().toString(), "", "", code);
        transBeliCall.enqueue(new Callback<TransBeliResponse>() {
            @Override
            public void onResponse(Call<TransBeliResponse> call, Response<TransBeliResponse> response) {
                loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();

                    if (status.equals("SUCCESS")) {
                        List<DataTransBeli> trans = response.body().getResult();
                        Intent inKonfirmasi = new Intent(getBaseContext(), TransKonfirmasi.class);
                        inKonfirmasi.putExtra("userID", response.body().getUserID());//
                        inKonfirmasi.putExtra("accessToken", strAccessToken);//
                        inKonfirmasi.putExtra("status", status);//
                        inKonfirmasi.putExtra("respMessage", response.body().getRespMessage());//
                        inKonfirmasi.putExtra("respTime", response.body().getTransactionDate());//
                        inKonfirmasi.putExtra("productCode", "VOUCHER GAME");//
                        inKonfirmasi.putExtra("billingReferenceID", response.body().getTransactionID());//
                        inKonfirmasi.putExtra("customerID", response.body().getMSISDN());//
                        inKonfirmasi.putExtra("customerMSISDN", response.body().getMSISDN());//
                        inKonfirmasi.putExtra("customerName", "");
                        inKonfirmasi.putExtra("period", "");
                        inKonfirmasi.putExtra("policeNumber", "");
                        inKonfirmasi.putExtra("lastPaidPeriod", "");
                        inKonfirmasi.putExtra("tenor", "");
                        inKonfirmasi.putExtra("lastPaidDueDate", "");
                        inKonfirmasi.putExtra("usageUnit", "TOPUP");
                        inKonfirmasi.putExtra("penalty", "");
                        inKonfirmasi.putExtra("payment", response.body().getNominal());//
                        inKonfirmasi.putExtra("minPayment", "");
                        inKonfirmasi.putExtra("minPayment", "");
                        inKonfirmasi.putExtra("additionalMessage", response.body().getAdditionalMessage());
                        inKonfirmasi.putExtra("billing", response.body().getNominal());//
                        inKonfirmasi.putExtra("sellPrice", "");
                        inKonfirmasi.putExtra("adminBank", "0");
                        inKonfirmasi.putExtra("profit", "");
                        startActivity(inKonfirmasi);
                        finish();
                    } else {
                        utilsAlert.globalDialog(TransVouchergame_product.this, titleAlert, error);
                        // Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    utilsAlert.globalDialog(TransVouchergame_product.this, titleAlert, getResources().getString(R.string.error_api));
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransBeliResponse> call, Throwable t) {
                loadingDialog.dismiss();
                utilsAlert.globalDialog(TransVouchergame_product.this, titleAlert, getResources().getString(R.string.error_api));
                //Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }
}
