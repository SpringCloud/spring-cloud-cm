package com.springcloud.cmserver.mgmt.cm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AlarmUtil {

	private static Logger logger = LoggerFactory.getLogger(AlarmUtil.class);
	

    public static void alarm(String ip,String level, String appInstanceId,String alarmType,String desc) {
    	

        String collector =  com.springcloud.cmserver.mgmt.cm.util.SystemConfig.getString("alarm.collector");
        if(collector==null || collector.trim().length()==0)
                return;
        int port = 8890;
        String[] ss = collector.split(":");
        if (ss.length == 2)
                port = Integer.parseInt(ss[1]);
        long t = System.currentTimeMillis();
        Socket socket = null;
        PrintWriter pw = null;
        try {
                socket = new Socket(ss[0], port);
                pw = new PrintWriter(socket.getOutputStream());
                
                String alarmStr = ip + "^" + appInstanceId
                        + "^"+level+"^0^New^Main_Alert^0^" + t + "^" + t
                        + "^"+desc+"^"+alarmType;
                pw.write(alarmStr);
                pw.flush();
        } catch (UnknownHostException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();

        } finally {
        	try {
        		pw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
    }
	

	
}
