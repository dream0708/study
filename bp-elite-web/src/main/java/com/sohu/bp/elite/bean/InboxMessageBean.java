package com.sohu.bp.elite.bean;

import java.util.HashMap;
import java.util.Map;

import com.sohu.bp.model.BpMessageDetail;
import com.sohu.bp.model.BpMessageDetailType;
import com.sohu.bp.model.BpMessageInteractionType;
import com.sohu.bp.utils.IpUtil;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-10-17 14:48:42
 * TODO
 */
public class InboxMessageBean
{
	private Long toId;
	private String topic;
	private String content;
	private BpMessageInteractionType interactionType = BpMessageInteractionType.FOLLOW;
	private BpMessageDetailType messageType = BpMessageDetailType.INTERACTION;
	
	private String host = "127.0.0.1";
	private Integer port = 1;
	
	
	public Long getToId() {
		return toId;
	}
	public void setToId(Long toId) {
		this.toId = toId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public BpMessageInteractionType getInteractionType() {
		return interactionType;
	}
	public void setInteractionType(BpMessageInteractionType interactionType) {
		this.interactionType = interactionType;
	}
	public BpMessageDetailType getMessageType() {
		return messageType;
	}
	public void setMessageType(BpMessageDetailType messageType) {
		this.messageType = messageType;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public BpMessageDetail getBpMessageDetail()
	{
		BpMessageDetail bpMessageDetail = new BpMessageDetail();
		bpMessageDetail.setToId(toId);
		bpMessageDetail.setTopic(topic);
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("content", content);
		dataMap.put("interactionType", interactionType);
		bpMessageDetail.setContent(JSONObject.fromObject(dataMap).toString());
		bpMessageDetail.setType(messageType);
		bpMessageDetail.setCreateHost(IpUtil.ip2Host(host));
		bpMessageDetail.setCreatePort(port);
		
		return bpMessageDetail;
	}
	
}