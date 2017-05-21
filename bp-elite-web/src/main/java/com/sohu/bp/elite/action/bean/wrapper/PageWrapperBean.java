package com.sohu.bp.elite.action.bean.wrapper;

import java.io.Serializable;

/**
 * 
 * @author nicholastang
 * 2016-08-31 16:05:44
 * TODO 包装pc页面的bean
 */
public class PageWrapperBean implements Serializable
{
	private static final long serialVersionUID = -2743108427262484456L;
	private String headerHtml = "";
	private String footerHtml = "";
	private String toolbarHtml = "";
	private String sidebarHtml = "";
	
	public String getHeaderHtml() {
		return headerHtml;
	}
	public void setHeaderHtml(String headerHtml) {
		this.headerHtml = headerHtml;
	}
	public String getFooterHtml() {
		return footerHtml;
	}
	public void setFooterHtml(String footerHtml) {
		this.footerHtml = footerHtml;
	}
	public String getToolbarHtml() {
		return toolbarHtml;
	}
	public void setToolbarHtml(String toolbarHtml) {
		this.toolbarHtml = toolbarHtml;
	}
	public String getSidebarHtml() {
		return sidebarHtml;
	}
	public void setSidebarHtml(String sidebarHtml) {
		this.sidebarHtml = sidebarHtml;
	}
	
	
	
	
}