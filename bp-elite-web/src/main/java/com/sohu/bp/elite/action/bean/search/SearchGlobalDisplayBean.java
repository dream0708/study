package com.sohu.bp.elite.action.bean.search;

import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhihao
 *         2016/8/23
 */
public class SearchGlobalDisplayBean {
	private String keywords = "";
    private List<SimpleFeedItemBean> questionList;
    private List<SimpleFeedItemBean> answerList;
    private List<UserDetailDisplayBean> userList;
    private List<TagItemBean> tagList;
    private Map<String, Integer> totalCounts;
    private PageWrapperBean pageWrapper;
    
    public static SearchGlobalDisplayBean getEmptyDisplayBean(){
        SearchGlobalDisplayBean bean = new SearchGlobalDisplayBean();
        bean.setQuestionList(new ArrayList<>());
        bean.setAnswerList(new ArrayList<>());
        bean.setUserList(new ArrayList<>());
        bean.setTagList(new ArrayList<>());

        Map<String, Integer> totalCounts = new HashMap<>();
        totalCounts.put("question", 0);
        totalCounts.put("answer", 0);
        totalCounts.put("user", 0);
        totalCounts.put("tag", 0);
        bean.setTotalCounts(totalCounts);
        return bean;
    }
    
    
    public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public List<SimpleFeedItemBean> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<SimpleFeedItemBean> questionList) {
        this.questionList = questionList;
    }

    public List<SimpleFeedItemBean> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<SimpleFeedItemBean> answerList) {
        this.answerList = answerList;
    }

    public List<UserDetailDisplayBean> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDetailDisplayBean> userList) {
        this.userList = userList;
    }

    public List<TagItemBean> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagItemBean> tagList) {
        this.tagList = tagList;
    }

    public Map<String, Integer> getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Map<String, Integer> totalCounts) {
        this.totalCounts = totalCounts;
    }

	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}

	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
    
}
