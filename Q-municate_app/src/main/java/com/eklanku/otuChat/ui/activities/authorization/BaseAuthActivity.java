package com.eklanku.otuChat.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Register;
import com.eklanku.otuChat.ui.fragments.dialogs.UserAgreementDialogFragment;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.connectycube.auth.model.ConnectycubeProvider;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.fragments.dialogs.UserAgreementDialogFragment;
import com.eklanku.otuChat.utils.AuthUtils;
import com.eklanku.otuChat.utils.StringObfuscator;
import com.eklanku.otuChat.utils.helpers.FlurryAnalyticsHelper;
import com.eklanku.otuChat.utils.helpers.GoogleAnalyticsHelper;
import com.eklanku.otuChat.utils.helpers.FacebookHelper;
import com.eklanku.otuChat.utils.helpers.FirebaseAuthHelper;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.LoginType;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import butterknife.Bind;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.Subscriber;

public abstract class BaseAuthActivity extends BaseActivity {

    private static String TAG = BaseAuthActivity.class.getSimpleName();

    protected static final String STARTED_LOGIN_TYPE = "started_login_type";

    @Nullable
    @Bind(R.id.email_textinputlayout)
    protected TextInputLayout emailTextInputLayout;

    @Nullable
    @Bind(R.id.email_edittext)
    protected EditText emailEditText;

    @Nullable
    @Bind(R.id.password_textinputlayout)
    protected TextInputLayout passwordTextInputLayout;

    @Nullable
    @Bind(R.id.password_edittext)
    protected EditText passwordEditText;

    protected FacebookHelper facebookHelper;
    private FirebaseAuthHelper firebaseAuthHelper;
    private FirebaseAuthCallback firebaseAuthCallback;

    protected LoginType loginType = LoginType.EMAIL;
    protected Resources resources;

    private ServiceManager serviceManager;

    public static void start(Context context) {
        Intent intent = new Intent(context, BaseAuthActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        facebookHelper.onActivityStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        facebookHelper.onActivityStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STARTED_LOGIN_TYPE, loginType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult, result code = " + requestCode);
        if (requestCode == FirebaseAuthHelper.RC_SIGN_IN){
            onReceiveFirebaseAuthResult(resultCode, data);
        }
        facebookHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void onReceiveFirebaseAuthResult(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            firebaseAuthHelper.refreshInternalFirebaseToken(firebaseAuthCallback);
        } else {
            //Sign in failed
            if (response == null) {
                // User pressed back button
                Log.i(TAG, "BACK button pressed");
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK || response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.dlg_internet_connection_error, Snackbar.LENGTH_INDEFINITE);
            }
        }
    }

    @Nullable
    @OnTextChanged(R.id.email_edittext)
    void onTextChangedEmail(CharSequence text) {
        emailTextInputLayout.setError(null);
    }

    @Nullable
    @OnTextChanged(R.id.password_edittext)
    void onTextChangedPassword(CharSequence text) {
        passwordTextInputLayout.setError(null);
    }

    private void initFields(Bundle savedInstanceState) {
        resources = getResources();
        if (savedInstanceState != null && savedInstanceState.containsKey(STARTED_LOGIN_TYPE)) {
            loginType = (LoginType) savedInstanceState.getSerializable(STARTED_LOGIN_TYPE);
        }
        facebookHelper = new FacebookHelper(this);
        firebaseAuthHelper = new FirebaseAuthHelper(BaseAuthActivity.this);
        firebaseAuthCallback = new FirebaseAuthCallback();
        failAction = new FailAction();
        serviceManager = ServiceManager.getInstance();
    }

    protected void startSocialLogin() {
        /*if (!appSharedHelper.isShownUserAgreement()) {
            UserAgreementDialogFragment
                    .show(getSupportFragmentManager(), new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            appSharedHelper.saveShownUserAgreement(true);
                            loginWithSocial();
                        }
                    });
        } else {*/
        loginWithSocial();
//        }
    }

    private void loginWithSocial() {
        appSharedHelper.saveFirstAuth(true);
        appSharedHelper.saveSavedRememberMe(true);
        if (loginType.equals(LoginType.FACEBOOK)) {
            facebookHelper.login(new FacebookLoginCallback());
        } else if (loginType.equals(LoginType.FIREBASE_PHONE)) {
            appSharedHelper.saveFerebaseProjectId(StringObfuscator.getFirebaseAuthProjectId());
            firebaseAuthHelper.loginByPhone(BaseAuthActivity.this);
        }
    }

    protected void startMainActivity(ConnectycubeUser user) {
        AppSession.getSession().updateUser(user);
        startMainActivity();
    }

    protected void startMainActivity() {
        //startActivity(new Intent(BaseAuthActivity.this, Register.class));
        MainActivity.start(BaseAuthActivity.this);
        finish();
    }

    protected void login(String userEmail, final String userPassword) {
        appSharedHelper.saveFirstAuth(true);
        appSharedHelper.saveSavedRememberMe(true);
        appSharedHelper.saveUsersImportInitialized(true);
        ConnectycubeUser user = new ConnectycubeUser(null, userPassword, userEmail);

        serviceManager.login(user).subscribe(new Observer<ConnectycubeUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + e.getMessage());
                hideProgress();
                AuthUtils.parseExceptionMessage(BaseAuthActivity.this, e.getMessage());
            }

            @Override
            public void onNext(ConnectycubeUser connectycubeUser) {
                performLoginSuccessAction(connectycubeUser);
            }
        });
    }

    protected void startLandingScreen() {
        LandingActivity.start(this);
        finish();
    }

    private void performLoginSuccessAction(ConnectycubeUser user) {
        user.setPassword(user.getLogin());
        user.setOldPassword(ConnectycubeSessionManager.getInstance().getToken());
        ServiceManager.getInstance().updateUserPassword(user).subscribe(new Subscriber<QMUser>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(QMUser qmUser) {
                startMainActivity(qmUser);

                // send analytics data
                GoogleAnalyticsHelper.pushAnalyticsData(BaseAuthActivity.this, user, "User Sign In");
                FlurryAnalyticsHelper.pushAnalyticsData(BaseAuthActivity.this);
            }
        });
        //startMainActivity(user);

        // send analytics data
        //GoogleAnalyticsHelper.pushAnalyticsData(this, user, "User Sign In");
        //FlurryAnalyticsHelper.pushAnalyticsData(this);
    }

    private Observer<ConnectycubeUser> socialLoginObserver = new Observer<ConnectycubeUser>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError " + e.getMessage());
            hideProgress();
            AuthUtils.parseExceptionMessage(BaseAuthActivity.this, e.getMessage());
        }

        @Override
        public void onNext(ConnectycubeUser connectycubeUser) {
            performLoginSuccessAction(connectycubeUser);
        }
    };

    private class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "+++ FacebookCallback call onSuccess from BaseAuthActivity +++");
            showProgress();
            serviceManager.login(ConnectycubeProvider.FACEBOOK, loginResult.getAccessToken().getToken(), null)
                    .subscribe(socialLoginObserver);
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "+++ FacebookCallback call onCancel from BaseAuthActivity +++");
            hideProgress();
        }

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG, "+++ FacebookCallback call onCancel BaseAuthActivity +++");
            hideProgress();
        }
    }

    private class FirebaseAuthCallback implements FirebaseAuthHelper.RequestFirebaseIdTokenCallback {

        @Override
        public void onSuccess(String authToken) {
            Log.d(TAG, "FirebaseAuthCallback onSuccess()");
            showProgress();
            serviceManager.login(ConnectycubeProvider.FIREBASE_PHONE, authToken, appSharedHelper.getFirebaseProjectId())
                    .subscribe(socialLoginObserver);
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "FirebaseAuthCallback onError()");
            hideProgress();
        }
    }
}