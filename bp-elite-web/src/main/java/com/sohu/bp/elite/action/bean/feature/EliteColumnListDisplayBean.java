package com.sohu.bp.elite.action.bean.feature;

import java.util.List;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.constants.Constants;

/**
 * 全部专栏展示页
 * @author zhijungou
 * 2016年12月2日
 */
public class EliteColumnListDisplayBean extends PageBean{
	private List<EliteColumnListDetailBean> columnList;
	private PageWrapperBean pageWrapper;
	
	public EliteColumnListDisplayBean(){
		this.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	}
	
	public List<EliteColumnListDetailBean> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<EliteColumnListDetailBean> columnList) {
		this.columnList = columnList;
	}
	public PageWrapperBean getPageWrapper() {
		return pageWrapper;
	}
	public void setPageWrapper(PageWrapperBean pageWrapper) {
		this.pageWrapper = pageWrapper;
	}
}
