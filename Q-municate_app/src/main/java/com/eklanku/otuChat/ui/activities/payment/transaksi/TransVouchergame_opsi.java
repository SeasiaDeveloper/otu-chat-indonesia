package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.google.gson.JsonNull;

public class TransVouchergame_opsi extends AppCompatActivity implements View.OnClickListener {

    ImageButton ibMegasus, ibGarena, ibGemscool, ibMobilelegend, ibGoogleplay, ibLyto, ibMol, ibCherry;
    String strUserID, strAccessToken;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_game);

        ibMegasus = findViewById(R.id.btnVGMegasus);
        ibGarena = findViewById(R.id.btnVGGarena);
        ibGemscool = findViewById(R.id.btnVGGamescool);
        ibMobilelegend = findViewById(R.id.btnVGMobilelegend);
        ibGoogleplay = findViewById(R.id.btnVGGoogleplay);
        ibLyto = findViewById(R.id.btnVGLyto);
        ibMol = findViewById(R.id.btnVGMol);
        ibCherry = findViewById(R.id.btnVGCherry);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ibMegasus.setOnClickListener(this);
        ibGarena.setOnClickListener(this);
        ibGemscool.setOnClickListener(this);
        ibMobilelegend.setOnClickListener(this);
        ibGoogleplay.setOnClickListener(this);
        ibLyto.setOnClickListener(this);
        ibMol.setOnClickListener(this);
        ibCherry.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        String jnsGame = "";
        switch (v.getId()) {
            case R.id.btnVGMegasus:
                jnsGame = "MEGAXUS";
                break;
            case R.id.btnVGGarena:
                jnsGame = "GARENA";
                break;
            case R.id.btnVGGamescool:
                jnsGame = "GEMSCOOL";
                break;
            case R.id.btnVGMobilelegend:
                jnsGame = "MOBILE LEGENDS";
                break;
            case R.id.btnVGGoogleplay:
                jnsGame = "GOOGLE PLAY ID";
                break;
            case R.id.btnVGLyto:
                jnsGame = "LYTO";
                break;
            case R.id.btnVGMol:
                jnsGame = "MOL";
                break;
            case R.id.btnVGCherry:
                jnsGame = "CHERRY";
                break;
        }

        Intent product = new Intent(getApplicationContext(), TransVouchergame_product.class);
        product.putExtra("jnsGame", jnsGame);
        startActivity(product);
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
}
