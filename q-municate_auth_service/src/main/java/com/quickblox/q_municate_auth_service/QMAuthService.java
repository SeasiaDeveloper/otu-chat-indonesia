package com.quickblox.q_municate_auth_service;


import com.connectycube.auth.ConnectycubeAuth;
import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeUserSessionDetails;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.server.Performer;
import com.connectycube.extensions.RxJavaPerformProcessor;
import com.quickblox.q_municate_base_service.QMBaseService;
import com.connectycube.users.ConnectycubeUsers;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;

public class QMAuthService extends QMBaseService {

    private static final String TAG = QMAuthService.class.getSimpleName();

    private static QMAuthService instance;

    private boolean authorized;

    private QMAuthServiceListener listener;

    public static QMAuthService getInstance(){
        return instance;
    }

    public static void init(){
        instance = new QMAuthService();
    }

    @Override
    protected void serviceWillStart() {
    }

    public Observable<ConnectycubeUser> login(final ConnectycubeUser user) {
        Observable<ConnectycubeUser> result = null;
        Performer<ConnectycubeUser> performer = ConnectycubeUsers.signIn(user);
        final Observable<ConnectycubeUser> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        result = observable.map(new Func1<ConnectycubeUser, ConnectycubeUser>() {
            @Override
            public ConnectycubeUser call(ConnectycubeUser connectycubeUser) {
                authorized = true;
                notifyLogin(connectycubeUser);
                return connectycubeUser;
            }
        });
        return result;
    }

    public ConnectycubeUser loginSync(final ConnectycubeUser user) throws ResponseException {
        ConnectycubeUser result =  ConnectycubeUsers.signIn(user).perform();
        authorized = true;
        notifyLogin(result);
        return result;
    }


    public Observable<ConnectycubeUser> login(final String socialProvider, final String accessToken, final String accessTokenSecret){
        Observable<ConnectycubeUser> result = null;
        Performer<ConnectycubeUser> performer = null;
        if (socialProvider.equals(ConnectycubeProvider.TWITTER_DIGITS)){
            performer = ConnectycubeUsers.signInUsingTwitterDigits(accessToken, accessTokenSecret);
        } else if (socialProvider.equals(ConnectycubeProvider.FIREBASE_PHONE)){
            performer = ConnectycubeUsers.signInUsingFirebase(accessTokenSecret, accessToken);
        } else {
            performer = ConnectycubeUsers.signInUsingSocialProvider(socialProvider, accessToken, accessTokenSecret);
        }
        final Observable<ConnectycubeUser> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        result = observable.map(new Func1<ConnectycubeUser, ConnectycubeUser>() {
            @Override
            public ConnectycubeUser call(ConnectycubeUser connectycubeUser) {
                authorized = true;
                notifyLogin(connectycubeUser);
                return connectycubeUser;
            }
        });
        return result;
    }

    public ConnectycubeUser loginSync(final String socialProvider, final String accessToken, final String accessTokenSecret) throws ResponseException {
        Performer<ConnectycubeUser> performer = null;
        if (socialProvider.equals(ConnectycubeProvider.TWITTER_DIGITS)){
            performer = ConnectycubeUsers.signInUsingTwitterDigits(accessToken, accessTokenSecret);
        } else if (socialProvider.equals(ConnectycubeProvider.FIREBASE_PHONE)){
            performer = ConnectycubeUsers.signInUsingFirebase(accessTokenSecret, accessToken);
        } else {
            performer = ConnectycubeUsers.signInUsingSocialProvider(socialProvider, accessToken, accessTokenSecret);
        }
        ConnectycubeUser result  = performer.perform();
        authorized = true;
        notifyLogin(result);
        return result;
    }

    public Observable<ConnectycubeUser> signup(final ConnectycubeUser user){
        Performer<ConnectycubeUser> performer = ConnectycubeUsers.signUp(user);
        Observable<ConnectycubeUser> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        return observable;
    }

    public Observable<ConnectycubeUser> signUpLogin(final ConnectycubeUser user){
        Observable<ConnectycubeUser> result = null;
        Performer<ConnectycubeUser> performer = ConnectycubeUsers.signUpSignInTask(user);
        final Observable<ConnectycubeUser> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        result = observable.map(new Func1<ConnectycubeUser, ConnectycubeUser>() {
            @Override
            public ConnectycubeUser call(ConnectycubeUser connectycubeUser) {
                authorized = true;
                notifyLogin(connectycubeUser);
                return connectycubeUser;
            }
        });
        return result;
    }

    public ConnectycubeUser signUpLoginSync(final ConnectycubeUser user) throws ResponseException {
        ConnectycubeUser result = ConnectycubeUsers.signUpSignInTask(user).perform();
        authorized = true;
        notifyLogin(result);
        return result;
    }

    public Observable<Void>  logout(){
        Observable<Void> result = null;
        Performer<Void> performer = ConnectycubeUsers.signOut();
        final Observable<Void> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        result = observable.flatMap(new Func1<Void, Observable<Void>>() {
            @Override
            public Observable<Void> call(Void qbVoid) {
                authorized = false;
                notifyLogout(QMAuthService.this);
                return observable;
            }
        });
        return result;
    }

    public void logoutSync() throws ResponseException {
        ConnectycubeUsers.signOut().perform();
        authorized = false;
        notifyLogout(this);
    }

    public Observable<Void> upgradeWebSessionToken(String webToken) {
        Performer<Void> performer = ConnectycubeAuth.upgradeWebSessionTokenToCurrentUser(webToken);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<ArrayList<ConnectycubeUserSessionDetails>> getUserSessions() {
        Performer<ArrayList<ConnectycubeUserSessionDetails>> performer = ConnectycubeAuth.getUserSessions();
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<Void> deleteUserSessionsExceptCurrent() {
        Performer<Void> performer = ConnectycubeAuth.deleteUserSessionsExceptCurrent();
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<Void> deleteUserSessionById(int sessionId) {
        Performer<Void> performer = ConnectycubeAuth.deleteUserSessionById(sessionId);
        return performer.convertTo(RxJavaPerformProcessor.INSTANCE);
    }

    public Observable<Void> resetPassword(String email) {
        Performer<Void> performer = ConnectycubeUsers.resetPassword(email);
        final Observable<Void> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);
        return observable;
    }

    public void resetPasswordSync(String email) throws ResponseException {
        ConnectycubeUsers.resetPassword(email).perform();
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public QMAuthServiceListener getListener() {
        return listener;
    }

    public void setListener(QMAuthServiceListener listener) {
        this.listener = listener;
    }

    private void notifyLogin(ConnectycubeUser user){
        if(listener != null){
            listener.login(user);
        }
    }

    private void notifyLogout(QMAuthService authService){
        if(listener != null){
            listener.logout(authService);
        }
    }

    public interface QMAuthServiceListener{
        void login(ConnectycubeUser user);
        void logout(QMAuthService authService);

    }

}
