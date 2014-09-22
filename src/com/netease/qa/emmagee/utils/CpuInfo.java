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
package com.netease.qa.emmagee.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import com.netease.qa.emmagee.service.EmmageeService;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.netease.qa.emmagee.R;

/**
 * operate CPU information
 * 
 * @author andrewleo
 */
public class CpuInfo {

	private static final String LOG_TAG = "Emmagee-" + CpuInfo.class.getSimpleName();

	private Context context;
	private long processCpu;
	private ArrayList<Long> idleCpu = new ArrayList<Long>();
	private ArrayList<Long> totalCpu = new ArrayList<Long>();
	private boolean isInitialStatics = true;
	private SimpleDateFormat formatterFile;
	private MemoryInfo mi;
	private long totalMemorySize;
	private long initialTraffic;
	private long lastestTraffic;
	private long traffic;
	private TrafficInfo trafficInfo;
	private ArrayList<String> cpuUsedRatio = new ArrayList<String>();
	private ArrayList<Long> totalCpu2 = new ArrayList<Long>();
	private long processCpu2;
	private ArrayList<Long> idleCpu2 = new ArrayList<Long>();
	private String processCpuRatio = "";
	private ArrayList<String> totalCpuRatio = new ArrayList<String>();
	private int pid;

	private static final String INTEL_CPU_NAME = "model name";
	private static final String CPU_DIR_PATH = "/sys/devices/system/cpu/";
	private static final String CPU_X86 = "x86";
	private static final String CPU_INFO_PATH = "/proc/cpuinfo";
	private static final String CPU_STAT = "/proc/stat";

	public CpuInfo(Context context, int pid, String uid) {
		this.pid = pid;
		this.context = context;
		trafficInfo = new TrafficInfo(uid);
		formatterFile = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mi = new MemoryInfo();
		totalMemorySize = mi.getTotalMemory();
		cpuUsedRatio = new ArrayList<String>();
	}

	/**
	 * read the status of CPU.
	 * 
	 * @throws FileNotFoundException
	 */
	public void readCpuStat() {
		String processPid = Integer.toString(pid);
		String cpuStatPath = "/proc/" + processPid + "/stat";
		try {
			// monitor cpu stat of certain process
			RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath, "r");
			String line = "";
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.setLength(0);
			while ((line = processCpuInfo.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}
			String[] tok = stringBuffer.toString().split(" ");
			processCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
			processCpuInfo.close();
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readTotalCpuStat();
	}

	/**
	 * read stat of each CPU cores
	 */
	private void readTotalCpuStat() {
		try {
			// monitor total and idle cpu stat of certain process
			RandomAccessFile cpuInfo = new RandomAccessFile(CPU_STAT, "r");
			String line = "";
			while ((null != (line = cpuInfo.readLine())) && line.startsWith("cpu")) {
				String[] toks = line.split("\\s+");
				idleCpu.add(Long.parseLong(toks[4]));
				totalCpu.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
						+ Long.parseLong(toks[6]) + Long.parseLong(toks[5]) + Long.parseLong(toks[7]));
			}
			cpuInfo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get CPU name.
	 * 
	 * @return CPU name
	 */
	public String getCpuName() {
		try {
			RandomAccessFile cpuStat = new RandomAccessFile(CPU_INFO_PATH, "r");
			// 需要判断是intel or arm
			if (Build.CPU_ABI.equalsIgnoreCase(CPU_X86)) {
				String line;
				while (null != (line = cpuStat.readLine())) {
					String[] values = line.split(":");
					if (values[0].contains(INTEL_CPU_NAME)) {
						cpuStat.close();
						return values[1];
					}
				}
			} else {
				String[] cpu = cpuStat.readLine().split(":"); // cpu信息的前一段是含有processor字符串，此处替换为不显示
				cpuStat.close();
				return cpu[1];
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "IOException: " + e.getMessage());
		}
		return "";
	}

	/**
	 * display directories naming with "cpu*"
	 * 
	 * @author andrewleo
	 */
	class CpuFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			// Check if filename matchs "cpu[0-9]"
			if (Pattern.matches("cpu[0-9]", pathname.getName())) {
				return true;
			}
			return false;
		}
	}

	/**
	 * get CPU core numbers
	 * 
	 * @return cpu core numbers
	 */
	public int getCpuNum() {
		try {
			// Get directory containing CPU info
			File dir = new File(CPU_DIR_PATH);
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			return files.length;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * get CPU core list
	 * 
	 * @return cpu core list
	 */
	public ArrayList<String> getCpuList() {
		ArrayList<String> cpuList = new ArrayList<String>();
		try {
			// Get directory containing CPU info
			File dir = new File(CPU_DIR_PATH);
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			for (int i = 0; i < files.length; i++) {
				cpuList.add(files[i].getName());
			}
			return cpuList;
		} catch (Exception e) {
			e.printStackTrace();
			cpuList.add("cpu0");
			return cpuList;
		}
	}

	/**
	 * reserve used ratio of process CPU and total CPU, meanwhile collect
	 * network traffic.
	 * 
	 * @return network traffic ,used ratio of process CPU and total CPU in
	 *         certain interval
	 */
	public ArrayList<String> getCpuRatioInfo(String totalBatt, String currentBatt, String temperature, String voltage) {

		DecimalFormat fomart = new DecimalFormat();
		fomart.setGroupingUsed(false);
		fomart.setMaximumFractionDigits(2);
		fomart.setMinimumFractionDigits(2);

		cpuUsedRatio.clear();
		idleCpu.clear();
		totalCpu.clear();
		totalCpuRatio.clear();
		readCpuStat();

		try {
			String mDateTime2;
			Calendar cal = Calendar.getInstance();
			if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"))) {
				mDateTime2 = formatterFile.format(cal.getTime().getTime() + 8 * 60 * 60 * 1000);
				totalBatt = "N/A";
				currentBatt = "N/A";
				temperature = "N/A";
				voltage = "N/A";
			} else
				mDateTime2 = formatterFile.format(cal.getTime().getTime());
			if (isInitialStatics) {
				initialTraffic = trafficInfo.getTrafficInfo();
				isInitialStatics = false;
			} else {
				lastestTraffic = trafficInfo.getTrafficInfo();
				if (initialTraffic == -1)
					traffic = -1;
				else
					traffic = (lastestTraffic - initialTraffic + 1023) / 1024;
				StringBuffer totalCpuBuffer = new StringBuffer();
				if (null != totalCpu2 && totalCpu2.size() > 0) {
					processCpuRatio = fomart.format(100 * ((double) (processCpu - processCpu2) / ((double) (totalCpu.get(0) - totalCpu2.get(0)))));
					for (int i = 0; i < (totalCpu.size() > totalCpu2.size() ? totalCpu2.size() : totalCpu.size()); i++) {
						String cpuRatio = "0.00";
						if (totalCpu.get(i) - totalCpu2.get(i) > 0) {
							cpuRatio = fomart
									.format(100 * ((double) ((totalCpu.get(i) - idleCpu.get(i)) - (totalCpu2.get(i) - idleCpu2.get(i))) / (double) (totalCpu
											.get(i) - totalCpu2.get(i))));
						}
						totalCpuRatio.add(cpuRatio);
						totalCpuBuffer.append(cpuRatio + ",");
					}
				} else {
					processCpuRatio = "0";
					totalCpuRatio.add("0");
					totalCpuBuffer.append("0,");
					totalCpu2 = (ArrayList<Long>) totalCpu.clone();
					processCpu2 = processCpu;
					idleCpu2 = (ArrayList<Long>) idleCpu.clone();
				}
				// 多核cpu的值写入csv文件中
				for (int i = 0; i < getCpuNum() - totalCpuRatio.size() + 1; i++) {
					totalCpuBuffer.append("0.00,");
				}
				long pidMemory = mi.getPidMemorySize(pid, context);
				String pMemory = fomart.format((double) pidMemory / 1024);
				long freeMemory = mi.getFreeMemorySize(context);
				String fMemory = fomart.format((double) freeMemory / 1024);
				String percent = context.getString(R.string.stat_error);
				if (totalMemorySize != 0) {
					percent = fomart.format(((double) pidMemory / (double) totalMemorySize) * 100);
				}

				if (isPositive(processCpuRatio) && isPositive(totalCpuRatio.get(0))) {
					// whether certain device supports traffic statics or not
					if (traffic == -1) {
						EmmageeService.bw.write(mDateTime2 + "," + pMemory + "," + percent + "," + fMemory + "," + processCpuRatio + ","
								+ totalCpuBuffer.toString() + "N/A" + "," + totalBatt + "," + currentBatt + "," + temperature + "," + voltage
								+ "\r\n");
					} else {
						EmmageeService.bw.write(mDateTime2 + "," + pMemory + "," + percent + "," + fMemory + "," + processCpuRatio + ","
								+ totalCpuBuffer.toString() + traffic + "," + totalBatt + "," + currentBatt + "," + temperature + "," + voltage
								+ "\r\n");
					}
					totalCpu2 = (ArrayList<Long>) totalCpu.clone();
					processCpu2 = processCpu;
					idleCpu2 = (ArrayList<Long>) idleCpu.clone();
					cpuUsedRatio.add(processCpuRatio);
					cpuUsedRatio.add(totalCpuRatio.get(0));
					cpuUsedRatio.add(String.valueOf(traffic));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cpuUsedRatio;
	}

	/**
	 * is text a positive number
	 * 
	 * @param text
	 * @return
	 */
	private boolean isPositive(String text) {
		Double num;
		try {
			num = Double.parseDouble(text);
		} catch (NumberFormatException e) {
			return false;
		}
		return num >= 0;
	}

}
