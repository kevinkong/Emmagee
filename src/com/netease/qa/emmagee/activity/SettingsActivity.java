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
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.netease.qa.emmagee.R;
import com.netease.qa.emmagee.utils.EncryptData;

/**
 * Setting Page of Emmagee
 * 
 * @author andrewleo
 */
public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "Emmagee-" + SettingsActivity.class.getSimpleName();

	private CheckBox chkFloat;
	private TextView tvTime;
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

		Properties properties = new Properties();
		final EncryptData des = new EncryptData("emmagee");
		Intent intent = this.getIntent();
		settingTempFile = getBaseContext().getFilesDir().getPath() + "\\EmmageeSettings.properties";

		chkFloat = (CheckBox) findViewById(R.id.floating);
		tvTime = (TextView) findViewById(R.id.time);
		about = (LinearLayout) findViewById(R.id.about);
		mailSettings = (LinearLayout) findViewById(R.id.mail_settings);
		SeekBar timeBar = (SeekBar) findViewById(R.id.timeline);
		ImageView btnSave = (ImageView) findViewById(R.id.btn_set);
		ImageView goBack = (ImageView) findViewById(R.id.go_back);
		RelativeLayout floating_item = (RelativeLayout) findViewById(R.id.floating_item);
		LinearLayout layGoBack = (LinearLayout) findViewById(R.id.lay_go_back);
		
		boolean floatingTag = true;
		
		btnSave.setVisibility(ImageView.INVISIBLE);
		try {
			properties.load(new FileInputStream(settingTempFile));
			String interval = (null == properties.getProperty("interval")) ? "" : properties.getProperty("interval").trim();
			String isfloat = (null == properties.getProperty("isfloat")) ? "" : properties.getProperty("isfloat").trim();
			time = "".equals(interval) ? "5" : interval;
			floatingTag = "false".equals(isfloat) ? false : true;
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception: " + e.getMessage());
			e.printStackTrace();
		}
		tvTime.setText(time);
		chkFloat.setChecked(floatingTag);
		timeBar.setProgress(Integer.parseInt(time));
		timeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				tvTime.setText(Integer.toString(arg1 + 1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// when tracking stoped, update properties file
				int interval = arg0.getProgress() + 1;
				try {
					Properties properties = new Properties();
					properties.load(new FileInputStream(settingTempFile));   
					properties.setProperty("interval", Integer.toString(interval));
					FileOutputStream fos = new FileOutputStream(settingTempFile);
					properties.store(fos, "Setting Data");
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		layGoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SettingsActivity.this.finish();
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, MainPageActivity.class);
				startActivity(intent);
			}
		});

		mailSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, MailSettingsActivity.class);
				startActivity(intent);
			}
		});

		about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});

		floating_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					chkFloat.setChecked(!chkFloat.isChecked());
					Properties properties = new Properties();
					properties.load(new FileInputStream(settingTempFile));  
					properties.setProperty("isfloat", chkFloat.isChecked() ? "true" : "false");
					FileOutputStream fos = new FileOutputStream(settingTempFile);
					properties.store(fos, "Setting Data");
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
					chkFloat.setChecked(chkFloat.isChecked() ? false : true);
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
