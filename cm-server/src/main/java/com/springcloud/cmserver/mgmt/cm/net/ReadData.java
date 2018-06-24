package com.springcloud.cmserver.mgmt.cm.net;

import java.net.Socket;


public class ReadData {
	
	private Socket socket;
	
	private Long lastReadTime;
	
	private int loginPeriod;

	public Long getLastReadTime() {
		return lastReadTime;
	}

	public void setLastReadTime(Long lastReadTime) {
		this.lastReadTime = lastReadTime;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getLoginPeriod() {
		return loginPeriod;
	}

	public void setLoginPeriod(int loginPeriod) {
		this.loginPeriod = loginPeriod;
	}
}
