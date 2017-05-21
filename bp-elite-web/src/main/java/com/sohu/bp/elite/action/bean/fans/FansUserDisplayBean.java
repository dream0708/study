package com.sohu.bp.elite.action.bean.fans;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 15:25:43
 * TODO
 */
public class FansUserDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
	private List<UserDetailDisplayBean> userList;
	private PageWrapperBean pageWrapper;
	public UserDetailDisplayBean getUser() {
		return user;
	}
	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
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