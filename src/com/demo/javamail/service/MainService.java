package com.demo.javamail.service;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.demo.javamail.R;
import com.demo.javamail.receiver.CallReceiver;
import com.demo.javamail.receiver.SmsReceiver;
import com.demo.javamail.receiver.UserPresentReceiver;
import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.TimeUtil;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;

public class MainService extends Service {

	private final static String TAG = "MainService";

	public final static int MSG_WHAT_EVENT = 99;
	public final static int MSG_WHAT_KEY_MSG = 198;
	public final static int MSG_WHAT_EVENT_READY = 199;
	public final static int MSG_WHAT_DIALOG_SHOW = 90;
	public final static int MSG_WHAT_USER_PRESENT = 100;
	public final static int TYPE_MSG = 300;
	public final static int TYPE_CALL = 301;

	// Auto start mail service after half an hour
	// public final static long TIME_READY = 30 * 60 * 1000;
	public final static long TIME_READY = 20000;

	private static AlertDialog mDialog_ServiceOn;

	public final static String JSON_KEY_EVENT_TYPE = "json_key_event_type";
	public final static String JSON_KEY_EVENT_NUM = "json_key_event_num";
	public final static String JSON_KEY_EVENT_BODY = "json_key_event_body";
	public final static String JSON_KEY_EVENT_TIME = "json_key_event_time";

	public final static String MSG_KEY_EVENT_TYPE = "msg_key_event_type";
	public final static String MSG_KEY_EVENT_NUM = "msg_key_event_num";
	public final static String MSG_KEY_EVENT_BODY = "msg_key_event_body";
	public final static String MSG_KEY_EVENT_TIME = "msg_key_event_time";

	private static int event_type;
	private static String event_num;
	private static String event_body;
	private static String event_time;

	public static ArrayList<JSONObject> mArray_event = null;
	public static JSONObject json_event = null;

	private static Context mContext;

	private IntentFilter filter_msg;
	private IntentFilter filter_call;
	private IntentFilter filter_userpresent;
	private SmsReceiver receiver_msg;
	private CallReceiver receiver_call;
	private UserPresentReceiver receiver_userpresent;

	// 是否已开启就绪计时
	public static boolean isReadyOn = false;
	// 转发功能是否已启动
	public static boolean isRunningOn = false;

	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MSG_WHAT_EVENT:
				LogUtil.v(TAG, "handle MSG_WHAT_EVENT");

				event_type = msg.getData().getInt(MSG_KEY_EVENT_TYPE, 0);
				event_num = msg.getData().getString(MSG_KEY_EVENT_NUM);
				event_body = msg.getData().getString(MSG_KEY_EVENT_BODY);
				event_time = msg.getData().getString(MSG_KEY_EVENT_TIME);

				if (isReadyOn) {
					// 创建一个ArrayList保存期间进入item
					LogUtil.v(TAG, "isReadyOn");

					json_event = new JSONObject();
					try {
						json_event.put(JSON_KEY_EVENT_TYPE, event_type);
						json_event.put(JSON_KEY_EVENT_NUM, event_num);
						json_event.put(JSON_KEY_EVENT_BODY, event_body);
						json_event.put(JSON_KEY_EVENT_TIME, event_time);
						mArray_event.add(json_event);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				if (!isRunningOn) {
					// 若转发未启动，则发送就绪消息，开启就绪flag，以保存就绪期间进入的item
					LogUtil.v(TAG, "!isRunningOn");

					if (!isReadyOn) {
						isReadyOn = true;

						mArray_event = new ArrayList<JSONObject>();
						json_event = new JSONObject();
						try {
							json_event.put(JSON_KEY_EVENT_TYPE, event_type);
							json_event.put(JSON_KEY_EVENT_NUM, event_num);
							json_event.put(JSON_KEY_EVENT_BODY, event_body);
							json_event.put(JSON_KEY_EVENT_TIME, event_time);
							mArray_event.add(json_event);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessageDelayed(MSG_WHAT_EVENT_READY,
								TIME_READY);
					}

				} else {
					// 若转发已启动，直接发送发邮消息
					LogUtil.v(TAG, "isRunningOn");

					mArray_event = new ArrayList<JSONObject>();
					json_event = new JSONObject();
					try {
						json_event.put(JSON_KEY_EVENT_TYPE, event_type);
						json_event.put(JSON_KEY_EVENT_NUM, event_num);
						json_event.put(JSON_KEY_EVENT_BODY, event_body);
						json_event.put(JSON_KEY_EVENT_TIME, event_time);
						mArray_event.add(json_event);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(MSG_WHAT_EVENT_READY);
				}

				break;

			case MSG_WHAT_KEY_MSG:
				LogUtil.v(TAG, "handle MSG_WHAT_KEY_MSG");
				// 启用发邮Service
				Intent intent_keymsg = new Intent(mContext,
						MailSenderService.class);
				intent_keymsg.putExtra("mailtype",
						MailSenderService.MAILTYPE_KEYMSG);
				intent_keymsg.putExtra("time", TimeUtil.getCurrentTime());
				mContext.startService(intent_keymsg);

				handler.sendEmptyMessage(MSG_WHAT_EVENT_READY);

				break;

			case MSG_WHAT_EVENT_READY:
				LogUtil.v(TAG, "handle MSG_WHAT_EVENT_READY");

				// 去重
				handler.removeMessages(MSG_WHAT_EVENT_READY);

				// 启用即收即发
				isRunningOn = true;
				// 关闭就绪flag
				isReadyOn = false;

				// 向用户展示一个前端对话框
				if (null == mDialog_ServiceOn) {
					showServiceOnDialog();
				}

				// 启用发邮Service
				Intent intent_event = new Intent(mContext,
						MailSenderService.class);
				intent_event.putExtra("mailtype",
						MailSenderService.MAILTYPE_EVENT);
				mContext.startService(intent_event);

				break;

			case MSG_WHAT_USER_PRESENT:
				LogUtil.v(TAG, "handle MSG_WHAT_USER_ON");

				handler.removeMessages(MSG_WHAT_EVENT_READY);
				MainService.clear();

				break;

			default:
				LogUtil.v(TAG, "handle msg what = " + msg.what);
				super.handleMessage(msg);
				break;

			}
		};
	};

	@Override
	public IBinder onBind(Intent arg0) {
		LogUtil.v(TAG, "onBind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.v(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		LogUtil.v(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		LogUtil.v(TAG, "onStartCommand");
		mContext = this;

		receiver_msg = new SmsReceiver();
		filter_msg = new IntentFilter();
		filter_msg.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter_msg.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver_msg, filter_msg);

		receiver_call = new CallReceiver();
		filter_call = new IntentFilter();
		filter_call.addAction("android.intent.action.PHONE_STATE");
		filter_call.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver_call, filter_call);

		receiver_userpresent = new UserPresentReceiver();
		filter_userpresent = new IntentFilter();
		filter_userpresent.addAction("android.intent.action.USER_PRESENT");
		filter_userpresent.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver_userpresent, filter_userpresent);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.v(TAG, "onDestroy");
		super.onDestroy();		

		unregisterReceiver(receiver_msg);
		unregisterReceiver(receiver_call);
		unregisterReceiver(receiver_userpresent);
		
		MainService.clear();

	}

	public static void showServiceOnDialog() {

		mDialog_ServiceOn = new AlertDialog.Builder(mContext).create();
		mDialog_ServiceOn.setIcon(android.R.drawable.ic_dialog_email);
		mDialog_ServiceOn.setTitle(mContext.getText(R.string.dialog_title));
		mDialog_ServiceOn.setMessage(mContext.getText(R.string.dialog_body));

		// This the point, this dialog won't block the user's current
		// activity via this attr
		mDialog_ServiceOn.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		mDialog_ServiceOn.setButton(DialogInterface.BUTTON_NEGATIVE,
				mContext.getText(R.string.dialog_stop),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		mDialog_ServiceOn.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				LogUtil.v(TAG, "dialog dismiss");

				MainService.clear();
			}
		});

		mDialog_ServiceOn.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				LogUtil.v(TAG, "dialog cancel");

				MainService.clear();
			}
		});

		mDialog_ServiceOn.show();
	}

	public static void clear() {
		isRunningOn = false;
		mDialog_ServiceOn = null;
		try {
			mArray_event.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
