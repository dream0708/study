package com.sohu.bp.elite.bean;

/**
 * 搜索结果
 * @author zhijungou
 * 2017年4月5日
 */
public class EliteSearchResultBean {
	private Long id;
	private String highlightText;
	private String highlightWord;
	public Long getId() {
		return id;
	}
	public EliteSearchResultBean setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getHighlightWord() {
		return highlightWord;
	}
	public EliteSearchResultBean setHighlightWord(String highlightWord) {
		this.highlightWord = highlightWord;
		return this;
	}
	public String getHighlightText() {
		return highlightText;
	}
	public EliteSearchResultBean setHighlightText(String highlightText) {
		this.highlightText = highlightText;
		return this;
	}
	
	
}
