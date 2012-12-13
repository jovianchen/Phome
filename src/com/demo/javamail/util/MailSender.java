package com.demo.javamail.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.demo.javamail.R;
import com.demo.javamail.service.MainService;

import android.content.Context;

public class MailSender {

	private final static String TAG = "MailSender";

	public static void sendTestMail(Context context, String mail, String pwd,
			String time) {

		MailInfo mailInfo = new MailInfo();
		mailInfo.setMailServerHost(getMailServerFromAddress(mail));
		mailInfo.setMailServerPort(getMailPortFromAddress(mail));
		mailInfo.setValidate(true);
		mailInfo.setUserName(mail);
		mailInfo.setPassword(pwd);
		mailInfo.setFromAddress(mail);
		mailInfo.setToAddress(mail);
		mailInfo.setSubject(context.getString(R.string.mail_test_subject));
		mailInfo.setContent(formatTestMail(context,
				context.getString(R.string.mail_test_body), time));

		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo, requireSSL(mail), requireTLS(mail));
	}
	
	public static void sendKeyMsgMail(Context context, String mail, String pwd,
			String time) {

		MailInfo mailInfo = new MailInfo();
		mailInfo.setMailServerHost(getMailServerFromAddress(mail));
		mailInfo.setMailServerPort(getMailPortFromAddress(mail));
		mailInfo.setValidate(true);
		mailInfo.setUserName(mail);
		mailInfo.setPassword(pwd);
		mailInfo.setFromAddress(mail);
		mailInfo.setToAddress(mail);
		mailInfo.setSubject(context.getString(R.string.mail_keymsg_subject));
		mailInfo.setContent(formatKeyMsgMail(context,
				context.getString(R.string.mail_keymsg_body), time));

		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo, requireSSL(mail), requireTLS(mail));
	}

	public static void sendEventMail(Context context, String mail, String pwd,
			ArrayList<JSONObject> array) {

		MailInfo mailInfo = new MailInfo();
		mailInfo.setMailServerHost(getMailServerFromAddress(mail));
		mailInfo.setMailServerPort(getMailPortFromAddress(mail));
		mailInfo.setValidate(true);
		mailInfo.setUserName(mail);
		mailInfo.setPassword(pwd);
		mailInfo.setFromAddress(mail);
		mailInfo.setToAddress(mail);
		mailInfo.setSubject(formatEventMailSubject(context, array));
		mailInfo.setContent(formatEventMailBody(context, array));
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo, requireSSL(mail), requireTLS(mail));
		
		MainService.mArray_event.clear();
	}

	private static String formatEventMailBody(Context context,
			ArrayList<JSONObject> array) {

		String result = null;

		ArrayList<JSONObject> array_msg = new ArrayList<JSONObject>();
		ArrayList<JSONObject> array_call = new ArrayList<JSONObject>();

		try {
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i).getInt(MainService.JSON_KEY_EVENT_TYPE) == MainService.TYPE_MSG) {
					array_msg.add(array.get(i));
				} else if (array.get(i).getInt(MainService.JSON_KEY_EVENT_TYPE) == MainService.TYPE_CALL) {
					array_call.add(array.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (array_msg.size() > 0) {
			result = context.getString(R.string.mail_title_msg) + "\n";
			result = result + formatEventMailBodyMsg(context, array_msg);
			if (array_call.size() > 0) {
				result = result + "\n"
						+ context.getString(R.string.mail_title_call) + "\n";
				result = result + formatEventMailBodyCall(context, array_call);
			}
		} else {
			if (array_call.size() > 0) {
				result = context.getString(R.string.mail_title_call) + "\n";
				result = result + formatEventMailBodyCall(context, array_call);
			}
		}

		return result;
	}

	private static String formatEventMailBodyCall(Context context,
			ArrayList<JSONObject> array) {

		String result = "";

		for (int i = 0; i < array.size(); i++) {
			try {
				result = result
						+ context.getString(R.string.mail_from)
						+ array.get(i)
								.getString(MainService.JSON_KEY_EVENT_NUM)
						+ formatContactName(
								context,
								array.get(i).getString(
										MainService.JSON_KEY_EVENT_NUM)) + "\n";
				result = result
						+ context.getString(R.string.mail_time)
						+ array.get(i).getString(
								MainService.JSON_KEY_EVENT_TIME) + "\n";
				result = result + "---\n";
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private static String formatEventMailBodyMsg(Context context,
			ArrayList<JSONObject> array) {

		String result = "";

		for (int i = 0; i < array.size(); i++) {
			try {
				result = result
						+ context.getString(R.string.mail_from)
						+ array.get(i)
								.getString(MainService.JSON_KEY_EVENT_NUM)
						+ formatContactName(
								context,
								array.get(i).getString(
										MainService.JSON_KEY_EVENT_NUM)) + "\n";
				result = result
						+ context.getString(R.string.mail_body)
						+ array.get(i).getString(
								MainService.JSON_KEY_EVENT_BODY) + "\n";
				result = result
						+ context.getString(R.string.mail_time)
						+ array.get(i).getString(
								MainService.JSON_KEY_EVENT_TIME) + "\n";
				result = result + "---\n";
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private static String formatEventMailSubject(Context context,
			ArrayList<JSONObject> array) {

		int num_msg = 0;
		int num_call = 0;
		String result = null;

		String s_basic = context.getString(R.string.mail_subject_basic);
		String s_msg = context.getString(R.string.mail_subject_msg);
		String s_call = context.getString(R.string.mail_subject_call);

		try {
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i).getInt(MainService.JSON_KEY_EVENT_TYPE) == MainService.TYPE_MSG) {
					num_msg++;
				} else if (array.get(i).getInt(MainService.JSON_KEY_EVENT_TYPE) == MainService.TYPE_CALL) {
					num_call++;
				}
			}

			if (num_msg > 0) {
				result = s_basic + num_msg + s_msg;
				if (num_call > 0) {
					result = result + ", " + num_call + s_call;
				}
			} else {
				if (num_call > 0) {
					result = s_basic + num_call + s_call;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String formatTestMail(Context context, String body,
			String time) {
		String result;
		result = body + "\n\n";
		result = result + context.getString(R.string.mail_time) + time;
		return result;
	}
	
	private static String formatKeyMsgMail(Context context, String body,
			String time) {
		String result;
		result = body + "\n\n";
		result = result + context.getString(R.string.mail_time) + time;
		return result;
	}

	private static String getMailServerFromAddress(String input) {

		try {

			String suffix = MailAddressAnalyst.getMailSuffixFromAddress(input
					.toLowerCase());

			Map<String, Map<String, String>> MailServerMap = new HashMap<String, Map<String, String>>();
			MailServerMap = MailServer.getMailServerMap();

			Map<String, String> MailMap = new HashMap<String, String>();
			MailMap = MailServerMap.get(suffix);

			String server = MailMap.get(MailServer.Key_Server);

			LogUtil.v(TAG, "get server from input " + server);

			return server;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getMailPortFromAddress(String input) {

		try {

			String suffix = MailAddressAnalyst.getMailSuffixFromAddress(input
					.toLowerCase());

			Map<String, Map<String, String>> MailServerMap = new HashMap<String, Map<String, String>>();
			MailServerMap = MailServer.getMailServerMap();

			Map<String, String> MailMap = new HashMap<String, String>();
			MailMap = MailServerMap.get(suffix);

			String port = MailMap.get(MailServer.Key_Port);

			LogUtil.v(TAG, "get port from input " + port);

			return port;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String formatContactName(Context context, String num) {
		LogUtil.v(TAG, "contact num = " + num);
		// tobe 测试中国号码格式支持
		// tobe 添加美国号码格式支持
		String result = new String();
		try {
			String name = ContactUtil.queryNameByNum(context, num);
			if (name != null && name != "") {
				result = "(" + name + ")";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean requireSSL(String mail) {
		if (getMailServerFromAddress(mail) == MailServer.Server_gmail) {
			return true;
		}
		return false;
	}

	private static boolean requireTLS(String mail) {
		if (getMailServerFromAddress(mail) == MailServer.Server_hotmail) {
			return true;
		}
		return false;
	}

}
