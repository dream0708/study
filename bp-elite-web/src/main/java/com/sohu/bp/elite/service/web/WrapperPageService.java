package com.sohu.bp.elite.service.web;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionDisplayBean;
import com.sohu.bp.elite.action.bean.subject.SubjectBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.enums.SideBarPageType;

/**
 * 
 * @author nicholastang
 * 2016-08-31 17:06:20
 * TODO 获取页面各个位置的service
 */
public interface WrapperPageService
{
	/**
	 * 获取首页（一般页面）的头部Html代码
	 * @return
	 */
	public String getIndexHeaderHtml(UserInfo userInfo, EliteQuestionDisplayBean questionBean, SubjectBean subjectBean, TagItemBean tagBean, boolean showBigToolbar);

	public String getIndexHeaderHtml(UserInfo userInfo, EliteQuestionDisplayBean questionBean, boolean showBigToolbar);

	public String getIndexHeaderHtml(UserInfo userInfo, SubjectBean subjectBean, boolean showBigToolbar);

	public String getIndexHeaderHtml(UserInfo userInfo, TagItemBean tagBean, boolean showBigToolbar);

	public String getIndexHeaderHtml(UserInfo userInfo, boolean showBigToolbar);
	
	/**
	 * 重置首页头部的html代码缓存
	 */
	public void resetIndexHeaderHtml();
	
	/**
	 * 获取首页（一般页面）的底部Html代码
	 * @return
	 */
	public String getIndexFooterHtml();
	
	/**
	 * 重置首页底部html代码缓存
	 */
	public void resetIndexFooterHtml();
	
	/**
	 * 获取顶部toolbar
	 * @return
	 */
	public String getToolbarHtml(UserInfo userInfo, EliteQuestionDisplayBean questionBean);

	public String getToolbarHtml(UserInfo userInfo);
	
	/**
	 * 重置顶部toolbar
	 */
	public void resetToolbarHtml();
	
	/**
	 * 获取PC侧边栏
	 * @param userInfoBean
	 * @return
	 */
	public String getSidebarHtml(UserDetailDisplayBean userInfoBean, SideBarPageType pageType);

}