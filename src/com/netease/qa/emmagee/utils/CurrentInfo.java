package com.netease.qa.emmagee.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import android.os.Build;
import android.util.Log;

public class CurrentInfo {
	private static final String LOG_TAG = "Emmagee-CurrentInfo";

	public Long getValue() {
		File f = null;
		// htc desire hd / desire z?
		if (Build.MODEL.toLowerCase().contains("desire hd")
				|| Build.MODEL.toLowerCase().contains("desire z")) {
			f = new File("/sys/class/power_supply/battery/batt_current");
			if (f.exists())
				return getCurrentValue(f, false);
		}

		// sony ericsson xperia x1
		f = new File(
				"/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/ds2746-battery/current_now");
		if (f.exists())
			return getCurrentValue(f, false);
		// xdandroid
		/* if (Build.MODEL.equalsIgnoreCase("MSM")) { */
		f = new File(
				"/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/battery/current_now");
		if (f.exists())
			return getCurrentValue(f, false);
		/* } */
		// droid eris
		f = new File("/sys/class/power_supply/battery/smem_text");
		if (f.exists())
			return getSMemValue();
		// some htc devices
		f = new File("/sys/class/power_supply/battery/batt_current");
		if (f.exists())
			return getCurrentValue(f, false);
		// nexus one
		f = new File("/sys/class/power_supply/battery/current_now");
		if (f.exists())
			return getCurrentValue(f, true);
		// samsung galaxy vibrant
		f = new File("/sys/class/power_supply/battery/batt_chg_current");
		if (f.exists())
			return getCurrentValue(f, false);
		// sony ericsson x10
		f = new File("/sys/class/power_supply/battery/charger_current");
		if (f.exists())
			return getCurrentValue(f, false);
		return null;
	}

	public static Long getSMemValue() {
		boolean success = false;
		String text = null;
		try {
			FileReader fr = new FileReader(
					"/sys/class/power_supply/battery/smem_text");
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				if (line.contains("I_MBAT")) {
					text = line.substring(line.indexOf("I_MBAT: ") + 8);
					success = true;
					break;
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (Exception ex) {
			Log.e(LOG_TAG, ex.getMessage());
			ex.printStackTrace();
		}
		Long value = null;
		if (success) {
			try {
				value = Long.parseLong(text);
			} catch (NumberFormatException nfe) {
				Log.e(LOG_TAG, nfe.getMessage());
				value = null;

			}
		}
		return value;
	}

	public static Long getBattAttrValue(File f, String dischargeField,
			String chargeField) {
		String text = null;
		Long value = null;
		try {
			// @@@ debug
			// StringReader fr = new
			// StringReader("vref: 1248\r\nbatt_id: 3\r\nbatt_vol: 4068\r\nbatt_current: 0\r\nbatt_discharge_current: 123\r\nbatt_temperature: 329\r\nbatt_temp_protection:normal\r\nPd_M:0\r\nI_MBAT:-313\r\npercent_last(RP): 94\r\npercent_update: 71\r\nlevel: 71\r\nfirst_level: 100\r\nfull_level:100\r\ncapacity:1580\r\ncharging_source: USB\r\ncharging_enabled: Slow\r\n");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			final String chargeFieldHead = chargeField + ": ";
			final String dischargeFieldHead = dischargeField + ": ";
			while (line != null) {
				if (line.contains(chargeField)) {
					text = line.substring(line.indexOf(chargeFieldHead)
							+ chargeFieldHead.length());
					try {
						value = Long.parseLong(text);
						if (value != 0)
							break;
					} catch (NumberFormatException nfe) {
						Log.e(LOG_TAG, nfe.getMessage(), nfe);
					}
				}
				// "batt_discharge_current:"
				if (line.contains(dischargeField)) {
					text = line.substring(line.indexOf(dischargeFieldHead)
							+ dischargeFieldHead.length());
					try {
						value = (-1) * Math.abs(Long.parseLong(text));
					} catch (NumberFormatException nfe) {
						Log.e(LOG_TAG, nfe.getMessage(), nfe);
					}
					break;
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (Exception ex) {
			Log.e(LOG_TAG, ex.getMessage(), ex);
		}
		return value;
	}

	public Long getCurrentValue(File file, boolean convertToMillis) {
		String text = null;
		try {
			FileInputStream fs = new FileInputStream(file);
			DataInputStream ds = new DataInputStream(fs);
			text = ds.readLine();
			ds.close();
			fs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Long value = null;

		if (text != null) {
			try {
				value = Long.parseLong(text);
			} catch (NumberFormatException nfe) {
				value = null;
			}
			if (convertToMillis)
				value = value / 1000; // convert to milliampere
		}

		return value;
	}
}
