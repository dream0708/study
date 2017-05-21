package com.sohu.bp.elite.action.bean;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;

import java.util.List;
import java.util.Map;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;

/**
 * 
 * @author nicholastang
 * 2016-08-12 15:15:27
 * TODO
 */
public class IndexDisplayBean extends PageBean
{
	private int totalAnswerNum = 0;
	private long latestTime = 0;
	private long oldestTime = 0;
	private List<SimpleFeedItemBean> feedItemList;
	private PageWrapperBean pageWrapper;
	private UserDetailDisplayBean user;
	private boolean hasLogin = false;
	private boolean hasPhoneNo = false;
	private boolean isQuestionRestrict = false;
	private Map<Integer, String> adMap;

	public long getLatestTime() {
		return latestTime;
	}

	public void setLatestTime(long latestTime) {
		this.latestTime = latestTime;
	}

	public long getOldestTime() {
		return oldestTime;
	}

	public void setOldestTime(long oldestTime) {
		this.oldestTime = oldestTime;
	}

	public List<SimpleFeedItemBean> getFeedItemList() {
		return feedItemList;
	}

	public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
		this.feedItemList = feedItemList;
	}

	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}

	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}

	public int getTotalAnswerNum() {
		return totalAnswerNum;
	}

	public void setTotalAnswerNum(int totalAnswerNum) {
		this.totalAnswerNum = totalAnswerNum;
	}

	public UserDetailDisplayBean getUser() {
		return user;
	}

	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}

	public boolean isHasLogin() {
		return hasLogin;
	}

	public void setHasLogin(boolean hasLogin) {
		this.hasLogin = hasLogin;
	}

	public boolean isHasPhoneNo() {
		return hasPhoneNo;
	}

	public void setHasPhoneNo(boolean hasPhoneNo) {
		this.hasPhoneNo = hasPhoneNo;
	}

	public boolean isQuestionRestrict() {
		return isQuestionRestrict;
	}

	public void setIsQuestionRestrict(boolean isQuestionRestrict) {
		this.isQuestionRestrict = isQuestionRestrict;
	}

	public Map<Integer, String> getAdMap() {
		return adMap;
	}

	public void setAdMap(Map<Integer, String> adMap) {
		this.adMap = adMap;
	}
}