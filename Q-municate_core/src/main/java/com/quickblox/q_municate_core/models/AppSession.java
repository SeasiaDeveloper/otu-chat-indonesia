package com.quickblox.q_municate_core.models;

import android.text.TextUtils;
import android.util.Log;

import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.connectycube.auth.session.ConnectycubeSessionParameters;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.connectycube.users.model.ConnectycubeUser;

import java.io.Serializable;
import java.util.Date;

public class AppSession implements Serializable {

    private static final Object lock = new Object();
    private static AppSession activeSession;

    private CoreSharedHelper coreSharedHelper;
    private LoginType loginType;
    private ConnectycubeUser connectycubeUser;

    private ChatState chatState = ChatState.FOREGROUND;

    private AppSession(ConnectycubeUser connectycubeUser) {
        coreSharedHelper = CoreSharedHelper.getInstance();
        this.connectycubeUser = connectycubeUser;
        this.loginType = getLoginTypeBySessionParameters(ConnectycubeSessionManager.getInstance().getSessionParameters());
        save();
    }

    public void updateState(ChatState state){
        chatState = state;
    }

    public ChatState getChatState() {
        return chatState;
    }

    public static void startSession(ConnectycubeUser user) {
        activeSession = new AppSession(user);
    }

    private static AppSession getActiveSession() {
        synchronized (lock) {
            return activeSession;
        }
    }

    public static AppSession load() {

        int userId = CoreSharedHelper.getInstance().getUserId();
        String userFullName = CoreSharedHelper.getInstance().getUserFullName();

        ConnectycubeUser connectycubeUser = new ConnectycubeUser();
        connectycubeUser.setId(userId);
        connectycubeUser.setEmail(CoreSharedHelper.getInstance().getUserEmail());
        connectycubeUser.setPassword(CoreSharedHelper.getInstance().getUserPassword());
        connectycubeUser.setFullName(userFullName);
        connectycubeUser.setFacebookId(CoreSharedHelper.getInstance().getFBId());
        connectycubeUser.setTwitterId(CoreSharedHelper.getInstance().getTwitterId());
        connectycubeUser.setTwitterDigitsId(CoreSharedHelper.getInstance().getTwitterDigitsId());
        connectycubeUser.setCustomData(CoreSharedHelper.getInstance().getUserCustomData());

        activeSession = new AppSession(connectycubeUser);

        return activeSession;
    }

    public static boolean isSessionExistOrNotExpired(long expirationTime) {
            ConnectycubeSessionManager sessionManager = ConnectycubeSessionManager.getInstance();
            String token = sessionManager.getToken();
            if (token == null) {
                Log.d("AppSession", "token == null");
                return false;
            }
            Date tokenExpirationDate = sessionManager.getTokenExpirationDate();
            long tokenLiveOffset = tokenExpirationDate.getTime() - System.currentTimeMillis();
            return tokenLiveOffset > expirationTime;
    }

    public static AppSession getSession() {
        AppSession activeSession = AppSession.getActiveSession();
        if (activeSession == null) {
            activeSession = AppSession.load();
        }
        return activeSession;
    }

    public void closeAndClear() {
        coreSharedHelper.clearUserData();

        activeSession = null;
    }

    public ConnectycubeUser getUser() {
        return connectycubeUser;
    }

    public void save() {
        saveUser(connectycubeUser);
    }

    public void updateUser(ConnectycubeUser connectycubeUser) {
        this.connectycubeUser = connectycubeUser;
        saveUser(this.connectycubeUser);
    }

    private void saveUser(ConnectycubeUser user) {
        coreSharedHelper.saveUserId(user.getId());
        coreSharedHelper.saveUserEmail(user.getEmail());
        coreSharedHelper.saveUserPassword(user.getPassword());
        coreSharedHelper.saveUserFullName(user.getFullName());
        coreSharedHelper.saveFBId(user.getFacebookId());
        coreSharedHelper.saveTwitterId(user.getTwitterId());
        coreSharedHelper.saveTwitterDigitsId(user.getTwitterDigitsId());
        coreSharedHelper.saveUserCustomData(user.getCustomData());
    }

    public boolean isLoggedIn() {
        return ConnectycubeSessionManager.getInstance().getSessionParameters() != null;
    }

    public boolean isSessionExist() {
        return !TextUtils.isEmpty(ConnectycubeSessionManager.getInstance().getToken());
    }

    public LoginType getLoginType() {
        return loginType;
    }

    private LoginType getLoginTypeBySessionParameters(ConnectycubeSessionParameters sessionParameters){
        LoginType result = null;
        if(sessionParameters == null){
            return null;
        }
        String socialProvider = sessionParameters.getSocialProvider();
        if(socialProvider == null){
            result = LoginType.EMAIL;
        } else if (socialProvider.equals(ConnectycubeProvider.FACEBOOK)){
            result = LoginType.FACEBOOK;
        } else if (socialProvider.equals(ConnectycubeProvider.FIREBASE_PHONE)){
            result = LoginType.FIREBASE_PHONE;
        } else if (socialProvider.equals(ConnectycubeProvider.TWITTER_DIGITS)){ //for correct migration from TWITTER_DIGITS to FIREBASE_PHONE
            result = LoginType.FIREBASE_PHONE;
        }

        if (result != null) {
            loginType = result;
        }

        return result;
    }

    public enum ChatState {
        BACKGROUND, FOREGROUND
    }

}