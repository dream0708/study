package com.sohu.bp.elite.action.bean.search;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 11:25:27
 * TODO
 */
public class SearchTagDisplayBean extends PageBean
{
	private String keywords;
	private List<TagItemBean> tagList;
	private PageWrapperBean pageWrapper;
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public List<TagItemBean> getTagList() {
		return tagList;
	}
	public void setTagList(List<TagItemBean> tagList) {
		this.tagList = tagList;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	
	
}