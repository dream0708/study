package com.sohu.bp.elite.api.api.bean;

/**
 * 
 * @author nicholastang
 * 2016-08-27 11:05:06
 * TODO 抓取的数据bean
 */
public class CrawlQAndAData
{
	private String title;
	private String content;
	private String tags;
	private String tagIds;
	private Long cityId;
	private Long postTime;
	private String sourceUrl;
	private String authorName;
	private String authorAvatar;
	private String authorUrl;
	private Long bpId;
	private String answerBody;
	private String deletedLine;
	private Boolean squareFlag = false;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Long getCityId() {
		return cityId;
	}
	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
	public Long getPostTime() {
		return postTime;
	}
	public void setPostTime(Long postTime) {
		this.postTime = postTime;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorAvatar() {
		return authorAvatar;
	}
	public void setAuthorAvatar(String authorAvatar) {
		this.authorAvatar = authorAvatar;
	}
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public String getAnswerBody() {
		return answerBody;
	}
	public void setAnswerBody(String answerBody) {
		this.answerBody = answerBody;
	}
	public Long getBpId() {
		return bpId;
	}
	public void setBpId(Long bpId) {
		this.bpId = bpId;
	}

	public String getTagIds() {
		return tagIds;
	}

	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}
	public Boolean getSquareFlag() {
		return squareFlag;
	}
	public void setSquareFlag(Boolean squareFlag) {
		this.squareFlag = squareFlag;
	}
	public String getDeletedLine() {
		return deletedLine;
	}
	public void setDeletedLine(String deletedLine) {
		this.deletedLine = deletedLine;
	}
}