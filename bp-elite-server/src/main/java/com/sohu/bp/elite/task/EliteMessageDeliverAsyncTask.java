package com.sohu.bp.elite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.elite.service.UserInfoService;
import com.sohu.bp.elite.util.SpringUtil;

/**
 * 
 * @author nicholastang
 * 2016-10-17 19:20:03
 * TODO
 */
public class EliteMessageDeliverAsyncTask extends EliteAsyncTask
{
	private static final Logger logger = LoggerFactory.getLogger(EliteMessageDeliverAsyncTask.class);
	private static NotifyService notifyService;
	private static UserInfoService userInfoService;

	private TEliteMessagePushType messageType;
	private Long receiverId;
	private TEliteMessageData messageData;
	private TEliteMessageStrategy strategy;


	public EliteMessageDeliverAsyncTask(TEliteMessagePushType messageType, Long receiverId, TEliteMessageData messageData, TEliteMessageStrategy strategy) {
		notifyService = (NotifyService)SpringUtil.getBean("notifyService");
		userInfoService = (UserInfoService)SpringUtil.getBean("userInfoService");

		this.messageType = messageType;
		this.receiverId = receiverId;
		this.messageData = messageData;
		this.strategy = strategy;
	}
	
	@Override
	public void run() {
		if (null == receiverId || null == messageType ||receiverId <= 0) {
			logger.error("parameters not enough to send message");
			return;
		}
		
		boolean flag = notifyService.postMessage(receiverId, messageType, messageData, strategy);
		logger.info("send message to bpId = {}, messageType = {}, strategy = {}, messageData = {}, result = {}", new Object[]{receiverId, messageType, strategy, messageData, flag});
	}
	

	
}