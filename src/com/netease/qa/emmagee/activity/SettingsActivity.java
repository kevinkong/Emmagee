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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.netease.qa.emmagee.R;
import com.netease.qa.emmagee.utils.EncryptData;

/**
 * Setting Page of Emmagee
 * 
 * @author andrewleo
 */
public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "Emmagee-"
			+ SettingsActivity.class.getSimpleName();

	private CheckBox chkFloat;
	private EditText edtTime;
	private String time;
	private String settingTempFile;
	private LinearLayout about;
	private LinearLayout mailSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);

		final EncryptData des = new EncryptData("emmagee");
		Intent intent = this.getIntent();
		settingTempFile = getBaseContext().getFilesDir().getPath()
				+ "\\EmmageeSettings.properties";

		chkFloat = (CheckBox) findViewById(R.id.floating);
		edtTime = (EditText) findViewById(R.id.time);
		about = (LinearLayout) findViewById(R.id.about);
		mailSettings = (LinearLayout) findViewById(R.id.mail_settings);

		ImageView btnSave = (ImageView) findViewById(R.id.btn_set);
		ImageView goBack = (ImageView) findViewById(R.id.go_back);
		boolean floatingTag = true;

		btnSave.setImageResource(R.drawable.actionbar_bg);
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(settingTempFile));
			String interval = (null == properties.getProperty("interval"))?"":properties.getProperty("interval").trim();
			String isfloat = (null == properties.getProperty("isfloat"))?"":properties.getProperty("isfloat").trim(); 
			time = "".equals(interval) ? "5" : interval;
			floatingTag = "false".equals(isfloat) ? false : true;
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception: " + e.getMessage());
			e.printStackTrace();
		}
		edtTime.setText(time);
		chkFloat.setChecked(floatingTag);

		goBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SettingsActivity.this.finish();
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this,
						MainPageActivity.class);
				startActivity(intent);
			}
		});
		
		mailSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this,
						MailSettingsActivity.class);
				startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			}
		});

		about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, AboutActivity.class);
				startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			}
		});

		chkFloat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Properties properties = new Properties();
					properties.setProperty("isfloat",
							chkFloat.isChecked() ? "true" : "false");
					FileOutputStream fos = new FileOutputStream(settingTempFile);
					properties.store(fos, "Setting Data");
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
					chkFloat.setChecked(chkFloat.isChecked() ? false : true);
				}
			}
		});
		// edtTime.setInputType(InputType.TYPE_CLASS_NUMBER);
		// btnSave.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// time = edtTime.getText().toString().trim();
		// if (!isNumeric(time)) {
		// Toast.makeText(SettingsActivity.this, "输入数据无效，请重新输入",
		// Toast.LENGTH_LONG).show();
		// edtTime.setText("");
		// } else if ("".equals(time) || Long.parseLong(time) == 0) {
		// Toast.makeText(SettingsActivity.this, "输入数据为空,请重新输入",
		// Toast.LENGTH_LONG).show();
		// edtTime.setText("");
		// } else if (Integer.parseInt(time) > 600) {
		// Toast.makeText(SettingsActivity.this, "数据超过最大值600，请重新输入",
		// Toast.LENGTH_LONG).show();
		// } else {
		// try {
		// Properties properties = new Properties();
		// properties.setProperty("interval", time);
		// properties.setProperty("isfloat",
		// chkFloat.isChecked() ? "true" : "false");
		// FileOutputStream fos = new FileOutputStream(
		// settingTempFile);
		// properties.store(fos, "Setting Data");
		// fos.close();
		// Toast.makeText(SettingsActivity.this,
		// getString(R.string.save_success_toast),
		// Toast.LENGTH_LONG).show();
		// Intent intent = new Intent();
		// setResult(Activity.RESULT_FIRST_USER, intent);
		// SettingsActivity.this.finish();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// });
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * is input a number.
	 * 
	 * @param inputStr
	 *            input string
	 * @return true is numeric
	 */
	private boolean isNumeric(String inputStr) {
		for (int i = inputStr.length(); --i >= 0;) {
			if (!Character.isDigit(inputStr.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
