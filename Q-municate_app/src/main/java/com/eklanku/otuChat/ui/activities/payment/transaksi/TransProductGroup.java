package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.util.Log;

import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.ui.activities.payment.models.DataProductGroup;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;

import retrofit2.Call;

public class TransProductGroup {
    ApiInterfacePayment mApiInterfacePayment;
    ApiClientPayment apiClientPayment;
    String productGroup;

    public String checkProductGroup(){
        mApiInterfacePayment = apiClientPayment.getClient().create(ApiInterfacePayment.class);
        try{
          //  Call<DataProductGroup> transBeliCall = mApiInterfacePayment.postTransBeli(PreferenceUtil.getNumberPhone(this)), selected_operator, nominal, txtNo.getText().toString(), "asuransibyr");

        }catch (Exception e){
            Log.e("ERROR GET PRODUCT GROUP", "checkProductGroup: "+e.getMessage());
        }

        return productGroup;
    }
}
