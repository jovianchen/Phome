package com.demo.javamail.receiver;

import com.demo.javamail.service.MainService;
import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.PrefsUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	private final static String TAG = "BootReceiver";
	private final static String ACTION = "android.intent.action.BOOT_COMPLETED";

	private static SharedPreferences prefs;
	private static boolean isSwitchOn;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			LogUtil.v("JavaMail BootReceiver", "onReceive");

			prefs = context.getSharedPreferences(PrefsUtil.PREFS_NAME, 0);
			isSwitchOn = prefs.getBoolean(PrefsUtil.FLAG_MAIN_SWITCH, false);

			if (isSwitchOn) {
				context.startService(new Intent(context, MainService.class));
			}
		}
	}
}
