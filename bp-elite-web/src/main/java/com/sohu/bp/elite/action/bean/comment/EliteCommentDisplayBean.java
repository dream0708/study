package com.sohu.bp.elite.action.bean.comment;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.util.HumanityUtil;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/8/10
 */
public class EliteCommentDisplayBean {
    private UserDetailDisplayBean user;
    private String answerId;
    private String content;
    private long publishTime;
    private String publishTimeHuman;
    private List<String> pics;

    public UserDetailDisplayBean getUser() {
        return user;
    }

    public void setUser(UserDetailDisplayBean user) {
        this.user = user;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishTimeHuman() {
        return HumanityUtil.humanityTime(publishTime);
    }

    public void setPublishTimeHuman(String publishTimeHuman) {
        this.publishTimeHuman = publishTimeHuman;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }
}
