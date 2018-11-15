package com.eklanku.otuChat.ui.activities.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.eklanku.otuChat.Application;

import java.util.HashMap;

public class PreferenceManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "qmunicatepreference";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USERID = "userid";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_IS_MEMBER = "is_member";

    public final String KEY_REFF_LAUNCH = "firstLaunch";
    public final String KEY_REFF_DATE = "referrerDate";
    public final String KEY_REFF_RAW = "referrerDataRaw";
    public final String KEY_REFF_DECODE = "referrerDataDecoded";



    public PreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUser(String userid) {
        editor.putString(KEY_USERID, userid);
        editor.commit();
    }

    public void createUserPayment(String userid, String accessToken, boolean isLogin) {
        editor.putString(KEY_USERID, userid);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }

    public void createReff(String launch, String date, String raw, String decode) {
        editor.putString(KEY_REFF_LAUNCH, launch);
        editor.putString(KEY_REFF_DATE, date);
        editor.putString(KEY_REFF_RAW, raw);
        editor.putString(KEY_REFF_DECODE, decode);
        editor.commit();
    }

    public void createTokenforResetPass(String accessToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));
        return user;
    }

    public HashMap<String, String> getUserDetailsPayment() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));
        user.put(KEY_ACCESS_TOKEN, pref.getString(KEY_ACCESS_TOKEN, null));

        user.put(KEY_REFF_LAUNCH, pref.getString(KEY_REFF_LAUNCH, null));
        user.put(KEY_REFF_DATE, pref.getString(KEY_REFF_DATE, null));
        user.put(KEY_REFF_RAW, pref.getString(KEY_REFF_RAW, null));
        user.put(KEY_REFF_DECODE, pref.getString(KEY_REFF_DECODE, null));
        return user;
    }

    public void updateReferrerData(Context context)
    {
        //boolean isReferrerDetected = Application.isReferrerDetected(context);
        String firstLaunch = Application.getFirstLaunch(context);
        String referrerDate = Application.getReferrerDate(context);
        String referrerDataRaw = Application.getReferrerDataRaw(context);
        String referrerDataDecoded = Application.getReferrerDataDecoded(context);

        createReff(firstLaunch, referrerDate, referrerDataRaw, referrerDataDecoded);
    }

}