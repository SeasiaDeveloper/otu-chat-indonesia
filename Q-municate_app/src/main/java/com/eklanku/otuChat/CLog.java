package com.eklanku.otuChat;

import android.util.Log;

public class CLog
{
	public static final String TAG = "bugfixDISCONNECT";

	public static void d(String logText){
		Log.d(TAG, logText);
	}

	public static void e(String logText){
		Log.e(TAG, logText);
	}

	public static void e(String logText, Exception e){
		Log.e(TAG, logText, e);
	}


}
