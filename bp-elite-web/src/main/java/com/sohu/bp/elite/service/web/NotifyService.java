package com.sohu.bp.elite.service.web;

import java.util.Map;

import com.sohu.bp.elite.bean.InboxMessageBean;
import com.sohu.bp.elite.enums.EliteMessageData;

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
	 * @param actionType
	 * @return
	 */
	public boolean notify2Statistic(Long objectId, Integer objectType, Integer notifyType);
	
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
	
}