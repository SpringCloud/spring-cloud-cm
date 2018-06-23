package com.springcloud.cmserver.mgmt.cm.util;

import com.springcloud.cmserver.mgmt.cm.socket.common.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallTechServiceRestUtil {	
	
	private static final int CONNECTION_TIMEOUT = 30000;
	
	public static String getOrdersFromTechService(String serviceURL) throws IOException{
		String result = "";
		URL url = new URL(serviceURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setDoOutput(true);
		httpConn.setUseCaches(false);
		httpConn.setRequestProperty("APPID", "mgmt-cm");
		httpConn.setRequestProperty("APPGROUPID", "mgmt");
		httpConn.setRequestProperty("APPINSTANCEID", "MGMT-CM");
		httpConn.setRequestProperty("TOKEN", Constants.TOKEN);
		httpConn.setRequestProperty("Cache-Control","no-cache"); 
		httpConn.setConnectTimeout(CONNECTION_TIMEOUT);
		httpConn.setReadTimeout(CONNECTION_TIMEOUT);
		httpConn.connect();
		int responseCode = httpConn.getResponseCode();
		InputStream input;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			input = httpConn.getInputStream();
		} else {
			input = httpConn.getErrorStream();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(input, "utf-8"));
		result = br.readLine();
		br.close();
		httpConn.disconnect();
		
		return result;
	}
	


}
