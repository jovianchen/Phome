package com.demo.javamail.receiver;

import com.demo.javamail.service.MainService;
import com.demo.javamail.util.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresentReceiver extends BroadcastReceiver {

	private final static String TAG = "UserPresentReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (MainService.isReadyOn) {			
			LogUtil.v(TAG,"user present");			
			MainService.handler
					.sendEmptyMessage(MainService.MSG_WHAT_USER_PRESENT);
		}
	}
}
