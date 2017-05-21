package com.sohu.bp.elite.action.bean.favorite;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;

/**
 * 
 * @author nicholastang
 * 2016-09-10 17:10:01
 * TODO
 */
public class FavoriteAnswerDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
    private List<SimpleFeedItemBean> answerList;
    private PageWrapperBean pageWrapper;
    
	public UserDetailDisplayBean getUser() {
		return user;
	}
	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
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