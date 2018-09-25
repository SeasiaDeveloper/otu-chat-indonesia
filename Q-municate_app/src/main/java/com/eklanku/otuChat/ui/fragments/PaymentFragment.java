package com.eklanku.otuChat.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.eklanku.otuChat.ui.activities.about.ContactUsActivity;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.RiwayatActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryBalanceActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryBonusActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryDespositActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryPenarikanActivity;
import com.eklanku.otuChat.ui.activities.payment.laporan.HistoryTrxActivity;
import com.eklanku.otuChat.ui.activities.payment.models.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Profile;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPIN;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPassword;
import com.eklanku.otuChat.ui.activities.payment.topup.AlertSyarat;
import com.eklanku.otuChat.ui.activities.payment.topup.TopupOrder;
import com.eklanku.otuChat.ui.activities.payment.transaksi.PaymentLogin;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransEtool;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPajak;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPulsa;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketData;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPln;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransSMS;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransVoucherGame;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransBpjs;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTv;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPdam;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTagihan;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTelkom;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransMultiFinance;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransKartuKredit;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransAsuransi;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPGN;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransWi;
import com.eklanku.otuChat.ui.activities.payment.transfer.TransDeposit;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.activities.settings.SettingTabPaymentActivity;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PaymentFragment extends Fragment {
    Context context;
    TextView //lblUsername,
            lblSaldo;
    ImageButton btnDeposit, btnTelkom, btnListrik, btnPulsa, btnVoucher, btnPdam, btnPajak,
            btnTagihan, btnBpjs, btnMultiFinance, btnKartuKredit, btnAsuransi, btnPGN,
            btnTv, btnPaket, btnSMS, btnEtool, btnWi;
    ImageButton btnRiwayat,btnTransfer,btnPengaturan;
    Button btnCallme;
    /* Button btnListrik, btnPulsa, btnVoucher, btnPdam, btnPajak,
             btnTagihan, btnBpjs, btnMultiFinance, btnKartuKredit, btnAsuransi, btnPGN,
             btnTv, btnPaket, btnCallme, btnSMS, btnEtool, btnWi;*/
    private PreferenceManager preferenceManager;
    private ApiInterfacePayment apiInterfacePayment;
    Dialog loadingDialog;
    private String strUserID, strAccessToken, strApIUse = "OTU";
    HashMap<String, String> user;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        preferenceManager = new PreferenceManager(getActivity());
        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        context = getActivity();
        View mView = inflater.inflate(R.layout.fragment_payment_new, container, false);
        initializeResources(mView);
        ButterKnife.bind(this, mView);

        TextView txt1 = mView.findViewById(R.id.txt1);
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AlertSyarat.class));
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        Log.d("AYIK", "OnResume userID " + strUserID + " accessToken " + strAccessToken);
        if (PreferenceUtil.isLoginStatus(getActivity())) {
            //Toast.makeText(context, "YES DEPOSITE " + PreferenceUtil.isLoginStatus(getActivity()), Toast.LENGTH_SHORT).show();
            loadDeposite(strUserID, strAccessToken);
        } else {
            //Toast.makeText(context, "NOT DEPOSITE " + PreferenceUtil.isLoginStatus(getActivity()), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeResources(View view) {
        // lblUsername = (TextView) view.findViewById(R.id.lblPaymentUser);
        lblSaldo = (TextView) view.findViewById(R.id.tvSaldo);
        btnDeposit = view.findViewById(R.id.btDeposit);
        btnMultiFinance = view.findViewById(R.id.btnMultiFinance);
        btnKartuKredit = view.findViewById(R.id.btnKartuKredit);
        btnAsuransi = view.findViewById(R.id.btnAsuransi);
        btnPGN = view.findViewById(R.id.btnPGN);
        btnListrik = view.findViewById(R.id.btnPln);
        btnPulsa = view.findViewById(R.id.btnPulsa);
        btnVoucher = view.findViewById(R.id.btnVoucher);
        btnPdam = view.findViewById(R.id.btnPdam);
        btnTagihan = view.findViewById(R.id.btnPascabayar);
        btnBpjs = view.findViewById(R.id.btnBpjs);
        btnTv = view.findViewById(R.id.btnTv);
        btnTelkom = view.findViewById(R.id.btnTelkom);
        btnPaket = view.findViewById(R.id.btnPaket);
        btnCallme = view.findViewById(R.id.btCallMe);
        btnRiwayat = view.findViewById(R.id.btnRiwayat);
        btnTransfer = view.findViewById(R.id.btnTransfer);
        btnPengaturan = view.findViewById(R.id.btnPengaturan);

        btnWi = view.findViewById(R.id.btn_wifi_id);
        btnSMS = view.findViewById(R.id.btn_sms);
        btnEtool = view.findViewById(R.id.btn_etool);
        btnPajak = view.findViewById(R.id.btnPajak);

        btnDeposit.setOnClickListener(new buttonListener());
        btnMultiFinance.setOnClickListener(new buttonListener());
        btnKartuKredit.setOnClickListener(new buttonListener());
        btnAsuransi.setOnClickListener(new buttonListener());
        btnPGN.setOnClickListener(new buttonListener());
        btnListrik.setOnClickListener(new buttonListener());
        btnPulsa.setOnClickListener(new buttonListener());
        btnVoucher.setOnClickListener(new buttonListener());
        btnPdam.setOnClickListener(new buttonListener());
        btnTagihan.setOnClickListener(new buttonListener());
        btnBpjs.setOnClickListener(new buttonListener());
        btnTv.setOnClickListener(new buttonListener());
        btnTelkom.setOnClickListener(new buttonListener());
        btnPaket.setOnClickListener(new buttonListener());
        btnRiwayat.setOnClickListener(new buttonListener());
        btnPengaturan.setOnClickListener(new buttonListener());
        btnTransfer.setOnClickListener(new buttonListener());
        btnCallme.setOnClickListener(new buttonListener());

        btnEtool.setOnClickListener(new buttonListener());
        btnSMS.setOnClickListener(new buttonListener());
        btnWi.setOnClickListener(new buttonListener());
        btnPajak.setOnClickListener(new buttonListener());

    }

    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!PreferenceUtil.isLoginStatus(context)) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("PERINGATAN!!!")
                        .setMessage("Untuk meningkatkan Kemanan Silahkan Login terlebih dahulu")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(context, PaymentLogin.class));


                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                return;
            }

            switch (v.getId()) {
                case R.id.btDeposit:
                    startActivity(new Intent(context, TopupOrder.class));
                    break;

                case R.id.btnPln:
                    startActivity(new Intent(context, TransPln.class));
                    break;

                case R.id.btnPulsa:
                    startActivity(new Intent(context, TransPulsa.class));
                    break;

                case R.id.btnVoucher:
                    startActivity(new Intent(context, TransVoucherGame.class));
                    break;

                case R.id.btnPdam:
                    startActivity(new Intent(context, TransPdam.class));
                    break;

                case R.id.btnPascabayar:
                    startActivity(new Intent(context, TransTagihan.class));
                    break;

                case R.id.btnBpjs:
                    startActivity(new Intent(context, TransBpjs.class));
                    break;

                case R.id.btnTv:
                    startActivity(new Intent(context, TransTv.class));
                    break;

                case R.id.btnTelkom:
                    startActivity(new Intent(context, TransTelkom.class));
                    break;

                case R.id.btnPaket:
                    startActivity(new Intent(context, TransPaketData.class));
                    break;
                case R.id.btnMultiFinance:
                    startActivity(new Intent(context, TransMultiFinance.class));
                    break;
                case R.id.btnKartuKredit:
                    startActivity(new Intent(context, TransKartuKredit.class));
                    break;
                case R.id.btnAsuransi:
                    startActivity(new Intent(context, TransAsuransi.class));
                    break;
                case R.id.btnPGN:
                    startActivity(new Intent(context, TransPGN.class));
                    break;
                case R.id.btCallMe:
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://eklanku.com/max")));
                    break;
                case R.id.btn_wifi_id:
                    startActivity(new Intent(context, TransWi.class));
                    break;
                case R.id.btn_etool:
                    startActivity(new Intent(context, TransEtool.class));
                    break;
                case R.id.btn_sms:
                    startActivity(new Intent(context, TransSMS.class));
                    break;
                case R.id.btnPajak:
                    startActivity(new Intent(context, TransPajak.class));
                    break;
                case R.id.btnRiwayat:
                    if (menuDialog()) {
                        startActivity(new Intent(context, RiwayatActivity.class));
                    }
                    break;
                case R.id.btnTransfer:
                    if (menuDialog()) {
                        startActivity(new Intent(context, TransDeposit.class));
                    }
                    break;
                case R.id.btnPengaturan:
                    if (menuDialog()) {
                        startActivity(new Intent(context, SettingTabPaymentActivity.class));
                    }
                    break;
                default:
                    Toast.makeText(context, getResources().getString(R.string.error_fungsi), Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.payment_menu, menu);

    }

    private boolean menuDialog() {
        if (!PreferenceUtil.isLoginStatus(context)) {
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(context)
                    .setTitle("PERINGATAN!!!")
                    .setMessage("Untuk meningkatkan Kemanan Silahkan Login terlebih dahulu")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(context, PaymentLogin.class));
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            return false;
        } else return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mnhistory:
                if (menuDialog()) {
                    startActivity(new Intent(context, HistoryBalanceActivity.class));
                }
                break;
            case R.id.mntrx:
                if (menuDialog()) {
                    startActivity(new Intent(context, HistoryTrxActivity.class));
                }
                break;
            case R.id.mndeposit:
                if (menuDialog()) {
                    startActivity(new Intent(context, HistoryDespositActivity.class));
                }
                break;
            case R.id.mnpenarikan:
                if (menuDialog()) {
                    startActivity(new Intent(context, HistoryPenarikanActivity.class));
                }
                break;
            case R.id.mnbonus:
                if (menuDialog()) {
                    startActivity(new Intent(context, HistoryBonusActivity.class));
                }
                break;
            case R.id.mnResetpass:
                if (menuDialog()) {
                    startActivity(new Intent(context, ResetPassword.class));
                }
                break;
            case R.id.mnProfilPay:
                if (menuDialog()) {
                    startActivity(new Intent(context, Profile.class));
                }
                break;
            case R.id.contactUs:
                if (menuDialog()) {
                    startActivity(new Intent(context, ContactUsActivity.class));
                }
                break;
            case R.id.mntopup:
                if (menuDialog()) {
                    startActivity(new Intent(context, TopupOrder.class));
                }
                break;
            case R.id.mntransfer:
                if (menuDialog()) {
                    startActivity(new Intent(context, TransDeposit.class));
                }
                break;
            case R.id.mnResetpin:
                if (menuDialog()) {
                    startActivity(new Intent(context, ResetPIN.class));
                }
                break;

            case R.id.settings:
                if (menuDialog()) {
                    startActivity(new Intent(context, SettingTabPaymentActivity.class));
                }
                break;

            case R.id.logout:
                logOutPayment();
                /*loadingDialog = ProgressDialog.show(getActivity(), "Harap Tunggu", "Melakukan proses logout");
                loadingDialog.setCanceledOnTouchOutside(true);
                preferenceManager.createUserPayment("", "", false);
                PreferenceUtil.setLoginStatus(getActivity(), false);
                //logout(strUserID, strAccessToken);*/
                break;

            case R.id.referal:
                //dialog();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    /*  private void dialog() {

          final Dialog dialog = new Dialog(getActivity());
          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          dialog.setContentView(R.layout.custom_dialog);
          dialog.setCancelable(false);

          final TextView tvStatus = (TextView) dialog.findViewById(R.id.status);
          final TextView tvFirst = (TextView) dialog.findViewById(R.id.firstlaunch);
          final TextView tvDate = (TextView) dialog.findViewById(R.id.date);
          final TextView tvRaw = (TextView) dialog.findViewById(R.id.raw);
          final TextView tvDecode = (TextView) dialog.findViewById(R.id.decode);

          tvStatus.setText("" + MainActivity.mainActivity.isReferrerDetected);
          tvFirst.setText(MainActivity.mainActivity.firstLaunch);
          tvDate.setText(MainActivity.mainActivity.referrerDate);
          tvRaw.setText(MainActivity.mainActivity.referrerDataRaw);
          tvDecode.setText(MainActivity.mainActivity.referrerDataDecoded);

          Button dialogButtonX = (Button) dialog.findViewById(R.id.btn_ok);
          dialogButtonX.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  dialog.dismiss();
              }
          });

          dialog.show();

      }
  */
    public static String getCurrentTime() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public void loadDeposite(String strUserID, String strAccessToken) {
        Log.d("AYIK", "OnLoad userID " + strUserID + " accessToken " + strAccessToken);
        Call<DataDeposit> userCall = apiInterfacePayment.getSaldo(strUserID, strApIUse, strAccessToken);
        userCall.enqueue(new Callback<DataDeposit>() {
            @Override
            public void onResponse(Call<DataDeposit> call, Response<DataDeposit> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    String balance = response.body().getBalance();
                    Log.d("OPPO-1", "onResponse: " + balance);

                    if (status.equals("SUCCESS")) {
                        Double total = 0.0d;
                        try {
                            if (balance != null && !balance.trim().isEmpty())
                                total = Double.valueOf(balance);
                        } catch (Exception e) {
                            total = 0.0d;
                        }
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                        String rupiah = format.format(total);

                        Log.d("OPPO-1", "onResponse: " + rupiah);

                        lblSaldo.setText(rupiah);
                    } else {
                        Toast.makeText(getActivity(), "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataDeposit> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_REGISTER", t.getMessage().toString());
            }
        });

    }

    public void logOutPayment() {
        //Toast.makeText(context, "LOGOUT YA OK?", Toast.LENGTH_SHORT).show();
        Call<ResetPassResponse> callResetPass = apiInterfacePayment.postLogoutPayment(strUserID, strAccessToken, getCurrentTime());
        callResetPass.enqueue(new Callback<ResetPassResponse>() {
            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    //Log.d("OPPO-1", "logOutPayment: "+status);
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(getActivity(), "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        //preferenceManager.createUserPayment("", "", false);
                        PreferenceUtil.setLoginStatus(getActivity(), false);
                    } else {
                        Toast.makeText(getActivity(), "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                //Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }


}
