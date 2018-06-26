package com.springcloud.cmserver.mgmt.cm.socket;

import com.springcloud.cmserver.mgmt.cm.socket.common.Constants;

import java.nio.channels.SocketChannel;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class PingThread implements Runnable {

	private NioServer server;
	private SocketChannel channel;
	
	public PingThread(NioServer server){
		this.server = server;
	}
	
	public PingThread(NioServer server, SocketChannel channel){
		this.server = server;
		this.channel = channel;
	}
	
	public void run(){
		try{
			while(true){
				Thread.sleep(Constants.PING_INTERVAL);
				server.send(channel, "ping".getBytes());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
