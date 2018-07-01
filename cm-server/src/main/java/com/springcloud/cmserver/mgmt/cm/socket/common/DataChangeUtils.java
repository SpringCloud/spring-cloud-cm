package com.springcloud.cmserver.mgmt.cm.socket.common;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class DataChangeUtils {

    /**
	 * int to byte
	 * 
	 * @param willChanged
	 */
	public static byte[] changeIntToByte(int willChanged) {
		byte buffer[] = new byte[4];
		buffer[0] = (byte) ((willChanged & 0xff000000) >>> 24);    
		buffer[1] = (byte) ((willChanged & 0x00ff0000) >>> 16);    
		buffer[2] = (byte) ((willChanged & 0x0000ff00) >>> 8);    
		buffer[3] = (byte) (willChanged & 0x000000ff);
		return buffer;
	}
	
	/**
	 * byte to int
	 * 
	 * @param a[4]
	 */
	public static int changeByteToInt(byte[] a){   
		int a0, a1, a2, a3, ret;    
		a0 = a[0]; a1 = a[1]; 
		a2 = a[2]; a3 = a[3];    

		if (a3 < 0){      
			a3 = a3 << 24;      
			a3 = a3 >>> 24;    
		}    
		if (a2 < 0){    
			a2 = a2 << 24;   
			a2 = a2 >>> 24;    
		}    
		if (a1 < 0){    
			a1 = a1 << 24;    
			a1 = a1 >>> 24;   
		}  
		
		ret = (a0 << 24 | a1 << 16 | a2 << 8 | a3);    
		return ret;   
	
	}
	
	public static int changeByteToInt(byte[] a,int begin){   
		int a0, a1, a2, a3, ret;    
		a0 = a[0+begin]; a1 = a[1+begin]; 
		a2 = a[2+begin]; a3 = a[3+begin];    
		
		if (a3 < 0){      
			a3 = a3 << 24;      
			a3 = a3 >>> 24;    
		}    
		if (a2 < 0){    
			a2 = a2 << 24;   
			a2 = a2 >>> 24;    
		}    
		if (a1 < 0){    
			a1 = a1 << 24;    
			a1 = a1 >>> 24;   
		}  
		
		ret = (a0 << 24 | a1 << 16 | a2 << 8 | a3);    
		return ret;   
	
	}

	/**
	 * string to byte
	 * 
	 * @param s
	 * @param length
	 * @return
	 */
	public static byte[] fixLengthStrToBytes(String s, int length) {
		byte[] b = new byte[length];
		if (s == null) {
			return b;
		}
		byte[] temp = s.getBytes();
		for (int k = 0; k < temp.length; k++) {
			b[k] = temp[k];
		}
		return b;
	}

	/**
	 * package fix length string
	 * 
	 * @param s
	 * @param length
	 * @return
	 */
	public static String fixLengthStr(String s, int length) {
		byte[] b = new byte[length];
		if (s == null) {
			return new String(b);
		}
		byte[] temp = s.getBytes();
		for (int k = 0; k < temp.length; k++) {
			b[k] = temp[k];
		}
		return new String(b);
	}
	
	/**
	 * byte array to string
	 * 
	 * @param a[4]
	 */
	public static String changeByteArrayToString(byte[] b,int offset,int length){
	    //return (new String(b,offset,length));
		return byte2string(b, offset, length);
	}
	
	public static String byte2string(byte[] b, int offset, int len) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(b, offset, len);
			DataInputStream inputstream = new DataInputStream(bais);
			return inputstream.readUTF();
		} catch (IOException e) {
			return null;
		}
	}
}