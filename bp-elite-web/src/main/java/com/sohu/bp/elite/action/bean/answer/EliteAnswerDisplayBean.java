package com.sohu.bp.elite.action.bean.answer;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.HumanityUtil;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class EliteAnswerDisplayBean {
    private String id;
    private String bpId;
    private String questionTitle;
    private String questionId;
    private String content;
    private String tagIds;
    private long publishTime;
    private String publishTimeHuman;
    private long updateTime;
    private String updateTimeHuman;
    private UserDetailDisplayBean user;
    private int commentNum;
    private String commentNumHuman;
    private int likeNum;
    private String likeNumHuman;
    private int treadNum;
    private String treadNumHuman;
    private long videoId;
    private boolean owner = false;
    private boolean hasLiked = false;
    private boolean hasTreaded = false;
    private boolean hasFavorited = false;
    private long specialId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBpId() {
        return bpId;
    }

    public void setBpId(String bpId) {
        this.bpId = bpId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = ContentUtil.removeContentImageProtocol(content);
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

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTimeHuman() {
        return HumanityUtil.humanityTime(updateTime);
    }

    public void setUpdateTimeHuman(String updateTimeHuman) {
        this.updateTimeHuman = updateTimeHuman;
    }

    public UserDetailDisplayBean getUser() {
        return user;
    }

    public void setUser(UserDetailDisplayBean user) {
        this.user = user;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getCommentNumHuman() {
        return HumanityUtil.humanityNumber(commentNum);
    }

    public void setCommentNumHuman(String commentNumHuman) {
        this.commentNumHuman = commentNumHuman;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getLikeNumHuman() {
        return HumanityUtil.humanityNumber(likeNum);
    }

    public void setLikeNumHuman(String likeNumHuman) {
        this.likeNumHuman = likeNumHuman;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public boolean isHasFavorited() {
        return hasFavorited;
    }

    public void setHasFavorited(boolean hasFavorited) {
        this.hasFavorited = hasFavorited;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public long getSpecialId() {
        return specialId;
    }

    public void setSpecialId(long specialId) {
        this.specialId = specialId;
    }

    public int getTreadNum() {
        return treadNum;
    }

    public void setTreadNum(int treadNum) {
        this.treadNum = treadNum;
    }

    public boolean isHasTreaded() {
        return hasTreaded;
    }

    public void setHasTreaded(boolean hasTreaded) {
        this.hasTreaded = hasTreaded;
    }

    public String getTreadNumHuman() {
        return HumanityUtil.humanityNumber(treadNum);
    }

    public void setTreadNumHuman(String treadNumHuman) {
        this.treadNumHuman = treadNumHuman;
    }
}

