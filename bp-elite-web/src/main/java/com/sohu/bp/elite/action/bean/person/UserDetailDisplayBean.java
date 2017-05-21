package com.sohu.bp.elite.action.bean.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.sohu.achelous.timeline.Constants.Constants;
import com.sohu.bp.elite.enums.BpUserType;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.util.HumanityUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.ImageUtil;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class UserDetailDisplayBean implements Serializable{
	private static final long serialVersionUID = -1316729065317626671L;
	private String bpId = "";
	private String bpIdOrigin = "";
	private String companyId = "";
	private String companyIdOrigin = "";
    private String nick = "";
	private String brief = "";
	private String tags = "";
    private String avatar = "";
    private String homeUrl = "";
    private Integer questionNum = 0;
    private Integer answerNum = 0;
    private Integer fansNum = 0;
	private Integer likeNum = 0;
	private String description = "";
	private String questionNumHuman;
	private String answerNumHuman;
	private String fansNumHuman;
	private String likeNumHuman;
	private Boolean invited = false;

	private String workTotalNumHuman;

    private boolean hasFollowed = false;
    private boolean authenticated = false;
    private boolean isOwner = false;
    
    private List<String> serviceAreaList = new ArrayList<String>();
    
    private Integer bpUserType = BpUserType.NONE.getValue();
    private String lastLoginInfo = "";
	private boolean isAccountSafe = true;
	
	private Integer identity = EliteUserIdentity.NORMAL.getValue();
	private String identityString = "00";
	
	private String highlightText = "";
	private String highlightWords = "";
	
	public String getBpId() {
		return bpId;
	}

	public void setBpId(String bpId) {
		this.bpId = bpId;
		if(StringUtils.isNotBlank(bpId))
			this.bpIdOrigin =  IDUtil.encodeIdOrigin(IDUtil.decodeId(bpId));
	}
	
	public String getCompanyId(){
		return companyId;
	}
	
	public void setCompanyId(String companyId){
		this.companyId = companyId;
		if(StringUtils.isNotBlank(companyId)) {
		    this.companyIdOrigin =  IDUtil.encodeIdOrigin(IDUtil.decodeId(companyId));
		}
	}
	public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getAvatar() {

        return avatar;
    }

    public void setAvatar(String avatar) {
		String retVal = avatar;
		if(avatar != null){
			retVal = ImageUtil.getSmallImage(avatar, com.sohu.bp.elite.constants.Constants.LITTLE_AVATAR, null);
		}
        this.avatar = retVal;
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

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public void setQuestionNumHuman(String questionNumHuman) {
		this.questionNumHuman = questionNumHuman;
	}

	public void setAnswerNumHuman(String answerNumHuman) {
		this.answerNumHuman = answerNumHuman;
	}

	public void setFansNumHuman(String fansNumHuman) {
		this.fansNumHuman = fansNumHuman;
	}

	public String getLikeNumHuman() {
		return HumanityUtil.humanityNumber(likeNum);
	}

	public void setLikeNumHuman(String likeNumHuman) {
		this.likeNumHuman = likeNumHuman;
	}

	public Integer getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
	}

	public String getWorkTotalNumHuman() {
		return HumanityUtil.humanityNumber(questionNum + answerNum);
	}

	public List<String> getServiceAreaList() {
		return serviceAreaList;
	}

	public void setServiceAreaList(List<String> serviceAreaList) {
		this.serviceAreaList = serviceAreaList;
	}

	public Integer getBpUserType() {
		return bpUserType;
	}

	public void setBpUserType(Integer bpUserType) {
		this.bpUserType = bpUserType;
	}

	public String getLastLoginInfo() {
		return lastLoginInfo;
	}

	public void setLastLoginInfo(String lastLoginInfo) {
		this.lastLoginInfo = lastLoginInfo;
	}

	public boolean isAccountSafe() {
		return isAccountSafe;
	}

	public void setAccountSafe(boolean isAccountSafe) {
		this.isAccountSafe = isAccountSafe;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
		
	public Boolean getInvited() {
		return invited;
	}

	public void setInvited(Boolean invited) {
		this.invited = invited;
	}

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public String getIdentityString() {
		return identityString;
	}

	public void setIdentityString(String identityString) {
		this.identityString = identityString;
	}

	public String getBpIdOrigin() {
		return bpIdOrigin;
	}

	public void setBpIdOrigin(String bpIdOrigin) {
		this.bpIdOrigin = bpIdOrigin;
	}

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getHighlightText() {
        return highlightText;
    }

    public void setHighlightText(String highlightText) {
        this.highlightText = highlightText;
    }

    public String getHighlightWords() {
        return highlightWords;
    }

    public void setHighlightWords(String highlightWords) {
        this.highlightWords = highlightWords;
    }

    public String getCompanyIdOrigin() {
        return companyIdOrigin;
    }

    public void setCompanyIdOrigin(String companyIdOrigin) {
        this.companyIdOrigin = companyIdOrigin;
    }
}
