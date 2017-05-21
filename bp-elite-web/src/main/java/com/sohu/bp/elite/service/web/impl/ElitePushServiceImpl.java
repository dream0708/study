package com.sohu.bp.elite.service.web.impl;

import java.util.Collection;
import java.util.Date;

import com.sohu.bp.elite.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.service.web.ElitePushService;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.elite.socketio.SquareEventObject;

import net.sf.json.JSONObject;
import scala.reflect.generic.Trees.New;

public class ElitePushServiceImpl implements ElitePushService {
	private static final Logger log = LoggerFactory.getLogger(ElitePushServiceImpl.class);
	private SquareService squareService;
	private static SocketIOServer server;
	private String host = "0.0.0.0";
	private int port = 9093;
	private int sleepTime = 4000;
	private int retryTimes = 20;
	public void setSquareService(SquareService squareService){
		this.squareService = squareService;
		
	}

	public void init() {
		
		int retryCount = 0;
		while(!NetUtil.isPortAvailable(port) && retryCount++ <= retryTimes){
			log.info("binding netty-socketio server failed, try again in {} s", new Object[]{sleepTime});
			try {
				Thread.sleep(sleepTime);
			}catch (Exception e){
				log.error("", e);
			}
		}
		if(!NetUtil.isPortAvailable(port)){
			log.error("init netty-socketio server failed!");
			return;
		}
		
		Configuration config = new Configuration();
		config.setHostname(host);
		config.setPort(port);
		server = new SocketIOServer(config);
		log.info("start netty-socketio server succeed!");
		server.addConnectListener(new ConnectListener() {
			
			@Override
			public void onConnect(SocketIOClient client) {
				log.info("client {} connect to server via websocket", new Object[]{client.getSessionId().toString()});
			}
		});
		server.addDisconnectListener(new DisconnectListener() {
			
			@Override
			public void onDisconnect(SocketIOClient client) {
				log.info("client {} disconnect from server via websocket", new Object[]{client.getSessionId().toString()});
			}
		});
		server.addEventListener("squareUnreadEvent", SquareEventObject.class, new DataListener<SquareEventObject>() {
			@Override
			public void onData(SocketIOClient client, SquareEventObject data, AckRequest ackRequest) throws Exception {
				if (client.isChannelOpen()) {
					Long latest = data.getLatestTime();
					if (null == latest || latest <= 0 ) latest = new Date().getTime();
					log.info("client {} send Data {} to server", new Object[]{client.getSessionId().toString(), latest});
					client.set("latestTime", latest);
					client.set("latestNum", 0l);
				}
			}
		});
		server.start();
	}
	public void destroy(){
		server.stop();
		log.info("******** netty-socketio server stop *******");
	}
	@Override
	public void squareUnreadPush() {
			Collection<SocketIOClient> clients = server.getAllClients();
			for(SocketIOClient client : clients){
				if (client.isChannelOpen()){
					Long latestTime = (Long) client.get("latestTime");
					Long latestNum = (Long) client.get("latestNum");
					if (null == latestTime) {
						latestTime = new Date().getTime();
						client.set("latestTime", latestTime);
					}
					if (null == latestNum) {
						latestNum = 0l;
						client.set("latestNum", latestNum);
					}
					Long num = squareService.getNewEliteNum(latestTime);
					if (num != latestNum) {
						client.set("latestNum", num);
						JSONObject data = new JSONObject();
						data.put("num", num);
						client.sendEvent(Constants.PUSH_SQUARE_UNREAD_EVENT, data);
					}
				} 
			}
	}
}
