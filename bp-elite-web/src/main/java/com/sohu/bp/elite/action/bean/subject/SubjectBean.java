package com.sohu.bp.elite.action.bean.subject;


import com.sohu.bp.elite.util.ImageUtil;

import net.sf.json.JSONArray;

/**
 * Created by nicholastang on 2016/12/8.
 */
public class SubjectBean {
    private String subjectId;
    private String name;
    private String cover;
    private String brief;
    private long updateTime;
    private JSONArray detail;
    private JSONArray recentSubject;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = ImageUtil.removeImgProtocol(cover);
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public JSONArray getDetail() {
        return detail;
    }

    public void setDetail(JSONArray detail) {
        this.detail = detail;
    }

    public JSONArray getRecentSubject() {
        return recentSubject;
    }

    public void setRecentSubject(JSONArray recentSubject) {
        this.recentSubject = recentSubject;
    }
}
