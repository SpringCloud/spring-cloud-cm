package com.springcloud.cmserver.mgmt.cm.ha;

import java.net.ServerSocket;


import org.apache.log4j.Category;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class Master extends Thread {
	
	protected static Category logger = Category.getInstance("socket");
	private int port;
	private String slaveIp;
	private int slavePort;

	public Master(int port, String slaveIp, int slavePort) {
		this.port = port;
		this.slaveIp = slaveIp;
		this.slavePort = slavePort;
		
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			ServerSocket server = null;
			try {
				server = new ServerSocket(port);
				logger.info("Master started!"+slavePort);
				while (true) {
					Socket socket = server.accept();
					String slaveAddr = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
					//int _slavePort = socket.getPort();
					
					if (slaveIp.equals(slaveAddr)) {
						logger.debug("Slave connect!");
						 com.springcloud.cmserver.mgmt.cm.ha.MasterHandler.getInstance(socket);
					} else {
						logger.error("Unknow client connect!");
						socket.close();
					}
				}
			} catch (IOException e) {
				logger.error("Master Exception! " + e.getMessage());
				try {
					server.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				server = null;
			}
		}
		logger.fatal("Master exit!");
	}

}
