package com.demo.javamail.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MailServer {

	public final static String Key_Suffix = "key_mail";
	public final static String Key_Server = "key_server";
	public final static String Key_Port = "key_port";

	public final static String Suffix_qq = "@qq.com";
	public final static String Server_qq = "smtp.qq.com";
	public final static String Port_qq = "25";

	public final static String Suffix_163 = "@163.com";
	public final static String Server_163 = "smtp.163.com";
	public final static String Port_163 = "25";

	public final static String Suffix_126 = "@126.com";
	public final static String Server_126 = "smtp.126.com";
	public final static String Port_126 = "25";

	public final static String Suffix_gmail = "@gmail.com";
	public final static String Server_gmail = "smtp.gmail.com";
	public final static String Port_gmail = "465";

	public final static String Suffix_hotmail = "@hotmail.com";
	public final static String Server_hotmail = "smtp.live.com";
	public final static String Port_hotmail = "25";

	public final static String Suffix_21cn = "@21cn.com";
	public final static String Server_21cn = "smtp.21cn.com";
	public final static String Port_21cn = "25";

	public final static String Suffix_sina_cn = "@sina.cn";
	public final static String Server_sina_cn = "smtp.sina.com";
	public final static String Port_sina_cn = "25";

	public static ArrayList<String> getMailSuffixList() {

		ArrayList<String> MailList = new ArrayList<String>();
		MailList.add(Suffix_qq);
		MailList.add(Suffix_163);
		MailList.add(Suffix_126);
		MailList.add(Suffix_gmail);
		// 不是很好用，而且影响界面美观
		// MailList.add(Suffix_hotmail);
		MailList.add(Suffix_21cn);
		MailList.add(Suffix_sina_cn);

		return MailList;
	}

	private static Map<String, Map<String, String>> MailServerMap;
	private static Map<String, String> MailMap;

	public static Map<String, Map<String, String>> getMailServerMap() {
		MailServerMap = new HashMap<String, Map<String, String>>();

		MailMap = new HashMap<String, String>();
		MailMap.put(Key_Server, Server_qq);
		MailMap.put(Key_Port, Port_qq);
		MailServerMap.put(Suffix_qq, MailMap);

		MailMap = new HashMap<String, String>();
		MailMap.put(Key_Server, Server_163);
		MailMap.put(Key_Port, Port_163);
		MailServerMap.put(Suffix_163, MailMap);

		MailMap = new HashMap<String, String>();
		MailMap.put(Key_Server, Server_126);
		MailMap.put(Key_Port, Port_126);
		MailServerMap.put(Suffix_126, MailMap);

		MailMap = new HashMap<String, String>();
		MailMap.put(Key_Server, Server_gmail);
		MailMap.put(Key_Port, Port_gmail);
		MailServerMap.put(Suffix_gmail, MailMap);

		// MailMap = new HashMap<String, String>();
		// MailMap.put(Key_Server, Server_hotmail);
		// MailMap.put(Key_Port, Port_hotmail);
		// MailServerMap.put(Suffix_hotmail, MailMap);

		MailMap = new HashMap<String, String>();
		MailMap.put(Key_Server, Server_21cn);
		MailMap.put(Key_Port, Port_21cn);
		MailServerMap.put(Suffix_21cn, MailMap);

		MailMap = new HashMap<String, String>();
		MailMap.put(Key_Server, Server_sina_cn);
		MailMap.put(Key_Port, Port_sina_cn);
		MailServerMap.put(Suffix_sina_cn, MailMap);

		return MailServerMap;
	}
}
