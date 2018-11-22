package com.eklanku.otuChat.utils.helpers;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.chats.BaseDialogActivity;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.chats.BaseDialogActivity;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

public class ActivityUIHelper {

    private BaseActivity baseActivity;

    public ActivityUIHelper(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void showChatMessageNotification(Bundle extras) {
        QMUser senderUser = (QMUser) extras.getSerializable(QBServiceConsts.EXTRA_USER);
        String message = extras.getString(QBServiceConsts.EXTRA_CHAT_MESSAGE);
        String dialogId = extras.getString(QBServiceConsts.EXTRA_DIALOG_ID);
        if (isChatDialogExist(dialogId) && senderUser != null) {
            message = baseActivity.getString(R.string.snackbar_new_message_title, senderUser.getFullName(), message);
            if (!TextUtils.isEmpty(message)) {
                showNewNotification(getChatDialogForNotification(dialogId), senderUser, message);
            }
        }
    }

    private boolean isChatDialogExist(String dialogId) {
        return DataManager.getInstance().getConnectycubeChatDialogDataManager().exists(dialogId);
    }

    private ConnectycubeChatDialog getChatDialogForNotification(String dialogId) {
        return DataManager.getInstance().getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
    }

    private void showNewNotification(final ConnectycubeChatDialog chatDialog, final QMUser senderUser, String message) {
        baseActivity.hideSnackBar();
        baseActivity.showSnackbar(message, Snackbar.LENGTH_LONG, R.string.dialog_reply,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showDialog(chatDialog, senderUser);
                    }
                });
    }

    private void showDialog(ConnectycubeChatDialog chatDialog, QMUser senderUser) {
        if (baseActivity instanceof BaseDialogActivity) {
            baseActivity.finish();
        }

        Log.i(ActivityUIHelper.class.getSimpleName(), "Show dialog from " + baseActivity.getClass().getSimpleName());

        if (ConnectycubeDialogType.PRIVATE.equals(chatDialog.getType())) {
            baseActivity.startPrivateChatActivity(senderUser, chatDialog);
        } else if(ConnectycubeDialogType.GROUP.equals(chatDialog.getType())) {
            baseActivity.startGroupChatActivity(chatDialog);
        } else {
            baseActivity.startBroadcastChatActivity(chatDialog);
        }
    }
}