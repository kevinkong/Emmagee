/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.netease.qa.emmagee.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.qa.emmagee.R;
import com.netease.qa.emmagee.utils.EncryptData;

/**
 * Mail Setting Page of Emmagee
 * 
 * @author andrewleo
 */
public class MailSettingsActivity extends Activity {

	private static final String LOG_TAG = "Emmagee-"
			+ MailSettingsActivity.class.getSimpleName();

	private EditText edtRecipients;
	private EditText edtSender;
	private EditText edtPassword;
	private EditText edtSmtp;
	private String sender;
	private String prePassword, curPassword;
	private String settingTempFile;
	private String recipients, smtp;
	private String[] receivers;
	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mail_settings);

		final EncryptData des = new EncryptData("emmagee");
		Intent intent = this.getIntent();
		settingTempFile = getBaseContext().getFilesDir().getPath()
				+ "\\EmmageeSettings.properties";
		;

		edtSender = (EditText) findViewById(R.id.sender);
		edtPassword = (EditText) findViewById(R.id.password);
		edtRecipients = (EditText) findViewById(R.id.recipients);
		edtSmtp = (EditText) findViewById(R.id.smtp);
		title = (TextView)findViewById(R.id.nb_title);
		ImageView btnSave = (ImageView) findViewById(R.id.btn_set);
		LinearLayout layGoBack = (LinearLayout) findViewById(R.id.lay_go_back);
		LinearLayout layBtnSet = (LinearLayout) findViewById(R.id.lay_btn_set);
		
		boolean floatingTag = true;
		
		title.setText(R.string.mail_settings);

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			sender = (null == properties.getProperty("sender")) ? ""
					: properties.getProperty("sender").trim();
			prePassword = (null == properties.getProperty("password")) ? ""
					: properties.getProperty("password").trim();
			recipients = (null == properties.getProperty("recipients")) ? ""
					: properties.getProperty("recipients").trim();
			smtp = (null == properties.getProperty("smtp")) ? "" : properties
					.getProperty("smtp").trim();
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		edtRecipients.setText(recipients);
		edtSender.setText(sender);
		edtPassword.setText(prePassword);
		edtSmtp.setText(smtp);

		layGoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MailSettingsActivity.this.finish();
			}
		});
		layBtnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				sender = edtSender.getText().toString().trim();
				if (!"".equals(sender) && !checkMailFormat(sender)) {
					Toast.makeText(MailSettingsActivity.this, getString(R.string.sender_mail_toast) + getString(R.string.format_incorrect_format),
							Toast.LENGTH_LONG).show();
					return;
				}
				recipients = edtRecipients.getText().toString().trim();
				receivers = recipients.split("\\s+");
				for (int i = 0; i < receivers.length; i++) {
					if (!"".equals(receivers[i])
							&& !checkMailFormat(receivers[i])) {
						Toast.makeText(MailSettingsActivity.this,
								getString(R.string.receiver_mail_toast) + "[" + receivers[i] + "]" + getString(R.string.format_incorrect_format),
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				curPassword = edtPassword.getText().toString().trim();
				smtp = edtSmtp.getText().toString().trim();
				if (checkMailConfig(sender, recipients, smtp, curPassword) == -1) {
					Toast.makeText(MailSettingsActivity.this,
							getString(R.string.info_incomplete_toast), Toast.LENGTH_LONG).show();
					return;
				}
				try {
					Properties properties = new Properties();
					properties.load(new FileInputStream(settingTempFile)); 
					properties.setProperty("sender", sender);
					Log.d(LOG_TAG, "sender=" + sender);
					try {
						properties.setProperty(
								"password",
								curPassword.equals(prePassword) ? curPassword
										: ("".equals(curPassword) ? "" : des
												.encrypt(curPassword)));
					} catch (Exception e) {
						properties.setProperty("password", "");
					}
					properties.setProperty("recipients", recipients);
					properties.setProperty("smtp", smtp);
					FileOutputStream fos = new FileOutputStream(settingTempFile);
					properties.store(fos, "Setting Data");
					fos.close();
					Toast.makeText(MailSettingsActivity.this, getString(R.string.save_success_toast),
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					setResult(Activity.RESULT_FIRST_USER, intent);
					MailSettingsActivity.this.finish();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private int checkMailConfig(String sender, String recipients, String smtp,
			String curPassword) {
		if (!"".equals(curPassword) && !"".equals(sender)
				&& !"".equals(recipients) && !"".equals(smtp)) {
			return 1;
		} else if ("".equals(curPassword) && "".equals(sender)
				&& "".equals(recipients) && "".equals(smtp)) {
			return 0;
		} else
			return -1;
	}

	/**
	 * check mail format
	 * 
	 * @return true: valid email address
	 */
	private boolean checkMailFormat(String mail) {
		String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*"
				+ "[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(mail);
		return m.matches();
	}
}
