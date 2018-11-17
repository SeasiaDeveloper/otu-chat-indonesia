package com.quickblox.q_municate_core.qb.helpers;

import android.content.Context;
import android.util.Log;

import com.connectycube.auth.ConnectycubeAuth;
import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.connections.tcp.TcpChatConnectionFabric;
import com.connectycube.chat.connections.tcp.TcpConfigurationBuilder;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.connectycube.users.model.ConnectycubeUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class QBChatRestHelper extends BaseHelper {

    private static final String TAG = QBChatRestHelper.class.getSimpleName();
    private static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

    private ConnectycubeChatService chatService;
    private ConnectionListener connectionListener = new ChatConnectionListener();

    public QBChatRestHelper(Context context) {
        super(context);
    }

    public synchronized void initChatService() throws XMPPException, SmackException {
        ConnectycubeChatService.setDefaultPacketReplyTimeout(ConstsCore.DEFAULT_PACKET_REPLY_TIMEOUT);

        TcpConfigurationBuilder configurationBuilder = new TcpConfigurationBuilder()
                .setSocketTimeout(0);

        ConnectycubeChatService.setConnectionFabric(new TcpChatConnectionFabric(configurationBuilder));

        chatService = ConnectycubeChatService.getInstance();

        chatService.removeConnectionListener(connectionListener);
        chatService.addConnectionListener(connectionListener);
    }

    public synchronized void login(ConnectycubeUser user) throws XMPPException, IOException, SmackException {
        if (!chatService.isLoggedIn() && user != null) {
            if (ConnectycubeProvider.FIREBASE_PHONE.equals(ConnectycubeSessionManager.getInstance().getSessionParameters().getSocialProvider())
                    && !ConnectycubeSessionManager.getInstance().isValidActiveSession()){
                CoreSharedHelper coreSharedHelper = new CoreSharedHelper(context);
                String currentFirebaseAccessToken = coreSharedHelper.getFirebaseToken();
                if (!ConnectycubeSessionManager.getInstance().getSessionParameters().getAccessToken().equals(currentFirebaseAccessToken)) {
                    ConnectycubeAuth.createSessionUsingFirebase(coreSharedHelper.getFirebaseProjectId(), currentFirebaseAccessToken).perform();
                    user.setPassword(ConnectycubeSessionManager.getInstance().getToken());
                    AppSession.getSession().updateUser(user);
                }
            }
            chatService.login(user);
            chatService.enableCarbons();
        }
    }
    
    public synchronized void logout() throws ResponseException, SmackException.NotConnectedException {
        if (chatService != null) {
            chatService.logout();
        }
    }

    public void destroy() {
        chatService.destroy();
    }

    public boolean isLoggedIn() {
        return chatService != null && chatService.isLoggedIn();
    }

    private void tryReloginToChatUsingNewToken(){
        if (!chatService.isLoggedIn()
                && chatService.getUser() != null
                && ConnectycubeSessionManager.getInstance().getSessionParameters() != null
                && ConnectycubeSessionManager.getInstance().getSessionParameters().getSocialProvider() != null){

            chatService.login(AppSession.getSession().getUser(), (EntityCallback) null);
        }
    }

    private class ChatConnectionListener implements ConnectionListener {

        @Override
        public void connected(XMPPConnection connection) {
            Log.e(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {
            Log.e(TAG, "authenticated");
        }

        @Override
        public void connectionClosed() {
            Log.e(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.e(TAG, "connectionClosedOnError, error: " + e.getMessage());
            //TODO VT temp solution before test in SDK
            //need renew user password in ConnectycubeChatService for user which was logged in
            //via social provider
            tryReloginToChatUsingNewToken();
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.e(TAG, "reconnectingIn(" + seconds + ")");
        }

        @Override
        public void reconnectionSuccessful() {
            Log.e(TAG, "reconnectionSuccessful()");
        }

        @Override
        public void reconnectionFailed(Exception error) {
            Log.e(TAG, "reconnectionFailed() " + error.getMessage());
            //TODO VT temp solution before test in SDK
            //need renew user password in ConnectycubeChatService for user which was logged in
            //via social provider
            tryReloginToChatUsingNewToken();
        }
    }
}