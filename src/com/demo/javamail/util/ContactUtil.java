package com.demo.javamail.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactUtil {

	public static String queryNameByNum(Context context, String num) {
		Cursor cursorOriginal = context
				.getContentResolver()
				.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
						ContactsContract.CommonDataKinds.Phone.NUMBER + "='"
								+ num + "'", null, null);
		if (null != cursorOriginal) {
			if (cursorOriginal.getCount() > 1) {
				return null;
			} else {
				if (cursorOriginal.moveToFirst()) {
					return cursorOriginal
							.getString(cursorOriginal
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

}
