package com.eklanku.otuChat.fcm;

import android.os.Bundle;
import android.util.Log;

import com.connectycube.pushnotifications.services.fcm.FcmPushListenerService;
import com.eklanku.otuChat.utils.helpers.notification.ChatNotificationHelper;

import java.util.Map;

import static com.quickblox.q_municate_core.utils.ConstsCore.*;


public class FirebasePushListenerService extends FcmPushListenerService {
    private String TAG = FirebasePushListenerService.class.getSimpleName();

    private ChatNotificationHelper chatNotificationHelper;

    public FirebasePushListenerService() {
        this.chatNotificationHelper = new ChatNotificationHelper(this);
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);

        String userId = (String) data.get(MESSAGE_USER_ID);
        String pushMessage = (String) data.get(MESSAGE);
        String dialogId = (String) data.get(MESSAGE_DIALOG_ID);
        String pushMessageType = (String) data.get(MESSAGE_TYPE);
        String pushVOIPType = (String) data.get(MESSAGE_VOIP_TYPE);

        Log.v(TAG, "sendPushMessage\n" + "Message: " + pushMessage + "\nUser ID: " + userId + "\nDialog ID: " + dialogId);

        Bundle extras = new Bundle();
        extras.putString(MESSAGE_USER_ID, userId);
        extras.putString(MESSAGE, pushMessage);
        extras.putString(MESSAGE_DIALOG_ID, dialogId);
        extras.putString(MESSAGE_TYPE, pushMessageType);
        extras.putString(MESSAGE_VOIP_TYPE, pushVOIPType);

        chatNotificationHelper.parseChatMessage(extras);
    }
}
