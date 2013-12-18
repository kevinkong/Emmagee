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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.qa.emmagee.service.EmmageeService;
import com.netease.qa.emmagee.utils.ProcessInfo;
import com.netease.qa.emmagee.utils.Programe;
import com.netease.qa.emmagee.R;

/**
 * Main Page of Emmagee
 * 
 * @author andrewleo
 */
public class MainPageActivity extends Activity {

	private static final String LOG_TAG = "Emmagee-" + MainPageActivity.class.getSimpleName();

	private static final int TIMEOUT = 20000;

	private List<Programe> processList;
	private ProcessInfo processInfo;
	private Intent monitorService;
	private ListView lstViProgramme;
	private Button btnTest;
	private boolean isRadioChecked = false;
	private int pid, uid;
	private String processName, packageName, settingTempFile;
	private boolean isServiceStop = false;
	private UpdateReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "MainActivity::onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		createNewFile();
		processInfo = new ProcessInfo();
		lstViProgramme = (ListView) findViewById(R.id.processList);
		btnTest = (Button) findViewById(R.id.test);
		btnTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				monitorService = new Intent();
				monitorService.setClass(MainPageActivity.this, EmmageeService.class);
				if ("开始测试".equals(btnTest.getText().toString())) {
					if (isRadioChecked) {
						Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
						Log.d(LOG_TAG, packageName);
						try {
							startActivity(intent);
						} catch (NullPointerException e) {
							Toast.makeText(MainPageActivity.this, "该程序无法启动", Toast.LENGTH_LONG).show();
							return;
						}
						waitForAppStart(packageName);
						monitorService.putExtra("processName", processName);
						monitorService.putExtra("pid", pid);
						monitorService.putExtra("uid", uid);
						monitorService.putExtra("packageName", packageName);
						monitorService.putExtra("settingTempFile", settingTempFile);
						startService(monitorService);
						btnTest.setText("停止测试");
					} else {
						Toast.makeText(MainPageActivity.this, "请选择需要测试的应用程序", Toast.LENGTH_LONG).show();
					}
				} else {
					btnTest.setText("开始测试");
					Toast.makeText(MainPageActivity.this, "测试结果文件：" + EmmageeService.resultFilePath, Toast.LENGTH_LONG).show();
					stopService(monitorService);
				}
			}
		});
		lstViProgramme.setAdapter(new ListAdapter());
	}

	/**
	 * customized BroadcastReceiver
	 * 
	 * @author andrewleo
	 */
	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			isServiceStop = intent.getExtras().getBoolean("isServiceStop");
			if (isServiceStop) {
				btnTest.setText("开始测试");
			}
		}
	}

	@Override
	protected void onStart() {
		Log.d(LOG_TAG, "onStart");
		receiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.netease.action.emmageeService");
		this.registerReceiver(receiver, filter);
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume");
		if (EmmageeService.isStop) {
			btnTest.setText("开始测试");
		}
	}

	/**
	 * create new file to reserve setting data.
	 */
	private void createNewFile() {
		Log.i(LOG_TAG, "create new file to save setting data");
		settingTempFile = getBaseContext().getFilesDir().getPath() + "\\EmmageeSettings.properties";
		Log.i(LOG_TAG, "settingFile = " + settingTempFile);
		File settingFile = new File(settingTempFile);
		if (!settingFile.exists()) {
			try {
				settingFile.createNewFile();
				Properties properties = new Properties();
				properties.setProperty("interval", "5");
				properties.setProperty("isfloat", "true");
				properties.setProperty("sender", "");
				properties.setProperty("password", "");
				properties.setProperty("recipients", "");
				properties.setProperty("smtp", "");
				FileOutputStream fos = new FileOutputStream(settingTempFile);
				properties.store(fos, "Setting Data");
				fos.close();
			} catch (IOException e) {
				Log.d(LOG_TAG, "create new file exception :" + e.getMessage());
			}
		}
	}

	/**
	 * wait for test application started.
	 * 
	 * @param packageName
	 *            package name of test application
	 */
	private void waitForAppStart(String packageName) {
		Log.d(LOG_TAG, "wait for app start");
		boolean isProcessStarted = false;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < startTime + TIMEOUT) {
			processList = processInfo.getRunningProcess(getBaseContext());
			for (Programe programe : processList) {
				if ((programe.getPackageName() != null) && (programe.getPackageName().equals(packageName))) {
					pid = programe.getPid();
					Log.d(LOG_TAG, "pid:" + pid);
					uid = programe.getUid();
					if (pid != 0) {
						isProcessStarted = true;
						break;
					}
				}
			}
			if (isProcessStarted) {
				break;
			}
		}
	}

	/**
	 * show a dialog when click return key.
	 * 
	 * @return Return true to prevent this event from being propagated further,
	 *         or false to indicate that you have not handled this event and it
	 *         should continue to be propagated.
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showDialog(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * set menu options,including cancel and setting options.
	 * 
	 * @return true
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST, 0, "退出").setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, Menu.FIRST, 1, "设置").setIcon(android.R.drawable.ic_menu_directions);
		return true;
	}

	/**
	 * trigger menu options.
	 * 
	 * @return false
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getOrder()) {
		case 0:
			showDialog(0);
			break;
		case 1:
			Intent intent = new Intent();
			intent.setClass(MainPageActivity.this, SettingsActivity.class);
			intent.putExtra("settingTempFile", settingTempFile);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * create a dialog.
	 * 
	 * @return a dialog
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new AlertDialog.Builder(this).setTitle("确定退出程序？").setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (monitorService != null) {
						Log.d(LOG_TAG, "stop service");
						stopService(monitorService);
					}
					Log.d(LOG_TAG, "exit Emmagee");
					EmmageeService.closeOpenedStream();
					finish();
					System.exit(0);
				}
			}).setNegativeButton("取消", null).create();
		default:
			return null;
		}
	}

	/**
	 * customizing adapter.
	 * 
	 * @author andrewleo
	 */
	private class ListAdapter extends BaseAdapter {
		List<Programe> programe;
		int tempPosition = -1;

		/**
		 * save status of all installed processes
		 * 
		 * @author andrewleo
		 */
		class Viewholder {
			TextView txtAppName;
			ImageView imgViAppIcon;
			RadioButton rdoBtnApp;
		}

		public ListAdapter() {
			programe = processInfo.getRunningProcess(getBaseContext());
		}

		@Override
		public int getCount() {
			return programe.size();
		}

		@Override
		public Object getItem(int position) {
			return programe.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Viewholder holder = new Viewholder();
			final int i = position;
			convertView = MainPageActivity.this.getLayoutInflater().inflate(R.layout.list_item, null);
			holder.imgViAppIcon = (ImageView) convertView.findViewById(R.id.image);
			holder.txtAppName = (TextView) convertView.findViewById(R.id.text);
			holder.rdoBtnApp = (RadioButton) convertView.findViewById(R.id.rb);
			holder.rdoBtnApp.setId(position);
			holder.rdoBtnApp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						isRadioChecked = true;
						// Radio function
						if (tempPosition != -1) {
							RadioButton tempButton = (RadioButton) findViewById(tempPosition);
							if ((tempButton != null) && (tempPosition != i)) {
								tempButton.setChecked(false);
							}
						}

						tempPosition = buttonView.getId();
						packageName = programe.get(tempPosition).getPackageName();
						processName = programe.get(tempPosition).getProcessName();
					}
				}
			});
			if (tempPosition == position) {
				if (!holder.rdoBtnApp.isChecked())
					holder.rdoBtnApp.setChecked(true);
			}
			Programe pr = (Programe) programe.get(position);
			holder.imgViAppIcon.setImageDrawable(pr.getIcon());
			holder.txtAppName.setText(pr.getProcessName());
			return convertView;
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
