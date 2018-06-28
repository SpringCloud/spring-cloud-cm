
package com.springcloud.cmserver.mgmt.cm.socket;

import java.nio.channels.SocketChannel;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class ServerDataEvent {
	public NioServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}