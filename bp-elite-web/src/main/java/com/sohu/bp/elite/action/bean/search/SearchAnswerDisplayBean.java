package com.sohu.bp.elite.action.bean.search;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 11:29:07
 * TODO
 */
public class SearchAnswerDisplayBean extends PageBean
{
	private String keywords = "";
	private List<SimpleFeedItemBean> answerList;
	private PageWrapperBean pageWrapper;
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public List<SimpleFeedItemBean> getAnswerList() {
		return answerList;
	}
	public void setAnswerList(List<SimpleFeedItemBean> answerList) {
		this.answerList = answerList;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}

}