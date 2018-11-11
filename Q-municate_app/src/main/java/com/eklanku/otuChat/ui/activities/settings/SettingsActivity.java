package com.eklanku.otuChat.ui.activities.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.feedback.FeedbackActivity;
import com.eklanku.otuChat.ui.activities.main.MainActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.payment.models.ResetPassResponse;
import com.eklanku.otuChat.ui.activities.profile.MyProfileActivity;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.connectycube.auth.session.ConnectycubeSettings;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.changepassword.ChangePasswordActivity;
import com.eklanku.otuChat.ui.activities.feedback.FeedbackActivity;
import com.eklanku.otuChat.ui.activities.invitefriends.InviteFriendsActivity;
import com.eklanku.otuChat.ui.activities.profile.MyProfileActivity;
import com.eklanku.otuChat.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.ToastUtils;
import com.eklanku.otuChat.utils.helpers.FacebookHelper;
import com.eklanku.otuChat.utils.helpers.FirebaseAuthHelper;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.LoginType;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;

public class SettingsActivity extends BaseLoggableActivity {

    public static final int REQUEST_CODE_LOGOUT = 300;

    @Bind(R.id.avatar_imageview)
    RoundedImageView avatarImageView;

    @Bind(R.id.full_name_edittext)
    TextView fullNameTextView;

    @Bind(R.id.push_notification_switch)
    SwitchCompat pushNotificationSwitch;

    @Bind(R.id.change_password_view)
    RelativeLayout changePasswordView;

    private QMUser user;
    private FacebookHelper facebookHelper;
    private FirebaseAuthHelper firebaseAuthHelper;

    Call<ResetPassResponse> callResetPass;
    ApiInterfacePayment mApiInterfacePayment;
    PreferenceManager preferenceManager;
    String strUserID, strAccessToken;

    public static void startForResult(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), SettingsActivity.class);
        //rina
//        Intent intent = new Intent(fragment.getActivity(), SettingsActivityOtu.class);
        fragment.getActivity().startActivityForResult(intent, REQUEST_CODE_LOGOUT);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
        preferenceManager = new PreferenceManager(this);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        strUserID = user.get(preferenceManager.KEY_USERID);
        strAccessToken = user.get(preferenceManager.KEY_ACCESS_TOKEN);

        addActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIData();
    }

    private void updateUIData() {
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        fillUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @OnClick(R.id.edit_profile_imagebutton)
    void editProfile() {
        MyProfileActivity.start(this);
    }

    @OnCheckedChanged(R.id.push_notification_switch)
    void enablePushNotification(boolean enable) {
        ConnectycubeSettings.getInstance().setEnablePushNotification(enable);
    }

    @OnClick(R.id.invite_friends_button)
    void inviteFriends() {
        InviteFriendsActivity.start(this);
    }

    @OnClick(R.id.give_feedback_button)
    void giveFeedback() {
        FeedbackActivity.start(this);
    }

    @OnClick(R.id.change_password_button)
    void changePassword() {
        ChangePasswordActivity.start(this);
    }

    @OnClick(R.id.logout_button)
    void logout() {
        if (checkNetworkAvailableWithError()) {
            TwoButtonsDialogFragment
                    .show(getSupportFragmentManager(), R.string.dlg_logout, R.string.dlg_confirm,
                            new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    showProgress();

                                    facebookHelper.logout();
                                    firebaseAuthHelper.logout();

                                    ServiceManager.getInstance().logout(new Subscriber<Void>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ErrorUtils.showError(SettingsActivity.this, e);
                                            hideProgress();
                                        }

                                        @Override
                                        public void onNext(Void aVoid) {
                                            setResult(RESULT_OK);
                                            hideProgress();
                                            logOutPayment();
//                                            finish();
                                        }
                                    });
                                }
                            });
        }

    }


    @OnClick(R.id.delete_my_account_button)
    void deleteAccount() {
        ToastUtils.longToast(R.string.coming_soon);
    }

    private void initFields() {
        title = getString(R.string.settings_title);
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        facebookHelper = new FacebookHelper(this);
        firebaseAuthHelper = new FirebaseAuthHelper(SettingsActivity.this);
    }

    private void fillUI() {
        pushNotificationSwitch.setChecked(ConnectycubeSettings.getInstance().isEnablePushNotification());
        changePasswordView.setVisibility(
                LoginType.EMAIL.equals(AppSession.getSession().getLoginType()) ? View.VISIBLE : View.GONE);
        fullNameTextView.setText(user.getFullName());

        showUserAvatar();
    }

    private void showUserAvatar() {
        ImageLoader.getInstance().displayImage(
                user.getAvatar(),
                avatarImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void addActions() {
        addAction(QBServiceConsts.LOGOUT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.LOGOUT_FAIL_ACTION);

        updateBroadcastActionList();
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
                        Toast.makeText(SettingsActivity.this, "SUCCESS LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                        PreferenceUtil.setLoginStatus(SettingsActivity.this, false);
                        MainActivity.mainActivity.tvSaldo.setText("0.00");
                        finish();
                        //paymentFragment.lblSaldoMain.setText("0.00");
                    } else {
                        finish();
                        Toast.makeText(SettingsActivity.this, "FAILED LOGOUT PAY [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    finish();
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPassResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                progressDialog.dismiss();
                finish();
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                //Log.d("API_TRANSBELI", t.getMessage().toString());
            }
        });
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

}