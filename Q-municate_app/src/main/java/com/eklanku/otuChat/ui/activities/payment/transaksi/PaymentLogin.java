package com.eklanku.otuChat.ui.activities.payment.transaksi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.LoginResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.google.firebase.auth.FirebaseAuth;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.main.Utils;
import com.eklanku.otuChat.ui.activities.payment.models.LoginResponse;
import com.eklanku.otuChat.ui.activities.payment.models.RegisterResponse;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPassword;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentLogin extends AppCompatActivity {

    TextInputLayout //layoutEmail,
            layoutPass;
    EditText //txtEmail,
            txtPass;
    TextView lblDaftar,
            lblInfo;
    Button btnLogin;
    Dialog loadingDialog;

    private ApiInterface mApiInterface;
    private PreferenceManager preferenceManager;
    private ApiInterfacePayment apiInterfacePayment;
    private String strUserID, strToken, strSecurityCode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_login);

        ButterKnife.bind(this);

        initializeResources();

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        preferenceManager = new PreferenceManager(this);

        //get userid from preference
        //UNUSED BECAUSE request use hp number
        /*HashMap<String, String> user = preferenceManager.getUserDetails();
        strUserID = user.get(preferenceManager.KEY_USERID);*/
    }

    private void initializeResources() {
        layoutPass = (TextInputLayout) findViewById(R.id.txtLayoutLoginPassword);
        txtPass = (EditText) findViewById(R.id.txtLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        lblDaftar = (TextView) findViewById(R.id.lblLoginRegister);
        lblInfo = (TextView) findViewById(R.id.textResponPaymentLogin);

        btnLogin.setOnClickListener(new loginButtonListener());
        lblDaftar.setOnClickListener(new RegisterLabelListener());
    }

    private class loginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (
                    !validatePassword()) {
                return;
            }

            loadingDialog = ProgressDialog.show(PaymentLogin.this, "Harap Tunggu", "Melakukan proses login");
            loadingDialog.setCanceledOnTouchOutside(true);

            String pass = txtPass.getText().toString().trim();
            strToken = getCurrentTime();

            strSecurityCode = Utils.md5(strToken + Utils.md5(pass));
            //VOLLEY==========================
            //login(PreferenceUtil.getNumberPhone(this)), strToken, strSecurityCode, pass);
            //================================

            Log.d("OPPO-1", "userid:" + PreferenceUtil.getNumberPhone(PaymentLogin.this) + ", token:" + strSecurityCode + ", " + strToken);
            // RETROFIT=============================================
            Call<LoginResponse> userCall = apiInterfacePayment.postLogin(PreferenceUtil.getNumberPhone(PaymentLogin.this), strToken, strSecurityCode, pass);
            userCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    loadingDialog.dismiss();
                    lblInfo.setVisibility(View.GONE);
                    Log.d("AYIK", "login:payment->" + response+ response.body());

                    Log.d("AYIK", "login:payment0->" + response.body().getStatus() + response.body().getRespMessage());

                    Log.d("AYIK", "login:payment1->" + "user:" + PreferenceUtil.getNumberPhone(PaymentLogin.this) + ", sec:" + strSecurityCode + ", token:" + strToken + "," + "pass:" + pass);

                    if (response.isSuccessful()) {
                        String status = response.body().getStatus();
                        String userID = response.body().getUserID();
                        String accessToken = response.body().getAccessToken();
                        String respMessage = response.body().getRespMessage();
                        String respTime = response.body().getRespTime();
                        Log.d("AYIK", "login:payment2->" + response.body().getStatus() + response.body().getRespMessage());
                        if (status.equals("SUCCESS")) {

                            //Toast.makeText(PaymentLogin.this, "userID" + userID + ", accessToken:" + accessToken, Toast.LENGTH_SHORT).show();
                            preferenceManager.createUserPayment(userID, accessToken, true);
                            PreferenceUtil.setLoginStatus(getApplicationContext(), true);
                            finish();
                            //Log.d("AYIK", "userid:" + userID + ", token:" + accessToken);
                        } else {
                            lblInfo.setText("Error:\n" + respMessage);
                            lblInfo.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                    lblInfo.setText(getResources().getString(R.string.error_api));
                    lblInfo.setVisibility(View.VISIBLE);
                }
            });
            // RETROFIT=============================================
        }
    }

    private class RegisterLabelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent inResetPass = new Intent(getBaseContext(), ResetPassword.class);
            startActivity(inResetPass);
            finish();
        }
    }

    private boolean validatePassword() {
        String pass = txtPass.getText().toString().trim();

        if (pass.isEmpty()) {
            layoutPass.setError("Kolom password tidak boleh kosong");
            requestFocus(txtPass);
            return false;
        }

        layoutPass.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static String getCurrentTime() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Makassar"));
            String currentDateTime = dateFormat.format(new Date()); // Find todays date


            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    //UNUSED IF RETROFIT NOT WORKING
   /* private void login(final String userid, final String token, final String securitycode, final String password) {
        StringRequest request = null;
        try {
            request = new StringRequest(Request.Method.POST, getResources().getString(R.string.url) + "login",
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("AYIK", "paymentlogin:response:" + response);
                            Toast.makeText(PaymentLogin.this, "SUKSES", Toast.LENGTH_SHORT).show();

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String msgError;
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        msgError = new String(error.networkResponse.data);
                        String message = Utils.responseMessage(statusCode, msgError);
                        Toast.makeText(PaymentLogin.this, "" + statusCode + ", " + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaymentLogin.this, "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
                    }
                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userID", userid);
                    params.put("token", token);
                    params.put("securityCode", securitycode);
                    params.put("passwd", password);
                    return params;
                }

            };


        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        // avoid retry
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyController.getInstance().addToRequestQueue(request);
    }*/

    /*=================================================RESET PASS LAMA============================*/
   /*private class RegisterLabelListener implements View.OnClickListener {
       @Override
       public void onClick(View v) {
           loadingDialog = ProgressDialog.show(PaymentLogin.this, "Harap Tunggu", "Cek Transaksi...");
           loadingDialog.setCanceledOnTouchOutside(true);

           Call<RegisterResponse> transDepositCall = mApiInterface.postResetPass(PreferenceUtil.getNumberPhone(this)));
           transDepositCall.enqueue(new Callback<RegisterResponse>() {
               @Override
               public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                   loadingDialog.dismiss();
                   if (response.isSuccessful()) {
                       String status = response.body().getStatus();
                       String error = response.body().getError();

                       if (status.equals("OK")) {
                           String msg = response.body().getMessage();
                           Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                       } else {
                           Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onFailure(Call<RegisterResponse> call, Throwable t) {
                   loadingDialog.dismiss();
                   Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
               }
           });

       }
   }

   /*================================================END RESET PASS==============================*/
}


