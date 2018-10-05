package com.quickblox.q_municate_core.utils.helpers;

import android.text.TextUtils;

import com.connectycube.core.helper.StringifyArrayList;
import com.connectycube.pushnotifications.model.ConnectycubeEnvironment;
import com.connectycube.pushnotifications.model.ConnectycubeEvent;
import com.connectycube.pushnotifications.model.ConnectycubeNotificationType;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONObject;

import java.util.List;

import static com.quickblox.q_municate_core.utils.ConstsCore.*;

public class CoreNotificationHelper {

    public static ConnectycubeEvent createPushEvent(List<Integer> userIdsList, String message, String messageType) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        userIds.addAll(userIdsList);
        ConnectycubeEvent event = new ConnectycubeEvent();
        event.setUserIds(userIds);
        event.setEnvironment(ConnectycubeEnvironment.PRODUCTION);
        event.setNotificationType(ConnectycubeNotificationType.PUSH);
        setMessage(event, message, messageType);
        return event;
    }

    private static void setMessage(ConnectycubeEvent event, String message, String messageType) {
        String eventMessage = message;
        if (isMessageWithParam(messageType)) {
            eventMessage = messageWithParams(message, messageType);
        }
        event.setMessage(eventMessage);
    }

    private static boolean isMessageWithParam(String messageType) {
        return !TextUtils.isEmpty(messageType);
    }

    private static String messageWithParams(String message, String messageType) {
        JSONObject json = new JSONObject();
        try {
            json.put(MESSAGE, message);
            // custom parameters
            json.put(MESSAGE_TYPE, messageType);
            if (isCallType(messageType)) {
                json.put(MESSAGE_IOS_VOIP, PUSH_MESSAGE_TYPE_VOIP);
                json.put(MESSAGE_VOIP_TYPE, PUSH_MESSAGE_TYPE_VOIP);
            }

        } catch (Exception e) {
            ErrorUtils.logError(e);
        }
        return json.toString();
    }

    private static boolean isCallType(String messageType) {
        return TextUtils.equals(messageType, PUSH_MESSAGE_TYPE_CALL);
    }
}