package com.sohu.bp.elite.action.bean.feedItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.bean.EliteOptionItemBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.HumanityUtil;
import com.sohu.bp.elite.util.ImageUtil;

/**
 * 
 * @author nicholastang
 * 2016-08-11 17:36:36
 * TODO
 */
public class SimpleFeedItemBean implements Serializable
{
	private static final long serialVersionUID = -2948116973715432678L;
	private String itemId = "";
	private String questionId;

	private Integer feedType = FeedType.ANSWER.getValue();
	private UserDetailDisplayBean creator;
	private String title = "";
    private String fullContent = "";
	private String content = "";
	//该字段将内容中的图片改为 [图片]
    private String imgContent = "";
    //保留<br>(pc, wap)或/n(wx, app)的字段
    private String formatContent = "";
	private List<EliteOptionItemBean> options;
	private List<String> coverImgList;
	private List<String> coverImgListSmall;
	private List<String> coverImgListMedium;
	private List<String> coverWapImgListSmall;
	private List<SimpleFeedItemBean> commentList;
	
	private long videoId = 0;

	private String produceText = "";
	private ProduceBean produceBean = new ProduceBean();
	
	private TagItemBean tag;
	private UserDetailDisplayBean user;
	
	private Date publishTime;
	private long produceTime = 0;

	private long updateTime = 0;
	private String updateTimeHuman;

	private List<String> picList;

	private Integer answerNum = 0;
	private Integer fansNum = 0;
	private Integer commentNum = 0;
	private Integer likeNum = 0;
	private Integer treadNum = 0;
	private Integer shareNum = 0;
	
	private boolean owner = false;
	private boolean hasFollowed = false;
	private boolean hasLiked = false;
	private boolean hasTread = false;
	private boolean hasFavorited = false;
	
	private String specialId = "";
	private Integer specialType;
	private String specialTitle;
	//用以标识站队的标示A或B
	private String specialLabel;
	
	private String highlightText = "";
	private String highlightWords = "";
	//用于推荐系统
	private String traceId;
	

	public List<String> getCoverImgListSmall() {
		return coverImgListSmall;
	}
	public void setCoverImgListSmall(List<String> coverImgListSmall) {
		this.coverImgListSmall = coverImgListSmall;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public TagItemBean getTag() {
		return tag;
	}
	public void setTag(TagItemBean tag) {
		this.tag = tag;
	}
	public Integer getFeedType() {
		return feedType;
	}
	public void setFeedType(Integer feedType) {
		this.feedType = feedType;
	}
	public UserDetailDisplayBean getCreator() {
		return creator;
	}
	public void setCreator(UserDetailDisplayBean creator) {
		this.creator = creator;
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
		this.content = content;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public String getPublishTimeHuman() {
		if(null == publishTime)
			return "";
		else
			return HumanityUtil.humanityTime(publishTime.getTime());
	}

	public Integer getAnswerNum() {
		return answerNum;
	}
	public void setAnswerNum(Integer answerNum) {
		this.answerNum = answerNum;
	}
	public String getAnswerNumHuman() {
		if(null == answerNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(answerNum);
	}
	
	public Integer getFansNum() {
		return fansNum;
	}
	public void setFansNum(Integer fansNum) {
		this.fansNum = fansNum;
	}
	public String getFansNumHuman() {
		if(null == fansNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(fansNum);
	}
	
	public Integer getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(Integer commentNum) {
		this.commentNum = commentNum;
	}
	public String getCommentNumHuman() {
		if(null == commentNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(commentNum);
	}
	
	public Integer getLikeNum() {
		return likeNum;
	}
	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
	}
	public String getLikeNumHuman() {
		if(null == likeNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(likeNum);
	}
	
	public Integer getShareNum() {
		return shareNum;
	}
	public void setShareNum(Integer shareNum) {
		this.shareNum = shareNum;
	}
	public String getShareNumHuman() {
		if(null == shareNum)
			return "0";
		else
			return HumanityUtil.humanityNumber(shareNum);
	}

	public UserDetailDisplayBean getUser() {
		return user;
	}
	public void setUser(UserDetailDisplayBean user) {
		this.user = user;
	}
	public List<String> getCoverImgList() {
		return coverImgList;
	}
	public void setCoverImgList(List<String> coverImgList) {
		//用于转成小图
		List<String> coverImgListSmall = new ArrayList<>();
		List<String> coverWapImgListSmall = new ArrayList<>();
		List<String> coverImgListMedium = new ArrayList<>();
		if(null != coverImgList){
			for(String coverImg : coverImgList){
				String imgSmall = ImageUtil.getSmallImage(coverImg, Constants.LITTLE_FEEDIMG, null);
				coverImgListSmall.add(imgSmall);
				String wapImgSmall = ImageUtil.getSmallImage(coverImg);
				coverWapImgListSmall.add(wapImgSmall);
				String imgMedium = ImageUtil.getSmallImage(coverImg, null, Constants.MEDIUM_IMAGE_RATIO);
				coverImgListMedium.add(imgMedium);
			}
		}
		
		setCoverImgListSmall(coverImgListSmall);
		setCoverImgListMedium(coverImgListMedium);
		setCoverWapImgListSmall(coverWapImgListSmall);
		this.coverImgList = ImageUtil.removeImgListProtocol(coverImgList);
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public List<String> getPicList() {
		return picList;
	}

	public void setPicList(List<String> picList) {
		this.picList = picList;
	}
	public String getProduceText() {
		return produceBean.getProduceText();
	}
	public void setProduceText(String produceText) {
		this.produceText = produceText;
	}
	public long getProduceTime() {
		return produceTime;
	}
	public void setProduceTime(long produceTime) {
		this.produceTime = produceTime;
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

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getFullContent() {
		return fullContent;
	}

	public void setFullContent(String fullContent) {
		this.fullContent = ContentUtil.removeContentImageProtocol(fullContent);
	}

	public long getVideoId() {
		return videoId;
	}

	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}
	public boolean getOwner() {
		return owner;
	}
	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isHasFollowed() {
		return hasFollowed;
	}

	public void setHasFollowed(boolean hasFollowed) {
		this.hasFollowed = hasFollowed;
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
	public List<SimpleFeedItemBean> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<SimpleFeedItemBean> commentList) {
		this.commentList = commentList;
	}
	public List<String> getCoverWapImgListSmall() {
		return coverWapImgListSmall;
	}
	public void setCoverWapImgListSmall(List<String> coverWapImgListSmall) {
		this.coverWapImgListSmall = coverWapImgListSmall;
	}
	public String getSpecialId() {
		return specialId;
	}
	public void setSpecialId(String specialId) {
		this.specialId = specialId;
	}
	public Integer getSpecialType() {
		return specialType;
	}
	public void setSpecialType(Integer specialType) {
		this.specialType = specialType;
	}
	public String getSpecialTitle() {
		return specialTitle;
	}
	public void setSpecialTitle(String specialTitle) {
		this.specialTitle = specialTitle;
	}
	public List<String> getCoverImgListMedium() {
		return coverImgListMedium;
	}
	public void setCoverImgListMedium(List<String> coverImgListMedium) {
		this.coverImgListMedium = coverImgListMedium;
	}

	public ProduceBean getProduceBean() {
		return produceBean;
	}

	public void setProduceBean(ProduceBean produceBean) {
		this.produceBean = produceBean;
	}
    public String getHighlightText() {
        return highlightText;
    }
    public void setHighlightText(String highlightText) {
        this.highlightText = highlightText;
    }
    public String getHighlightWords() {
        return highlightWords;
    }
    public void setHighlightWords(String highlightWords) {
        this.highlightWords = highlightWords;
    }
    public List<EliteOptionItemBean> getOptions() {
        return options;
    }
    public void setOptions(List<EliteOptionItemBean> options) {
        this.options = options;
    }
	public String getSpecialLabel() {
		return specialLabel;
	}
	public void setSpecialLabel(String specialLabel) {
		this.specialLabel = specialLabel;
	}
    public String getImgContent() {
        return imgContent;
    }
    public void setImgContent(String imgContent) {
        this.imgContent = imgContent;
    }
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
    public Integer getTreadNum() {
        return treadNum;
    }
    public void setTreadNum(Integer treadNum) {
        this.treadNum = treadNum;
    }
    public boolean isHasTread() {
        return hasTread;
    }
    public void setHasTread(boolean hasTread) {
        this.hasTread = hasTread;
    }
    public String getFormatContent() {
        return formatContent;
    }
    public void setFormatContent(String formatContent) {
        this.formatContent = formatContent;
    }
}