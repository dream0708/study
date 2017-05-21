package com.sohu.bp.elite.action.bean.feature;

import java.util.List;

import javax.swing.text.AbstractDocument.Content;

import com.sohu.bp.elite.action.bean.FeedListDisplayBean;
import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.ImageUtil;

public class EliteColumnQuestionsDisplayBean extends AbstractEliteColumn{
	private List<SimpleFeedItemBean> feedItemList;
	private PageWrapperBean pageWrapper;
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	public List<SimpleFeedItemBean> getFeedItemList() {
		return feedItemList;
	}
	public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
		this.feedItemList = feedItemList;
	}
}
