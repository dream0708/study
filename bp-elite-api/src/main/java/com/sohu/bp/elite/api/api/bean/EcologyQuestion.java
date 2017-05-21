package com.sohu.bp.elite.api.api.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholastang on 2017/3/13.
 */
public class EcologyQuestion extends AbstractEntity {
    private static final long serialVersionUID = 8819326745861051373L;
    private Long id = 17605L;
    private String title = "12312";
    private List<String> fiedls = new ArrayList<>();
    private String link = "http://test.bar.focus.cn/q/dl1s2";
    private Integer followedNum = 0;
    private Integer answerNum = 0;
    private List<EcologyAnswer> answers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getFiedls() {
        return fiedls;
    }

    public void setFiedls(List<String> fiedls) {
        this.fiedls = fiedls;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public List<EcologyAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<EcologyAnswer> answers) {
        this.answers = answers;
    }

    @Override
    public Serializable getInternalId() {
        return this.getId();
    }
}
