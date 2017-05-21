package com.sohu.bp.elite.action.bean.message;

import java.io.Serializable;

public class MessageItemBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String messageId;
	String updateTime;
	String topic;
	String content;
	String objectType;
	String objectId;
	Integer specialType;
	
	public String toString() {
		return "objectId = " + objectId + " objectType : " + objectType + " content : " + content;
	}
	public String getMessageId() {
		return messageId;
	}
	public MessageItemBean setMessageId(String messageId) {
		this.messageId = messageId;
		return this;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public MessageItemBean setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	public String getTopic() {
		return topic;
	}
	public MessageItemBean setTopic(String topic) {
		this.topic = topic;
		return this;
	}
	public String getContent() {
		return content;
	}
	public MessageItemBean setContent(String content) {
		this.content = content;
		return this;
	}
	public String getObjectType() {
		return objectType;
	}
	public MessageItemBean setObjectType(String objectType) {
		this.objectType = objectType;
		return this;
	}
	public String getObjectId() {
		return objectId;
	}
	public MessageItemBean setObjectId(String objectId) {
		this.objectId = objectId;
		return this;
	}
    public Integer getSpecialType() {
        return specialType;
    }
    public MessageItemBean setSpecialType(Integer specialType) {
        this.specialType = specialType;
        return this;
    }
}
