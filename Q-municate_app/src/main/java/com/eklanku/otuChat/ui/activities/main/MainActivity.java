package com.eklanku.otuChat.ui.activities.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.Application;
import com.eklanku.otuChat.ReferrerReceiver;
import com.eklanku.otuChat.ui.activities.authorization.SplashActivity;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.payment.models.DataBanner;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.LoadBanner;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Register;
import com.eklanku.otuChat.ui.activities.settings.SettingTabPaymentFragment;
import com.eklanku.otuChat.ui.adapters.chats.DialogsListAdapter;
import com.eklanku.otuChat.ui.fragments.CallFragment;
import com.eklanku.otuChat.ui.fragments.PaymentFragment;
import com.eklanku.otuChat.ui.views.banner.GlideImageLoader;
<<<<<<< HEAD
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
=======
import com.eklanku.otuChat.utils.helpers.AddressBookHelper;
>>>>>>> origin/feature/address_book
import com.google.firebase.auth.FirebaseAuth;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.payment.models.DataDeposit;
import com.eklanku.otuChat.ui.activities.payment.models.RegisterResponse;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.rest.ApiClient;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiClientProfile;
import com.eklanku.otuChat.ui.activities.rest.ApiInterface;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfaceProfile;
import com.eklanku.otuChat.ui.activities.settings.SettingsActivityOtu;
import com.eklanku.otuChat.ui.adapters.chats.DialogsListAdapter;
import com.eklanku.otuChat.ui.fragments.chats.DialogsListFragment;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.helpers.FacebookHelper;
import com.eklanku.otuChat.utils.helpers.ImportFriendsHelper;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.eklanku.otuChat.utils.MediaUtils;

import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.models.UserCustomData;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.Utils;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.yyydjk.library.BannerLayout;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import com.eklanku.otuChat.ui.views.banner.GlideImageLoader;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.eklanku.otuChat.utils.helpers.DbHelper;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.PagedRequestBuilder;
import com.connectycube.users.ConnectycubeUsers;
import com.connectycube.users.model.ConnectycubeUser;

public class MainActivity extends BaseLoggableActivity implements ObservableScrollViewCallbacks {

    @Bind(R.id.bannerLayout)
    BannerLayout bannerSlider;

    private static final String TAG = MainActivity.class.getSimpleName();

    private FacebookHelper facebookHelper;

    private ImportFriendsSuccessAction importFriendsSuccessAction;
    private ImportFriendsFailAction importFriendsFailAction;

    //ayik
    /*Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem tabChats;
    TabItem tabStatus;
    TabItem tabCalls;*/

    Intent intent;
    public static final int REQUEST_CODE_LOGOUT = 300;
    protected BaseActivity baseActivity;
    private DialogsListAdapter dialogsListAdapter;
    ApiInterface mApiInterface;
    ApiInterfaceProfile apiInterfaceProfile;
    ApiInterfacePayment mApiInterfacePayment;

    private String idEklanku;
    private PreferenceManager preferenceManager;
    public static MainActivity mainActivity;

    String strUserID;
    String strAccessToken;
    String strApIUse = "OTU";

    private static String[] banner_promo;
    BannerLayout bannerLayout;
    //CardView cvMain;
    //public static TextView lblSaldo;
    LinearLayout layoutCollaps, layoutSaldo;

   /* boolean isReferrerDetected = Application.isReferrerDetected(getApplicationContext());
    String firstLaunch = Application.getFirstLaunch(getApplicationContext());
    String referrerDate = Application.getReferrerDate(getApplicationContext());
    String referrerDataRaw = Application.getReferrerDataRaw(getApplicationContext());
    String referrerDataDecoded = Application.getReferrerDataDecoded(getApplicationContext());*/

    public boolean isReferrerDetected;
    public String firstLaunch, referrerDate, referrerDataRaw, referrerDataDecoded;

    private static final int REQUEST_READ_PHONE_STATE = 0;
    boolean doubleBackToExitPressedOnce = false;

    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };

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

//                    if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
//                        logOutPayment();
//                    }

                    layoutSaldo.setVisibility(View.GONE);
                    break;
                case R.id.navigation_call:
                    fragment = new CallFragment();

                    layoutSaldo.setVisibility(View.GONE);
                    if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                        strUserID = user.get(preferenceManager.KEY_USERID);
                        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
                    }
                   /* if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        logOutPayment();
                    }*/

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

                    break;


               /* case R.id.navigation_pengaturan:
=======
                /*case R.id.navigation_pengaturan:
>>>>>>> 98ec7323b604b24ea4af934e918e2b49186ff6ed
                    fragment = new SettingTabPaymentFragment();

                    if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                        strUserID = user.get(preferenceManager.KEY_USERID);
                        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
                    }
                    *//*if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                        logOutPayment();
<<<<<<< HEAD
                    }

=======
                    }*//*
                    layoutSaldo.setVisibility(View.GONE);
>>>>>>> 98ec7323b604b24ea4af934e918e2b49186ff6ed
                    break;*/
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //lblSaldo = (TextView) findViewById(R.id.tvSaldo);
        layoutCollaps = findViewById(R.id.layout_);
        layoutSaldo = findViewById(R.id.laySaldo);
        layoutSaldo.setVisibility(View.GONE);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterfaceProfile = ApiClientProfile.getClient().create(ApiInterfaceProfile.class);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);

        preferenceManager = new PreferenceManager(this);

        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        populate();
        initFields();
        setUpActionBarWithUpButton();
        synchContacts();
        syncAddressBook();

        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }

        addDialogsAction();
        openPushDialogIfPossible();

        mToolbarView = findViewById(R.id.toolbar_view);
        //cvMain = findViewById(R.id.cv_main);

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.density_200);

        Activity activity = this;
        if (!activity.isFinishing()) {
            loadBanner();
            //loadDeposite(strUserID,strAccessToken);
        }

        /*tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);

        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));*/

        /*viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
                    HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                    strUserID = user.get(preferenceManager.KEY_USERID);
                    strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);
                }
                switch (position) {
                    case 0:
//                        if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
//                            logOutPayment();
//                        }
//                        break;
                    case 1:
//                        if (PreferenceUtil.isLoginStatus(MainActivity.this)) {
//                            logOutPayment();
//                        }
//                        break;
                    case 2:

                        if (!PreferenceUtil.isMemberStatus(MainActivity.this)) {

                            cekMember();
                        }
                        // DO NOTHING
                        break;
                    default:
                        //DO NOTHING
                }
            }
        });
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.setCurrentItem(0);
        tabLayout.getTabAt(2).setCustomView(R.layout.custom_tabs_payment);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tabs_calls);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_chats);*/

        preferenceManager = new PreferenceManager(this);

        if (!PreferenceUtil.isFirstLaunch(this)) {
            restartApp();
        }

        loadFragment(new DialogsListFragment());

        mainActivity = this;
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
        } else {
            startGroupChatActivity(chatDialog);
        }
    }

    private void initFields() {
        Log.d(TAG, "initFields()");
        mDbHelper = new DbHelper(this);
        title = " " + AppSession.getSession().getUser().getFullName();
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
        actualizeCurrentTitle();
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver, new IntentFilter(ReferrerReceiver.ACTION_UPDATE_DATA));
        super.onResume();
        addActions();

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

    public void logOutPayment() {
        Log.d("OPPO-1", strUserID + ", " + strAccessToken + ", " + getCurrentTime());
        Call<ResetPassResponse> callResetPass = mApiInterfacePayment.postLogoutPayment(strUserID, strAccessToken, getCurrentTime());
        callResetPass.enqueue(new Callback<ResetPassResponse>() {

            @Override
            public void onResponse(Call<ResetPassResponse> call, Response<ResetPassResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();

                    if (status.equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(MainActivity.this, "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setLoginStatus(MainActivity.this, false);
                    } else {
                        Toast.makeText(MainActivity.this, "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                //Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
    }

    private void actualizeCurrentTitle() {
        if (AppSession.getSession().getUser().getFullName() != null) {
            title = " " + AppSession.getSession().getUser().getFullName();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateReceiver);
        super.onPause();
        removeActions();
    }

    @Override
    protected void onDestroy() {
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
        }
    }

    private void loadLogoActionBar(String logoUrl) {
        ImageLoader.getInstance().loadImage(logoUrl, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        setActionBarIcon(MediaUtils.getRoundIconDrawable(MainActivity.this, loadedBitmap));
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

    public void load_deposit() {
        Call<DataDeposit> userCall = mApiInterfacePayment.getSaldo(strUserID, strApIUse, strAccessToken);
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

                        ((TextView) findViewById(R.id.tvSaldo)).setText(rupiah);
                    } else {
                        Toast.makeText(getBaseContext(), "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataDeposit> call, Throwable t) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                Log.d("API_REGISTER", t.getMessage().toString());
            }
        });

    }

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


    private void cekMember() {
//        Log.d("OPPO-1", "cekMember:process"+AppSession.getSession().getUser().getPhone());
        // Call<DataProfile> isMember = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(this)), "OTU");
        Call<DataProfile> isMember = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(this), "OTU");

        isMember.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
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

    public void loadBanner() {
        bannerSlider.setImageLoader(new GlideImageLoader());
        List<String> urls = new ArrayList<>();
        Call<LoadBanner> callLoadBanner = mApiInterfacePayment.getBanner(PreferenceUtil.getNumberPhone(MainActivity.this), strApIUse);
        callLoadBanner.enqueue(new Callback<LoadBanner>() {
            @Override
            public void onResponse(Call<LoadBanner> call, Response<LoadBanner> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals("SUCCESS")) {
                        final List<DataBanner> result = response.body().getRespMessage();
                        banner_promo = new String[result.size()];
                        if (result.size() > 0) {
                            try {
                                for (int i = 0; i < result.size(); i++) {
                                    banner_promo[i] = result.get(i).getBaner_promo();
                                    urls.add(banner_promo[i]);
                                }
                            } catch (Exception e) {
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                            }
                        } else {
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                        }
                    } else {
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                    }
                    bannerSlider.setViewUrls(urls);
                }
            }

            @Override
            public void onFailure(Call<LoadBanner> call, Throwable t) {
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");

                bannerSlider.setViewUrls(urls);
            }
        });
    }

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

    private void updateData() {
        isReferrerDetected = Application.isReferrerDetected(getApplicationContext());
        firstLaunch = Application.getFirstLaunch(getApplicationContext());
        referrerDate = Application.getReferrerDate(getApplicationContext());
        referrerDataRaw = Application.getReferrerDataRaw(getApplicationContext());
        referrerDataDecoded = Application.getReferrerDataDecoded(getApplicationContext());

       /* StringBuilder sb = new StringBuilder();
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
        }*/

       /* content.setText(Html.fromHtml(sb.toString()));
        content.setMovementMethod(new LinkMovementMethod());*/
    }

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
                        SplashActivity.start(MainActivity.this);
                        finish();
                    }
                });
        builder.show();
    }

    public void loadDeposite(String strUserID, String strAccessToken) {
        //Log.d("AYIK", "OnLoad userID " + strUserID + " accessToken " + strAccessToken);
        Call<DataDeposit> userCall = mApiInterfacePayment.getSaldo(strUserID, strApIUse, strAccessToken);
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

                        //lblSaldo.setText(rupiah);
                    } else {
                        Toast.makeText(MainActivity.this, "Load balance deposit gagal:\n" + error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataDeposit> call, Throwable t) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });

    }

  /*  @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateReceiver);
        super.onPause();
    }*/

   /* @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver, new IntentFilter(ReferrerReceiver.ACTION_UPDATE_DATA));
        super.onResume();
    }*/
}