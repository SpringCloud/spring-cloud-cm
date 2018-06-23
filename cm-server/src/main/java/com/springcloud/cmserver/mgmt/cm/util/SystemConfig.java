package com.springcloud.cmserver.mgmt.cm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class SystemConfig {
	
	private static String configFilePathName = "/system-config.properties";
	private static String configRealPath = "/abp_home/ucm/config/system-config.properties";

	private static Properties prop;

	static {
		try {
			File f = new File(configRealPath);
			InputStream is = null;
			if (f.exists()) {
				is = new FileInputStream(configRealPath);
			} else {
				is = SystemConfig.class
						.getResourceAsStream(configFilePathName);
			}
				
			prop = new Properties();
			prop.load(is);
		} catch (Throwable e) {
			throw new RuntimeException("config-file cannot be load: "
					+ configFilePathName);
		}
	}

	public static List getValueList(String keyPrefix, String charset,
			String newCharset) {
		List list = new ArrayList();
		Iterator it = prop.keySet().iterator();
		String tempKey = null;
		String tempValue = null;
		while (it.hasNext()) {
			tempKey = (String) it.next();
			if (tempKey.startsWith(keyPrefix)) {
				try {
					tempValue = new String(getString(tempKey, "").getBytes(
							charset), newCharset);
				} catch (UnsupportedEncodingException e) {
					tempValue = "";
				}
				list.add(tempValue);
			}
		}
		return list;
	}

	public static Set getValueSet(String keyPrefix, String charset,
			String newCharset) {
		Set set = new HashSet();
		Iterator it = prop.keySet().iterator();
		String tempKey = null;
		String tempValue = null;
		while (it.hasNext()) {
			tempKey = (String) it.next();
			if (tempKey.startsWith(keyPrefix)) {
				try {
					tempValue = new String(getString(tempKey, "").getBytes(
							charset), newCharset);
				} catch (UnsupportedEncodingException e) {
					tempValue = "";
				}
				set.add(tempValue);
			}
		}
		return set;
	}

	public static String getString(String key) {
		return prop.getProperty(key);
	}

	public static String getString(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}

	public static boolean getBoolean(String key) {
		String tempValue = getString(key);
		return Boolean.valueOf(tempValue).booleanValue();
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		String tempValue = getString(key, "" + defaultValue);
		return Boolean.valueOf(tempValue).booleanValue();
	}

	public static double getDouble(String key) {
		String tempValue = getString(key);
		return Double.parseDouble(tempValue);
	}

	public static double getDouble(String key, double defaultValue) {
		String tempValue = getString(key, "" + defaultValue);
		return Double.parseDouble(tempValue);
	}

	public static float getFloat(String key) {
		String tempValue = getString(key);
		return Float.parseFloat(tempValue);
	}

	public static float getFloat(String key, float defaultValue) {
		String tempValue = getString(key, "" + defaultValue);
		return Float.parseFloat(tempValue);
	}

	public static int getInt(String key) {
		String tempValue = getString(key);
		return Integer.parseInt(tempValue);
	}

	public static int getInt(String key, int defaultValue) {
		String tempValue = getString(key, "" + defaultValue);
		return Integer.parseInt(tempValue);
	}

	public static long getLong(String key) {
		String tempValue = getString(key);
		return Long.parseLong(tempValue);
	}

	public static long getLong(String key, long defaultValue) {
		String tempValue = getString(key, "" + defaultValue);
		return Long.parseLong(tempValue);
	}
}
