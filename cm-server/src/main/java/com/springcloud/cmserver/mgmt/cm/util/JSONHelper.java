package com.springcloud.cmserver.mgmt.cm.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONHelper {

	/**
	 * Convert java.sql.ResultSet object to JSONArray object
	 * 
	 * @param rs
	 *            java.sql.ResultSet object to be converted
	 * @param maxrow
	 *            maximum number of rows to log the memoryresultset to logger
	 * @return MemoryResultSet containing the transformed rows from ResultSet
	 *         object
	 * @throws SQLException
	 */	
	public static JSONArray sqlResultSet2JSONArray(ResultSet rs)
			throws JSONException, SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		JSONArray array = new JSONArray();
		JSONObject o = null;
		int colCount = rsMeta.getColumnCount();
		String colName = null;
		String colValue = null;
		while (rs.next()) {
			o = new JSONObject();
			for (int i = 0; i < colCount; i++) {
				colName = rsMeta.getColumnName(i + 1);
				colValue = rs.getString(colName);
				o.put(colName, colValue != null ? colValue : "");
			}
			array.put(o);
		}
		rs.close();
		return array;
	}
	
	/**
	 * Convert JSONObject to String
	 * @param o
	 * @return
	 * @throws JSONException
	 */
	public static String jsonObject2String(JSONObject o) throws JSONException {
		if(o == null){
			throw new JSONException("null exception! input parameter JSONObject is null!");
		}else{
			return o.toString();
		}
	}
	
	/**
	 * Convert String to JSONObject
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject string2JSONObject(String jsonString) throws JSONException {
		return new JSONObject(jsonString);
	}
	
	private static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * concat subJSONObject to mainJSONObject
	 * @param mainJSONObject
	 * @param subJSONObject
	 * @throws JSONException
	 */
	public static void concat(JSONObject mainJSONObject, JSONObject subJSONObject) throws JSONException {
		try{
			Iterator it = subJSONObject.keys();
			String key;
			while(it.hasNext()){
				key = (String)it.next();
				mainJSONObject.put(key, subJSONObject.get(key));
			}
		}catch(Exception e){
			throw new JSONException(e);
		}
	}
	
	public static JSONArray concat(JSONArray array1, JSONArray array2) throws JSONException {
		JSONArray array = new JSONArray();
		
		try{
			for(int i=0;i<array1.length();i++){
				array.put(array1.get(i));
			}
			for(int i=0;i<array2.length();i++){
				array.put(array2.get(i));
			}
		}catch(Exception e){
			throw new JSONException(e);
		}
		return array;
	}
	
	/**
	 * get value from jsonObject and when error will give a default value
	 * @param jObj
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(JSONObject jObj, String key, String defaultValue){
		String str;
		try{
			str = jObj.getString(key);
			if(str.equals(""))
				str = defaultValue;
		}catch(Exception e){
			str = defaultValue;
		}
		return str;
	}
	
	public static String getString(JSONObject jObj, String key, String defaultValue){
		String str;
		try{
			str = jObj.getString(key);
			if(str.equals(""))
				str = defaultValue;
		}catch(Exception e){
			str = defaultValue;
		}
		return str;
	}
	
	public static JSONObject getJSONObject(JSONObject jObj, String key, JSONObject defaultValue){
		JSONObject obj;
		try{
			obj = jObj.getJSONObject(key);
		}catch(Exception e){
			obj = defaultValue;
		}
		return obj;
	}
	
	public static JSONObject getJSONObject(JSONArray jAry, int index, JSONObject defaultValue){
		JSONObject obj;
		try{
			obj = jAry.getJSONObject(index);
		}catch(Exception e){
			obj = defaultValue;
		}
		return obj;
	}
	
	public static JSONArray getJSONArray(JSONObject jObj, String key, JSONArray defaultValue){
		JSONArray array;
		try{
			array = jObj.getJSONArray(key);
		}catch(Exception e){
			array = defaultValue;
		}
		return array;
	}
	
	public static long getLong(JSONObject jObj, String key, long defaultValue){
		long str;
		try{
			str = jObj.getLong(key);
		}catch(Exception e){
			str = defaultValue;
		}
		return str;
	}
	public static int getInt(JSONObject jObj, String key, int defaultValue){
		int str;
		try{
			str = jObj.getInt(key);
		}catch(Exception e){
			str = defaultValue;
		}
		return str;
	}
	public static double getDouble(JSONObject jObj, String key, double defaultValue){
		double str;
		try{
			str = jObj.getDouble(key);
		}catch(Exception e){
			str = defaultValue;
		}
		return str;
	}
	public static boolean getBoolean(JSONObject jObj, String key, boolean defaultValue){
		boolean str;
		try{
			str = jObj.getBoolean(key);
		}catch(Exception e){
			str = defaultValue;
		}
		return str;
	}
	
	public static void put(JSONObject jObj, String key, Object value){
		if(jObj == null)
			jObj = new JSONObject();
		try{
			jObj.put(key, value);
		}catch(JSONException je){}
	}
	
	public static void put(JSONObject jObj, String key, int value){
		if(jObj == null)
			jObj = new JSONObject();
		try{
			jObj.put(key, value);
		}catch(JSONException je){}
	}
	
	public static void put(JSONObject jObj, String key, String value){
		if(jObj == null)
			jObj = new JSONObject();
		try{
			jObj.put(key, value);
		}catch(JSONException je){}
	}
	
	public static void put(JSONObject jObj, String key, boolean value){
		if(jObj == null)
			jObj = new JSONObject();
		try{
			jObj.put(key, value);
		}catch(JSONException je){}
	}
	
	public static void put(JSONObject jObj, String key, long value){
		if(jObj == null)
			jObj = new JSONObject();
		try{
			jObj.put(key, value);
		}catch(JSONException je){}
	}
	
	public static void put(JSONObject jObj, String key, double value){
		if(jObj == null)
			jObj = new JSONObject();
		try{
			jObj.put(key, value);
		}catch(JSONException je){}
	}
	
	public static void main(String []args){
		/*try{
			JSONObject o1 = new JSONObject();
			o1.put("t1", "test1");
			JSONObject o2 = new JSONObject();
			o2.put("t2", "test2");
			concat(o1, o2);
			Iterator it = o1.keys();
			while(it.hasNext()){
				System.out.println((String)it.next());
			}
			System.out.println(jsonObject2String(o1));
		}catch(JSONException e){
			e.printStackTrace();
		}*/
		try{
			String str = JSONHelper.getString(null, "str", "string");
			System.out.println("str = " + str);
		}catch(Exception e){
			System.out.println("===> " + e.getMessage());
		}
	}

}
