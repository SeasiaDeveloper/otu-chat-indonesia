package com.eklanku.otuChat.ui.activities.authorization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.eklanku.otuChat.App;
import com.eklanku.otuChat.Application;
import com.eklanku.otuChat.ReferrerReceiver;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.BuildConfig;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;

public class SplashActivity extends BaseAuthActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int DELAY_FOR_OPENING_LANDING_ACTIVITY = 3000;
    //ApiInterfacePayment mApiInterfacePayment;
    private PreferenceManager preferenceManager;
    public boolean isReferrerDetected;
    public String firstLaunch, referrerDate, referrerDataRaw, referrerDataDecoded;
    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        //TODO VT temp code for correct migration from Twitter Digits to Firebase Phone Auth
        //should be removed in next release
        if (ConnectycubeSessionManager.getInstance().getSessionParameters() != null
                && ConnectycubeProvider.TWITTER_DIGITS.equals(ConnectycubeSessionManager.getInstance().getSessionParameters().getSocialProvider())) {
            restartAppWithFirebaseAuth();
            return;
        }
        //TODO END

        appInitialized = true;
        AppSession.load();
        preferenceManager = new PreferenceManager(this);

        processPushIntent();

        /*if (ConnectycubeSessionManager.getInstance().getSessionParameters() != null && appSharedHelper.isSavedRememberMe()) {
            startLastOpenActivityOrMain();
            //cekMember();
        } else {
            startLandingActivity();
        }*/
        fBaseConf();

    }

    private void processPushIntent() {
        boolean openPushDialog = getIntent().getBooleanExtra(QBServiceConsts.EXTRA_SHOULD_OPEN_DIALOG, false);
        CoreSharedHelper.getInstance().saveNeedToOpenDialog(openPushDialog);
    }

    private void startLandingActivity() {
        ServiceManager.getInstance().initUserTable();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LandingActivity.start(SplashActivity.this);
                finish();
            }
        }, DELAY_FOR_OPENING_LANDING_ACTIVITY);
    }

    private void startLastOpenActivityOrMain() {
        Class<?> lastActivityClass;
        boolean needCleanTask = false;
        try {
            String lastActivityName = appSharedHelper.getLastOpenActivity();
            if (lastActivityName != null) {
                lastActivityClass = Class.forName(appSharedHelper.getLastOpenActivity());
            } else {
                needCleanTask = true;
                lastActivityClass = MainActivity.class;
            }
        } catch (ClassNotFoundException e) {
            needCleanTask = true;
            lastActivityClass = MainActivity.class;
        }

        startActivityByName(lastActivityClass, needCleanTask);
    }

    private void restartAppWithFirebaseAuth() {
        ServiceManager.getInstance().logout(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Intent intent = new Intent(App.getInstance(), LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LandingActivity.start(App.getInstance(), intent);
                finish();
            }

            @Override
            public void onNext(Void aVoid) {
                Intent intent = new Intent(App.getInstance(), LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LandingActivity.start(App.getInstance(), intent);
                finish();
            }
        });
    }

//    private void cekMember() {
//        Log.d("AYIK", "cekMember:process");
//        Call<DataProfile> isMember = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(this), "OTU");
//
//        //Call<DataProfile> isMember = mApiInterfacePayment.isMember(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), "OTU");
//        isMember.enqueue(new Callback<DataProfile>() {
//            @Override
//            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
//                if (response.isSuccessful()) {
//                    String status = response.body().getStatus();
//                    String msg = response.body().getRespMessage();
//                    String errNumber = response.body().getErrNumber();
//                    if (errNumber.equalsIgnoreCase("0")) {
//                        startLastOpenActivityOrMain();
//                        Log.d("AYIK", "cekMember:process success");
//                    } else {
//                        Log.d("AYIK", "cekMember:process failed token");
//                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Log.d("AYIK", "cekMember:process error api");
//                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataProfile> call, Throwable t) {
//                Log.d("AYIK", "cekMember:process failure");
//                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void updateData() {
        isReferrerDetected = Application.isReferrerDetected(getApplicationContext());
        firstLaunch = Application.getFirstLaunch(getApplicationContext());
        referrerDate = Application.getReferrerDate(getApplicationContext());
        referrerDataRaw = Application.getReferrerDataRaw(getApplicationContext());
        referrerDataDecoded = Application.getReferrerDataDecoded(getApplicationContext());

        StringBuilder sb = new StringBuilder();
        sb.append("<b>First launch:</b>")
                .append("<br/>")
                .append(firstLaunch)
                .append("<br/><br/>")
                .append("<b>Referrer detection:</b>")
                .append("<br/>")
                .append(referrerDate);
        if (isReferrerDetected) {
            sb.append("<br/><br/>")
                    .append("<b>Raw referrer:</b>")
                    .append("<br/>")
                    .append(referrerDataRaw);

            if (referrerDataDecoded != null) {
                sb.append("<br/><br/>")
                        .append("<b>Decoded referrer:</b>")
                        .append("<br/>")
                        .append(referrerDataDecoded);
            }
        }

       /* content.setText(Html.fromHtml(sb.toString()));
        content.setMovementMethod(new LinkMovementMethod());*/

        preferenceManager.createReff(firstLaunch, referrerDate, referrerDataRaw, referrerDataDecoded);

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver, new IntentFilter(ReferrerReceiver.ACTION_UPDATE_DATA));
        super.onResume();
    }

    private void fBaseConf() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
        fetchWelcome();

    }

    private void fetchWelcome() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();

                        } else {
                            //Log.d(TAG, "3 " + "Fetch Failed");
                        }
                        displayWelcomeMessage();
                    }
                });
    }

    private void displayWelcomeMessage() {
        long versionServer = mFirebaseRemoteConfig.getLong("versi");
        long versionApp = BuildConfig.VERSION_CODE;

        if (versionApp < versionServer) {
            updateApp();
        } else {
            if (ConnectycubeSessionManager.getInstance().getSessionParameters() != null && appSharedHelper.isSavedRememberMe()) {
                startLastOpenActivityOrMain();
            } else {
                startLandingActivity();
            }
        }
    }

    public void updateApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Aplikasi anda versi lama. Silahkan update ke versi terbaru");
        builder.setNegativeButton("Tutup",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        finish();
                    }
                });
        builder.show();
    }
}