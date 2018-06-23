package com.springcloud.cmserver.mgmt.cm.ha;

import  com.springcloud.cmserver.mgmt.cm.socket.ChannelPool;
import  com.springcloud.cmserver.mgmt.cm.socket.NioServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class Slave extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(Slave.class);
	
	private final static int MAX_CONNECT_MASTER_COUNT = 3;
	
	private String masterIp;
	private int masterPort;
	
	private  com.springcloud.cmserver.mgmt.cm.ha.SlaveHandler handler;
	
	public Slave(String masterIp, int masterPort) {
		// TODO Auto-generated constructor stub
		this.masterIp = masterIp;
		this.masterPort = masterPort;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler =  com.springcloud.cmserver.mgmt.cm.ha.SlaveHandler.getInstance(masterIp, masterPort);
			NioServer slaveServer = NioServer.getInstance();
			if (handler != null && handler.isRun()) {
				if (slaveServer.acceptRequest) {
					slaveServer.acceptRequest = false;
				}
				handler.send("ping".getBytes());
			} else {
				int tryCount = 0;
				while (true) {
					if (tryCount > MAX_CONNECT_MASTER_COUNT) {
					} else {
						tryCount ++;
					}
					handler =  com.springcloud.cmserver.mgmt.cm.ha.SlaveHandler.getInstance(masterIp, masterPort);
					if (handler != null && handler.isRun()) {
						if (slaveServer.acceptRequest) {
							slaveServer.acceptRequest = false;
							ChannelPool.getInstance().clearChannelList();
						}
						break;
					}
					if (!slaveServer.acceptRequest) {
						slaveServer.acceptRequest = true;
					}
					
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
