package com.demo.javamail.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailAddressAnalyst {
	public static boolean isMailAddress(String input) {
		Pattern pattern = Pattern.compile(
				"[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	public static String getMailSuffixFromAddress(String input) {
		String result = new String();
		try {
			result = input.substring(input.indexOf("@"), input.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
