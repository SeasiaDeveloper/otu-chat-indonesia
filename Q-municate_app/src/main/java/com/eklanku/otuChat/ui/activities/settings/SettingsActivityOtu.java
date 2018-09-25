package com.eklanku.otuChat.ui.activities.settings;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.profile.EditProfilActivity;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.auth.session.QBSettings;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.profile.EditProfilActivity;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.helpers.FacebookHelper;
import com.eklanku.otuChat.utils.helpers.FirebaseAuthHelper;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.LoginType;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_user_service.model.QMUser;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingsActivityOtu extends BaseLoggableActivity {

    public static final int REQUEST_CODE_LOGOUT = 300;

    private QMUser user;
    private FacebookHelper facebookHelper;
    private FirebaseAuthHelper firebaseAuthHelper;

    @Bind(R.id.full_name_edittext)
    TextView fullNameTextView;

    @Bind(R.id.avatar_imageview)
    RoundedImageView avatarImageView;

    @Bind(R.id.nomorhp)
    TextView nomorhp;

    @Override
    protected int getContentResId() {
        return R.layout.activity_settings_profil;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
    }

    private void initFields() {
        title = getString(R.string.settings_title);
        user = UserFriendUtils.createLocalUser(AppSession.getSession().getUser());
        facebookHelper = new FacebookHelper(this);
        firebaseAuthHelper = new FirebaseAuthHelper(SettingsActivityOtu.this);
    }

    private void fillUI() {
//        pushNotificationSwitch.setChecked(QBSettings.getInstance().isEnablePushNotification());
//        changePasswordView.setVisibility(
//                LoginType.EMAIL.equals(AppSession.getSession().getLoginType()) ? View.VISIBLE : View.GONE);
        fullNameTextView.setText(user.getFullName());

        nomorhp.setText(PreferenceUtil.getNumberPhone(this));

        showUserAvatar();
    }

    private void showUserAvatar() {
        ImageLoader.getInstance().displayImage(
                user.getAvatar(),
                avatarImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public static void startForResult(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), SettingsActivityOtu.class);
        fragment.getActivity().startActivityForResult(intent, REQUEST_CODE_LOGOUT);
    }

    //call setting profile

//    @OnClick(R.id.settingsHead)
//    void editProfile() {
//        EditProfilActivity.start(this);
//    }

    @SuppressWarnings("unused")
    @OnClick(R.id.settingsHead)
    public void launchEditProfile() {

        EditProfilActivity.start(this);
//        RateHelper.significantEvent(this);
//        if (AppHelper.isAndroid5()) {
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
//                    new Pair<>(userAvatar, "userAvatar"),
//                    new Pair<>(userName, "userName"),
//                    new Pair<>(userStatus, "userStatus"),
//                    new Pair<>(settingsHead, "settingsHead")
//            );
//        Intent mIntent = new Intent(this, EditProfilActivity.class);
//        startActivity(mIntent);
//            startActivity(mIntent, options.toBundle());

//        } else {
//            AppHelper.LaunchActivity(this, EditProfileActivity.class);
//        }
    }
}
