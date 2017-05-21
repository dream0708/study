package com.sohu.bp.elite.action.bean.follow;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import java.util.List;

import com.sohu.bp.elite.action.bean.tag.TagItemBean;

/**
 * 
 * @author nicholastang
 * 2016-08-12 11:53:09
 * TODO 关注的标签页对应的bean
 */
public class FollowTagDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
	private List<TagItemBean> tagList;
	private PageWrapperBean pageWrapper;

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

	public UserDetailDisplayBean getUser() {
		return user;
	}

	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}
	
}