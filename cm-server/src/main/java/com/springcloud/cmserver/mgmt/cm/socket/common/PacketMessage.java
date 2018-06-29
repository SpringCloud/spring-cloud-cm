package com.springcloud.cmserver.mgmt.cm.socket.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class PacketMessage {
	
	public static String PRIVATE_KEY = "_PRIVATE_KEY";

	private int packetLength;

	private int commandId;

	private int privateValue;

	private String msgBody;

	//private byte[] buf;

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public int getPacketLength() {
		byte[] msg = msgBody.getBytes();
		packetLength = 12 + msg.length;
		return packetLength;
	}

	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	public int getPrivateValue() {
		return privateValue;
	}

	public void setPrivateValue(int privateValue) {
		this.privateValue = privateValue;
	}
	
	public PacketMessage() {
		privateValue = 0;
		msgBody = "";
	}

	public PacketMessage(byte[] buf) {
		//this.buf = buf;
		packetLength = DataChangeUtils.changeByteToInt(buf, 0);
		commandId = DataChangeUtils.changeByteToInt(buf, 4);
		privateValue = DataChangeUtils.changeByteToInt(buf, 8);
		msgBody = DataChangeUtils.changeByteArrayToString(buf, 12,
				packetLength - 12);
				
	}

	public String getMsgBody() {
		return msgBody;
	}

	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
	
	public void resetCommandId(int commandId) {
		this.commandId = commandId;
	}
	
	public void resetPrivateValue(int privateValue){
		this.privateValue = privateValue;
	}
	
	public void resetMsgBody(String msgBody){
		/*byte[] msg = msgBody.getBytes();
		this.packetLength = 12 + msg.length;*/
		this.msgBody = msgBody;
		//this.buf = null;
	}

	private byte toByteArray()[] {
		try {			
			byte[] msg = string2byte(msgBody);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(DataChangeUtils
					.changeIntToByte(12 + msg.length));
			byteArrayOutputStream.write(DataChangeUtils
					.changeIntToByte(commandId)); // commandId
			byteArrayOutputStream.write(DataChangeUtils
					.changeIntToByte(privateValue)); // privateValue
			byteArrayOutputStream.write(msg); // msg
			byte []byteArray = byteArrayOutputStream.toByteArray();
			
			return byteArray;
		} catch (IOException e) {
			return null;
		} finally {
		}
	}
	
	public byte[] string2byte(String s) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream outputstream = new DataOutputStream(baos);
			outputstream.writeUTF(s);
			return baos.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public byte getByteArray()[] {
		return toByteArray();
	}
}
