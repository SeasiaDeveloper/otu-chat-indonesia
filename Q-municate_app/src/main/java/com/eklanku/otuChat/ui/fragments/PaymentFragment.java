package com.eklanku.otuChat.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.RiwayatActivity;
import com.eklanku.otuChat.ui.activities.payment.doku.CCPaymentActivity;

import com.eklanku.otuChat.ui.activities.payment.doku.DokuWalletPaymentActivity;
import com.eklanku.otuChat.ui.activities.payment.models.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models.DataDetailSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.DataSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Register;
import com.eklanku.otuChat.ui.activities.payment.topup.AlertSyarat;
import com.eklanku.otuChat.ui.activities.payment.topup.TopupOrder;
import com.eklanku.otuChat.ui.activities.payment.transaksi.PaymentLogin;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransESaldo_opsi;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransEtool;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketTelp;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPulsa;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketData;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPln;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransSMS;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransBpjs;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTv;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPdam;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTagihan;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTelkom;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransMultiFinance;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransVouchergame_opsi;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransWi;
import com.eklanku.otuChat.ui.activities.payment.transfer.TransDeposit;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PaymentFragment extends Fragment {
    Context context;
    public TextView //lblUsername,
            lblSaldo, lblSaldoMain, txtEklmain;
    TextView btnDeposit, tvBonus;
    ImageButton btnTelkom, btnListrik, btnPulsa, btnVoucher, btnPdam, btnPajak,
            btnTagihan, btnBpjs, btnMultiFinance, btnKartuKredit, btnAsuransi, btnPGN,
            btnTv, btnPaket, btnSMS, btnEtool, btnWi, btnhotel, btnpesawat, btnkeretapai, btnesaldo;
    ImageButton btnRiwayat, btnTransfer /*btnPengaturan*/;
    ImageButton btnCallme;
    /* Button btnListrik, btnPulsa, btnVoucher, btnPdam, btnPajak,
             btnTagihan, btnBpjs, btnMultiFinance, btnKartuKredit, btnAsuransi, btnPGN,
             btnTv, btnPaket, btnCallme, btnSMS, btnEtool, btnWi;*/
    private PreferenceManager preferenceManager;
    private ApiInterfacePayment apiInterfacePayment;
    Dialog loadingDialog;
    private String strUserID, strAccessToken, strApIUse = "OTU";
    HashMap<String, String> user;
    DrawerLayout drawer;

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

        setActivityBannerVisibility(View.VISIBLE);

        View mView = inflater.inflate(R.layout.fragment_payment_new, container, false);
        initializeResources(mView);
        ButterKnife.bind(this, mView);

        lblSaldoMain = getActivity().findViewById(R.id.tvSaldo);
        btnDeposit = getActivity().findViewById(R.id.btDeposit);
        btnCallme = getActivity().findViewById(R.id.btnCallMe);
        btnRiwayat = getActivity().findViewById(R.id.btnRiwayat);
        btnTransfer = getActivity().findViewById(R.id.btnTransfer);
        tvBonus = getActivity().findViewById(R.id.tvBonus);

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDialog()) {
                    //startActivity(new Intent(context, TopupOrder.class));
                    //startActivity(new Intent(context, HomeGatewayDoku.class));
                    dialogTopup();
                }
            }
        });

        btnCallme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://eklanku.com/max")));
            }
        });
        btnRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDialog()) {
                    startActivity(new Intent(getActivity(), RiwayatActivity.class));
                }
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuDialog()) {
                    startActivity(new Intent(context, TransDeposit.class));
                }
            }
        });
        //textview.setText("text");

        TextView txt1 = mView.findViewById(R.id.txt1);
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AlertSyarat.class));
            }
        });

        apiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            if (!PreferenceUtil.isMemberStatus(getActivity())) {
                cekMember();
            }

        }

        drawer = getActivity().findViewById(R.id.drawer_layout);
        return mView;
    }

    private void dialogTopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_topup);
        dialog.setCancelable(false);

        final Button btnTk = dialog.findViewById(R.id.btn_tk);
        final Button btnCC = dialog.findViewById(R.id.btn_cc);

        final Button btnFirstPay = dialog.findViewById(R.id.btn_first);
        final Button btnSecondPay = dialog.findViewById(R.id.btn_second);
        final Button btnDoku = dialog.findViewById(R.id.btn_doku);
        final Button btnMandiri = dialog.findViewById(R.id.btn_mandiri);
        final Button btnVA = dialog.findViewById(R.id.btn_va);
        final Button btnVAM = dialog.findViewById(R.id.btn_vam);

        final ImageButton btnClose = dialog.findViewById(R.id.btn_cancel);

        btnTk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(getActivity(), TopupOrder.class));

            }
        });

        btnCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(getActivity(), CCPaymentActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        btnFirstPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSecondPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DokuWalletPaymentActivity.class);
                startActivity(i);
            }
        });

        btnMandiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnVA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnVAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onResume() {
        super.onResume();
        user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        if (PreferenceUtil.isLoginStatus(getActivity())) {
            //Toast.makeText(context, "YES DEPOSITE " + PreferenceUtil.isLoginStatus(getActivity()), Toast.LENGTH_SHORT).show();

            Activity activity = getActivity();
            if (activity != null && isAdded())
                //loadDeposite(strUserID, strAccessToken);
                LoadSaldoBonus(strUserID, strAccessToken);

        } else {
            //Toast.makeText(context, "NOT DEPOSITE " + PreferenceUtil.isLoginStatus(getActivity()), Toast.LENGTH_SHORT).show();
        }
    }

    private void setActivityBannerVisibility(int visibility) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.bannerSlider.setVisibility(visibility);
        }
    }

    private void initializeResources(View view) {
        // lblUsername = (TextView) view.findViewById(R.id.lblPaymentUser);
        lblSaldo = (TextView) view.findViewById(R.id.tvSaldo);

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
        ImageButton btnPaketTelp = view.findViewById(R.id.btn_telp);
        //btnPengaturan = view.findViewById(R.id.btnPengaturan);

        btnWi = view.findViewById(R.id.btn_wifi_id);
        btnSMS = view.findViewById(R.id.btn_sms);
        btnEtool = view.findViewById(R.id.btn_etool);
        btnPajak = view.findViewById(R.id.btnPajak);
        btnhotel = view.findViewById(R.id.btnhotel);
        btnpesawat = view.findViewById(R.id.btnPesawat);
        btnkeretapai = view.findViewById(R.id.btnKAI);

        btnesaldo = view.findViewById(R.id.btn_e_saldo);

        //btnDeposit.setOnClickListener(new buttonListener());
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
        btnhotel.setOnClickListener(new buttonListener());
        btnpesawat.setOnClickListener(new buttonListener());
        btnkeretapai.setOnClickListener(new buttonListener());
        //btnRiwayat.setOnClickListener(new buttonListener());
        //btnPengaturan.setOnClickListener(new buttonListener());
        //btnTransfer.setOnClickListener(new buttonListener());
        //btnCallme.setOnClickListener(new buttonListener());

        btnEtool.setOnClickListener(new buttonListener());
        btnSMS.setOnClickListener(new buttonListener());
        btnWi.setOnClickListener(new buttonListener());
        btnPajak.setOnClickListener(new buttonListener());
        btnesaldo.setOnClickListener(new buttonListener());


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
                /*case R.id.btDeposit:
                    startActivity(new Intent(context, TopupOrder.class));
                    break;*/

                case R.id.btnPln:
                    startActivity(new Intent(context, TransPln.class));
                    break;

                case R.id.btnPulsa:
                    startActivity(new Intent(context, TransPulsa.class));
                    break;

                case R.id.btnVoucher:
                    startActivity(new Intent(context, TransVouchergame_opsi.class));
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
                    // startActivity(new Intent(context, TransKartuKredit.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnAsuransi:
                    //startActivity(new Intent(context, TransAsuransi.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnPGN:
                    //startActivity(new Intent(context, TransPGN.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
               /* case R.id.btnCallMe:
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://eklanku.com/max")));
                    break;*/
                case R.id.btn_wifi_id:
                    startActivity(new Intent(context, TransWi.class));
                    break;
                case R.id.btn_etool:
                    startActivity(new Intent(context, TransEtool.class));
                    break;
                case R.id.btn_sms:
                    startActivity(new Intent(context, TransSMS.class));
                    break;

                case R.id.btn_telp:
                    startActivity(new Intent(context, TransPaketTelp.class));
                    break;
                case R.id.btnPajak:
//                    startActivity(new Intent(context, TransPajak.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnhotel:
//                    startActivity(new Intent(context, TransPajak.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnPesawat:
//                    startActivity(new Intent(context, TransPajak.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnKAI:
//                    startActivity(new Intent(context, TransPajak.class));
                    Toast.makeText(context, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_e_saldo:
                    if (menuDialog()) {
                        startActivity(new Intent(context, TransESaldo_opsi.class));
                    }
                    break;
               /* case R.id.btnTransfer:
                    if (menuDialog()) {
                        startActivity(new Intent(context, TransDeposit.class));
                    }
                    break;*/
               /* case R.id.btnPengaturan:
                    if (menuDialog()) {
                        startActivity(new Intent(context, SettingTabPaymentActivity.class));
                    }
                    break;*/
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
        inflater.inflate(R.menu.main_menu, menu);

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

  /*  @Override
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


                Activity activity = getActivity();
                if (activity != null && isAdded())
                    logOutPayment();
                *//*loadingDialog = ProgressDialog.show(getActivity(), "Harap Tunggu", "Melakukan proses logout");
                loadingDialog.setCanceledOnTouchOutside(true);
                preferenceManager.createUserPayment("", "", false);
                PreferenceUtil.setLoginStatus(getActivity(), false);
                //logout(strUserID, strAccessToken);*//*
                break;

            case R.id.referal:
                //dialog();
                break;
        }

        return super.onOptionsItemSelected(item);

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            drawer.openDrawer(GravityCompat.END);
        }

        if (id == R.id.action_notification) {
            Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
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
                Log.d("AYIK", "OnLoad userID " + strUserID + " response.isSuccessful()) " + response.isSuccessful());
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    String balance = response.body().getBalance();

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

                        /*Double nomBonus = 0.0d;
                        try{
                            if(nomBonus != null && !bonus.trim().isEmpty()){
                                nomBonus = Double.valueOf(bonus);
                            }else{
                                nomBonus = 0.0d;
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        String rupiahBonus = format.format(nomBonus);

                        Log.d("OPPO-1", "onResponse: " + rupiahBonus);
                        tvBonus.setText(rupiahBonus);
                        */

                        lblSaldoMain.setText(rupiah);

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
            }
        });

    }

    //===========================================API LAMA
    /*public void LoadSaldoBonus(String strUserID, String strAccessToken){

        Call<DataSaldoBonus> userCall = apiInterfacePayment.getSaldodetail(strUserID, strApIUse, strAccessToken);
        userCall.enqueue(new Callback<DataSaldoBonus>() {
            @Override
            public void onResponse(Call<DataSaldoBonus> call, Response<DataSaldoBonus> response) {

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    String id_member = "", sisa_uang = "", carier_member = "",bonus_member = "";
                    Log.d("OPPO-1", "OnLoad userID " + strUserID + " response.isSuccessful()) " + response.isSuccessful());
                    if (status.equals("SUCCESS")) {

                        final List<DataDetailSaldoBonus> products = response.body().getBalance();
                        for (int i = 0; i < products.size(); i++) {
                            id_member = products.get(i).getId_member();
                            sisa_uang = products.get(i).getSisa_uang();
                            carier_member = products.get(i).getCarier_member();
                            bonus_member = products.get(i).getBonus_member();
                        }

                        Double total = 0.0d;
                        try {
                            if (sisa_uang != null && !sisa_uang.trim().isEmpty())
                                total = Double.valueOf(sisa_uang);
                        } catch (Exception e) {
                            total = 0.0d;
                        }
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                        String rupiah = format.format(total);

                        Double nomBonus = 0.0d;
                        try{
                            if(nomBonus != null && !bonus_member.trim().isEmpty()){
                                nomBonus = Double.valueOf(bonus_member);
                            }else{
                                nomBonus = 0.0d;
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        String rupiahBonus = format.format(nomBonus);

                        Log.d("OPPO-1", "onResponse: " + rupiahBonus);
                        tvBonus.setText("Rp"+nomBonus);

                        lblSaldoMain.setText(rupiah);

                    } else {
                        Toast.makeText(getActivity(), "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataSaldoBonus> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    public void LoadSaldoBonus(String strUserID, String strAccessToken) {
        Log.d("OPPO-1", "OnLoad userID " + strUserID + ", " + strAccessToken);
        Call<DataSaldoBonus> userCall = apiInterfacePayment.getSaldodetail(strUserID, strApIUse, strAccessToken);
        userCall.enqueue(new Callback<DataSaldoBonus>() {
            @Override
            public void onResponse(Call<DataSaldoBonus> call, Response<DataSaldoBonus> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    String id_member = "", sisa_uang = "", carier_member = "", bonus_member = "";
                    Log.d("OPPO-1", "OnLoad userID " + strUserID + " response.isSuccessful()) " + response.isSuccessful());
                    if (status.equals("SUCCESS")) {

                        final List<DataDetailSaldoBonus> products = response.body().getBalance();
                        for (int i = 0; i < products.size(); i++) {
                            id_member = products.get(i).getId_member();
                            sisa_uang = products.get(i).getSisa_uang();
                            carier_member = products.get(i).getCarier_member();
                            bonus_member = products.get(i).getBonus_member();
                        }

                        Double total = 0.0d;
                        try {
                            if (sisa_uang != null && !sisa_uang.trim().isEmpty())
                                total = Double.valueOf(sisa_uang);
                        } catch (Exception e) {
                            total = 0.0d;
                        }
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                        String rupiah = format.format(total);

                        Double nomBonus = 0.0d;
                        try {
                            if (nomBonus != null && !bonus_member.trim().isEmpty()) {
                                nomBonus = Double.valueOf(bonus_member);
                            } else {
                                nomBonus = 0.0d;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String rupiahBonus = format.format(nomBonus);

                        Log.d("OPPO-1", "onResponse: " + rupiahBonus);
                        tvBonus.setText("Rp" + nomBonus);

                        lblSaldoMain.setText(rupiah);

                    } else {
                        Toast.makeText(getActivity(), "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataSaldoBonus> call, Throwable t) {
                Log.d("OPPO-1", "onFailure: "+t.getMessage());
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
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

    private void cekMember() {

        Call<DataProfile> isMember = apiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(getActivity()), "OTU");

        //Toast.makeText(context, "" + PreferenceUtil.getNumberPhone(getActivity()), Toast.LENGTH_SHORT).show();
        isMember.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String errNumber = response.body().getErrNumber();
                    if (errNumber.equalsIgnoreCase("0")) {
                        PreferenceUtil.setMemberStatus(getActivity(), true);
                    } else if (errNumber.equalsIgnoreCase("4")) {//awalnya 5
                        lauchRegister();
                    } else {
                        //Toast.makeText(getActivity(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }
//==================================API LAMA
    /*private void cekMember() {
        Call<DataProfile> isMember = apiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(getActivity()), "OTU");
        //Toast.makeText(context, "" + PreferenceUtil.getNumberPhone(getActivity()), Toast.LENGTH_SHORT).show();
        isMember.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String errNumber = response.body().getErrNumber();
                    if (errNumber.equalsIgnoreCase("0")) {
                        PreferenceUtil.setMemberStatus(getActivity(), true);
                    } else if (errNumber.equalsIgnoreCase("5")) {
                        lauchRegister();
                    } else {
                        Toast.makeText(getActivity(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void lauchRegister() {
        Intent register = new Intent(getActivity(), Register.class);
        startActivity(register);
        //finish();
    }

}
