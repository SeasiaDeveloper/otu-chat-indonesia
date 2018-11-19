package com.eklanku.otuChat.ui.activities.chats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;

import butterknife.Bind;

public class BroadcastDialogDetailsActivity extends BaseLoggableActivity {

    @Bind(R.id.name_textview)
    TextView broadcastTextView;

    @Bind(R.id.avatar_imageview)
    RoundedImageView photoImageView;

    private ConnectycubeChatDialog dialog;

    public static void start(Activity context, String dialogId) {
        Intent intent = new Intent(context, BroadcastDialogDetailsActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_broadcast_dialog_details;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();

        setUpActionBarWithUpButton();
        loadAvatar(dialog.getPhoto());
    }

    private void initFields() {
        String dialogId = (String) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG_ID);
        dialog = DataManager.getInstance().getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
        title = getString(R.string.dialog_broadcast_details_title);
        broadcastTextView.setText(dialog.getName());
    }

    private void loadAvatar(String photoUrl) {
        ImageLoader.getInstance().displayImage(photoUrl, photoImageView,
                ImageLoaderUtils.UIL_GROUP_AVATAR_DISPLAY_OPTIONS);
    }
}
