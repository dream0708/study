package com.sohu.bp.elite.action.bean.question;


import java.util.ArrayList;
import java.util.List;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.bean.EliteOptionItemBean;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.HumanityUtil;
import com.sohu.bp.elite.util.ImageUtil;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class EliteQuestionDisplayBean {
    private String id;
    private String bpId;
    private String title;
    private String content;
    private String plainText;
    private List<String> imageList;
    private List<String> imageSmallList;
    private String tagIds;
    private List<TagItemBean> tagList;
    private long publishTime;
    private String publishTimeHuman;
    private long updateTime;
    private String updateTimeHuman;
    private UserDetailDisplayBean user;
    private int answerNum;
    private List<Integer> answerCounts;
    private String answerNumHuman;
    private int fansNum;
    private String fansNumHuman;
    private long videoId;
    private boolean hasFollowed = false;
    private boolean owner = false;
    private int totalChoosedNum;
    private List<EliteOptionItemBean> options;
    private List<List<UserInfo>> choosedUsers;
    private List<UserInfo> followUsers;
    private int specialType;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = ContentUtil.removeContentImageProtocol(content);
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public List<TagItemBean> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagItemBean> tagList) {
        this.tagList = tagList;
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

    public int getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(int answerNum) {
        this.answerNum = answerNum;
    }

    public String getAnswerNumHuman() {
        return HumanityUtil.humanityNumber(answerNum);
    }

    public void setAnswerNumHuman(String answerNumHuman) {
        this.answerNumHuman = answerNumHuman;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public String getFansNumHuman() {
        return HumanityUtil.humanityNumber(fansNum);
    }

    public void setFansNumHuman(String fansNumHuman) {
        this.fansNumHuman = fansNumHuman;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public boolean isHasFollowed() {
        return hasFollowed;
    }

    public void setHasFollowed(boolean hasFollowed) {
        this.hasFollowed = hasFollowed;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
    	if(null != imageList && imageList.size() > 0){
    		List<String> imageSmallList = new ArrayList<>();
    		imageList.forEach(image -> imageSmallList.add(ImageUtil.getSmallImage(image)));
    		setImageSmallList(imageSmallList);
    	}
        this.imageList = ImageUtil.removeImgListProtocol(imageList);
    }

	public List<String> getImageSmallList() {
		return imageSmallList;
	}

	public void setImageSmallList(List<String> imageSmallList) {
		this.imageSmallList = imageSmallList;
	}

    public List<EliteOptionItemBean> getOptions() {
        return options;
    }

    public void setOptions(List<EliteOptionItemBean> options) {
        this.options = options;
    }

    public int getSpecialType() {
        return specialType;
    }

    public void setSpecialType(int specialType) {
        this.specialType = specialType;
    }

    public List<Integer> getAnswerCounts() {
        return answerCounts;
    }

    public void setAnswerCounts(List<Integer> answerCounts) {
        this.answerCounts = answerCounts;
    }

    public int getTotalChoosedNum() {
        return totalChoosedNum;
    }

    public void setTotalChoosedNum(int totalChoosedNum) {
        this.totalChoosedNum = totalChoosedNum;
    }

    public List<List<UserInfo>> getChoosedUsers() {
        return choosedUsers;
    }

    public void setChoosedUsers(List<List<UserInfo>> choosedUsers) {
        this.choosedUsers = choosedUsers;
    }

    public long getSpecialId() {
        return specialId;
    }

    public void setSpecialId(long specialId) {
        this.specialId = specialId;
    }

    public List<UserInfo> getFollowUsers() {
        return followUsers;
    }

    public void setFollowUsers(List<UserInfo> followUsers) {
        this.followUsers = followUsers;
    }
}
