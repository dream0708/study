package com.sohu.bp.elite.action.bean.fans;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 15:24:47
 * TODO
 */
public class FansTagDisplayBean extends PageBean
{
	private TagItemBean tag;
	private List<UserDetailDisplayBean> userList;
	private PageWrapperBean pageWrapper;
	
	
	public TagItemBean getTag() {
		return tag;
	}
	public void setTag(TagItemBean tag) {
		this.tag = tag;
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