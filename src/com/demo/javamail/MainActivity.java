package com.demo.javamail;

import com.demo.javamail.service.MainService;
import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.PrefsUtil;

import com.demo.javamail.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";
	private final static String mail_null = "点我设置接收邮箱";
	private final static int MSG_READY = 99;

	private static SharedPreferences prefs;
	private static SharedPreferences.Editor prefs_editor;

	public static boolean isSwitchOn = false;
	public static boolean isFirstSet = false;
	public static boolean isReadyWake = false;
	public static boolean isServiceOn = false;

	public static String mail;
	public static String pwd;

	private Button btn_switch;
	private ImageView iv_mail;
	private TextView tv_mail;

	private Typeface typeFace;

	private static AlertDialog mDialog_Guide;

	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_READY:
				// 加入唤醒机制
				LogUtil.v("handler", "wake up!");
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/AGENCYB.TTF");
		prefs = getSharedPreferences(PrefsUtil.PREFS_NAME, 0);
		prefs_editor = prefs.edit();
		mail = prefs.getString(PrefsUtil.KEY_MAIL, mail_null);
		pwd = prefs.getString(PrefsUtil.KEY_PWD, null);
		isSwitchOn = prefs.getBoolean(PrefsUtil.FLAG_MAIN_SWITCH, false);

		initLayout();
	}

	@Override
	protected void onResume() {
		LogUtil.v(TAG, "onResume");
		setScreenStateBySwitch();
		tv_mail.setText(mail);

		super.onResume();
	}

	@Override
	protected void onStop() {
		LogUtil.v(TAG, "onStop");

		super.onStop();
	}

	private void initLayout() {
		btn_switch = (Button) findViewById(R.id.btn_switch);
		iv_mail = (ImageView) findViewById(R.id.iv_mail);
		tv_mail = (TextView) findViewById(R.id.tv_mail);
	}

	private void setScreenStateBySwitch() {
		if (isSwitchOn) {
			btn_switch.setBackgroundResource(R.drawable.btn_on);
			iv_mail.setImageResource(R.drawable.ic_mail);
			tv_mail.setTextAppearance(this, R.style.Text_Mail_Available);
			tv_mail.setTypeface(typeFace);
		} else {
			btn_switch.setBackgroundResource(R.drawable.btn_off);
			iv_mail.setImageResource(R.drawable.ic_mail_disable);
			tv_mail.setTextAppearance(this, R.style.Text_Mail_Disable);
			tv_mail.setTypeface(typeFace);
		}
	}

	public void onButtonSwitchClicked(View View) {

		// handler.sendEmptyMessageDelayed(MSG_READY, 2000);

		if (mail.equals(mail_null)) {
			isFirstSet = true;
			Intent intent = new Intent(this, InputMailActivity.class);
			startActivityForResult(intent, 0);
		}

		if (!isFirstSet) {
			isSwitchOn = !isSwitchOn;
		}

		prefs_editor.putBoolean(PrefsUtil.FLAG_MAIN_SWITCH, isSwitchOn);
		prefs_editor.commit();

		if (isSwitchOn) {
			startService(new Intent(this, MainService.class));
			showGuideDialog();
		} else {
			stopService(new Intent(this, MainService.class));
		}

		setScreenStateBySwitch();
	}

	public void onLayoutMailClicked(View view) {
		Intent intent = new Intent(this, InputMailActivity.class);
		startActivityForResult(intent, 0);
	}

	public void onButtonInfoClicked(View view) {
		Intent intent = new Intent(this, InfoActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) {
		case RESULT_OK:
			LogUtil.v(TAG, "return result OK");
			isFirstSet = false;
			isSwitchOn = true;

			prefs_editor.putBoolean(PrefsUtil.FLAG_MAIN_SWITCH, true);
			prefs_editor.putString(PrefsUtil.KEY_MAIL, mail);
			prefs_editor.putString(PrefsUtil.KEY_PWD, pwd);
			prefs_editor.commit();

			startService(new Intent(this, MainService.class));
			break;
		default:
			LogUtil.v(TAG, "return result FALSE");
			break;
		}
	}

	private void showGuideDialog() {
		mDialog_Guide = new AlertDialog.Builder(this).create();
		mDialog_Guide.setIcon(android.R.drawable.ic_dialog_info);
		mDialog_Guide.setTitle(this
				.getText(R.string.dialog_title_guide));
		mDialog_Guide.setMessage(this
				.getText(R.string.dialog_guide));

		mDialog_Guide.setButton(DialogInterface.BUTTON_NEGATIVE,
				this.getText(R.string.dialog_i_know),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		mDialog_Guide.show();
	}
}
