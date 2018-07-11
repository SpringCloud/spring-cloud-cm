package com.springcloud.cmserver.mgmt.cm;

import com.springcloud.cmserver.mgmt.cm.ha.Master;
import com.springcloud.cmserver.mgmt.cm.ha.Slave;
import com.springcloud.cmserver.mgmt.cm.net.ManageHeartBeatThread;
import com.springcloud.cmserver.mgmt.cm.net.ManageServer;
import com.springcloud.cmserver.mgmt.cm.socket.ChannelPool;
import com.springcloud.cmserver.mgmt.cm.socket.EchoWorker;
import com.springcloud.cmserver.mgmt.cm.socket.NioServer;
import com.springcloud.cmserver.mgmt.cm.socket.Worker;
import com.springcloud.cmserver.mgmt.cm.util.SystemConfig;
import com.springcloud.cmserver.mgmt.cm.util.TaskUtility;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class Startup {
	
	private static Logger logger = LoggerFactory.getLogger(Startup.class);
	
	public static String startupTime = "";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int socketPort = SystemConfig.getInt("socketPort");
		String runMode = SystemConfig.getString("runMode");
		PropertyConfigurator.configure(SystemConfig.getString("log4jConfigLocation"));
		try {
			EchoWorker worker = new EchoWorker();
			new Thread(worker).start();
			NioServer.init(null, socketPort, worker);
			ChannelPool.createInstance();
			ChannelPool.setNioServer(NioServer.getInstance());
			logger.info("Socket start succeed! port : {}" , socketPort);
		} catch (IOException e) {
			logger.info("Socket start failed! err:{}" , e.getMessage());
		}
				
		int corePoolSize, maximumPoolSize, workQueue;
		corePoolSize = SystemConfig.getInt("corePoolSize", 5);
		maximumPoolSize = SystemConfig.getInt("maximumPoolSize", 20);
		workQueue = SystemConfig.getInt("workQueue", 10);
		//init thread pool
		TaskUtility.createInstance(corePoolSize, maximumPoolSize, workQueue);
		
		//start guard thread
		int guardPort = SystemConfig.getInt("guardPort");
		if (runMode != null && "master".equals(runMode)) {
			String cm_slave = SystemConfig.getString("cm_slave");
			if (cm_slave != null && !"".equals(cm_slave.trim())) {
				String[] slaveAddress = cm_slave.split(":");
				if (slaveAddress.length == 2) {
					Master master = new Master(guardPort,slaveAddress[0],Integer.parseInt(slaveAddress[1]));
					new Thread(master).start();
					logger.info("master thread start succeed! port : {}" , guardPort);
				}
			}
		} else {
			String cm_master = SystemConfig.getString("cm_master");
			if (cm_master != null && !"".equals(cm_master.trim())) {
				String[] masterAddress = cm_master.split(":");
				if (masterAddress.length == 2) {
					Slave slave = new Slave(masterAddress[0],guardPort);
					new Thread(slave).start();
					logger.info("slave thread start succeed!");
				}
			}
		}
		
		int managePort = SystemConfig.getInt("managePort");
		ManageServer t = new ManageServer(managePort,ctx);
		new Thread(t).start();
		
		ManageHeartBeatThread mhbt = new ManageHeartBeatThread();
		new Thread(mhbt).start();
		logger.info("ManageServer start succeed!port: {}" , managePort);
	}

}

