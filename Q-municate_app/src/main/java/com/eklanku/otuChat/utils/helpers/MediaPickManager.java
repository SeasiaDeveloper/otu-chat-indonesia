package com.eklanku.otuChat.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.util.Log;

import com.eklanku.otuChat.tasks.GetFilepathFromUriTask;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.utils.MediaUtils;
import com.eklanku.otuChat.utils.listeners.OnMediaPickedListener;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.ui.kit.chatmessage.adapter.utils.LocationUtils;

public class MediaPickManager extends Fragment {
    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String TAG = MediaPickManager.class.getSimpleName();

    private OnMediaPickedListener listener;

    public static void start(FragmentActivity activity, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        start(activity.getSupportFragmentManager(), args);
    }

    private static void start(FragmentManager fm, Bundle args) {
        MediaPickManager fragment = (MediaPickManager) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new MediaPickManager();
            fm.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
            fragment.setArguments(args);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMediaPickedListener) {
            listener = (OnMediaPickedListener) context;
        }

        if (listener == null) {
            throw new IllegalStateException(
                    "Either activity or fragment should implement OnMediaPickedListener");
        }
        int requestCode = getArguments().getInt(ARG_REQUEST_CODE);
        switch (requestCode) {
            case MediaUtils.IMAGE_REQUEST_CODE:
                MediaUtils.startMediaPicker(this);
                setupActivityToBeNonLoggable(getActivity());
                break;
            case MediaUtils.CAMERA_PHOTO_REQUEST_CODE:
                MediaUtils.startCameraPhotoForResult(this);
                setupActivityToBeNonLoggable(getActivity());
                break;
            case MediaUtils.CAMERA_VIDEO_REQUEST_CODE:
                MediaUtils.startCameraVideoForResult(this);
                setupActivityToBeNonLoggable(getActivity());
                break;
            case MediaUtils.LOCATION_REQUEST_CODE:
                MediaUtils.startMapForResult(this);
                setupActivityToBeNonLoggable(getActivity());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isResultFromMediaPick(requestCode, resultCode, data)) {
            if (requestCode == MediaUtils.IMAGE_VIDEO_LOCATION_REQUEST_CODE) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    double latitude = bundle.getDouble(ConstsCore.EXTRA_LOCATION_LATITUDE);
                    double longitude = bundle.getDouble(ConstsCore.EXTRA_LOCATION_LONGITUDE);
                    String location = LocationUtils.generateLocationJson(new Pair<>(ConstsCore.LATITUDE_PARAM, latitude),
                            new Pair<>(ConstsCore.LONGITUDE_PARAM, longitude));
                    listener.onMediaPicked(requestCode, Attachment.Type.LOCATION, location);
                }
            } else {
                if ((requestCode == MediaUtils.CAMERA_PHOTO_REQUEST_CODE || requestCode == MediaUtils.CAMERA_VIDEO_REQUEST_CODE) && (data == null || data.getData() == null)) {
                    // Hacky way to get EXTRA_OUTPUT param to work.
                    // When setting EXTRA_OUTPUT param in the camera intent there is a chance that data will return as null
                    // So we just pass temporary camera file as a data, because RESULT_OK means that photo was written in the file.
                    data = new Intent();
                    data.setData(MediaUtils.getValidUri(MediaUtils.getLastUsedCameraFile(), this.getContext()));
                }

                new GetFilepathFromUriTask(getChildFragmentManager(), listener,
                        getArguments().getInt(ARG_REQUEST_CODE)).execute(data);
            }
        } else {
            if (listener != null) {
                listener.onMediaPickClosed(getArguments().getInt(ARG_REQUEST_CODE));
            }
        }
        stop(getFragmentManager());
    }

    private boolean isResultFromMediaPick(int requestCode, int resultCode, Intent data) {
        return resultCode == Activity.RESULT_OK && ((requestCode == MediaUtils.CAMERA_PHOTO_REQUEST_CODE || requestCode == MediaUtils.CAMERA_VIDEO_REQUEST_CODE) || (requestCode == MediaUtils.GALLERY_REQUEST_CODE && data != null)
                || (requestCode == MediaUtils.IMAGE_VIDEO_LOCATION_REQUEST_CODE && data != null));
    }

    public void stop(FragmentManager fm) {
        Log.d(TAG, "MediaPickManager stop");
        fm.beginTransaction().remove(this).commitAllowingStateLoss();
    }

    private void setupActivityToBeNonLoggable(Activity activity) {
        if (activity instanceof BaseLoggableActivity) {
            BaseLoggableActivity loggableActivity = (BaseLoggableActivity) activity;
            loggableActivity.canPerformLogout.set(false);
        }
    }
}
