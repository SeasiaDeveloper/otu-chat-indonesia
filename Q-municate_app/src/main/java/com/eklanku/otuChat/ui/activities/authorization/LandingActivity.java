package com.eklanku.otuChat.ui.activities.authorization;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class LandingActivity extends BaseAuthActivity {

    @Bind(R.id.app_version_textview)
    TextView appVersionTextView;

    @Bind(R.id.phone_number_connect_button)
    Button phoneNumberConnectButton;

    //FACEBOOK KIT====================================
    public static int APP_REQUEST_CODE = 99;
    private Button login, logout;
    private String TAG = "AYIK";
    //================================================

    private int loginTryCount = 0;
    public ServiceManager serviceManager;
    private SignUpSuccessAction signUpSuccessAction;

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

        initVersionName();

        serviceManager = ServiceManager.getInstance();
        signUpSuccessAction = new SignUpSuccessAction();
        addAction(QBServiceConsts.SIGNUP_SUCCESS_ACTION, signUpSuccessAction);

        //FACEBOOK KIT==========================================
        logout = findViewById(R.id.logout);
        login = findViewById(R.id.login);

        /*if(AccountKit.getCurrentAccessToken() != null) {
            AccountKit.logOut();
        }*/
        getCurrentAccount();
        //======================================================
    }

    @OnClick(R.id.login_button)
    void login(View view) {
        LoginActivity.start(LandingActivity.this);
        finish();
    }

    @OnClick(R.id.phone_number_connect_button)
    void phoneNumberConnect(View view) {
        if (checkNetworkAvailableWithError()) {
            loginType = LoginType.FIREBASE_PHONE;
            startSocialLogin();
        }
    }

    @Override
    public void checkShowingConnectionError() {
        // nothing. Toolbar is missing.
    }

    private void startSignUpActivity() {
        SignUpActivity.start(LandingActivity.this);
        finish();
    }

    private void initVersionName() {
        appVersionTextView.setText(StringObfuscator.getAppVersionName());
    }

    //FACEBOOK KIT=========================================
    private void getCurrentAccount() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null) {
            Log.e(TAG, accessToken.toString());
            //Handle Returning User
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    // Get Account Kit ID
                    String accountKitId = account.getId();
                    Log.e(TAG, accountKitId);

                    if (account.getPhoneNumber() != null) {
                        Log.e(TAG, "" + account.getPhoneNumber().getCountryCode());
                        Log.e(TAG, "" + account.getPhoneNumber().getPhoneNumber());

                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String phoneNumberString = phoneNumber.toString();
                        //logout.setVisibility(View.VISIBLE);
                        login.setVisibility(View.GONE);
                        Log.e(TAG, phoneNumberString);
                        PreferenceUtil.setNumberPhone(LandingActivity.this, phoneNumberString);
                        authenticateWithNumber(phoneNumber.getRawPhoneNumber());
                        //authenticateWithNumber("919898989898");
                    }
                }

                @Override
                public void onError(final AccountKitError error) {
                    // Handle Error
                    Log.e(TAG, error.toString());
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
        uiManager = new SkinManager(com.facebook.accountkit.ui.LoginType.PHONE, SkinManager.Skin.TRANSLUCENT,
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.colorTextOtu,null):getResources().getColor(R.color.colorTextOtu)),
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
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage = "";
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                errorDialog(loginResult.getError().toString());
                Log.d(TAG, "Error " + loginResult.getError().toString());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                // Success! Start your next activity...
                getCurrentAccount();
            }

            // Surface the result to your user in an appropriate way.
            if(!toastMessage.isEmpty())
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        } else {
            logout.setVisibility(View.GONE);
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

    public void logout(@Nullable View view) {
        AccountKit.logOut();
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            Log.d(TAG, "Still Logged in...");
        } else {
            logout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }
    }
    //=====================================================

    // QBAuth with phone number

    protected void authenticateWithNumber(String strPhoneNumber){
        showProgress();

        Log.d(TAG, "authenticateWithNumber: "+strPhoneNumber);
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
                if (loginTryCount <= 2)
                {
                    signUpWithNumber(userPhone);
                }
                else
                {
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
        Log.d("OPPO-1", "signUpWithNumber signUpWithNumber: "+userPhone);
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
}