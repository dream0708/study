package com.sohu.bp.elite.action.bean.home;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.company.CompanyItemBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-09-06 19:38:18
 * TODO
 */
public class SelfHomeDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
	private List<SimpleFeedItemBean> feedItemList;
	
	private long oldestTime = 0;
	private PageWrapperBean pageWrapper;
	
	private String selfUrl = "";

	public UserDetailDisplayBean getUser() {
		return user;
	}

	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}

	public List<SimpleFeedItemBean> getFeedItemList() {
		return feedItemList;
	}

	public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
		this.feedItemList = feedItemList;
	}

	public long getOldestTime() {
		return oldestTime;
	}

	public void setOldestTime(long oldestTime) {
		this.oldestTime = oldestTime;
	}

	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}

	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}

	public String getSelfUrl() {
		return selfUrl;
	}

	public void setSelfUrl(String selfUrl) {
		this.selfUrl = selfUrl;
	}
	
	
}