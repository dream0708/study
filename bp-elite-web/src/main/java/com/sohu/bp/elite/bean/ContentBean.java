package com.sohu.bp.elite.bean;

import java.util.List;

import com.sohu.bp.elite.util.ImageUtil;

/**
 * 
 * @author nicholastang
 * 2016-08-27 11:16:58
 * TODO
 */
public class ContentBean
{
	private String plainText = "";
	private String imgText = "";
	private String formatText = "";
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
		this.imgList = ImageUtil.removeImgListProtocol(imgList);
	}
    public String getImgText() {
        return imgText;
    }
    public void setImgText(String imgText) {
        this.imgText = imgText;
    }
    public String getFormatText() {
        return formatText;
    }
    public void setFormatText(String formatText) {
        this.formatText = formatText;
    }	
}