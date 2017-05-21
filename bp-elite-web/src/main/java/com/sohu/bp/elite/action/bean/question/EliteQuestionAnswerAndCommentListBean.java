package com.sohu.bp.elite.action.bean.question;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.answer.EliteAnswerDisplayBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.enums.EliteAdminStatus;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/10
 */
public class EliteQuestionAnswerAndCommentListBean extends PageBean {
    private int admin = EliteAdminStatus.NONE.getValue();
    private EliteQuestionDisplayBean question;
    private EliteAnswerDisplayBean answer;
    private List<SimpleFeedItemBean> commentList;

    public EliteQuestionDisplayBean getQuestion() {
        return question;
    }

    public void setQuestion(EliteQuestionDisplayBean question) {
        this.question = question;
    }

    public EliteAnswerDisplayBean getAnswer() {
        return answer;
    }

    public void setAnswer(EliteAnswerDisplayBean answer) {
        this.answer = answer;
    }

    public List<SimpleFeedItemBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<SimpleFeedItemBean> commentList) {
        this.commentList = commentList;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}

