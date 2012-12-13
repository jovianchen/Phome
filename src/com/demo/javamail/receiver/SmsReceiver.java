package com.demo.javamail.receiver;

import com.demo.javamail.R;
import com.demo.javamail.service.MainService;
import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.TimeUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private final static String TAG = "SmsReceiver";

	public static String msgPhoneNum;
	public static String msgBody;
	public static String msgTime;

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle b = intent.getExtras();
		Object messages[] = (Object[]) b.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);

			// 设置过滤内容
			// if (smsMessage[n].getMessageBody().contains("hahaha")) {
			// this.abortBroadcast();
			// }

			msgPhoneNum = smsMessage[n].getOriginatingAddress();
			msgBody = smsMessage[n].getDisplayMessageBody();
			// msgBody = smsMessage[n].getMessageBody();
			LogUtil.v(TAG, "msgPhoneNum = " + msgPhoneNum);
			LogUtil.v(TAG, "msgBody = " + msgBody);			

			msgTime = TimeUtil.getCurrentTime();

			if (context.getString(R.string.key_message).equals(msgBody)) {
				// 启动密信
				LogUtil.e(TAG, "get key message");				
				
				// 告知MainService收到Key Message
				MainService.handler
						.sendEmptyMessage(MainService.MSG_WHAT_KEY_MSG);
			} else {
				// 普通短信				

				// 构造一个发送往MainService的消息，写入本条短信相关数据
				Message message = new Message();
				message.what = MainService.MSG_WHAT_EVENT;
				Bundle bundle = new Bundle();
				bundle.putInt(MainService.MSG_KEY_EVENT_TYPE,
						MainService.TYPE_MSG);
				bundle.putString(MainService.MSG_KEY_EVENT_NUM, msgPhoneNum);
				bundle.putString(MainService.MSG_KEY_EVENT_BODY, msgBody);
				bundle.putString(MainService.MSG_KEY_EVENT_TIME, msgTime);
				message.setData(bundle);
				MainService.handler.sendMessage(message);
			}

		}
	}
}
