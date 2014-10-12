package com.netease.qa.emmagee.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Settings {
    
    public static final String KEY_SENDER = "sender";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_RECIPIENTS = "recipients";
    public static final String KEY_SMTP = "smtp";
    public static final String KEY_ISFLOAT = "isfloat";
    public static final String KEY_INTERVAL = "interval";
    public static SharedPreferences getDefaultSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
}
