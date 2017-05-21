package com.sohu.bp.elite.action.bean.search;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 11:29:29
 * TODO
 */
public class SearchQuestionDisplayBean extends PageBean
{
	private String keywords = "";
	private List<SimpleFeedItemBean> questionList;
	private PageWrapperBean pageWrapper;
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public List<SimpleFeedItemBean> getQuestionList() {
		return questionList;
	}
	public void setQuestionList(List<SimpleFeedItemBean> questionList) {
		this.questionList = questionList;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
}