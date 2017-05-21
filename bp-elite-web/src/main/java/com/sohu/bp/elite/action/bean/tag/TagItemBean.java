package com.sohu.bp.elite.action.bean.tag;

import com.sohu.bp.elite.util.HumanityUtil;
import com.sohu.bp.elite.util.ImageUtil;

import java.io.Serializable;

/**
 * 
 * @author nicholastang
 * 2016-08-12 12:06:06
 * TODO 单个tag的bean
 */
public class TagItemBean implements Serializable
{
	private static final long serialVersionUID = -9164341323873084539L;
	private String tagId;
	private String tagName;
	private String tagDesc;
	private String tagCover;
	private Integer tagStatus;
	
	private Integer questionNum;

	private Integer answerNum;

	private Integer fansNum;
	
	private boolean hasFollowed = false;

	
	
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagDesc() {
		return tagDesc;
	}
	public void setTagDesc(String tagDesc) {
		this.tagDesc = tagDesc;
	}
	public String getTagCover() {
		return tagCover;
	}
	public void setTagCover(String tagCover) {
		this.tagCover = ImageUtil.removeImgProtocol(tagCover);
	}
	public Integer getTagStatus() {
		return tagStatus;
	}
	public void setTagStatus(Integer tagStatus) {
		this.tagStatus = tagStatus;
	}
	public Integer getQuestionNum() {
		return questionNum;
	}
	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}
	public String getQuestionNumHuman() {
		if(null == questionNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(questionNum);
	}
	
	public Integer getAnswerNum() {
		return answerNum;
	}
	public void setAnswerNum(Integer answerNum) {
		this.answerNum = answerNum;
	}
	public String getAnswerNumHuman() {
		if(null == answerNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(answerNum);
	}
	
	public Integer getFansNum() {
		return fansNum;
	}
	public void setFansNum(Integer fansNum) {
		this.fansNum = fansNum;
	}
	public String getFansNumHuman() {
		if(null == fansNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(fansNum);
	}
	public boolean isHasFollowed() {
		return hasFollowed;
	}
	public void setHasFollowed(boolean hasFollowed) {
		this.hasFollowed = hasFollowed;
	}
	
	
	
}