package com.springcloud.cmserver.mgmt.cm.net;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ManageSocketPool {
	
	private static ManageSocketPool instance = null;
	
	private static ConcurrentMap<Integer,  com.springcloud.cmserver.mgmt.cm.net.ReadData> readSocketDataMap = new ConcurrentHashMap<Integer,  com.springcloud.cmserver.mgmt.cm.net.ReadData>();
	
	public synchronized static ManageSocketPool getInstance() {
		if (instance == null) {
			instance = new ManageSocketPool();
		}
		return instance;
	}
	
	public void put(int socket,  com.springcloud.cmserver.mgmt.cm.net.ReadData data) {
		readSocketDataMap.put(socket, data);
	}
	
	public  com.springcloud.cmserver.mgmt.cm.net.ReadData get(int socket) {
		return readSocketDataMap.get(socket);
	}
	
	public void setLastHeartBeat(int socket,long lastHeartBeatTime) {
		 com.springcloud.cmserver.mgmt.cm.net.ReadData data = get(socket);
		data.setLastReadTime(lastHeartBeatTime);
	}
	
	public int getClientSize() {
		return readSocketDataMap.size();
	}
	
	public void removeClient(int socket) {
		readSocketDataMap.remove(socket);
	}
	
	public Collection< com.springcloud.cmserver.mgmt.cm.net.ReadData> getReadDataConnection() {
		return readSocketDataMap.values();
	}
}
