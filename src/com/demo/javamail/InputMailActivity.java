package com.demo.javamail;

import java.util.ArrayList;

import com.demo.javamail.service.MailSenderService;
import com.demo.javamail.util.LogUtil;
import com.demo.javamail.util.MailAddressAnalyst;
import com.demo.javamail.util.MailServer;
import com.demo.javamail.util.NetUtil;
import com.demo.javamail.util.PrefsUtil;
import com.demo.javamail.util.TimeUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class InputMailActivity extends Activity {

	private final static String TAG = "InputMailActivity";

	private static SharedPreferences prefs;
	private static SharedPreferences.Editor prefs_editor;

	private Intent intent;

	private TextView tv_tips;
	private EditText et_mail;
	private EditText et_pwd;
	private Spinner sp_mail;

	private ArrayAdapter<String> adapter_mailsuffix;
	private ArrayList<String> array_mailsuffix;
	private String string_mailsuffix;

	private Animation anim_fade;
	private boolean isEtMailNeverFocus;
	private boolean isEtPwdNeverFocus;
	private boolean isEtMailInitFocus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputmail);

		intent = this.getIntent();

		array_mailsuffix = MailServer.getMailSuffixList();
		adapter_mailsuffix = new ArrayAdapter<String>(this,
				R.layout.spinner_mailsuffix, array_mailsuffix);
		adapter_mailsuffix
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		prefs = getSharedPreferences(PrefsUtil.PREFS_NAME, 0);
		prefs_editor = prefs.edit();

		anim_fade = AnimationUtils.loadAnimation(this, R.anim.fade);

		initLayout();
		//et_mail初始化界面时获得焦点的替死鬼
		isEtMailInitFocus = false;
		isEtMailNeverFocus = true;
		isEtPwdNeverFocus = true;

	}

	@Override
	protected void onResume() {
		LogUtil.v(TAG, "onResume");
		et_mail.setText(prefs.getString(PrefsUtil.KEY_MAIL_NAME_CACHE, null));
		et_pwd.setText(prefs.getString(PrefsUtil.KEY_PWD_CACHE, null));
		string_mailsuffix = prefs.getString(PrefsUtil.KEY_MAIL_SUFFIX_CACHE,
				null);

		int init_sp_selection = array_mailsuffix.indexOf(string_mailsuffix);
		sp_mail.setSelection(init_sp_selection);
		super.onResume();
	}

	@Override
	protected void onStop() {
		LogUtil.v(TAG, "onStop");
		prefs_editor.putString(PrefsUtil.KEY_MAIL_NAME_CACHE, et_mail.getText()
				.toString());
		prefs_editor.putString(PrefsUtil.KEY_PWD_CACHE, et_pwd.getText()
				.toString());
		prefs_editor.putString(PrefsUtil.KEY_MAIL_SUFFIX_CACHE,
				string_mailsuffix);
		prefs_editor.commit();
		super.onStop();
	}

	private void initLayout() {
		tv_tips = (TextView) findViewById(R.id.tv_tips);
		et_mail = (EditText) findViewById(R.id.et_mail);

		et_mail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEtMailNeverFocus) {
					isEtMailNeverFocus = false;
					tv_tips.setText(InputMailActivity.this.getString(R.string.tip_mail));
					tv_tips.startAnimation(anim_fade);
				}
			}
		});

		et_mail.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (!isEtMailInitFocus) {
						isEtMailInitFocus = true;
					} else if (isEtMailNeverFocus) {
						isEtMailNeverFocus = false;
						tv_tips.setText(InputMailActivity.this.getString(R.string.tip_mail));
						tv_tips.startAnimation(anim_fade);
					}
				}
			}
		});

		et_pwd = (EditText) findViewById(R.id.et_pwd);

		et_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (isEtPwdNeverFocus) {
						isEtPwdNeverFocus = false;
						tv_tips.setText(InputMailActivity.this.getString(R.string.tip_pwd));
						tv_tips.startAnimation(anim_fade);
					}
				}
			}
		});

		sp_mail = (Spinner) findViewById(R.id.sp_mail);
		sp_mail.setAdapter(adapter_mailsuffix);		
		sp_mail.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				string_mailsuffix = array_mailsuffix.get(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	public void onButtonTestClicked(View View) {
		LogUtil.v(TAG, "button test clicked");

		if (!NetUtil.checkNetState(this)) {
			Toast.makeText(this, this.getString(R.string.toast_neterror), Toast.LENGTH_LONG).show();
			return;
		}

		if (!checkInput()) {
			return;
		}

		Intent intent = new Intent(this, MailSenderService.class);
		intent.putExtra("mailtype", MailSenderService.MAILTYPE_TEST);
		intent.putExtra("mail", et_mail.getText().toString()
				+ string_mailsuffix);
		intent.putExtra("pwd", et_pwd.getText().toString());
		intent.putExtra("time", TimeUtil.getCurrentTime());
		this.startService(intent);

		Toast.makeText(this, this.getString(R.string.toast_testmail), Toast.LENGTH_LONG).show();
	}

	public void onButtonFinishClicked(View View) {
		LogUtil.v(TAG, "button finish clicked");
		if (!checkInput())
			return;
		MainActivity.mail = et_mail.getText().toString() + string_mailsuffix;
		MainActivity.pwd = et_pwd.getText().toString();
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	private boolean checkInput() {
		return checkMailInput(et_mail.getText().toString())
				& checkPwdInput(et_pwd.getText().toString());
	}

	private boolean checkMailInput(String input) {

		if (input.length()==0||input==null||input=="") {
			Toast.makeText(this, this.getText(R.string.toast_mail), Toast.LENGTH_LONG).show();
			return false;
		}

		if (MailAddressAnalyst.isMailAddress(input)) {
			Toast.makeText(this, this.getText(R.string.toast_suffix), Toast.LENGTH_LONG)
					.show();
			return false;
		}

		return true;
	}

	private boolean checkPwdInput(String input) {
		if (input.length()==0||input==null||input=="") {
			Toast.makeText(this, this.getText(R.string.toast_pwd), Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}
