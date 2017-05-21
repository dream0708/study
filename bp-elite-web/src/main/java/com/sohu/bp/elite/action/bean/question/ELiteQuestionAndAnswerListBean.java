package com.sohu.bp.elite.action.bean.question;

import java.util.List;
import java.util.Map;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.enums.EliteAdminStatus;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class ELiteQuestionAndAnswerListBean extends PageBean {
    private int admin = EliteAdminStatus.NONE.getValue();
    private UserDetailDisplayBean user;
    private EliteQuestionDisplayBean question;
    private List<SimpleFeedItemBean> answerList;
    private PageWrapperBean pageWrapper;
    private boolean answered;
    private int choosedOption;
    private boolean hasPhoneNo;
    private boolean isAnswerRestrict = false;
    private int specialType; 
    
    private Map<Integer, String> adMap;

    public UserDetailDisplayBean getUser() {
        return user;
    }

    public void setUser(UserDetailDisplayBean user) {
        this.user = user;
    }

    public EliteQuestionDisplayBean getQuestion() {
        return question;
    }

    public void setQuestion(EliteQuestionDisplayBean question) {
        this.question = question;
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

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean getHasPhoneNo() {
        return hasPhoneNo;
    }

    public void setHasPhoneNo(boolean hasPhoneNo) {
        this.hasPhoneNo = hasPhoneNo;
    }

	public boolean getIsAnswerRestrict() {
		return isAnswerRestrict;
	}

	public void setIsAnswerRestrict(boolean isAnswerRestrict) {
		this.isAnswerRestrict = isAnswerRestrict;
	}

	public Map<Integer, String> getAdMap() {
		return adMap;
	}

	public void setAdMap(Map<Integer, String> adMap) {
		this.adMap = adMap;
	}

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getSpecialType() {
        return specialType;
    }

    public void setSpecialType(int specialType) {
        this.specialType = specialType;
    }

    public int getChoosedOption() {
        return choosedOption;
    }

    public void setChoosedOption(int choosedOption) {
        this.choosedOption = choosedOption;
    }
}
