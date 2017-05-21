package com.sohu.bp.elite.api.api.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholastang on 2017/3/13.
 */
public class EcologyUser extends AbstractEntity {
    private static final long serialVersionUID = 3063556562830653795L;
    private Long id = 0L;
    private String avatar = "http://sucimg.itc.cn/avatarimg/s_1177315115_1448526994995";
    private String nick = "搜狐焦点网友";
    private String link = "http://test.bar.focus.cn/pu/5ct22y1";
    private String ask_link = "http://test.bar.focus.cn/q/go?invitedUserId=5ct22y1";
    private Integer followedNum = 0;
    private Integer answerNum = 0;
    private List<String> fields = new ArrayList<>();
    private String desc = "什么都没留下";
    private Integer type = 10;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAsk_link() {
        return ask_link;
    }

    public void setAsk_link(String ask_link) {
        this.ask_link = ask_link;
    }

    public Integer getFollowedNum() {
        return followedNum;
    }

    public void setFollowedNum(Integer followedNum) {
        this.followedNum = followedNum;
    }

    public Integer getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Integer answerNum) {
        this.answerNum = answerNum;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public Serializable getInternalId() {
        return this.getId();
    }
}
