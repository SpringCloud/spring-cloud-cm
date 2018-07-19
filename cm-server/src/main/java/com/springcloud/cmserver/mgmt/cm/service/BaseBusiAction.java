package com.springcloud.cmserver.mgmt.cm.service;

import java.nio.channels.SocketChannel;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.springcloud.cmserver.mgmt.cm.socket.ChannelPool;
import com.springcloud.cmserver.mgmt.cm.util.JSONHelper;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public abstract class BaseBusiAction {
	
	private static Logger logger = LoggerFactory.getLogger(BaseBusiAction.class);

	protected ChannelPool pool = null;
	
	public abstract void perform(SocketChannel channel, JSONObject params, ActionResponse resp) throws Exception;
	
	public JSONObject execute(ApplicationContext context, JSONObject pars, SocketChannel channel) { 
		
		ActionResponse resp = new ActionResponse();
		try{
			perform(channel, pars, resp);
			if(resp.getDoneCode() != -1000){
				resp.setOutput("DONECODE", resp.getDoneCode());
			}else{
				resp.setOutput("DONECODE", 0);
			}
		}catch(Exception e){
			logger.error(getAnalysisOfException(e));
			try {
				resp.setOutput("DONECODE", -1);
				resp.setOutput("DONEMSG", e.getMessage());
			}catch (JSONException je) {
				logger.error("resp set out error! "  + je);
			}
		}
		if(resp.isReply()){
			return resp.getOutput();
		}else{
			return null;
		}
	}

	public static String getAnalysisOfException(Throwable e){
		StringBuffer sb = new StringBuffer();
		sb.append(e.getClass().getName()+": "+e.getMessage()+"\n");
		StackTraceElement[] elems = e.getStackTrace();
		int len = elems.length;
		StackTraceElement tempElem = null;
		for(int i = 0; i < len; i++){
			tempElem = elems[i];
			sb.append("\tat "+tempElem.getClassName()+"."+tempElem.getMethodName()+": ("+tempElem.getFileName()+": "+tempElem.getLineNumber()+")\n");
		}
		return sb.toString().trim();
	}
	
	protected void setInstanceChannel(String appInstanceId, String appId, SocketChannel socketChannel, String appType) {
		pool = ChannelPool.getInstance();
		pool.put(appInstanceId, appId, socketChannel, appType);
	}
	
	public class ActionResponse {
		private int doneCode;
		private JSONObject output;
		private boolean reply;
		
		public ActionResponse() {
			doneCode = -1000;
			output = new JSONObject();
			reply = true;
		}

		public int getDoneCode() {
			return doneCode;
		}

		public void setDoneCode(int doneCode) {
			this.doneCode = doneCode;
		}

		public JSONObject getOutput() {
			return output;
		}

		public void setOutput(JSONObject jObj) {
			if(output != null){
				try {
					if(jObj != null){
						JSONHelper.concat(output, jObj);
					}
				} catch (JSONException e) {
					output = jObj;
				}
			}else{
				output = jObj;
			}
		}
		
		public void setOutput(String name, Object obj) throws JSONException {
			if(output == null){
				output = new JSONObject();
			}
			try{
				output.put(name, obj);
			}catch(JSONException je){
				throw je;
			}
		}

		public boolean isReply() {
			return reply;
		}

		public void setReply(boolean reply) {
			this.reply = reply;
		}
	}
	
}


