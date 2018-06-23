package com.springcloud.cmserver.mgmt.cm.ha;

import org.apache.log4j.Category;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class MasterHandler extends Thread {
	
	protected static final Category logger = Category.getInstance("socket");
	
	boolean isRun = true;
	
	Socket socket;
	
	InputStream input = null;
	OutputStream output = null;
	

	
	private static MasterHandler instance = null;
	
	public MasterHandler(Socket s) {
		socket = s;
		
		try {
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);
			input = s.getInputStream();
			output = s.getOutputStream();
		} catch (IOException e) {
			logger.error("MasterHandler constructor error! And error is " + e.getMessage());
		}
	}
	
	public synchronized static MasterHandler getInstance(Socket s){
		if(instance == null){
			instance = new MasterHandler(s);
			instance.start();
		}
		return instance;
	}
	
	public static MasterHandler getInstance(){
		return instance;
	}
	
	public boolean send(){
		try{
			output.write("ping".getBytes());
			output.flush();
			//String socketAddr = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress() + "|" + socket.getPort();
			logger.debug("Ping Slave");
			return true;
		}catch(IOException e){
			logger.debug("Ping Slave error " + " And error is " + e.getMessage());
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
		int maxBufferLength = 256;
		byte[] buf = new byte[maxBufferLength];
		while (isRun) {
			input.read(buf);
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
		instance = null;
	}
	
}
