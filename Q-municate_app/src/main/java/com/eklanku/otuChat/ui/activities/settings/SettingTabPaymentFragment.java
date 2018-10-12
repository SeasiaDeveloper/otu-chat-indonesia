package com.eklanku.otuChat.ui.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.DeleteAccount;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Profile;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPIN;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPassword;

public class SettingTabPaymentFragment extends Fragment {

    Button btnProfile, btnResetPIN, btnPass, btnNonaktifAccount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting_tab_payment, container, false);
        initializeResources(view);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Profile.class));
                // finish();
            }
        });
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ResetPassword.class));
                // finish();
            }
        });
        btnResetPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ResetPIN.class));
                //  finish();
            }
        });

        btnNonaktifAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DeleteAccount.class);
                startActivity(i);
                //finish();
            }
        });
        return view;
    }

    private void initializeResources(View view) {

       btnProfile = view.findViewById(R.id.btn_profile);
       btnResetPIN = view.findViewById(R.id.btn_reset_pin);
       btnPass = view.findViewById(R.id.btn_reset_pass);
       btnNonaktifAccount = view.findViewById(R.id.btn_non_aktif_account);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
