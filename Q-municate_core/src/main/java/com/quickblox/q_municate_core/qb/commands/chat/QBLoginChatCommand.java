package com.quickblox.q_municate_core.qb.commands.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.connectycube.core.exception.ResponseException;
import com.quickblox.q_municate_core.core.command.ServiceCommand;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.helpers.QBChatRestHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.connectycube.users.model.ConnectycubeUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class QBLoginChatCommand extends ServiceCommand {

    private static final String TAG = QBLoginChatCommand.class.getSimpleName();

    private QBChatRestHelper chatRestHelper;

    public QBLoginChatCommand(Context context, QBChatRestHelper chatRestHelper, String successAction,
                              String failAction) {
        super(context, successAction, failAction);
        this.chatRestHelper = chatRestHelper;
    }

    public static void start(Context context) {
        Intent intent = new Intent(QBServiceConsts.LOGIN_CHAT_ACTION, null, context, QBService.class);
        context.startService(intent);
    }

    @Override
    public Bundle perform(Bundle extras) throws Exception {
        final ConnectycubeUser currentUser = AppSession.getSession().getUser();

        Log.i(TAG, "login with user login:" + currentUser.getLogin()
                + ", user id:" + currentUser.getId()
                + ", pswd=" + currentUser.getPassword() + ", fb id:" + currentUser.getFacebookId()
                + ", tw dg id:" + currentUser.getTwitterDigitsId());

        Log.i(TAG, "session token:" + ConnectycubeSessionManager.getInstance().getToken()
                + "\n, token exp date: " + ConnectycubeSessionManager.getInstance().getTokenExpirationDate()
                + "\n, is valid token:" + ConnectycubeSessionManager.getInstance().isValidActiveSession());


        // We don't make login if QB session was deleted by one of expiration cases :
        // for ex when social provider token is no more valid
        if (ConnectycubeSessionManager.getInstance().getSessionParameters() == null) {
            throw new ResponseException("invalid session");
        }

        try
        {
            login(currentUser);
        }catch (XMPPException | IOException | SmackException e){
            String message = "empty error message";
            if(!TextUtils.isEmpty(e.getMessage())){
                message = e.getMessage();
            }
        }

        return extras;
    }

    private void login(ConnectycubeUser currentUser) throws XMPPException, IOException, SmackException {
        chatRestHelper.login(currentUser);
    }

}