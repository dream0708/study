package com.sohu.bp.elite.action.bean.question;

import java.util.List;

import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.bean.EliteOptionItemBean;

/**
 * 修改问题的bean
 * @author zhijungou
 * 2017年4月18日
 */
public class EliteQuestionUpdateBean {
    private String questionId;
    private String title;
    private String content;
    private String plainText;
    private List<String> imageList;
    private List<TagItemBean> tagList;
    private Long videoId;
    private String vId;
    private Integer specialType;
    private String specialId;
    private String options;
    
    public String getQuestionId() {
        return questionId;
    }
    public EliteQuestionUpdateBean setQuestionId(String questionId) {
        this.questionId = questionId;
        return this;
    }
    public String getTitle() {
        return title;
    }
    public EliteQuestionUpdateBean setTitle(String title) {
        this.title = title;
        return this;
    }
    public String getContent() {
        return content;
    }
    public EliteQuestionUpdateBean setContent(String content) {
        this.content = content;
        return this;
    }
    public String getPlainText() {
        return plainText;
    }
    public EliteQuestionUpdateBean setPlainText(String plainText) {
        this.plainText = plainText;
        return this;
    }
    public List<String> getImageList() {
        return imageList;
    }
    public EliteQuestionUpdateBean setImageList(List<String> imageList) {
        this.imageList = imageList;
        return this;
    }
    public List<TagItemBean> getTagList() {
        return tagList;
    }
    public EliteQuestionUpdateBean setTagList(List<TagItemBean> tagList) {
        this.tagList = tagList;
        return this;
    }
    public Long getVideoId() {
        return videoId;
    }
    public EliteQuestionUpdateBean setVideoId(Long videoId) {
        this.videoId = videoId;
        return this;
    }
    public String getvId() {
        return vId;
    }
    public EliteQuestionUpdateBean setvId(String vId) {
        this.vId = vId;
        return this;
    }
    public Integer getSpecialType() {
        return specialType;
    }
    public EliteQuestionUpdateBean setSpecialType(Integer specialType) {
        this.specialType = specialType;
        return this;
    }
    public String getSpecialId() {
        return specialId;
    }
    public EliteQuestionUpdateBean setSpecialId(String specialId) {
        this.specialId = specialId;
        return this;
    }
    public String getOptions() {
        return options;
    }
    public EliteQuestionUpdateBean setOptions(String options) {
        this.options = options;
        return this;
    }
}
