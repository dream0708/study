package com.sohu.bp.elite.bean;

/**
 * create time: 2015年12月22日 下午7:19:20
 * @auther dexingyang
 */
public class Picture {
	
	private int width;
	private int height;
	private String ext = "";
	private String filePath;
	private String imgName;
	    
	public int getWidth() {
		return width;
	}
	    
	public void setWidth(int width) {
		this.width = width;
	}
	    
	public int getHeight() {
		return height;
	}
	    
	public void setHeight(int height) {
		this.height = height;
	}
	    
	public String getExt() {
	    return ext;
	}
	    
	public void setExt(String ext) {
	    this.ext = ext;
	}

	public String getFilePath() {
	    return filePath;
	}

	public void setFilePath(String filePath) {
	    this.filePath = filePath;
	}
	    
	public String getImgName() {
	    return imgName;
	}

	public void setImgName(String imgName) {
	    this.imgName = imgName;
	}

	public String toString(){
	    return "[width]="+width+",[height]="+height+",[ext]="+ext;
	}
}
