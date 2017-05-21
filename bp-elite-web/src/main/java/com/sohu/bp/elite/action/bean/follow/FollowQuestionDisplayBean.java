package com.sohu.bp.elite.action.bean.follow;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import java.util.List;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;

/**
 * 
 * @author nicholastang
 * 2016-08-12 11:54:18
 * TODO 关注的问题展示页对应的bean
 */
public class FollowQuestionDisplayBean
{
	private UserDetailDisplayBean user;
	private List<SimpleFeedItemBean> questionList;

	public List<SimpleFeedItemBean> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<SimpleFeedItemBean> questionList) {
		this.questionList = questionList;
	}

	public UserDetailDisplayBean getUser() {
		return user;
	}

	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}
}