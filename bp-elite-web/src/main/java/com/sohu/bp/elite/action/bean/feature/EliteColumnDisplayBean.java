package com.sohu.bp.elite.action.bean.feature;

import java.util.ArrayList;
import java.util.List;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.ImageUtil;

import net.sf.json.JSONObject;

/**
 * 专栏的展示页
 * @author zhijungou
 * 2016年12月2日
 */
public class EliteColumnDisplayBean extends AbstractEliteColumn{
	private List<TagDisplayBean> tags;
	private List<JSONObject> content;
	private Integer types;
	private boolean hasFavorited = false;
	private PageWrapperBean pageWrapper;
	private List<SimpleFeedItemBean> questionList = new ArrayList<>();
	private List<JSONObject> recentList;	
	
	public List<TagDisplayBean> getTags() {
		return tags;
	}
	public void setTags(List<TagDisplayBean> tags) {
		this.tags = tags;
	}
	public List<JSONObject> getContent() {
		return content;
	}
	public void setContent(List<JSONObject> content) {		
		this.content = content;
	}
	public Integer getTypes() {
		return types;
	}
	public void setTypes(Integer types) {
		this.types = types;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	public List<SimpleFeedItemBean> getQuestionList() {
		return questionList;
	}
	public void setQuestionList(List<SimpleFeedItemBean> questionList) {
		this.questionList = questionList;
	}
	public List<JSONObject> getRecentList() {
		return recentList;
	}
	public void setRecentList(List<JSONObject> recentList) {
		this.recentList = recentList;
	}
    public boolean isHasFavorited() {
        return hasFavorited;
    }
    public void setHasFavorited(boolean hasFavorited) {
        this.hasFavorited = hasFavorited;
    }
}
