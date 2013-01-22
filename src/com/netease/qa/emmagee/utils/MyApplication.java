package com.netease.qa.emmagee.utils;

import android.app.Application;
import android.view.WindowManager;

public class MyApplication extends Application {

	private static final String LOG_TAG = "Emmagee-"
			+ MyApplication.class.getSimpleName();
	
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}
}
