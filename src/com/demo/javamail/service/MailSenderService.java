package com.demo.javamail.service;

import java.util.ArrayList;

import org.json.JSONObject;

import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.MailSender;
import com.demo.javamail.util.PrefsUtil;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

public class MailSenderService extends IntentService {

	private final static String TAG = "MailSenderService";

	public final static int MAILTYPE_EVENT = 0;
	public final static int MAILTYPE_TEST = 1;
	public final static int MAILTYPE_KEYMSG = 2;

	public MailSenderService() {
		super("MailSenderService");
		LogUtil.v(TAG, "MailSenderService Create");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		int mail_type = intent.getIntExtra("mailtype", -1);

		if (mail_type == MAILTYPE_EVENT) {
			SharedPreferences prefs = getSharedPreferences(
					PrefsUtil.PREFS_NAME, 0);
			String mail = new String();
			String pwd = new String();
			try {
				mail = prefs.getString(PrefsUtil.KEY_MAIL, null);
				pwd = prefs.getString(PrefsUtil.KEY_PWD, null);
			} catch (Exception e) {
				LogUtil.e(TAG, "prefs nullpointer!");
			}

			ArrayList<JSONObject> array_event = MainService.mArray_event;
			if (array_event.size() > 0) {
				MailSender.sendEventMail(this, mail, pwd, array_event);
			}
		}

		else if (mail_type == MAILTYPE_TEST) {

			String mail = intent.getStringExtra("mail");
			String pwd = intent.getStringExtra("pwd");
			String time = intent.getStringExtra("time");

			MailSender.sendTestMail(this, mail, pwd, time);

		}

		else if (mail_type == MAILTYPE_KEYMSG) {
			
			SharedPreferences prefs = getSharedPreferences(
					PrefsUtil.PREFS_NAME, 0);
			String mail = new String();
			String pwd = new String();
			try {
				mail = prefs.getString(PrefsUtil.KEY_MAIL, null);
				pwd = prefs.getString(PrefsUtil.KEY_PWD, null);
			} catch (Exception e) {
				LogUtil.e(TAG, "prefs nullpointer!");
			}
			String time = intent.getStringExtra("time");

			MailSender.sendKeyMsgMail(this, mail, pwd, time);

		}

	}

}
