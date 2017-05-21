package com.sohu.bp.elite.service;

import java.util.Map;

import com.sohu.bp.elite.bean.InboxMessageBean;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;

/**
 * 
 * @author nicholastang
 * 2016-08-27 20:38:34
 * TODO 通知服务，汇集所有的通知服务
 */
public interface NotifyService
{
	/**
	 * 给统计组发消息
	 * @param objectId
	 * @param objectType
	 * @param notifyType
	 * @return
	 */
	public boolean notify2Statistic(Long objectId, Integer objectType, Integer notifyType);
	/**
	 * 
	 * @param objectId
	 * @param objectType
	 * @param notifyType
	 * @param extra extra=1:rebuild questions/answers for user extra=2:rebuild answers for questions
	 * @return
	 */
	public boolean notify2Statistic(Long objectId, Integer objectType, Integer notifyType, Integer extra);

	/**
	 * 通知到站内信
	 * @param messageBean
	 * @return
	 */
	public boolean notify2Inbox(InboxMessageBean messageBean);

	/**
	 * 通知到手机
	 * @param templateId
	 * @param destnumber
	 * @return
	 */
	public boolean notify2CellPhone(Integer templateId, String destnumber, Map<String, String> params);
	/**
	 * 通知到微信
	 * @param bpId
	 * @param templateId
	 * @param url
	 * @param data
	 * @return
	 */
	public boolean notify2Wechat(long bpId, int templateId, String url, String data);
	/**
	 * 发送消息
	 * @param bpId
	 * @param messageType
	 * @param messageData
	 * @param strategy
	 * @return
	 */
	public boolean postMessage(long bpId, TEliteMessagePushType messageType, TEliteMessageData messageData, TEliteMessageStrategy strategy);
}