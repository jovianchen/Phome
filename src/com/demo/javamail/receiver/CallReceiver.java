package com.demo.javamail.receiver;

import com.demo.javamail.service.MainService;
import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.TimeUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {

	private final static String TAG = "CallReceiver";

	public static boolean incomingFlag;
	public static String incoming_number;
	public static String incoming_time;

	@Override
	public void onReceive(Context context, Intent intent) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Service.TELEPHONY_SERVICE);

		switch (tm.getCallState()) {
		// 响铃
		case TelephonyManager.CALL_STATE_RINGING:
			// 标识当前动作为来电
			incomingFlag = true;

			incoming_number = intent.getStringExtra("incoming_number");
			LogUtil.v(TAG, "RINGING :" + incoming_number);
			incoming_time = TimeUtil.getCurrentTime();

			// 构造一个发送往MainService的消息，写入本次来电相关数据
			Message message = new Message();
			message.what = MainService.MSG_WHAT_EVENT;
			Bundle bundle = new Bundle();
			bundle.putInt(MainService.MSG_KEY_EVENT_TYPE, MainService.TYPE_CALL);
			bundle.putString(MainService.MSG_KEY_EVENT_NUM, incoming_number);
			bundle.putString(MainService.MSG_KEY_EVENT_TIME, incoming_time);
			message.setData(bundle);
			MainService.handler.sendMessage(message);

			break;
		// 接听
		case TelephonyManager.CALL_STATE_OFFHOOK:
			if (incomingFlag) {
				LogUtil.v(TAG, "incoming ACCEPT :" + incoming_number);
				// 发送一个中止指令
				if (MainService.isReadyOn) {
					MainService.handler
							.sendEmptyMessage(MainService.MSG_WHAT_USER_PRESENT);
				}
			}
			break;
		// 挂掉
		case TelephonyManager.CALL_STATE_IDLE:
			if (incomingFlag) {
				LogUtil.v(TAG, "incoming IDLE");

				// 来电者挂电话是否触发该处？
				// 发送一个中止指令
				// if (MainService.isReadyOn) {
				// MainService.handler
				// .sendEmptyMessage(MainService.MSG_WHAT_USER_PRESENT);
				// }
			}
			break;
		}
	}
}
