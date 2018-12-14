package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataAllProduct;
import com.eklanku.otuChat.ui.activities.payment.models.DataProduct;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransVouchergame_opsi extends AppCompatActivity implements View.OnClickListener {

    ImageButton ibMegasus, ibGarena, ibGemscool, ibMobilelegend, ibGoogleplay, ibLyto, ibMol, ibCherry, ibPubg, ibAeria, ibWavegame;
    String strUserID, strAccessToken;

    ApiInterfacePayment apiInterfacePayment;
    PreferenceManager preferenceManager;

    Utils utilsAlert;
    String titleAlert = "VOUCHER GAME";
    LinearLayout layoutView;
    ProgressBar progressBar;
    TextView tvEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_game);

        utilsAlert = new Utils(TransVouchergame_opsi.this);

        ibMegasus = findViewById(R.id.btnVGMegasus);
        ibGarena = findViewById(R.id.btnVGGarena);
        ibGemscool = findViewById(R.id.btnVGGamescool);
        ibMobilelegend = findViewById(R.id.btnVGMobilelegend);
        ibGoogleplay = findViewById(R.id.btnVGGoogleplay);
        ibLyto = findViewById(R.id.btnVGLyto);
        ibMol = findViewById(R.id.btnVGMol);
        ibCherry = findViewById(R.id.btnVGCherry);
        ibPubg = findViewById(R.id.btnVGPUBG);
        ibAeria = findViewById(R.id.btnAeria);
        ibWavegame = findViewById(R.id.btnWavegame);

        layoutView = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.progress);
//        tvEmpty = findViewById(R.id.tv_empty);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(PreferenceManager.KEY_USERID);
        strAccessToken = user.get(PreferenceManager.KEY_ACCESS_TOKEN);

        ibMegasus.setOnClickListener(this);
        ibGarena.setOnClickListener(this);
        ibGemscool.setOnClickListener(this);
        ibMobilelegend.setOnClickListener(this);
        ibGoogleplay.setOnClickListener(this);
        ibLyto.setOnClickListener(this);
        ibMol.setOnClickListener(this);
        ibCherry.setOnClickListener(this);
        ibPubg.setOnClickListener(this);
        ibAeria.setOnClickListener(this);
        ibWavegame.setOnClickListener(this);


        getProduct_Game();
    }


    @Override
    public void onClick(View v) {

        String jnsGame = "";
        String img = "";
        switch (v.getId()) {
            case R.id.btnVGMegasus:
                jnsGame = "MEGAXUS";
                img = "megasus";
                break;
            case R.id.btnVGGarena:
                jnsGame = "GARENA";
                img = "garena";
                break;
            case R.id.btnVGGamescool:
                jnsGame = "GEMSCOOL";
                img = "gemscool";
                break;
            case R.id.btnVGMobilelegend:
                jnsGame = "MOBILE LEGENDS";
                img = "mobilelegend";
                break;
            case R.id.btnVGGoogleplay:
                jnsGame = "GOOGLE PLAY ID";
                img = "googleplay";
                break;
            case R.id.btnVGLyto:
                jnsGame = "LYTO";
                img = "lyto";
                break;
            case R.id.btnVGMol:
                jnsGame = "MOL";
                img = "molpoint";
                break;
            case R.id.btnVGCherry:
                jnsGame = "CHERRY";
                img = "cherry";
                break;
            case R.id.btnVGPUBG:
                jnsGame = "PUBG";
                img = "pubg";
                break;
            case R.id.btnAeria:
                jnsGame = "AERIA";
                img = "aeria";
                break;
            case R.id.btnWavegame:
                jnsGame = "WAVEGAME";
                img = "wavegame";
                break;
        }

        detailProduct(jnsGame, img);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payment_transaction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_transaction_confirmation:
                Toast.makeText(this, "Konfirmasi pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_transaction_evidence:
                Toast.makeText(this, "Kirim bukti pembayaran", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ArrayList<String> listCode;
    ArrayList<String> listPrice;
    ArrayList<String> listName;
    ArrayList<String> listEP;
    ArrayList<String> listisActive;
    ArrayList<String> listType;
    ArrayList<String> listProviderProduct;

    public void getProduct_Game() {
        showProgress(true);
        Log.d("OPPO-1", "getProduct_Game: " + strUserID + ", " + strAccessToken);
        Call<DataAllProduct> product_game = apiInterfacePayment.getproduct_game(strUserID, strAccessToken, "OTU");
        product_game.enqueue(new Callback<DataAllProduct>() {
            @Override
            public void onResponse(Call<DataAllProduct> call, Response<DataAllProduct> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    listCode = new ArrayList<>();
                    listPrice = new ArrayList<>();
                    listName = new ArrayList<>();
                    listEP = new ArrayList<>();
                    listisActive = new ArrayList<>();
                    listType = new ArrayList<>();
                    listProviderProduct = new ArrayList<>();
                    listCode.clear();
                    listPrice.clear();
                    listName.clear();
                    listEP.clear();
                    listisActive.clear();
                    listType.clear();
                    listProviderProduct.clear();
                    String status = response.body().getStatus();
                    String respMessage = response.body().getRespMessage();
                    Log.d("OPPO-1", "status: " + status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        List<DataProduct> data = response.body().getData();
                        for (int i = 0; i < data.size(); i++) {
                            listCode.add(data.get(i).getCode());
                            listPrice.add(data.get(i).getPrice());
                            listName.add(data.get(i).getName());
                            listEP.add(data.get(i).getEp());
                            listisActive.add(data.get(i).getIsActive());
                            listType.add(data.get(i).getType());
                            listProviderProduct.add(data.get(i).getProvider());
                        }
                        Log.d("OPPO-1", "onResponse: " + listCode);
                    } else {
                        utilsAlert.globalDialog(TransVouchergame_opsi.this, titleAlert, respMessage);
                    }
                } else {
                    utilsAlert.globalDialog(TransVouchergame_opsi.this, titleAlert, "1. " + getResources().getString(R.string.error_api));
                }
            }

            @Override
            public void onFailure(Call<DataAllProduct> call, Throwable t) {
                showProgress(false);
                Log.d("OPPO-1", "onFailure: " + t.getMessage());
            }
        });
    }


    //public void detailProduct(String provider) {
    public void detailProduct(String provider, String imgOpr) {
        ArrayList<String> a, b, c, d, e;
        a = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>();
        d = new ArrayList<>();
        e = new ArrayList<>();
        a.clear();
        b.clear();
        c.clear();
        d.clear();
        e.clear();
        for (int i = 0; i < listCode.size(); i++) {
            Log.d("OPPO-1", "detailProduct: " + listCode);
           /* Log.d("OPPO-1", "detailProduct>>: " + listProviderProduct.get(i) + " > " + provider);
            for (int i = 0; i < listCode.size(); i++) {*/
            if (listProviderProduct.get(i).equalsIgnoreCase(provider)) {
                Log.d("OPPO-1", "detailProduct>>: " + listProviderProduct.get(i) + " > " + provider);
                a.add(listName.get(i));
                b.add(listPrice.get(i));
                c.add(listEP.get(i));
                d.add(listProviderProduct.get(i));
                e.add(listCode.get(i));
            }
        }
        Intent product = new Intent(getApplicationContext(), TransVouchergame_product.class);
        product.putExtra("listName", a);
        product.putExtra("listPrice", b);
        product.putExtra("listEP", c);
        product.putExtra("listProvider", d);
        product.putExtra("listCode", e);
        product.putExtra("jnsGame", provider);
        product.putExtra("imgOpr", imgOpr);
        startActivity(product);

    }
//    }
    //}


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
