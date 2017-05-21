package com.sohu.bp.elite.action.bean.person;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;

import java.util.List;

/**
 * 
 * @author nicholastang
 * 2016-09-07 20:37:47
 * TODO
 */
public class UserStatusItemDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
	private List<SimpleFeedItemBean> feedItemList;
	private PageWrapperBean pageWrapper;
	
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
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	
	
}