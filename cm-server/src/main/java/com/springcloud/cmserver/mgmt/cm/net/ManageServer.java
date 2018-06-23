package com.springcloud.cmserver.mgmt.cm.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class ManageServer extends Thread {
	protected static Logger logger = LoggerFactory.getLogger(ManageServer.class);
	private int port;
	private static ApplicationContext context;
	public ManageServer(int port,ApplicationContext context) {
		this.port = port;
		this.context = context;
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			ServerSocket server = null;
			try {
				server = new ServerSocket(port);
				while (true) {
					Socket socket = server.accept();
					ManageHandler handler = new ManageHandler(socket,context);
					new Thread(handler).start();
				}
			} catch (IOException e) {
				try {
					server.close();
				} catch (IOException ioe) {
					logger.error("manage server run error",ioe.getMessage());
				}
				server = null;
			}
		}
	}
}
