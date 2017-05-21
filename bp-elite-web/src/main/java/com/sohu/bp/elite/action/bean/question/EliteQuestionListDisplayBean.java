package com.sohu.bp.elite.action.bean.question;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class EliteQuestionListDisplayBean extends PageBean {
    private UserDetailDisplayBean user;
    private List<SimpleFeedItemBean> questionList;
    private PageWrapperBean pageWrapper;

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

    public UserDetailDisplayBean getUser() {
        return user;
    }

    public void setUser(UserDetailDisplayBean user) {
        this.user = user;
    }
}
