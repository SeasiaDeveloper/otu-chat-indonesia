package com.eklanku.otuChat.ui.fragments.call;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.call.CallActivity;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.quickblox.q_municate_core.models.StartConversationReason;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.call.RingtonePlayer;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCSessionDescription;
import com.connectycube.videochat.RTCTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IncomingCallFragment extends Fragment implements Serializable, View.OnClickListener {

    public static final String TAG = IncomingCallFragment.class.getSimpleName();

    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private TextView typeIncCall;
    private TextView callerName;
    private ImageView avatarImageView;
    private ImageButton rejectBtn;
    private ImageButton takeBtn;

    private ArrayList<Integer> opponents;
    private List<ConnectycubeUser> opponentsFromCall = new ArrayList<>();
    private RTCSessionDescription sessionDescription;
    private Vibrator vibrator;
    private RTCTypes.ConferenceType conferenceType;
    private View view;
    private long lastClickTime = 0l;
    private RingtonePlayer ringtonePlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        Log.d(TAG, "onCreate() from IncomeCallFragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            opponents = getArguments().getIntegerArrayList(QBServiceConsts.EXTRA_OPPONENTS);
            sessionDescription = (RTCSessionDescription) getArguments().getSerializable(QBServiceConsts.EXTRA_SESSION_DESCRIPTION);
            conferenceType = (RTCTypes.ConferenceType) getArguments().getSerializable(QBServiceConsts.EXTRA_CONFERENCE_TYPE);

            Log.d(TAG, conferenceType.toString() + "From onCreateView()");
        }

        if (savedInstanceState == null) {
            view = inflater.inflate(R.layout.fragment_income_call, container, false);

            initUI(view);
            setDisplayedTypeCall(conferenceType);
            initButtonsListener();
        }
        if(getActivity() != null && getActivity() instanceof  CallActivity)
        {
            ((CallActivity)getActivity()).hideActionBar();
        }

        ringtonePlayer = new RingtonePlayer(getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startCallNotification();
    }

    public void onStop() {
        stopCallNotification();
        super.onDestroy();
        Log.d(TAG, "onDestroy() from IncomeCallFragment");
    }

    private void initButtonsListener() {
        rejectBtn.setOnClickListener(this);
        takeBtn.setOnClickListener(this);
    }

    private void initUI(View view) {

        typeIncCall = (TextView) view.findViewById(R.id.type_inc_call);

        avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);

        callerName = (TextView) view.findViewById(R.id.calling_to_text_view);
        callerName.setLines(1);

        rejectBtn = (ImageButton) view.findViewById(R.id.rejectBtn);
        takeBtn = (ImageButton) view.findViewById(R.id.takeBtn);

        setOpponentAvatarAndName();
    }

    private void setOpponentAvatarAndName() {
        QMUser opponent = ((CallActivity) getActivity()).getOpponentAsUserFromDB(sessionDescription.getCallerID());
        ImageLoader.getInstance().displayImage(opponent.getAvatar(), avatarImageView, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
        callerName.setText(opponent.getFullName());
    }

    public void startCallNotification() {
        ringtonePlayer.play(false);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }
    }

    private void stopCallNotification() {
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void setDisplayedTypeCall(RTCTypes.ConferenceType conferenceType) {
        typeIncCall.setText(getString(RTCTypes.ConferenceType.CONFERENCE_TYPE_VIDEO.equals(conferenceType) ?
                R.string.call_incoming_video_call : R.string.call_incoming_audio_call));
        takeBtn.setImageResource(RTCTypes.ConferenceType.CONFERENCE_TYPE_VIDEO.equals(conferenceType) ?
                R.drawable.ic_video_white : R.drawable.ic_call);
    }

    @Override
    public void onClick(View v) {

        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return;
        }
        lastClickTime = SystemClock.uptimeMillis();

        switch (v.getId()) {
            case R.id.rejectBtn:
                reject();
                break;
            case R.id.takeBtn:
                accept();
                break;
            default:
                break;
        }
    }

    private void accept() {
        takeBtn.setClickable(false);
        stopCallNotification();

        ((CallActivity) getActivity())
                .checkPermissionsAndStartCall(StartConversationReason.INCOME_CALL_FOR_ACCEPTION);
        Log.d(TAG, "Call is started");
    }

    private void reject() {
        rejectBtn.setClickable(false);
        Log.d(TAG, "Call is rejected");

        stopCallNotification();

        ((CallActivity) getActivity()).rejectCurrentSession();
    }
}