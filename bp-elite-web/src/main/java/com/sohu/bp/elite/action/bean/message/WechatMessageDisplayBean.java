package com.sohu.bp.elite.action.bean.message;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;

public class WechatMessageDisplayBean extends PageBean{
	int noreadCount;
	List<MessageItemBean> messageList;
	
	public int getNoreadCount() {
		return noreadCount;
	}
	public void setNoreadCount(int noreadCount) {
		this.noreadCount = noreadCount;
	}
	public List<MessageItemBean> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<MessageItemBean> messageList) {
		this.messageList = messageList;
	}
}
