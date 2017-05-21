package com.sohu.bp.elite.action.bean.home;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.company.CompanyItemBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;

/**
 * 
 * @author nicholastang
 * 2016-09-06 19:38:26
 * TODO
 */
public class CompanyHomeDisplayBean extends PageBean
{
	private UserDetailDisplayBean user;
	private CompanyItemBean company;
	private List<SimpleFeedItemBean> feedItemList;
	
	private long oldestTime = 0;
	private PageWrapperBean pageWrapper;
	
	private String descHomeUrl = "";
	private String designUrl = "";
	private String instanceUrl = "";
	private String designerUrl = "";
	private String foremanUrl = "";
	private String commentUrl = "";
	
	
	public UserDetailDisplayBean getUser() {
		return user;
	}
	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}
	public CompanyItemBean getCompany() {
		return company;
	}
	public void setCompany(CompanyItemBean company) {
		this.company = company;
	}
	public List<SimpleFeedItemBean> getFeedItemList() {
		return feedItemList;
	}
	public void setFeedItemList(List<SimpleFeedItemBean> feedItemList) {
		this.feedItemList = feedItemList;
	}
	public long getOldestTime() {
		return oldestTime;
	}
	public void setOldestTime(long oldestTime) {
		this.oldestTime = oldestTime;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
	public String getDescHomeUrl() {
		return descHomeUrl;
	}
	public void setDescHomeUrl(String descHomeUrl) {
		this.descHomeUrl = descHomeUrl;
	}
	public String getDesignUrl() {
		return designUrl;
	}
	public void setDesignUrl(String designUrl) {
		this.designUrl = designUrl;
	}
	public String getInstanceUrl() {
		return instanceUrl;
	}
	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}
	public String getDesignerUrl() {
		return designerUrl;
	}
	public void setDesignerUrl(String designerUrl) {
		this.designerUrl = designerUrl;
	}
	public String getForemanUrl() {
		return foremanUrl;
	}
	public void setForemanUrl(String foremanUrl) {
		this.foremanUrl = foremanUrl;
	}
	public String getCommentUrl() {
		return commentUrl;
	}
	public void setCommentUrl(String commentUrl) {
		this.commentUrl = commentUrl;
	}
	
	
}