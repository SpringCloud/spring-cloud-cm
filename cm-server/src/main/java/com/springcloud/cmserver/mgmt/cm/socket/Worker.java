package com.springcloud.cmserver.mgmt.cm.socket;

import com.springcloud.cmserver.mgmt.cm.socket.common.Constants;
import com.springcloud.cmserver.mgmt.cm.socket.common.PacketMessage;
import com.springcloud.cmserver.mgmt.cm.util.JSONHelper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class Worker extends Thread {

    private static Logger logger = LoggerFactory.getLogger(Worker.class);

    private ServerDataEvent serverData;

    public Worker(ServerDataEvent serverData) {
        this.serverData = serverData;
    }

   

    public void run() {
        PacketMessage recvPacket = new PacketMessage(serverData.data);
        PacketMessage sendPacket = null;
        JSONObject params = null;
        JSONObject result = null;
        String busiName = null;
        try {
            String msgBody = recvPacket.getMsgBody();
            params = JSONHelper.string2JSONObject(msgBody);
            params.put(Constants.PACKET_MESSAGE_COMMANDID, recvPacket.getCommandId());
            params.put(Constants.PACKET_MESSAGE_PRIVATE, recvPacket.getPrivateValue());

            busiName = recvPacket.getCommandId() + "";


            if (!String.valueOf(Constants.AGENT_HEART_CHECK).equals(busiName)) {
                logger.debug("Receive : busiName: {} msgBody: {}", new Object[]{busiName, msgBody});
            }

         //business service
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            recvPacket.resetMsgBody(result.toString());
            sendPacket = recvPacket;
            if (sendPacket != null) {
                serverData.server.send(serverData.socket, sendPacket.getByteArray());
            }
            if (!String.valueOf(Constants.AGENT_HEART_CHECK).equals(busiName)) {
                logger.debug("Send : busiName:" + sendPacket.getCommandId() + " msgBody:" + sendPacket.getMsgBody());
            }
        }

    }

}
