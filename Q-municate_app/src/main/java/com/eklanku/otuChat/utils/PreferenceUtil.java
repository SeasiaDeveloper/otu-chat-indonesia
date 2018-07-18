package com.eklanku.otuChat.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by agree on 14/11/17.
 */

public class PreferenceUtil {

    private static final String TAG = PreferenceUtil.class.getSimpleName();

    private static final String AUTH_NAME = "auth_name";

    private static final String PROMPTED_PUSH_REGISTRATION_PREF = "pref_prompted_push_registration";
    private static final String LOCAL_NUMBER_PREF = "pref_local_number";
    private static final String REFF_NUMBER_PREF = "reff_local_number";
    private static final String VERIFYING_STATE_PREF = "pref_verifying";
    private static final String REGISTERED_GCM_PREF = "pref_gcm_registered";
    private static final String PROFILE_NAME_PREF = "pref_profile_name";
    private static final String SYSTEM_EMOJI_PREF = "pref_system_emoji";
    private static final String LANGUAGE_PREF = "pref_language";
    private static final String MEMBER_KEY_PREF = "pref_member_key";
    private static final String PAYMENTID_KEY_PREF = "pref_paymentid_key";
    private static final String PAYMENTSTATUS_LOGIN_PREF = "pref_paymenstatus_key";
    private static final String PAYMENTEID_KEY_PREF = "pref_paymenteid_key";
    private static final String PAYMENTNAME_KEY_PREF = "pref_paymentname_key";
    private static final String PAYMENTEMAIL_KEY_PREF = "pref_paymentemail_key";

    public static @Nullable void setAuthName(Context context, String name) {
        setStringPreference(context, AUTH_NAME, name);
    }

    public static @Nullable
    String getAuthName(Context context) {
        return getStringPreference(context, AUTH_NAME, null);
    }

    public static boolean isVerifying(Context context) {
        return getBooleanPreference(context, VERIFYING_STATE_PREF, false);
    }

    public static void setVerifying(Context context, boolean verifying) {
        setBooleanPreference(context, VERIFYING_STATE_PREF, verifying);
    }

    public static boolean isPushRegistered(Context context) {
        return getBooleanPreference(context, REGISTERED_GCM_PREF, false);
    }

    public static void setPushRegistered(Context context, boolean registered) {
        Log.w(TAG, "Setting push registered: " + registered);
        setBooleanPreference(context, REGISTERED_GCM_PREF, registered);
    }

    public static boolean hasPromptedPushRegistration(Context context) {
        return getBooleanPreference(context, PROMPTED_PUSH_REGISTRATION_PREF, false);
    }

    public static void setPromptedPushRegistration(Context context, boolean value) {
        setBooleanPreference(context, PROMPTED_PUSH_REGISTRATION_PREF, value);
    }

    public static String getLocalNumber(Context context) {
        return getStringPreference(context, LOCAL_NUMBER_PREF, null);
    }

    public static void setLocalNumber(Context context, String localNumber) {
        setStringPreference(context, LOCAL_NUMBER_PREF, localNumber);
    }

    public static String getReffNumber(Context context) {
        return getStringPreference(context, REFF_NUMBER_PREF, null);
    }
    public static void setReffNumber(Context context, String reff) {
        setStringPreference(context, REFF_NUMBER_PREF, reff);
    }

    public static void setProfileName(Context context, String name) {
        setStringPreference(context, PROFILE_NAME_PREF, name);
    }

    public static String getProfileName(Context context) {
        return getStringPreference(context, PROFILE_NAME_PREF, null);
    }

    public static boolean isSystemEmojiPreferred(Context context) {
        return getBooleanPreference(context, SYSTEM_EMOJI_PREF, false);
    }

    public static String getLanguage(Context context) {
        return getStringPreference(context, LANGUAGE_PREF, "zz");
    }

    public static void setLanguage(Context context, String language) {
        setStringPreference(context, LANGUAGE_PREF, language);
    }

    public static String getMemberKey(Context context) {
        return getStringPreference(context, MEMBER_KEY_PREF, null);
    }

    public static void setMemberKey(Context context, String key) {
        setStringPreference(context, MEMBER_KEY_PREF, key);
    }

    public static String getPaymentId(Context context) {
        return getStringPreference(context, PAYMENTID_KEY_PREF, null);
    }

    public static void setPaymentId(Context context, String id) {
        setStringPreference(context, PAYMENTID_KEY_PREF, id);
    }
/*
    public static String getLoginStatus(Context context) {
        return getStringPreference(context, PAYMENTSTATUS_LOGIN_PREF, null);
    }

    public static void setLoginStatus(Context context, String id) {
        setStringPreference(context, PAYMENTSTATUS_LOGIN_PREF, id);
    }
*/
    public static boolean isLoginStatus(Context context) {
        return getBooleanPreference(context, PAYMENTSTATUS_LOGIN_PREF, false);
    }

    public static void setLoginStatus(Context context, boolean haslogin) {
        setBooleanPreference(context, PAYMENTSTATUS_LOGIN_PREF, haslogin);
    }

    public static boolean isMemberStatus(Context context) {
        return getBooleanPreference(context, MEMBER_KEY_PREF, false);
    }

    public static void setMemberStatus(Context context, boolean haslogin) {
        setBooleanPreference(context, MEMBER_KEY_PREF, haslogin);
    }

    public static String getPaymentEid(Context context) {
        return getStringPreference(context, PAYMENTEID_KEY_PREF, null);
    }

    public static void setPaymentEid(Context context, String eid) {
        setStringPreference(context, PAYMENTEID_KEY_PREF, eid);
    }

    public static String getPaymentName(Context context) {
        return getStringPreference(context, PAYMENTNAME_KEY_PREF, null);
    }

    public static void setPaymentName(Context context, String name) {
        setStringPreference(context, PAYMENTNAME_KEY_PREF, name);
    }

    public static String getPaymentEmail(Context context) {
        return getStringPreference(context, PAYMENTEMAIL_KEY_PREF, null);
    }

    public static void setPaymentEmail(Context context, String email) {
        setStringPreference(context, PAYMENTEMAIL_KEY_PREF, email);
    }


    public static void setBooleanPreference(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void setStringPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    private static int getIntegerPreference(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    private static void setIntegerPrefrence(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    private static boolean setIntegerPrefrenceBlocking(Context context, String key, int value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
    }

    private static long getLongPreference(Context context, String key, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }

    private static void setLongPreference(Context context, String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
    }
}
