package com.eklanku.otuChat.ui.activities.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.activities.authorization.LandingActivity;
import com.eklanku.otuChat.ui.activities.barcode.WebQRCodeActivity;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.payment.models2.DataDetailSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.models2.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models2.DataSaldoBonus;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.DeleteAccount;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Profile;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Register;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPIN;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.ResetPassword;
import com.eklanku.otuChat.ui.activities.payment.transaksi2.PaymentLogin;
import com.eklanku.otuChat.ui.activities.settings.SettingsActivity;
import com.eklanku.otuChat.ui.fragments.CallFragment;
import com.eklanku.otuChat.ui.fragments.PaymentFragment;
import com.eklanku.otuChat.ui.views.banner.GlideImageLoader;
import com.eklanku.otuChat.ui.views.banner2.BannerDataManager;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.eklanku.otuChat.utils.helpers.AddressBookHelper;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.models2.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest2.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiClientProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest2.ApiInterfacePayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfaceProfile;
import com.eklanku.otuChat.ui.activities.settings.SettingsActivityOtu;
import com.eklanku.otuChat.ui.fragments.chats.DialogsListFragment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.helpers.FacebookHelper;
import com.eklanku.otuChat.utils.helpers.ImportFriendsHelper;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.eklanku.otuChat.utils.MediaUtils;

import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.UserCustomData;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.Utils;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
/*import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;*/
import com.yyydjk.library.BannerLayout;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.eklanku.otuChat.utils.helpers.DbHelper;

public class MainActivity extends BaseLoggableActivity implements ObservableScrollViewCallbacks/*, NavigationView.OnNavigationItemSelectedListener*/ {

    @Bind(R.id.bannerLayout)
    public BannerLayout bannerSlider;

    private static final String TAG = MainActivity.class.getSimpleName();

    private FacebookHelper facebookHelper;

    private ImportFriendsSuccessAction importFriendsSuccessAction;
    private ImportFriendsFailAction importFriendsFailAction;

    Intent intent;
    public static final int REQUEST_CODE_LOGOUT = 300;
    protected BaseActivity baseActivity;

    ApiInterface mApiInterface;
    ApiInterfaceProfile apiInterfaceProfile;
    ApiInterfacePayment mApiInterfacePayment;

    private PreferenceManager preferenceManager;
    public static MainActivity mainActivity;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";
    PaymentFragment paymentFragment;

    LinearLayout layoutCollaps, layoutSaldo, layoutDivider;
    CircleImageView img;
    TextView txt, txtEkl, tvStarMember;

    private static final int REQUEST_READ_PHONE_STATE = 0;
    boolean doubleBackToExitPressedOnce = false;

    //private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    DrawerLayout drawer;
    public TextView tvSaldo;

    Call<DataSaldoBonus> userCall;
    Call<DataProfile> isMemberCall;
    Call<ResetPassResponse> callResetPass;

    private Observer mBannerDataObserver;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Klik tombol kembali 2x untuk menutup aplikasi", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_chat:

                    fragment = new DialogsListFragment();

                    if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                        strUserID = user.get(preferenceManager.KEY_USERID);
                        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
                    }

                    layoutSaldo.setVisibility(View.GONE);
                    layoutDivider.setVisibility(View.GONE);
                    //bannerSlider.setVisibility(View.GONE);
                    break;
                case R.id.navigation_call:
                    fragment = new CallFragment();

                    layoutSaldo.setVisibility(View.GONE);
                    layoutDivider.setVisibility(View.GONE);
                    if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                        strUserID = user.get(preferenceManager.KEY_USERID);
                        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
                    }
                    //bannerSlider.setVisibility(View.GONE);

                    break;
                case R.id.navigation_payment:
                    fragment = new PaymentFragment();
                    if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                        strUserID = user.get(preferenceManager.KEY_USERID);
                        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
                    }
                    if (!PreferenceUtil.isMemberStatus(MainActivity.this)) {
                        cekMember();
                    }

                    layoutSaldo.setVisibility(View.VISIBLE);
                    layoutDivider.setVisibility(View.VISIBLE);
                    //bannerSlider.setVisibility(View.VISIBLE);
                    break;

            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        processPushIntent(getIntent());

        if (checkAndStartLanding()) {
            return;
        }

        if (checkAndStartLastOpenActivity()) {
            return;
        }


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //lblSaldo = (TextView) findViewById(R.id.tvSaldo);
        layoutCollaps = findViewById(R.id.layout_);
        layoutSaldo = findViewById(R.id.laySaldo);
        layoutDivider = findViewById(R.id.divider_banner);
        tvSaldo = findViewById(R.id.tvSaldo);
        layoutSaldo.setVisibility(View.GONE);
        layoutDivider.setVisibility(View.GONE);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterfaceProfile = ApiClientProfile.getClient().create(ApiInterfaceProfile.class);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_right);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        img = header.findViewById(R.id.profile_image);
        txt = header.findViewById(R.id.profile_name);
        txtEkl = header.findViewById(R.id.tvEkl);
        tvStarMember = header.findViewById(R.id.tvStarMember);

        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        paymentFragment = new PaymentFragment();

        populate();
        initFields();
        setUpActionBarWithUpButton();
        synchContacts();
        syncAddressBook();

        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }

        addDialogsAction();

        mToolbarView = findViewById(R.id.toolbar_view);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.density_200);

        Activity activity = this;

        if (PreferenceUtil.isLoginStatus(this)) {
            if (!activity.isFinishing()) {
                loadSaldoBonus(strUserID, strAccessToken);
            }
        }

        initBanner();


        if (!PreferenceUtil.isFirstLaunch(this)) {
            restartApp();
        }

        loadFragment(new DialogsListFragment());

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        //display the right navigation drawer
        displayRightNavigation();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setItemIconTintList(null);
        txtEkl.setText(strUserID);
        mainActivity = this;
    }

    private void initBanner() {
        bannerSlider.setImageLoader(new GlideImageLoader());
        mBannerDataObserver = (observable, arg) -> {
            if (observable instanceof BannerDataManager) {
                bannerSlider.setViewUrls(((BannerDataManager) observable).getUrls(false));
            }
        };
        BannerDataManager.getInstance().addObserver(mBannerDataObserver);
        if (BannerDataManager.getInstance().getUrls(false).size() > 0) {
            bannerSlider.setViewUrls(BannerDataManager.getInstance().getUrls(true));
        }

        //banner data manager new
        Map<String, List<String>> map = BannerDataManager.getInstance().getList(true);

        bannerSlider.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int i) {
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    String url = entry.getValue().get(i);
                    if (url != null && !TextUtils.isEmpty(url)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Link belum tersedia", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    private boolean checkAndStartLastOpenActivity() {
        Class<?> lastActivityClass;
        boolean needCleanTask = false;
        try {
            String lastActivityName = appSharedHelper.getLastOpenActivity();
            if (lastActivityName != null) {
                lastActivityClass = Class.forName(appSharedHelper.getLastOpenActivity());
                startActivityByName(lastActivityClass, needCleanTask);
                return true;
            }
        } catch (ClassNotFoundException e) {
            return false;
        }

        return false;
    }

    private void processPushIntent(Intent intent) {
        boolean openPushDialog = intent.getBooleanExtra(QBServiceConsts.EXTRA_SHOULD_OPEN_DIALOG, false);
        CoreSharedHelper.getInstance().saveNeedToOpenDialog(openPushDialog);
    }

    private boolean checkAndStartLanding() {
        //TODO VT temp code for correct migration from Twitter Digits to Firebase Phone Auth
        //should be removed in next release
        if (ConnectycubeSessionManager.getInstance().getSessionParameters() != null
                && ConnectycubeProvider.TWITTER_DIGITS.equals(ConnectycubeSessionManager.getInstance().getSessionParameters().getSocialProvider())) {
            restartAppWithFirebaseAuth();
            return true;
        }
        //TODO END

        if (app.isNeedToUpdate()) {
            startLandingScreen();
            return true;
        }

        if (!appSharedHelper.isSavedRememberMe() ||
                (ConnectycubeSessionManager.getInstance().getSessionParameters() == null && appSharedHelper.isSavedRememberMe())) {
            startLandingScreen();
            return true;
        }

        return false;
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(layoutCollaps, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    private void openPushDialogIfPossible() {
        CoreSharedHelper sharedHelper = CoreSharedHelper.getInstance();
        if (sharedHelper.needToOpenDialog()) {
            ConnectycubeChatDialog chatDialog = DataManager.getInstance().getConnectycubeChatDialogDataManager()
                    .getByDialogId(sharedHelper.getPushDialogId());
            QMUser user = QMUserService.getInstance().getUserCache().get((long) sharedHelper.getPushUserId());
            if (chatDialog != null) {
                startDialogActivity(chatDialog, user);
            }
        }
    }

    private void startDialogActivity(ConnectycubeChatDialog chatDialog, QMUser user) {
        if (ConnectycubeDialogType.PRIVATE.equals(chatDialog.getType())) {
            startPrivateChatActivity(user, chatDialog);
        } else if (ConnectycubeDialogType.GROUP.equals(chatDialog.getType())) {
            startGroupChatActivity(chatDialog);
        } else {
            startBroadcastChatActivity(chatDialog);
        }
    }

    private void initFields() {
        Log.d(TAG, "initFields()");
        mDbHelper = new DbHelper(this);
        title = " " + AppSession.getSession().getUser().getFullName();
        txt.setText(AppSession.getSession().getUser().getFullName());
        importFriendsSuccessAction = new ImportFriendsSuccessAction();
        importFriendsFailAction = new ImportFriendsFailAction();
        facebookHelper = new FacebookHelper(MainActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookHelper.onActivityResult(requestCode, resultCode, data);
        if (SettingsActivityOtu.REQUEST_CODE_LOGOUT == requestCode && RESULT_OK == resultCode) {
            startLandingScreen();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onResume() {
        app.fetchFirebaseRemoteConfigValues();
        actualizeCurrentTitle();
        super.onResume();
        addActions();

        openPushDialogIfPossible();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processPushIntent(intent);
        super.onNewIntent(intent);
    }

    public static String getCurrentTime() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Makassar"));
            String currentDateTime = dateFormat.format(new Date()); // Find todays date
            Log.d("OPPO-1", "getCurrentTime: " + currentDateTime);
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public void warningLogoutpay() {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this)
                /*.setTitle("PERINGATAN!!!")*/
                .setMessage("Yakin logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOutPayment();
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

    public void logOutPayment() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sedang logout, mohon tunggu.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        callResetPass = mApiInterfacePayment.postLogoutPayment(strUserID, strAccessToken, getCurrentTime());
        callResetPass.enqueue(new Callback<ResetPassResponse>() {

            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {
                if (call.isCanceled()) {
                    return;
                }
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(MainActivity.this, "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setLoginStatus(MainActivity.this, false);
                        tvSaldo.setText("0.00");
                        //paymentFragment.lblSaldoMain.setText("0.00");
                    } else {
                        Toast.makeText(MainActivity.this, "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private void actualizeCurrentTitle() {
        if (AppSession.getSession().getUser().getFullName() != null) {
            title = " " + AppSession.getSession().getUser().getFullName();
            txt.setText(AppSession.getSession().getUser().getFullName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeActions();
    }

    @Override
    protected void onDestroy() {
        cancelCalls();
        BannerDataManager.getInstance().deleteObserver(mBannerDataObserver);

        super.onDestroy();
        removeDialogsAction();
    }

    @Override
    protected void checkShowingConnectionError() {
        if (!isNetworkAvailable()) {
            setActionBarTitle(getString(R.string.dlg_internet_connection_is_missing));
            setActionBarIcon(null);
        } else {
            setActionBarTitle(title);
            checkVisibilityUserIcon();
        }
    }

    @Override
    protected void performLoginChatSuccessAction(Bundle bundle) {
        super.performLoginChatSuccessAction(bundle);
        actualizeCurrentTitle();
    }

    private void cancelCalls() {
        if (userCall != null && !userCall.isCanceled()) {
            userCall.cancel();
        }

        if (isMemberCall != null && !isMemberCall.isCanceled()) {
            isMemberCall.cancel();
        }

        if (callResetPass != null && !callResetPass.isCanceled()) {
            callResetPass.cancel();
        }
    }

    private void addDialogsAction() {
        addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, new LoadChatsSuccessAction());
    }

    private void removeDialogsAction() {
        removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
    }

    private void addActions() {
        addAction(QBServiceConsts.IMPORT_FRIENDS_SUCCESS_ACTION, importFriendsSuccessAction);
        addAction(QBServiceConsts.IMPORT_FRIENDS_FAIL_ACTION, importFriendsFailAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.IMPORT_FRIENDS_SUCCESS_ACTION);
        removeAction(QBServiceConsts.IMPORT_FRIENDS_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void checkVisibilityUserIcon() {
        UserCustomData userCustomData = Utils.customDataToObject(AppSession.getSession().getUser().getCustomData());
        if (!TextUtils.isEmpty(userCustomData.getAvatarUrl())) {
            loadLogoActionBar(userCustomData.getAvatarUrl());
        } else {
            setActionBarIcon(MediaUtils.getRoundIconDrawable(this,
                    BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_user)));
            img.setImageResource(R.drawable.placeholder_user);
        }
    }

    private void loadLogoActionBar(String logoUrl) {
        ImageLoader.getInstance().loadImage(logoUrl, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        setActionBarIcon(MediaUtils.getRoundIconDrawable(MainActivity.this, loadedBitmap));
                        img.setImageBitmap(loadedBitmap);
                    }
                });
    }

    private void performImportFriendsSuccessAction() {
        appSharedHelper.saveUsersImportInitialized(true);
        hideProgress();
    }

    private void performImportFriendsFailAction(Bundle bundle) {
        performImportFriendsSuccessAction();
    }


    private void startImportFriends() {
        ImportFriendsHelper importFriendsHelper = new ImportFriendsHelper(MainActivity.this);

        if (facebookHelper.isSessionOpened()) {
            importFriendsHelper.startGetFriendsListTask(true);
        } else {
            importFriendsHelper.startGetFriendsListTask(false);
        }

        hideProgress();
    }

    private class ImportFriendsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performImportFriendsSuccessAction();
        }
    }

    private class ImportFriendsFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performImportFriendsFailAction(bundle);
        }
    }

    /*======================================load deposit==========================================================*/

//    public void load_deposit() {
//        Call<DataDeposit> userCall = mApiInterfacePayment.getSaldo(strUserID, strApIUse, strAccessToken);
//        userCall.enqueue(new Callback<DataDeposit>() {
//            @Override
//            public void onResponse(Call<DataDeposit> call, Response<DataDeposit> response) {
//                if (response.isSuccessful()) {
//                    String status = response.body().getStatus();
//                    String error = response.body().getRespMessage();
//                    String balance = response.body().getBalance();
//                    Log.d("OPPO-1", "onResponse: " + balance);
//
//                    if (status.equals("SUCCESS")) {
//                        Double total = 0.0d;
//                        try {
//                            if (balance != null && !balance.trim().isEmpty())
//                                total = Double.valueOf(balance);
//                        } catch (Exception e) {
//                            total = 0.0d;
//                        }
//                        Locale localeID = new Locale("in", "ID");
//                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
//                        String rupiah = format.format(total);
//
//                        Log.d("OPPO-1", "onResponse: " + rupiah);
//
//                        ((TextView) findViewById(R.id.tvSaldo)).setText(rupiah);
//                    } else {
//                        Toast.makeText(getBaseContext(), "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataDeposit> call, Throwable t) {
//                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
//                Log.d("API_REGISTER", t.getMessage().toString());
//            }
//        });
//
//    }

    private void populate() {
        if (!mayRequest()) {
            return;
        }
    }

    private boolean mayRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (
                checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED /*&&
                        checkSelfPermission(VIBRATE) == PackageManager.PERMISSION_GRANTED*/
                ) {

            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            requestPermissions(new String[]{

                    READ_PHONE_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,
                    CAMERA, RECEIVE_SMS, READ_CONTACTS, MODIFY_AUDIO_SETTINGS, RECORD_AUDIO/*, VIBRATE*/
            }, REQUEST_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{

                    READ_PHONE_STATE,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE,
                    CAMERA, RECEIVE_SMS, READ_CONTACTS, MODIFY_AUDIO_SETTINGS, RECORD_AUDIO/*, VIBRATE*/

            }, REQUEST_READ_PHONE_STATE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 10 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[7] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[8] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[9] == PackageManager.PERMISSION_GRANTED /*&&
                    grantResults[10] == PackageManager.PERMISSION_GRANTED*/
                    )


            {

                syncAddressBook();
                synchContacts();
            } else {
                finish();

            }
        }
    }

    public void lauchRegister() {
        Intent register = new Intent(getBaseContext(), Register.class);
        startActivity(register);
        finish();
    }

    //===========================API LAMA===============================================

/*    private void cekMember() {
//        Log.d("OPPO-1", "cekMember:process"+AppSession.getSession().getUser().getPhone());
        // Call<DataProfile> isMemberCall = mApiInterfacePayment.isMemberCall(PreferenceUtil.getNumberPhone(this)), "OTU");
        isMemberCall = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(this), "OTU");
        isMemberCall.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (call.isCanceled()) {
                    return;
                }
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String errNumber = response.body().getErrNumber();
                    if (errNumber.equalsIgnoreCase("0")) {
                        PreferenceUtil.setMemberStatus(MainActivity.this, true);
                    } else if (errNumber.equalsIgnoreCase("5")) {
                        lauchRegister();
                    } else {
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    //===========================================API BARU============================================
    private void cekMember() {
        isMemberCall = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(this), "OTU");
        isMemberCall.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (call.isCanceled()) {
                    return;
                }
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String errNumber = response.body().getErrNumber();
                    if (errNumber.equalsIgnoreCase("0")) {
                        PreferenceUtil.setMemberStatus(MainActivity.this, true);
                    } else if (errNumber.equalsIgnoreCase("4")) {//awalnya 5
                        lauchRegister();
                    } else {
                        Toast.makeText(getBaseContext(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public ArrayList<String> arrayPhone = new ArrayList<>();
    public ArrayList<String> arrayName = new ArrayList<>();
    private int intCurrentPage = 0;
    private int startPosition = 0;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PER_PAGE = 200; //5; //200;
    private static final int COUNTRY_CODE = 62; // 91; 62;


    private void synchContacts() {
        if (!mDbHelper.isContactsExists()) {
//            readContacts();
        }
    }

    private void syncAddressBook() {
        Log.d(TAG, "syncAddressBook start");
        AddressBookHelper.getInstance().updateAddressBookContactList(mDbHelper, AppSession.getSession().getUser().getPassword()).onErrorResumeNext(e -> Observable.empty())
                .subscribe(success -> Log.d(TAG, "syncAddressBook success= " + success));
    }

//    public void readContacts() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
//            intCurrentPage++;
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        arrayPhone.clear();
//                        ContentResolver cr = getContentResolver();
//                        Cursor cur;
//
//                        String limit = String.valueOf(startPosition) + ", " + PER_PAGE;
//
//                        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC LIMIT " + limit);
//                        if (cur.getCount() > 0) {
//                            arrayName.clear();
//                            while (cur.moveToNext()) {
//                                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
//                                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                                            new String[]{id}, null);
//                                    while (pCur.moveToNext()) {
//                                        String phonenumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                                        phonenumber = phonenumber.replaceAll("[()-]", "");
//                                        phonenumber = phonenumber.replaceAll(" ", "");
//                                        if (phonenumber.startsWith("0")) {
//                                            phonenumber = phonenumber.replaceFirst("0", String.valueOf(COUNTRY_CODE));
//                                        } else if (!phonenumber.startsWith(String.valueOf(COUNTRY_CODE))) {
//                                            phonenumber = COUNTRY_CODE + phonenumber;
//                                        }
//                                        if (!arrayPhone.contains(phonenumber.trim())) {
//                                            if (!phonenumber.isEmpty()) {
//                                                arrayPhone.add(phonenumber);
//                                                arrayName.add(contactname);
//                                            }
//                                        }
//                                    }
//                                    pCur.close();
//                                }
//                            }
//                            sendContact();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }).start();
//        }
//    }

//    private void sendContact() {
//        PagedRequestBuilder pagedRequestBuilder = new PagedRequestBuilder();
//        pagedRequestBuilder.setPage(1);
//        pagedRequestBuilder.setPerPage(arrayPhone.size());
//
//        for (int i = 0; i < arrayPhone.size(); ++i) {
//            if (!mDbHelper.isPhoneNumberExists(arrayPhone.get(i))) {
//                mDbHelper.insertContact(new ContactsModel(arrayPhone.get(i), arrayName.get(i), "0"));
//            }
//        }
//
//        ConnectycubeUsers.getUsersByPhoneNumbers(arrayPhone, pagedRequestBuilder).performAsync(new EntityCallback<ArrayList<ConnectycubeUser>>() {
//            @Override
//            public void onSuccess(ArrayList<ConnectycubeUser> users, Bundle params) {
//                if (users.size() > 0) {
//                    for (int j = 0; j < users.size(); ++j) {
//                        if (!mDbHelper.isConnectycubeUser(users.get(j).getPhone())) {
//                            mDbHelper.updateContact(users.get(j).getPhone(), users.get(j).getId());
//                        }
//                    }
//                }
//                startPosition = intCurrentPage * PER_PAGE;
//                readContacts();
//            }
//
//            @Override
//            public void onError(ResponseException errors) {
//                Log.e("Error", errors.getErrors().toString());
//            }
//        });
//    }

    /*================================================end load deposit======================================================*/

    /*=====================================load deposit lama=====================================================*/
    /*public void load() {
        Call<DataDeposit> userCall = mApiInterfacePayment.getUserDeposit(PreferenceUtil.getNumberPhone(this)));
        userCall.enqueue(new Callback<DataDeposit>() {
            @Override
            public void onResponse(Call<DataDeposit> call, Response<DataDeposit> response) {
                //   loadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();
                    String msg = response.body().getMessage();
                    Log.d("OPPO-1", "onResponse: " + status + " / " + msg);

                    if (status.equals("OK")) {
                        // Toast.makeText(getBaseContext(), "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                        Double total = 0.0d;
                        try {
                            if (msg != null && !msg.trim().isEmpty())
                                total = Double.valueOf(msg);
                        } catch (Exception e) {
                            total = 0.0d;
                        }
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
                        String rupiah = format.format(total);

                        ((TextView) findViewById(R.id.tvSaldo)).setText(rupiah);
                    } else {
                        Toast.makeText(getBaseContext(), "Load balance deposit gagal:\n" + msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataDeposit> call, Throwable t) {
                //                   loadingDialog.dismiss();
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_REGISTER", t.getMessage().toString());
            }
        });

    }
    /*====================================================end load deposit lamaa==================================================*/

    /*=============================AYIK===========================================*/
    /*
    private void loadDetail() {
        String phone = PreferenceUtil.getNumberPhone(this));
        Call<RegisterResponse> userCall = apiInterfaceProfile.postDetail(phone);
        userCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getError();
                    if (status.equals("OK")) {
                        idEklanku = response.body().getMessage();
                        //save userid
                        preferenceManager.createUser(idEklanku);
                    } else {
                        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                        preferenceManager.createUser(phone);
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*=====================================end ayik=============================================*/

    public void restartApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Silahkan restart aplikasi OTU Chat");
        builder.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        PreferenceUtil.setFirstLaunch(MainActivity.this, true);
                        startLandingScreen();
                    }
                });
        if (!com.eklanku.otuChat.utils.Utils.isActivityFinishedOrDestroyed(this)) {
            builder.show();
        }
    }


    /*@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       *//* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*//*

        switch (item.getItemId()) {
            case R.id.account_settings:
                drawer.openDrawer(GravityCompat.END);
                break;
            case R.id.action_search:
                launchContactsActivity();
                break;
            case R.id.action_start_invite_friends:
                InviteFriendsActivity.start(this);
                break;
            case R.id.action_start_feedback:
                FeedbackActivity.start(this);
                break;
            case R.id.action_start_settings:
                //SettingsActivity.startForResult(DialogsListFragment.class);
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_start_about:
                AboutActivity.start(this);
                break;
            case R.id.action_web_qr_code:
                WebQRCodeActivity.start(this);
                break;
            case R.id.action_notification:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        //Toast.makeText(MainActivity.this, "Handle from navigation right", Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    private void displayRightNavigation() {
        final NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_right);
        navigationViewRight.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here
                switch (item.getItemId()) {
                    case R.id.action_web_qr_code:
                        WebQRCodeActivity.start(MainActivity.this);
                        break;
                    case R.id.action_notification:
                        Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        if (menuDialog()) {
                            Intent profile = new Intent(MainActivity.this, Profile.class);
                            startActivity(profile);
                        }

                        break;
                    case R.id.action_resetpass:
                        if (menuDialog()) {
                            Intent resetPass = new Intent(MainActivity.this, ResetPassword.class);
                            startActivity(resetPass);
                        }

                        break;
                    case R.id.action_resetpin:
                        if (menuDialog()) {
                            Intent resetPin = new Intent(MainActivity.this, ResetPIN.class);
                            startActivity(resetPin);
                        }

                        break;
                    case R.id.nav_logout:
                        warningLogoutpay();
                        break;

                    case R.id.action_hapusakun:
                        Intent nonAktif = new Intent(MainActivity.this, DeleteAccount.class);
                        startActivity(nonAktif);
                        break;
                }

                //Toast.makeText(MainActivity.this, "Handle from navigation right", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;

            }
        });
    }

    private boolean menuDialog() {
        if (!PreferenceUtil.isLoginStatus(MainActivity.this)) {
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("PERINGATAN!!!")
                    .setMessage("Untuk meningkatkan Kemanan Silahkan Login terlebih dahulu")
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, PaymentLogin.class));
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

//==========================================API LAMA
    /*public void loadSaldoBonus(String strUserID, String strAccessToken) {

        userCall = mApiInterfacePayment.getSaldodetail(strUserID, strApIUse, strAccessToken);
        userCall.enqueue(new Callback<DataSaldoBonus>() {
            @Override
            public void onResponse(Call<DataSaldoBonus> call, Response<DataSaldoBonus> response) {
                if (call.isCanceled()) {
                    return;
                }

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
                        if (carier_member.equals("FREE")) {
                            tvStarMember.setText("REGULER");
                        } else {
                            tvStarMember.setText(carier_member);
                        }

                    } else {
                        // Toast.makeText(MainActivity.this, "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataSaldoBonus> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                // Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void loadSaldoBonus(String strUserID, String strAccessToken) {
        Log.d("OPPO-1", "loadSaldoBonus: " + strUserID + ", " + strAccessToken);
        userCall = mApiInterfacePayment.getSaldodetail(strUserID, strApIUse, strAccessToken);
        userCall.enqueue(new Callback<DataSaldoBonus>() {
            @Override
            public void onResponse(Call<DataSaldoBonus> call, Response<DataSaldoBonus> response) {
                if (call.isCanceled()) {
                    return;
                }

                Log.d("AYIK", "response-saldo->"+ response.body()+"\n"+ response.toString());

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String error = response.body().getRespMessage();
                    String id_member = "", sisa_uang = "", carier_member = "", bonus_member = "";
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

                        if (carier_member.equals("FREE")) {
                            tvStarMember.setText("REGULER");
                        } else {
                            tvStarMember.setText(carier_member);
                        }

                    } else {
                        // Toast.makeText(MainActivity.this, "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataSaldoBonus> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                // Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }
}