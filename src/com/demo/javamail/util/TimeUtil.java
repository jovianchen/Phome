package com.demo.javamail.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy'-'MM'-'dd HH:mm:ss");
		return df.format(new Date());
	}
}
