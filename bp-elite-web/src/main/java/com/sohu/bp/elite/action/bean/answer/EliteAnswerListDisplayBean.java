package com.sohu.bp.elite.action.bean.answer;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/12
 */
public class EliteAnswerListDisplayBean extends PageBean{
    UserDetailDisplayBean user;
    List<SimpleFeedItemBean> answerList;

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
}
