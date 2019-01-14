package com.eklanku.otuChat.ui.activities.authorization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.settings.SettingsActivity;
import com.eklanku.otuChat.ui.fragments.dialogs.base.OneButtonDialogFragment;
import com.eklanku.otuChat.utils.AuthUtils;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.StringObfuscator;
/*import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;*/
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;


import com.connectycube.core.EntityCallback;
import com.connectycube.core.ConnectycubeErrors;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.PagedRequestBuilder;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.LoginType;
import com.quickblox.q_municate_core.qb.commands.rest.QBSignUpCommand;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.ConnectycubeUsers;
import com.connectycube.users.model.ConnectycubeUser;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LandingActivity extends BaseAuthActivity {

    @Bind(R.id.app_version_textview)
    TextView appVersionTextView;

//    @Bind(R.id.phone_number_connect_button)
//    Button phoneNumberConnectButton;

    //FACEBOOK KIT====================================
    public static int APP_REQUEST_CODE = 99;
    private Button login; //logout;
    private String TAG = "AYIK";
    //================================================
    private static final int REQUEST_READ_PHONE_STATE = 0;

    private int loginTryCount = 0;
    public ServiceManager serviceManager;
    private SignUpSuccessAction signUpSuccessAction = new SignUpSuccessAction();


    public static void start(Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, Intent intent) {
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_landing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populate();

        initVersionName();

        serviceManager = ServiceManager.getInstance();
        serviceManager.initUserTable();

        //getCurrentAccount();

        //FACEBOOK KIT==========================================
        //logout = findViewById(R.id.logout);
        login = findViewById(R.id.login);

        if (app.isNeedToUpdate()) {
            updateApp();
        }

        /*if(AccountKit.getCurrentAccessToken() != null) {
            AccountKit.logOut();
        }*/
        getCurrentAccount();
        //======================================================
    }

    private void updateApp() {
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
//    @OnClick(R.id.login_button)
//    void login(View view) {
//        LoginActivity.start(LandingActivity.this);
//        finish();
//    }

//    @OnClick(R.id.phone_number_connect_button)
//    void phoneNumberConnect(View view) {
//        if (checkNetworkAvailableWithError()) {
//            loginType = LoginType.FIREBASE_PHONE;
//            startSocialLogin();
//        }
//    }

    private void addActions() {
        addAction(QBServiceConsts.SIGNUP_SUCCESS_ACTION, signUpSuccessAction);
        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.SIGNUP_SUCCESS_ACTION);
        updateBroadcastActionList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addActions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeActions();
    }

    @Override
    public void checkShowingConnectionError() {
        // nothing. Toolbar is missing.
    }

//    private void startSignUpActivity() {
//        SignUpActivity.start(LandingActivity.this);
//        finish();
//    }

    private void initVersionName() {
        appVersionTextView.setText(StringObfuscator.getAppVersionName());
    }

    //FACEBOOK KIT=========================================
    private void getCurrentAccount() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null) {
            //Log.e(TAG, accessToken.toString());
            //Handle Returning User
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    // Get Account Kit ID
                    String accountKitId = account.getId();
                    //Log.e(TAG, accountKitId);

                    if (account.getPhoneNumber() != null) {
                       /* Log.e(TAG, "" + account.getPhoneNumber().getCountryCode());
                        Log.e(TAG, "" + account.getPhoneNumber().getPhoneNumber());*/

                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String phoneNumberString = phoneNumber.toString();
                        //logout.setVisibility(View.VISIBLE);
                        login.setVisibility(View.GONE);
                        //Log.e(TAG, phoneNumberString);
                        PreferenceUtil.setNumberPhone(LandingActivity.this, phoneNumberString);
                        authenticateWithNumber(phoneNumber.getRawPhoneNumber());
                        //authenticateWithNumber("919898989898");
                    }
                }

                @Override
                public void onError(final AccountKitError error) {
                    // Handle Error
                    //Log.e(TAG, error.toString());
                    Toast.makeText(LandingActivity.this, "ERROR, " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void phoneLogin(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        UIManager uiManager;
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        com.facebook.accountkit.ui.LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN

        //di param UI Manager di hilangkan parameter com.facebook.accountkit.ui.LoginType.PHONE
        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.colorTextOtu, null) : getResources().getColor(R.color.colorTextOtu)),
                R.drawable.ic_background,
                SkinManager.Tint.WHITE,
                0.55
        );
        configurationBuilder.setDefaultCountryCode("ID");
        configurationBuilder.setUIManager(uiManager);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            if (data != null) {
                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                String toastMessage = "";
                if (loginResult.getError() != null) {
                    toastMessage = loginResult.getError().getErrorType().getMessage();
                    errorDialog(loginResult.getError().toString());
                    //Log.d(TAG, "Error " + loginResult.getError().toString());
                } else if (loginResult.wasCancelled()) {
                    toastMessage = "Login Cancelled";
                } else {
                    // Success! Start your next activity...
                    getCurrentAccount();
                }
                // Surface the result to your user in an appropriate way.
                if (!toastMessage.isEmpty())
                    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                //Log.d(TAG, "Error data is empty");
            }

        } else {
            //logout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }

    public void errorDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

//    public void logout(@Nullable View view) {
//        AccountKit.logOut();
//        AccessToken accessToken = AccountKit.getCurrentAccessToken();
//        if (accessToken != null) {
//            Log.d(TAG, "Still Logged in...");
//        } else {
//            logout.setVisibility(View.GONE);
//            login.setVisibility(View.VISIBLE);
//        }
//    }
    //=====================================================

    // QBAuth with phone number

    protected void authenticateWithNumber(String strPhoneNumber) {
        showProgress();

        Log.d(TAG, "authenticateWithNumber: " + strPhoneNumber);
        loginType = LoginType.FIREBASE_PHONE;
        login(strPhoneNumber);
    }

    protected void login(String userPhone) {

        loginTryCount++;
        appSharedHelper.saveFirstAuth(true);
        appSharedHelper.saveSavedRememberMe(true);
        appSharedHelper.saveUsersImportInitialized(true);
        ConnectycubeUser user = new ConnectycubeUser(userPhone, userPhone, null);

        serviceManager.login(user).subscribe(new Observer<ConnectycubeUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("LOGIN ERROR", "onError" + e.getMessage());
                if (loginTryCount <= 2) {
                    signUpWithNumber(userPhone);
                } else {
                    hideProgress();

                    OneButtonDialogFragment.show(getSupportFragmentManager(), R.string.dlg_auth_error_message, false, new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            loginType = LoginType.FIREBASE_PHONE;
                            startSocialLogin();
                        }
                    });
                }
                //AuthUtils.parseExceptionMessage(LandingActivity.this, e.getMessage());

            }

            @Override
            public void onNext(ConnectycubeUser connectycubeUser) {
                performLoginSuccessAction();
            }
        });
    }

    private void performLoginSuccessAction() {
        hideProgress();

        startMainActivity();
    }

    protected void signUpWithNumber(String userPhone) {
        //Log.d("OPPO-1", "signUpWithNumber signUpWithNumber: "+userPhone);
        ConnectycubeUser connectycubeUser = new ConnectycubeUser(userPhone, userPhone, null);
        connectycubeUser.setPhone(userPhone);
        connectycubeUser.setFullName(userPhone);
        appSharedHelper.saveUsersImportInitialized(false);
        DataManager.getInstance().clearAllTables();
        QBSignUpCommand.start(LandingActivity.this, connectycubeUser, null);
    }

    private class SignUpSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            ConnectycubeUser user = (ConnectycubeUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
            login(user.getLogin());
        }
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
                        checkSelfPermission(BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED /*&&
                        checkSelfPermission(VIBRATE) == PackageManager.PERMISSION_GRANTED*/
                ) {

            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            requestPermissions(new String[]{

                    READ_PHONE_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,
                    CAMERA, RECEIVE_SMS, READ_CONTACTS, MODIFY_AUDIO_SETTINGS, BLUETOOTH, BLUETOOTH_ADMIN, RECORD_AUDIO/*, VIBRATE*/
            }, REQUEST_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{

                    READ_PHONE_STATE,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE,
                    CAMERA, RECEIVE_SMS, READ_CONTACTS, MODIFY_AUDIO_SETTINGS, BLUETOOTH, BLUETOOTH_ADMIN, RECORD_AUDIO/*, VIBRATE*/

            }, REQUEST_READ_PHONE_STATE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 12 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[7] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[8] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[9] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[10] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[11] == PackageManager.PERMISSION_GRANTED/*&&
                    grantResults[10] == PackageManager.PERMISSION_GRANTED*/
                    )


            {

            } else {
                finish();

            }
        }
    }
}