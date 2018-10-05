package com.quickblox.q_municate_core.qb.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.Signaling;
import com.connectycube.chat.WebRTCSignaling;
import com.connectycube.chat.listeners.VideoChatSignalingManagerListener;
import com.quickblox.q_municate_core.models.CallPushParams;
import com.quickblox.q_municate_core.models.StartConversationReason;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCClient;
import com.connectycube.videochat.RTCConfig;
import com.connectycube.videochat.RTCSession;
import com.connectycube.videochat.callbacks.RTCClientSessionCallbacks;
import com.connectycube.videochat.callbacks.RTCSessionConnectionCallbacks;
import com.connectycube.videochat.callbacks.RTCSignalingCallback;

import org.webrtc.CameraVideoCapturer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QBCallChatHelper extends BaseHelper {

    private static final String TAG = QBCallChatHelper.class.getSimpleName();

    private static final int MAX_OPPONENTS_COUNT = 1;
    private static final int DISCONNECT_TIME = 30;
    private static final int ANSWER_TIME_INTERVAL = 60;

    private RTCClient qbRtcClient;
    private Class<? extends Activity> activityClass;

    private RTCSession currentQbRtcSession;
    private RTCClientSessionCallbacks qbRtcClientSessionCallbacks;
    private CallPushParams callPushParams;

    public QBCallChatHelper(Context context) {
        super(context);
    }

    public void init(ConnectycubeChatService qbChatService) {
        Log.d(TAG, "init()");

        qbRtcClient = RTCClient.getInstance(context);

        qbChatService.getVideoChatWebRTCSignalingManager()
                .addSignalingManagerListener(new VideoChatSignalingManagerListenerImpl());

        qbRtcClient.addSessionCallbacksListener(new RTCClientSessionCallbacksImpl());

        setUpCallClient();
    }

    public void initActivityClass(Class<? extends Activity> activityClass) {
        Log.d(TAG, "initActivityClass()");
        this.activityClass = activityClass;
        Log.d("test_crash_1", "initActivityClass(), activityClass = " + activityClass);
    }

    public void setCallPushParams(CallPushParams callPushParams) {
        this.callPushParams = callPushParams;
    }

    public RTCSession getCurrentRtcSession() {
        return currentQbRtcSession;
    }

    public void initCurrentSession(RTCSession qbRtcSession, RTCSignalingCallback qbRtcSignalingCallback,
                                   RTCSessionConnectionCallbacks qbRtcSessionConnectionCallbacks) {
        this.currentQbRtcSession = qbRtcSession;
        initCurrentSession(qbRtcSignalingCallback, qbRtcSessionConnectionCallbacks);
    }

    public void initCurrentSession(RTCSignalingCallback qbRtcSignalingCallback,
                                   RTCSessionConnectionCallbacks qbRtcSessionConnectionCallbacks) {
        this.currentQbRtcSession.addSignalingCallback(qbRtcSignalingCallback);
        this.currentQbRtcSession.addSessionCallbacksListener(qbRtcSessionConnectionCallbacks);
    }

    public void releaseCurrentSession(RTCSignalingCallback qbRtcSignalingCallback,
                                      RTCSessionConnectionCallbacks qbRtcSessionConnectionCallbacks) {
        if (currentQbRtcSession != null) {
            currentQbRtcSession.removeSignalingCallback(qbRtcSignalingCallback);
            currentQbRtcSession.removeSessionCallbacksListener(qbRtcSessionConnectionCallbacks);
            currentQbRtcSession = null;
        }
    }

    private void setUpCallClient() {
        Log.d(TAG, "setUpCallClient()");

        RTCConfig.setMaxOpponentsCount(MAX_OPPONENTS_COUNT);
        RTCConfig.setDisconnectTime(DISCONNECT_TIME);
        RTCConfig.setAnswerTimeInterval(ANSWER_TIME_INTERVAL);
        RTCConfig.setDebugEnabled(true);

        qbRtcClient.prepareToProcessCalls();
    }

    private void startCallActivity(RTCSession qbRtcSession) {
        QMUser user = QMUserService.getInstance().getUserCache().get((long) qbRtcSession.getSessionDescription().getCallerID());

        if (user != null) {
            Log.d(TAG, "startCallActivity(), user = " + user);
            Log.d(TAG, "startCallActivity(), qbRtcSession.getConferenceType() = " + qbRtcSession
                    .getConferenceType());
            Log.d(TAG, "startCallActivity(), qbRtcSession.getSessionDescription() = " + qbRtcSession
                    .getSessionDescription());

            List<ConnectycubeUser> connectycubeUsersList = new ArrayList<>(1);
            connectycubeUsersList.add(UserFriendUtils.createConnectycubeUser(user));
            Intent intent = new Intent(context, activityClass);
            intent.putExtra(QBServiceConsts.EXTRA_OPPONENTS, (Serializable) connectycubeUsersList);
            intent.putExtra(QBServiceConsts.EXTRA_START_CONVERSATION_REASON_TYPE,
                    StartConversationReason.INCOME_CALL_FOR_ACCEPTION);
            intent.putExtra(QBServiceConsts.EXTRA_CONFERENCE_TYPE, qbRtcSession.getConferenceType());
            intent.putExtra(QBServiceConsts.EXTRA_SESSION_DESCRIPTION, qbRtcSession.getSessionDescription());
            intent.putExtra(QBServiceConsts.EXTRA_PUSH_CALL, callPushParams);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } else {
            throw new NullPointerException("user is null!");
        }
    }

    public void addRTCSessionUserCallback(RTCClientSessionCallbacks qbRtcClientSessionCallbacks) {
        this.qbRtcClientSessionCallbacks = qbRtcClientSessionCallbacks;
    }

    public void removeRTCSessionUserCallback() {
        this.qbRtcClientSessionCallbacks = null;
    }

    private class VideoChatSignalingManagerListenerImpl implements VideoChatSignalingManagerListener {

        private final String TAG = VideoChatSignalingManagerListenerImpl.class.getSimpleName();

        @Override
        public void signalingCreated(Signaling qbSignaling, boolean createdLocally) {
            if (!createdLocally) {
                qbRtcClient.addSignaling((WebRTCSignaling) qbSignaling);
            }
        }
    }

    private class RTCClientSessionCallbacksImpl implements RTCClientSessionCallbacks {

        private final String TAG = RTCClientSessionCallbacksImpl.class.getSimpleName();

        @Override
        public void onReceiveNewSession(RTCSession qbRtcSession) {
            Log.d(TAG, "onReceiveNewSession(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());
            if (currentQbRtcSession != null) {
                Log.d(TAG, "onReceiveNewSession(). Stop new session. Device now is busy");
                if (!qbRtcSession.equals(currentQbRtcSession)) {
                    qbRtcSession.rejectCall(null);
                }
            } else {
                Log.d(TAG, "onReceiveNewSession(). init session.");
                if (activityClass != null) {

                    startCallActivity(qbRtcSession);
                    currentQbRtcSession = qbRtcSession;
                }
            }
        }

        @Override
        public void onUserNotAnswer(RTCSession qbRtcSession, Integer integer) {
            Log.d(TAG, "onUserNotAnswer(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onUserNotAnswer(qbRtcSession, integer);
            }
        }

        @Override
        public void onCallRejectByUser(RTCSession qbRtcSession, Integer integer, Map<String, String> map) {
            Log.d(TAG, "onCallRejectByUser(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onCallRejectByUser(qbRtcSession, integer, map);
            }
        }

        @Override
        public void onCallAcceptByUser(RTCSession qbRtcSession, Integer integer, Map<String, String> map) {
            Log.d(TAG, "onCallAcceptByUser(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onCallAcceptByUser(qbRtcSession, integer, map);
            }
        }

        @Override
        public void onReceiveHangUpFromUser(RTCSession qbrtcSession, Integer integer, Map<String, String> map) {
            Log.d(TAG,
                    "onReceiveHangUpFromUser(), qbRtcSession.getSession() = " + qbrtcSession.getSessionID());

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onReceiveHangUpFromUser(qbrtcSession, integer, map);
            }
        }

        @Override
        public void onUserNoActions(RTCSession qbRtcSession, Integer integer) {
            Log.d(TAG, "onUserNoActions(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onUserNoActions(qbRtcSession, integer);
            }
        }

        @Override
        public void onSessionClosed(RTCSession qbRtcSession) {
            Log.d(TAG, "onSessionClosed(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onSessionClosed(qbRtcSession);
            }
        }

        @Override
        public void onSessionStartClose(RTCSession qbRtcSession) {
            Log.d(TAG, "onSessionStartClose(), qbRtcSession.getSession() = " + qbRtcSession.getSessionID());
            CoreSharedHelper.getInstance().saveLastOpenActivity(null);

            if (qbRtcClientSessionCallbacks != null) {
                qbRtcClientSessionCallbacks.onSessionStartClose(qbRtcSession);
            }
        }
    }

    class CameraErrorHandler implements CameraVideoCapturer.CameraEventsHandler {

        @Override
        public void onCameraError(String s) {
            Log.e(TAG, "Error on cams, error = " + s);
        }

        @Override
        public void onCameraDisconnected() {

        }

        @Override
        public void onCameraFreezed(String s) {
            Log.e(TAG, "Camera is frozen " + s);
        }

        @Override
        public void onCameraOpening(String i) {
            Log.e(TAG, "Camera opening = " + i);
        }

        @Override
        public void onFirstFrameAvailable() {
            Log.e(TAG, "Camera first frame available");
        }

        @Override
        public void onCameraClosed() {
            Log.e(TAG, "Camera closed");
        }
    }
}