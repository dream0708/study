package com.sohu.bp.elite.action.bean.search;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 11:27:57
 * TODO
 */
public class SearchUserDisplayBean extends PageBean
{
	private String keywords;
	private List<UserDetailDisplayBean> userList;
	private PageWrapperBean pageWrapper;
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public List<UserDetailDisplayBean> getUserList() {
		return userList;
	}
	public void setUserList(List<UserDetailDisplayBean> userList) {
		this.userList = userList;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	
	
}