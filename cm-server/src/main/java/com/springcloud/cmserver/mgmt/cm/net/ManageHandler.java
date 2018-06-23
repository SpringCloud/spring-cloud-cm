package com.springcloud.cmserver.mgmt.cm.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.net.Socket;



public class ManageHandler implements Runnable {

	protected static final Logger logger = LoggerFactory.getLogger(ManageHandler.class);
	
	boolean isRun = true;
	Socket socket;
	InputStream input = null;
	OutputStream output = null;
	 com.springcloud.cmserver.mgmt.cm.net.ManageSocketPool pool = null;
	
	private ApplicationContext context;
	
	public ManageHandler(Socket s,ApplicationContext context) {
		socket = s;
		try {
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);
			input = s.getInputStream();
			output = s.getOutputStream();
			pool =  com.springcloud.cmserver.mgmt.cm.net.ManageSocketPool.getInstance();
			this.context = context;
		} catch (IOException e) {
			logger.error("ManageHandler constructor error! And error is " + e.getMessage());
		}
	}
	
	public boolean send(byte[] data){
		try{
			output.write(data);
			output.flush();
			return true;
		}catch(IOException e){
			isRun = false;
			close();
			return false;
		}
	}
	
	/**
	 * process read
	 * @throws IOException
	 */
	protected void read() throws IOException {
		while (isRun) {
			 com.springcloud.cmserver.mgmt.cm.net.ReadData data = null;
			if (pool.get(socket.hashCode()) == null) {
				data = new  com.springcloud.cmserver.mgmt.cm.net.ReadData();
				data.setLastReadTime(System.currentTimeMillis());
				data.setSocket(socket);
				data.setLoginPeriod(0);
				pool.put(socket.hashCode(), data);
				send("Welcome to CM server,man~!\r\nlogin as:".getBytes());
			} else {
				data = pool.get(socket.hashCode());
				BufferedReader buf = new BufferedReader(new InputStreamReader(input)); 
				String inputStr = buf.readLine();
				if (inputStr == null) {
					isRun = false;
					socket.close();
					pool.removeClient(socket.hashCode());
				} else {
					/* com.springcloud.cmserver.mgmt.cm.net.ManageWorker worker = ( com.springcloud.cmserver.mgmt.cm.net.ManageWorker)context.getBean("manageWorker");
					String result = worker.doWork(inputStr, data);
					send(result.getBytes());*/
				}
			}
		}
	}
	
	public void run(){
		try {
			read();
		} catch (IOException e) {
			logger.error("Read error! And error is " + e.getMessage());
			isRun = false;
		} finally{
			close();
		}
	}
	
	public void close(){
		//instance = null;
	}
}
