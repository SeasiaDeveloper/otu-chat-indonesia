package com.eklanku.otuChat.ui.activities.payment.settingpayment;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Device {
	Context mContext;
	public Device(Context mContext){
	       this.mContext = mContext;
	}
	public TelephonyManager telephonyManager() {
		TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager;		
	}
}
