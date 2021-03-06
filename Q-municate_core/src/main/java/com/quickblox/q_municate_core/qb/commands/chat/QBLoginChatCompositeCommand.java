package com.quickblox.q_municate_core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.q_municate_core.core.command.CompositeServiceCommand;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.network.NetworkGCMTaskService;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class QBLoginChatCompositeCommand extends CompositeServiceCommand {
    private static final String TAG = QBLoginChatCompositeCommand.class.getSimpleName();

    private static boolean isRunning;

    public QBLoginChatCompositeCommand(Context context, String successAction, String failAction) {
        super(context, successAction, failAction);
    }

    public static void start(Context context) {
        Log.i(TAG, "start");
        setIsRunning(true);
        Intent intent = new Intent(QBServiceConsts.LOGIN_CHAT_COMPOSITE_ACTION, null, context, QBService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    protected Bundle perform(Bundle extras) throws Exception {
        setIsRunning(false);
        if (AppSession.ChatState.FOREGROUND == AppSession.getSession().getChatState()) {
            try {
                super.perform(extras);
            } catch (XMPPException | IOException | SmackException e) {
                scheduleLogin();
                throw e;
            }
        }
        return extras;
    }

    private void scheduleLogin(){
        NetworkGCMTaskService.scheduleOneOff(context, "");
    }

    public static boolean isRunning(){
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        QBLoginChatCompositeCommand.isRunning = isRunning;
    }
}