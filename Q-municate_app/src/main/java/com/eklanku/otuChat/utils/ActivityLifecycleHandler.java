package com.eklanku.otuChat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

<<<<<<< HEAD
import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.activities.authorization.LandingActivity;
=======
import com.eklanku.otuChat.CLog;
>>>>>>> origin/feature/disconnectBug
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.connectycube.auth.model.ConnectycubeProvider;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.connectycube.core.helper.Lo;
import com.connectycube.chat.ConnectycubeChatService;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.utils.helpers.FirebaseAuthHelper;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLogoutAndDestroyChatCommand;

public class ActivityLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ActivityLifecycleHandler.class.getSimpleName();

    private int numberOfActivitiesInForeground;
    private boolean chatDestroyed = false;

    @SuppressLint("LongLogTag")
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("ActivityLifecycleHandler", "onActivityCreated " + activity.getClass().getSimpleName());
    }

    @SuppressLint("LongLogTag")
    public void onActivityStarted(final Activity activity) {
        Log.d("ActivityLifecycleHandler", "onActivityStarted " + activity.getClass().getSimpleName());
        CLog.d("ActivityLifecycleHandler onActivityStarted " + activity.getClass().getSimpleName());
        boolean activityLogeable = isActivityLogeable(activity);
        chatDestroyed = chatDestroyed && !isLoggedIn();
        Log.d(TAG, "onActivityStarted , chatDestroyed=" + chatDestroyed + ", numberOfActivitiesInForeground= " + numberOfActivitiesInForeground);
        if (numberOfActivitiesInForeground == 0 && activityLogeable) {
            CLog.d("ActivityLifecycleHandler numberOfActivitiesInForeground == 0 && activityLogeable");
            AppSession.getSession().updateState(AppSession.ChatState.FOREGROUND);
            if (chatDestroyed) {
                CLog.d("ActivityLifecycleHandler chatDestroyed");
                boolean isLoggedIn = AppSession.getSession().isLoggedIn();
                Log.d(TAG, "isSessionExist()" + isLoggedIn);
                boolean canLogin = chatDestroyed && isLoggedIn;
                boolean networkAvailable = ((BaseActivity) activity).isNetworkAvailable();
                Log.d(TAG, "networkAvailable" + networkAvailable);
                if (canLogin && !QBLoginChatCompositeCommand.isRunning()) {
                    CLog.d("ActivityLifecycleHandler canLogin && !QBLoginChatCompositeCommand.isRunning()");
                    if (ConnectycubeProvider.FIREBASE_PHONE.equals(ConnectycubeSessionManager.getInstance().getSessionParameters().getSocialProvider())
                            && !ConnectycubeSessionManager.getInstance().isValidActiveSession()){
                        CLog.d("ActivityLifecycleHandler start refreshInternalFirebaseToken");
                        Log.d(TAG, "start refresh Firebase token");
                        new FirebaseAuthHelper(activity).refreshInternalFirebaseToken(new FirebaseAuthHelper.RequestFirebaseIdTokenCallback() {
                            @Override
                            public void onSuccess(String accessToken) {
                                CLog.d("ActivityLifecycleHandler refreshInternalFirebaseToken onSuccess");
                                QBLoginChatCompositeCommand.start(activity);
                            }

                            @Override
                            public void onError(Exception e) {
                                String message = "empty error message";
                                if(e!=null){
                                    if(!TextUtils.isEmpty(e.getMessage())){
                                        message = e.getMessage();
                                    }
                                }
                                CLog.d("ActivityLifecycleHandler refreshInternalFirebaseToken onError " + message);
                            }
                        });
                    } else {
                        CLog.d("ActivityLifecycleHandler else refreshInternalFirebaseToken start QBLoginChatCompositeCommand");
                        QBLoginChatCompositeCommand.start(activity);
                    }
                }
            }
        }

        if (activityLogeable) {
            Log.d("ActivityLifecycle", "++numberOfActivitiesInForeground");
            ++numberOfActivitiesInForeground;
        }

    }

    @SuppressLint("LongLogTag")
    public void onActivityResumed(Activity activity) {
        Log.d("ActivityLifecycleHandler", "onActivityResumed " + activity.getClass().getSimpleName() + " count of activities = " + numberOfActivitiesInForeground);
        CLog.d("ActivityLifecycleHandler onActivityResumed " + activity.getClass().getSimpleName());
    }

    public boolean isActivityLogeable(Activity activity) {
        return (activity instanceof Loggable);
    }

    public void onActivityPaused(Activity activity) {
        CLog.d("ActivityLifecycleHandler onActivityPaused " + activity.getClass().getSimpleName());
    }

    public void onActivityStopped(Activity activity) {
        CLog.d("ActivityLifecycleHandler onActivityStopped " + activity.getClass().getSimpleName());
        //Count only our app logeable activity
        if (activity instanceof Loggable) {
            Log.d("ActivityLifecycle", "--numberOfActivitiesInForeground");
            --numberOfActivitiesInForeground;
        }
        Lo.g("onActivityStopped" + numberOfActivitiesInForeground);

        if (numberOfActivitiesInForeground == 0 && activity instanceof Loggable) {
            AppSession.getSession().updateState(AppSession.ChatState.BACKGROUND);
            boolean isLoggedIn = isLoggedIn();
            Log.d(TAG, "isLoggedIn= " + isLoggedIn);
            if (!isLoggedIn) {
                chatDestroyed = true;
                return;
            }
            chatDestroyed = ((Loggable) activity).isCanPerformLogoutInOnStop();
            Log.d(TAG, "onDestroy chatDestroyed= " + chatDestroyed);
            if (chatDestroyed) {
                QBLogoutAndDestroyChatCommand.start(activity, true);
            }
        }
    }

    private boolean isLoggedIn() {
        return ConnectycubeChatService.getInstance().isLoggedIn();
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
}