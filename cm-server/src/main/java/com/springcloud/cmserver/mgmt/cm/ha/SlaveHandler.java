package com.springcloud.cmserver.mgmt.cm.ha;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class SlaveHandler extends Thread {
	
	private static SlaveHandler instance = null;
	
	private boolean isRun = false;
	
	private Socket socket;
	
	private InputStream input = null;
	private OutputStream output = null;
	
	public SlaveHandler(String ip, int port) {		
		try {
			socket = new Socket(ip, port);
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			isRun = true;
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public synchronized static SlaveHandler getInstance(String ip, int port){
		if(instance == null){
			instance = new SlaveHandler(ip, port);
			instance.start();
		}
		return instance;
	}
	
	public static SlaveHandler getInstance(){
		return instance;
	}
	
	public boolean isRun(){
		return isRun;
	}
	
	public boolean send(byte []byteArray) {
		try{
			output.write(byteArray);
			output.flush();
			return true;
		}catch(IOException e){
			isRun = false;
			close();
			return false;
		}
	}
	
	/**
	 * Read input stream
	 * @throws IOException
	 */
	protected void read() throws IOException {
		int maxBufferLength = 256;
		byte[] buf = new byte[maxBufferLength];
		
		while (isRun) {
			input.read(buf);
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			read();
		} catch (IOException e) {
			isRun = false;
		} finally{
			close();
		}
	}

	public void close(){
		try {
			input.close();
			output.close();
			socket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		instance = null;
	}

}
