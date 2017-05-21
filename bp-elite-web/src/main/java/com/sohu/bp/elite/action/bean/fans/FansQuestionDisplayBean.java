package com.sohu.bp.elite.action.bean.fans;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-09-02 15:21:58
 * TODO
 */
public class FansQuestionDisplayBean extends PageBean
{
	private EliteQuestionDisplayBean question;
	private List<UserDetailDisplayBean> userList;
	private PageWrapperBean pageWrapper;
	public EliteQuestionDisplayBean getQuestion() {
		return question;
	}
	public void setQuestion(EliteQuestionDisplayBean question) {
		this.question = question;
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