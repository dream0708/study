package com.sohu.bp.elite.api.api.bean;

/**
 * 建立用户索引数据的bean
 * @author nicholastang
 * 2016-10-28 14:07:56
 * TODO
 */
public class IndexUserBean
{
	private Long bpId = 0L;
	private String nick = "";
	private Long birthday = 0L;
	private Integer gender = 0;
	private Integer identity = 0;
	private String description = "";
	private Integer grade = 0;
	private Integer gold = 0;
	private Integer status = 0;
	private String tagIds = "";
	private Long lastLoginTime = 0l;
	private Long firstLoginTime = 0l;
	private Integer firstLogin = 0;
	private Long areaCode = 0L;
	private Integer answerNum = 0;
	private Integer questionNum = 0;
	
	public Long getBpId() {
		return bpId;
	}
	public void setBpId(Long bpId) {
		this.bpId = bpId;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public Long getBirthday() {
		return birthday;
	}
	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getIdentity() {
		return identity;
	}
	public void setIdentity(Integer identity) {
		this.identity = identity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	public Integer getGold() {
		return gold;
	}
	public void setGold(Integer gold) {
		this.gold = gold;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTagIds() {
		return tagIds;
	}
	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}
	public Long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Long getFirstLoginTime() {
		return firstLoginTime;
	}
	public void setFirstLoginTime(Long firstLoginTime) {
		this.firstLoginTime = firstLoginTime;
	}
	public Integer getFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(Integer firstLogin) {
		this.firstLogin = firstLogin;
	}
    public Long getAreaCode() {
        return areaCode;
    }
    public void setAreaCode(Long areaCode) {
        this.areaCode = areaCode;
    }

	public Integer getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(Integer answerNum) {
		this.answerNum = answerNum;
	}

	public Integer getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}
}