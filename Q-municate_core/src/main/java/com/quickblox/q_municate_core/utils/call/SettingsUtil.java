package com.quickblox.q_municate_core.utils.call;

import android.content.Context;

import com.quickblox.q_municate_core.R;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCMediaConfig;

import java.util.List;

public class SettingsUtil {

    private static final String TAG = SettingsUtil.class.getSimpleName();

    private static void setSettingsForMultiCall(List<ConnectycubeUser> users) {
        if (users.size() <= 2) {
            int width = RTCMediaConfig.getVideoWidth();
            if (width > RTCMediaConfig.VideoQuality.VGA_VIDEO.width) {
                RTCMediaConfig.setVideoWidth(RTCMediaConfig.VideoQuality.VGA_VIDEO.width);
                RTCMediaConfig.setVideoHeight(RTCMediaConfig.VideoQuality.VGA_VIDEO.height);
            }
        } else {
            //set to minimum settings
            RTCMediaConfig.setVideoWidth(RTCMediaConfig.VideoQuality.QVGA_VIDEO.width);
            RTCMediaConfig.setVideoHeight(RTCMediaConfig.VideoQuality.QVGA_VIDEO.height);
            RTCMediaConfig.setVideoHWAcceleration(false);
            RTCMediaConfig.setVideoCodec(null);
        }
    }

    public static void setSettingsStrategy(Context context, List<ConnectycubeUser> usersList) {
        if (usersList.size() == 1) {
            setSettingsFromPreferences(context);
        } else {
            setSettingsForMultiCall(usersList);
        }
    }

    private static void setSettingsFromPreferences(Context context) {
        CoreSharedHelper coreSharedHelper = CoreSharedHelper.getInstance();
        // Check HW codec flag.
        boolean hwCodec = coreSharedHelper.getCallHwCodec(
                context.getResources().getBoolean(R.bool.call_hw_codec_default));

        RTCMediaConfig.setVideoHWAcceleration(hwCodec);

        // Get video resolution from settings.
        int resolutionItem = coreSharedHelper.getCallResolution(
                context.getResources().getInteger(R.integer.call_resolution_default));

        ErrorUtils.logError(TAG, "resolutionItem = " + resolutionItem);

        setVideoQuality(resolutionItem);

        // Get start bitrate.
        String bitrateTypeDefault = context.getString(R.string.call_startbitrate_default);
        String bitrateType = coreSharedHelper.getCallStartbitrate(bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            int bitrateValue = coreSharedHelper.getCallStartbitrateValue(
                    context.getResources().getInteger(R.integer.call_startbitrate_value_default));
            RTCMediaConfig.setVideoStartBitrate(bitrateValue);
        }

        int videoCodecItem = coreSharedHelper.getCallVideoCodec(
                context.getResources().getInteger(R.integer.call_video_codec_default));
        for (RTCMediaConfig.VideoCodec codec : RTCMediaConfig.VideoCodec.values()) {
            if (codec.ordinal() == videoCodecItem) {
                ErrorUtils.logError(TAG, "videoCodecItem = " + codec.getDescription());
                RTCMediaConfig.setVideoCodec(codec);
                break;
            }
        }

        String audioCodecDescription = coreSharedHelper.getCallAudioCodec(
                context.getString(R.string.call_audio_codec_default));
        RTCMediaConfig.AudioCodec audioCodec = RTCMediaConfig.AudioCodec.ISAC.getDescription()
                .equals(audioCodecDescription) ? RTCMediaConfig.AudioCodec.ISAC : RTCMediaConfig.AudioCodec.OPUS;
        ErrorUtils.logError(TAG, "audioCodec = " + audioCodec.getDescription());
        RTCMediaConfig.setAudioCodec(audioCodec);
    }

    private static void setVideoQuality(int resolutionItem) {
        if (resolutionItem != -1) {
            setVideoFromLibraryPreferences(resolutionItem);
        }
    }

    private static void setVideoFromLibraryPreferences(int resolutionItem) {
        for (RTCMediaConfig.VideoQuality quality : RTCMediaConfig.VideoQuality.values()) {
            if (quality.ordinal() == resolutionItem) {
                ErrorUtils.logError(TAG, "resolution = " + quality.height + ":" + quality.width);
                RTCMediaConfig.setVideoHeight(quality.height);
                RTCMediaConfig.setVideoWidth(quality.width);
            }
        }
    }
}