package com.eklanku.otuChat.utils.helpers;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.eklanku.otuChat.R;;
import com.connectycube.users.model.ConnectycubeUser;

public class GoogleAnalyticsHelper {

    public static void pushAnalyticsData(Context context, ConnectycubeUser user, String action) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(context.getString(R.string.google_analytics_tracking_id));

        // You only need to set User ID on a tracker once. By setting it on the tracker, the ID will be
        // sent with all subsequent hits.
        tracker.set("&uid", String.valueOf(user.getId()));
        // This hit will be sent with the User ID value and be visible in User-ID-enabled views (profiles).
        tracker.send(new HitBuilders.EventBuilder().setCategory("UX").setAction(action).build());
    }
}