
package com.springcloud.cmserver.mgmt.cm.socket;

import  com.springcloud.cmserver.mgmt.cm.util.TaskUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class EchoWorker implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(EchoWorker.class);
	
	private List< com.springcloud.cmserver.mgmt.cm.socket.ServerDataEvent> queue = new LinkedList< com.springcloud.cmserver.mgmt.cm.socket.ServerDataEvent>();
	
	private TaskUtility task = TaskUtility.getInstance();
	
	/**
	 * Deal received data by channel
	 * @param server
	 * @param socket
	 * @param data
	 * @param count
	 */
	public void processData( com.springcloud.cmserver.mgmt.cm.socket.NioServer server, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized(queue) {
			queue.add(new  com.springcloud.cmserver.mgmt.cm.socket.ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}		
		
	}
	
	public void run() {
		 com.springcloud.cmserver.mgmt.cm.socket.ServerDataEvent dataEvent;
		
		while(true) {
			// Wait for data to become available
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					}
				}
				dataEvent = ( com.springcloud.cmserver.mgmt.cm.socket.ServerDataEvent) queue.remove(0);
			}
			task.execute(new Worker(dataEvent));
			// Return to sender
			///dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}
