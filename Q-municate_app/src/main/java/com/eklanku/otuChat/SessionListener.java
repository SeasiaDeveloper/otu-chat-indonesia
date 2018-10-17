package com.eklanku.otuChat;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.connectycube.auth.session.ConnectycubeSessionParameters;
import com.connectycube.auth.session.SessionListenerImpl;
import com.eklanku.otuChat.service.SessionJobService;
import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.eklanku.otuChat.service.SessionJobService;
import com.eklanku.otuChat.ui.activities.authorization.LandingActivity;
import com.eklanku.otuChat.utils.helpers.FirebaseAuthHelper;
import com.quickblox.q_municate_core.models.AppSession;
import com.connectycube.users.model.ConnectycubeUser;

public class SessionListener {

    //Last seen

    private static final String TAG = SessionListener.class.getSimpleName();

    private final QBSessionListener listener;

    public SessionListener(){
        listener = new QBSessionListener();
        ConnectycubeSessionManager.getInstance().addListener(listener);
    }

    private static class QBSessionListener extends SessionListenerImpl {

        @Override
        public void onSessionUpdated(ConnectycubeSessionParameters sessionParameters) {
            Log.d(TAG, "onSessionUpdated pswd:" + sessionParameters.getUserPassword()
                    + ", iserId : " + sessionParameters.getUserId());
            ConnectycubeUser connectycubeUser = AppSession.getSession().getUser();
            if (sessionParameters.getSocialProvider() != null) {
                connectycubeUser.setPassword(ConnectycubeSessionManager.getInstance().getToken());
            } else {
                connectycubeUser.setPassword(sessionParameters.getUserPassword());
            }
            AppSession.getSession().updateUser(connectycubeUser);
        }

        @Override
        public void onProviderSessionExpired(String provider) {
            Log.d(TAG, "onProviderSessionExpired :" +provider );

            if (ConnectycubeProvider.FIREBASE_PHONE.equals(provider)
                    || ConnectycubeProvider.TWITTER_DIGITS.equals(provider)) { //for correct migration from TWITTER_DIGITS to FIREBASE_PHONE

                new FirebaseAuthHelper(App.getInstance()).refreshInternalFirebaseToken(new FirebaseAuthHelper.RequestFirebaseIdTokenCallback() {
                    @Override
                    public void onSuccess(String authToken) {
                        Log.d(TAG, "onSuccess authToken: " + authToken);
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAuthHelper.EXTRA_FIREBASE_ACCESS_TOKEN, authToken);
                        SessionJobService.startSignInSocial(App.getInstance(), bundle);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError error: " + e.getMessage());
                        Intent intent = new Intent(App.getInstance(), LandingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        LandingActivity.start(App.getInstance(), intent);
                    }
                });
            }
        }
    }
}
