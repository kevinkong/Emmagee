package com.netease.qa.emmagee.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

public class TrafficInfo {

	private static final String LOG_TAG = "Emmagee-"
			+ TrafficInfo.class.getSimpleName();
	
	private String uid;
	
	public TrafficInfo(String uid){
		this.uid = uid;
	}

	/**
	 * get traffic information include received and send traffic
	 * 
	 * @return total traffic include received and send traffic
	 */
	public long getTrafficInfo() {
		Log.i(LOG_TAG,"get traffic information");
		String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
		String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
		long rcvTraffic = -1;
		long sndTraffic = -1;
		try {
			RandomAccessFile raf_r = new RandomAccessFile(rcvPath, "r");
			RandomAccessFile raf_s = new RandomAccessFile(sndPath, "r");
			rcvTraffic = Long.parseLong(raf_r.readLine());
			sndTraffic = Long.parseLong(raf_s.readLine());
		} catch (FileNotFoundException e) {
			rcvTraffic = -1;
			sndTraffic = -1;
		} catch (NumberFormatException e) {
			Log.e(LOG_TAG, "NumberFormatException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}
		if (rcvTraffic == -1 || sndTraffic == -1) {
			return -1;
		} else
			return (rcvTraffic + sndTraffic);
		/*
		 * traf_r = TrafficStats.getUidRxBytes(ActivityMain.uid); traf_s =
		 * TrafficStats.getUidTxBytes(ActivityMain.uid);
		 */
	}
}
