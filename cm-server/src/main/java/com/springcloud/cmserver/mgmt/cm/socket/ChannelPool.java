package com.springcloud.cmserver.mgmt.cm.socket;

import com.springcloud.cmserver.mgmt.cm.socket.common.Constants;
import com.springcloud.cmserver.mgmt.cm.socket.common.PacketMessage;
import com.springcloud.cmserver.mgmt.cm.util.AlarmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author HanSenJ
 * @email 28707699@qq.com
 */
public class ChannelPool extends Thread {

    private static Logger logger = LoggerFactory.getLogger(ChannelPool.class);

    private static ChannelPool instance = null;//new ChannelPool();
    private Hashtable<String, List<ChannelConnection>> pool = new Hashtable<String, List<ChannelConnection>>();
    private static com.springcloud.cmserver.mgmt.cm.socket.NioServer server = null;

    public static synchronized void createInstance() {
        instance = new ChannelPool();
        instance.start();
    }

    public static void setNioServer(com.springcloud.cmserver.mgmt.cm.socket.NioServer ser) {
        server = ser;
    }

    public static ChannelPool getInstance() {
        return instance;
    }


    public void put(String appInstanceId, String appId, SocketChannel channel, String appType) {
        ChannelConnection cc = new ChannelConnection(channel, appInstanceId, appType);
        List<ChannelConnection> channelList = pool.get(appId);
        if (channelList == null) {
            channelList = new ArrayList<ChannelConnection>();
            channelList.add(cc);
            pool.put(appId, channelList);
        } else {

            boolean exsit = false;
            for (ChannelConnection tcc : channelList) {
                if (tcc.instanceId.equals(appInstanceId)) {
                    exsit = true;
                }
            }

            if (!exsit) {
                channelList.add(cc);
            }
        }
    }

    public List<String> getInstanceList(String appId) {
        List<String> instanceList = new ArrayList<String>();
        List<ChannelConnection> channelList = pool.get(appId);
        if (channelList != null) {
            for (ChannelConnection cc : channelList) {
                instanceList.add(cc.instanceId);
            }
        }
        return instanceList;
    }

    public Set<String> getAppList() {
        return pool.keySet();
    }

    public List<SocketChannel> getChannelListByAppId(String appId) {
        List<SocketChannel> channelList = new ArrayList<SocketChannel>();
        if (appId != null) {
            List<ChannelConnection> allList = pool.get(appId);
            if (allList != null) {
                for (ChannelConnection cc : allList) {
                    channelList.add(cc.channel);
                }
            }
        }
        return channelList;
    }

    public boolean isConnected(String appId, String appInstanceId) {

        boolean isConnected = false;
        List<ChannelConnection> ccList = pool.get(appId);

        if (ccList == null) {
            isConnected = false;
        } else {
            for (ChannelConnection cc : ccList) {
                if (cc.instanceId.equals(appInstanceId)) {
                    isConnected = true;
                    break;
                }
            }
        }
        return isConnected;
    }

    public void clearChannelList() {

        Collection<List<ChannelConnection>> connectionListSet = pool.values();
        Iterator<List<ChannelConnection>> it = connectionListSet.iterator();
        while (it.hasNext()) {
            List<ChannelConnection> connectionList = it.next();
            for (ChannelConnection cc : connectionList) {
                try {
                    if (cc.channel.isConnected()) {
                        cc.channel.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        pool.clear();
    }

    public boolean checkIn(String appId, String instanceId) {
        List<ChannelConnection> ccList = pool.get(appId);
        if (ccList == null) {
            return false;
        } else {

            boolean exist = false;
            for (ChannelConnection cc : ccList) {
                if (cc.instanceId.equals(instanceId)) {
                    cc.heartCheckTime = System.currentTimeMillis();
                    exist = true;
                }
            }
            if (!exist) {
                return false;
            }

            return true;
        }
    }


    public boolean sendMsg(String appId, byte[] data) throws IOException {
        List<ChannelConnection> ccList = pool.get(appId);
        if (ccList == null) {
            return false;
        } else {
            for (ChannelConnection cc : ccList) {
                SocketChannel channel = cc.channel;
                if (channel.isConnected()) {
                    server.send(channel, data);
                }
            }
            return true;
        }
    }

    public void run() {
        logger.debug("ping thread started!");
        String id;
        PacketMessage pm = new PacketMessage();
        pm.setCommandId(Constants.CM_PING);
        pm.setMsgBody("");
        byte[] data = pm.getByteArray();
        String removeId;
        while (true) {
            try {
                sleep(30000);
                //logger.debug("begin ping!");
                removeId = "";
                Set<String> set = pool.keySet();
                Iterator<String> it = set.iterator();
                while (it.hasNext()) {
                    id = it.next();
                    List<ChannelConnection> ccList = pool.get(id);

                    for (int index = 0; index < ccList.size(); index++) {
                        ChannelConnection cc = ccList.get(index);
                        if (cc.channel.isConnected()) {
                            if ((System.currentTimeMillis() - cc.heartCheckTime) > Constants.PING_INTERVAL) {
                                server.send(cc.channel, data);
                            }
                        } else {
                            logger.debug("App instanceId : {} disconnect!", cc.instanceId);

                            if (id.startsWith("TS_")) {
                                //alarm("","Error",cc.instanceId);
                                AlarmUtil.alarm(cc.instanceId, "Error", cc.instanceId, "service.heartbeat", "DOWN");
                            }

                            if (cc != null && cc.channel != null) {
                                cc.channel.close();
                            }

                            ccList.remove(index);
                            index--;
                            removeId += ("," + cc.instanceId);
                        }
                    }
                }
                if (!removeId.equals("")) {
                    removeId = removeId.substring(1);
                    String[] rid = removeId.split(",");
                    String appInstanceId;
                    ApplicationContext context = com.springcloud.cmserver.mgmt.cm.socket.Worker.getContext();
                    for (int i = 0; i < rid.length; i++) {
                        appInstanceId = rid[i];
                      //  AppsService service = (AppsService) context.getBean("coreAppsService");
                        //String str = service.updateAppInstanceState(appInstanceId, Constants.APP_INSTANCE_STATE_OFF);
                        //logger.debug("updateAppInstanceState return : {}", str);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("err : " + e.getMessage());
            }
        }

    }


    public class ChannelConnection {
        long heartCheckTime;
        SocketChannel channel;
        String instanceId;
        String appType;

        public ChannelConnection(SocketChannel channel, String instanceId, String appType) {
            this.heartCheckTime = System.currentTimeMillis();
            this.channel = channel;
            this.appType = appType;
            this.instanceId = instanceId;
        }
    }

    public static void main(String[] args) {
        ChannelPool pool = new ChannelPool();
        //pool.alarm("", "", "");
    }
}
