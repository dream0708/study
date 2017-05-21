package com.sohu.bp.elite.action.bean.tag;

import java.io.Serializable;
import java.util.List;

import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.util.HumanityUtil;

/**
 * 
 * @author nicholastang
 * 2016-08-11 17:27:10
 * TODO 封装标签广场数据的bean
 */
public class LabelPlazaDisplayBean implements Serializable
{
	private static final long serialVersionUID = 9013848876606790532L;
	private TagItemBean tag;
	
	private List<SimpleFeedItemBean> feedItemList;
	
	private long latestTime = 0;
	private long oldestTime = 0;
	
	private PageWrapperBean pageWrapper;
	 
	public TagItemBean getTag() {
		return tag;
	}

	public void setTag(TagItemBean tag) {
		this.tag = tag;
	}

	public List<SimpleFeedItemBean> getFeedItemList() {
		return feedItemList;
	}

	public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
		this.feedItemList = feedItemList;
	}

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

	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}

	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	
	
	
}