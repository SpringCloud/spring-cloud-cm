package com.springcloud.cmserver.mgmt.cm.socket;

import java.nio.channels.SocketChannel;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class ChangeRequest {
	public static final int REGISTER = 1;
	public static final int CHANGEOPS = 2;
	
	public SocketChannel socket;
	public int type;
	public int ops;
	
	
	public ChangeRequest(SocketChannel socket, int type, int ops) {
		this.socket = socket;
		this.type = type;
		this.ops = ops;
	}
}