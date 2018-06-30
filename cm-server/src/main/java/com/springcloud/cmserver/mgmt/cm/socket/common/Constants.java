package com.springcloud.cmserver.mgmt.cm.socket.common;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class Constants {
	
	public final static String PACKET_MESSAGE_PRIVATE = "_PACKET_MESSAGE_PRIVATE";
	public final static String PACKET_MESSAGE_COMMANDID = "_PACKET_MESSAGE_COMMANDID";
	
	public final static int PING_INTERVAL = 2*60*1000;
	
	public final static int AGENT_INIT = 1001;
	public final static int AGENT_HEART_CHECK = 1002;
	public final static int AGENT_APP_QUERY_CONFIG = 1003;//COMMAND_CLIENT_AMQUERYCONFIG
	public final static int AGENT_TECHSERVICE_QUERY_CONFIG = 1004;
	public final static int AGENT_TECHSERVICE_CONFIG_CHANGE = 1009;
	
	public final static int CM_PING = 2001;
	public final static int CM_TECHSERVICE_CONFIG_CHANGE = 2002;
	public final static int CM_STUDIO_TECHSERVICE_CONFIG_CHANGE = 2005;
	public final static int CM_STUDIO_APP_SUB_TS = 2006;
	
	public final static String APP_INSTANCE_STATE_ON = "on";
	public final static String APP_INSTANCE_STATE_OFF = "off";
	
	public final static String APP_GET_CONF_ENVIRONMENT = "environment";
	public final static String APP_GET_CONF_MANAGEMENT = "management";
	
	public final static String TECH_SERVICE = "TS";
	public final static String BUSINESS_SERVICE = "BS";
	public final static String BUSINESS_MODUE = "BM";
	public final static String TOKEN = "TEST-TOKEN";
	
	public final static String QUERY_APP_ALL = "ALL";//GET ALL APP LIST;
	public final static String QUERY_APP_ORDER = "ORDER";//GET ORDER APP.

}
