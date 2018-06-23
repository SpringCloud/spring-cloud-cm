package com.springcloud.cmserver.mgmt.cm.net;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class ManageHeartBeatThread implements Runnable {
	private static  com.springcloud.cmserver.mgmt.cm.net.ManageSocketPool pool =  com.springcloud.cmserver.mgmt.cm.net.ManageSocketPool.getInstance();
	
	public void checkManageHeartBeat() {
		Collection< com.springcloud.cmserver.mgmt.cm.net.ReadData> cData = pool.getReadDataConnection();
		Iterator< com.springcloud.cmserver.mgmt.cm.net.ReadData> it = cData.iterator();
		while(it.hasNext()) {
			 com.springcloud.cmserver.mgmt.cm.net.ReadData rd = it.next();
			if ((System.currentTimeMillis() - rd.getLastReadTime())>10*60*1000) {
				try {
					if (rd.getSocket().isConnected()) {
						rd.getSocket().close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				pool.removeClient(rd.getSocket().hashCode());
			}
		}
	}
	
	@Override
	public void run() {
		while (true) {
			checkManageHeartBeat();
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
