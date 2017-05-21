package com.sohu.bp.elite.api.task;

import java.io.Serializable;
import java.util.List;

/**
 * 异步调用抓取任务类，存储固定时间发布的回答
 * @author zhijungou
 * 2016年11月7日
 */
public class EliteCrawlSquareAsycTask implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long questionId;
	private String content;
	private Long createTime;
	private Long updateTime;
	private List<Integer> tagIds;
	private Long bpid;
	private String sourceUrl;
	private boolean squareFlag;
	
	public EliteCrawlSquareAsycTask(){}
	public EliteCrawlSquareAsycTask(Long questionId, String content, Long createTime, Long updateTime, List<Integer> tagIds, Long bpid, String sourceUrl){
		this.questionId = questionId;
		this.content = content;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.tagIds = tagIds;
		this.bpid = bpid;
		this.setSourceUrl(sourceUrl);
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public List<Integer> getTagIds() {
		return tagIds;
	}
	public void setTagIds(List<Integer> tagIds) {
		this.tagIds = tagIds;
	}
	public Long getBpid() {
		return bpid;
	}
	public void setBpid(Long bpid) {
		this.bpid = bpid;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public boolean isSquareFlag() {
		return squareFlag;
	}
	public void setSquareFlag(boolean squareFlag) {
		this.squareFlag = squareFlag;
	}
}
