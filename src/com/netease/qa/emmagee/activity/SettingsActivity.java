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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.netease.qa.emmagee.utils.Settings;

/**
 * Setting Page of Emmagee
 * 
 * @author andrewleo
 */
public class SettingsActivity extends Activity {

    private static final String LOG_TAG = "Emmagee-" + SettingsActivity.class.getSimpleName();

	private CheckBox chkFloat;
	private TextView tvTime;
	private LinearLayout about;
	private LinearLayout mailSettings;

    private SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);

		chkFloat = (CheckBox) findViewById(R.id.floating);
		tvTime = (TextView) findViewById(R.id.time);
		about = (LinearLayout) findViewById(R.id.about);
		mailSettings = (LinearLayout) findViewById(R.id.mail_settings);
		SeekBar timeBar = (SeekBar) findViewById(R.id.timeline);
		ImageView btnSave = (ImageView) findViewById(R.id.btn_set);
		ImageView goBack = (ImageView) findViewById(R.id.go_back);
		RelativeLayout floatingItem = (RelativeLayout) findViewById(R.id.floating_item);
		LinearLayout layGoBack = (LinearLayout) findViewById(R.id.lay_go_back);


		btnSave.setVisibility(ImageView.INVISIBLE);
		preferences = Settings.getDefaultSharedPreferences(getApplicationContext());
		int interval = preferences.getInt(Settings.KEY_INTERVAL, 5);
		boolean isfloat = preferences.getBoolean(Settings.KEY_ISFLOAT, true);
		
		
		tvTime.setText(String.valueOf(interval));
		chkFloat.setChecked(isfloat);
		timeBar.setProgress(interval);
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
				// when tracking stoped, update preferences
				int interval = arg0.getProgress() + 1;
				preferences.edit().putInt(Settings.KEY_INTERVAL, interval).commit();
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

		floatingItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    chkFloat.setChecked(!chkFloat.isChecked());
			    preferences.edit().putBoolean(Settings.KEY_ISFLOAT, chkFloat.isChecked()).commit();
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

}
