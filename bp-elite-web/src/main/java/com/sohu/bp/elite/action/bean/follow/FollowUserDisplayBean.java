package com.sohu.bp.elite.action.bean.follow;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;

import java.util.List;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-08-12 11:55:07
 * TODO 
 */
public class FollowUserDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
	private List<UserDetailDisplayBean> userList;
	private PageWrapperBean pageWrapper;

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

	public UserDetailDisplayBean getUser() {
		return user;
	}

	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}
}