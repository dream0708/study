package com.sohu.bp.elite.api.api.bean;


import java.util.List;

/**
 * 
 * @author nicholastang
 * 2016-08-27 11:16:58
 * TODO
 */
public class ContentBean
{
	private String plainText = "";
	private List<String> imgList = null;

	public String getPlainText() {
		return plainText;
	}

	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}

	public List<String> getImgList() {
		return imgList;
	}

	public void setImgList(List<String> imgList) {
		this.imgList = imgList;
	}
}