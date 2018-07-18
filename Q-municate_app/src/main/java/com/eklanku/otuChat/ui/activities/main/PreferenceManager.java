package com.eklanku.otuChat.ui.activities.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class PreferenceManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "qmunicatepreference";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USERID = "userid";
    public static final String KEY_ACCESS_TOKEN= "access_token";
    public static final String KEY_IS_MEMBER = "is_member";

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

    public void createTokenforResetPass(String accessToken){
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
        return user;
    }



}