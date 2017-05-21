package com.sohu.bp.elite.action.bean.feature;

import java.io.Serializable;

//用于app问答的头图
public class EliteHeadFragment implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 4426362561014291194L;
    private String title;
    private String url;
    private String pic;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getPic() {
        return pic;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }
    
    
}
