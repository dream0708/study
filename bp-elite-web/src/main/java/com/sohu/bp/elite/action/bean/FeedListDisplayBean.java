package com.sohu.bp.elite.action.bean;

import java.util.List;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
/**
 * 
 * @author zhijungou
 * 2016年9月1日
 */
public class FeedListDisplayBean extends PageBean {
	
	private List<SimpleFeedItemBean> feedItemList;

	public List<SimpleFeedItemBean> getFeedItemList() {
		return feedItemList;
	}

	public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
		this.feedItemList = feedItemList;
	}
	
	
}
