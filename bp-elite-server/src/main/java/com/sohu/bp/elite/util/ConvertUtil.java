package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtil;

import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteExpertTeam;
import com.sohu.bp.elite.model.TEliteExpertTeamInfo;
import com.sohu.bp.elite.model.TEliteFollow;
import com.sohu.bp.elite.model.TEliteFollowType;
import com.sohu.bp.elite.model.TEliteFragment;
import com.sohu.bp.elite.model.TEliteFragmentType;
import com.sohu.bp.elite.model.TEliteMedia;
import com.sohu.bp.elite.model.TEliteMediaStatus;
import com.sohu.bp.elite.model.TEliteMediaType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSquareItem;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteTopic;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteColumn;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.persistence.EliteExpertTeamInfo;
import com.sohu.bp.elite.persistence.EliteFollow;
import com.sohu.bp.elite.persistence.EliteFragment;
import com.sohu.bp.elite.persistence.EliteMedia;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.persistence.EliteSubject;
import com.sohu.bp.elite.persistence.EliteTopic;
import com.sohu.bp.elite.persistence.EliteUser;

/**
 * @author zhangzhihao
 *         2016/7/16
 */
public class ConvertUtil {

    /*将thrift model 转换成 persistence*/
    public static EliteQuestion convert(TEliteQuestion tEliteQuestion){
        if(tEliteQuestion == null)
            return null;
        EliteQuestion eliteQuestion = new EliteQuestion();
        BeanUtil.copyProperties(tEliteQuestion, eliteQuestion, "createTime","updateTime","publishTime");
        if(tEliteQuestion.getCreateTime() > 0)
            eliteQuestion.setCreateTime(new Date(tEliteQuestion.getCreateTime()));
        if(tEliteQuestion.getUpdateTime() > 0)
            eliteQuestion.setUpdateTime(new Date(tEliteQuestion.getUpdateTime()));
        if(tEliteQuestion.getPublishTime() > 0)
            eliteQuestion.setPublishTime(new Date(tEliteQuestion.getPublishTime()));
        return eliteQuestion;
    }

    public static EliteAnswer convert(TEliteAnswer tEliteAnswer){
        if(tEliteAnswer == null)
            return null;
        EliteAnswer eliteAnswer = new EliteAnswer();
        BeanUtil.copyProperties(tEliteAnswer, eliteAnswer, "createTime","updateTime","publishTime");
        if(tEliteAnswer.getCreateTime() > 0)
            eliteAnswer.setCreateTime(new Date(tEliteAnswer.getCreateTime()));
        if(tEliteAnswer.getUpdateTime() > 0)
            eliteAnswer.setUpdateTime(new Date(tEliteAnswer.getUpdateTime()));
        if(tEliteAnswer.getPublishTime() > 0)
            eliteAnswer.setPublishTime(new Date(tEliteAnswer.getPublishTime()));
        return eliteAnswer;
    }

    public static EliteUser convert(TEliteUser tEliteUser){
        if(tEliteUser == null)
            return null;
        EliteUser eliteUser = new EliteUser();
        BeanUtil.copyProperties(tEliteUser, eliteUser, "birthday", "nick", "lastLoginTime", "firstLoginTime", "id");
        if(tEliteUser.getBirthday() > 0)
            eliteUser.setBirthday(new Date(tEliteUser.getBirthday()));
        if(tEliteUser.getFirstLoginTime() > 0)
            eliteUser.setFirstLoginTime(new Date(tEliteUser.getFirstLoginTime()));
        if(tEliteUser.getLastLoginTime() > 0)
            eliteUser.setLastLoginTime(new Date(tEliteUser.getLastLoginTime()));
        eliteUser.setId(tEliteUser.getBpId());
        return eliteUser;
    }
    
    public static EliteSubject convert(TEliteSubject tEliteSubject){
    	if(tEliteSubject == null)
    		return null;
    	EliteSubject eliteSubject = new EliteSubject();
    	BeanUtil.copyProperties(tEliteSubject, eliteSubject, "createTime","updateTime");
    	if(tEliteSubject.getCreateTime() > 0 )
        	eliteSubject.setCreateTime(new Date(tEliteSubject.getCreateTime()));
        if(tEliteSubject.getUpdateTime() >0)
        	eliteSubject.setUpdateTime(new Date(tEliteSubject.getUpdateTime()));
    	return eliteSubject;
    }
    
    public static EliteTopic convert(TEliteTopic tEliteTopic){
    	if(tEliteTopic == null) return null;
    	EliteTopic eliteTopic =new EliteTopic();
    	BeanUtil.copyProperties(tEliteTopic, eliteTopic, "startTime","endTime","createTime","updateTime");
    	if(tEliteTopic.getCreateTime() > 0)
    		eliteTopic.setCreateTime(new Date(tEliteTopic.getCreateTime()));
    	if(tEliteTopic.getUpdateTime() > 0)
    		eliteTopic.setUpdateTime(new Date(tEliteTopic.getUpdateTime()));
    	if(tEliteTopic.getStartTime() > 0)
    		eliteTopic.setStartTime(new Date(tEliteTopic.getStartTime()));
    	if(tEliteTopic.getEndTime() > 0)
    		eliteTopic.setEndTime(new Date(tEliteTopic.getEndTime()));
    	return eliteTopic;
    }
    
    public static EliteMedia convert(TEliteMedia tEliteMedia){
    	if(tEliteMedia == null) return null;
    	EliteMedia eliteMedia = new EliteMedia();
    	BeanUtil.copyProperties(tEliteMedia, eliteMedia, "type", "updateTime", "uploadTime", "status");
    	eliteMedia.setStatus(tEliteMedia.getStatus().getValue());
    	eliteMedia.setType(tEliteMedia.getType().getValue());
    	if(tEliteMedia.getUpdateTime() > 0)
    		eliteMedia.setUpdateTime(new Date(tEliteMedia.getUpdateTime()));
    	if(tEliteMedia.getUploadTime() > 0)
    		eliteMedia.setUploadTime(new Date(tEliteMedia.getUploadTime()));
    	return eliteMedia;
    }
    
    public static EliteFragment convert(TEliteFragment tEliteFragment){
    	if(null == tEliteFragment) return null;
    	EliteFragment eliteFragment = new EliteFragment();
    	BeanUtil.copyProperties(tEliteFragment, eliteFragment, "type","createTime","updateTime");
    	if(tEliteFragment.getCreateTime() > 0)
    		eliteFragment.setCreateTime(new Date(tEliteFragment.getCreateTime()));
    	if(tEliteFragment.getUpdateTime() > 0)
    		eliteFragment.setUpdateTime(new Date(tEliteFragment.getUpdateTime()));
    	eliteFragment.setType(tEliteFragment.getType().getValue());
    	return eliteFragment;
    }
    
    public static EliteFollow convert(TEliteFollow tEliteFollow){
    	if(null == tEliteFollow) return null;
    	EliteFollow eliteFollow = new EliteFollow();
    	BeanUtil.copyProperties(tEliteFollow, eliteFollow, "type","createTime","updateTime");
    	if(tEliteFollow.getCreateTime() > 0)
    		eliteFollow.setCreateTime(new Date(tEliteFollow.getCreateTime()));
    	if(tEliteFollow.getUpdateTime() > 0)
    		eliteFollow.setUpdateTime(new Date(tEliteFollow.getUpdateTime()));
    	eliteFollow.setType(tEliteFollow.getType().getValue());
    	return eliteFollow;
    }
    
    public static EliteColumn convert(TEliteColumn tEliteColumn){
    	if(null == tEliteColumn) return null;
    	EliteColumn eliteColumn = new EliteColumn();
    	BeanUtil.copyProperties(tEliteColumn, eliteColumn, "createTime", "updateTime", "publishTime");
    	if(tEliteColumn.getCreateTime() > 0)
    		eliteColumn.setCreateTime(new Date(tEliteColumn.getCreateTime()));
    	if(tEliteColumn.getUpdateTime() > 0)
    		eliteColumn.setUpdateTime(new Date(tEliteColumn.getUpdateTime()));
    	if(tEliteColumn.getPublishTime() > 0)
    		eliteColumn.setPublishTime(new Date(tEliteColumn.getPublishTime()));
    	return eliteColumn;
    }
    
    public static EliteExpertTeam convert(TEliteExpertTeam tEliteExpertTeam){
        if (null == tEliteExpertTeam) return null;
        EliteExpertTeam eliteExpertTeam = new EliteExpertTeam();
        BeanUtil.copyProperties(tEliteExpertTeam, eliteExpertTeam, "lastPushTime", "lastAnsweredTime", "bpId");
        eliteExpertTeam.setId(tEliteExpertTeam.getBpId());
        if (tEliteExpertTeam.getLastAnsweredTime() > 0)
            eliteExpertTeam.setLastAnsweredTime(new Date(tEliteExpertTeam.getLastAnsweredTime()));
        if (tEliteExpertTeam.getLastPushTime() > 0)
            eliteExpertTeam.setLastPushTime(new Date(tEliteExpertTeam.getLastPushTime()));
        return eliteExpertTeam;
    }
    
    public static EliteExpertTeamInfo convert(TEliteExpertTeamInfo tEliteExpertTeamInfo) {
        if (null == tEliteExpertTeamInfo) return null;
        EliteExpertTeamInfo eliteExpertTeamInfo = new EliteExpertTeamInfo();
        BeanUtil.copyProperties(tEliteExpertTeamInfo, eliteExpertTeamInfo);
        return eliteExpertTeamInfo;
    }
    
    /*将persistence 转换成 thrift model*/
    public static TEliteQuestion convert(EliteQuestion eliteQuestion){
        if(eliteQuestion == null)
            return null;
        TEliteQuestion tEliteQuestion = new TEliteQuestion();
        BeanUtil.copyProperties(eliteQuestion, tEliteQuestion, "createTime","updateTime","publishTime");
        if(eliteQuestion.getCreateTime() != null)
            tEliteQuestion.setCreateTime(eliteQuestion.getCreateTime().getTime());
        if(eliteQuestion.getUpdateTime() != null)
            tEliteQuestion.setUpdateTime(eliteQuestion.getUpdateTime().getTime());
        if(eliteQuestion.getPublishTime() != null)
            tEliteQuestion.setPublishTime(eliteQuestion.getPublishTime().getTime());
        return tEliteQuestion;
    }

    public static List<TEliteQuestion> convertQuestionList(List<EliteQuestion> questions){
        if(questions == null)
            return null;
        List<TEliteQuestion> tEliteQuestions = new ArrayList<>();
        for(EliteQuestion question : questions){
            tEliteQuestions.add(convert(question));
        }
        return tEliteQuestions;
    }

    public static TEliteAnswer convert(EliteAnswer eliteAnswer){
        if(eliteAnswer == null)
            return null;
        TEliteAnswer tEliteAnswer = new TEliteAnswer();
        BeanUtil.copyProperties(eliteAnswer, tEliteAnswer, "createTime","updateTime","publishTime");
        if(eliteAnswer.getCreateTime() != null)
            tEliteAnswer.setCreateTime(eliteAnswer.getCreateTime().getTime());
        if(eliteAnswer.getUpdateTime() != null)
            tEliteAnswer.setUpdateTime(eliteAnswer.getUpdateTime().getTime());
        if(eliteAnswer.getPublishTime() != null)
            tEliteAnswer.setPublishTime(eliteAnswer.getPublishTime().getTime());
        return tEliteAnswer;
    }

    public static List<TEliteAnswer> convertAnswerList(List<EliteAnswer> answers){
        if(answers == null)
            return null;
        List<TEliteAnswer> tEliteAnswers = new ArrayList<>();
        for(EliteAnswer answer : answers){
            tEliteAnswers.add(convert(answer));
        }
        return tEliteAnswers;
    }

    public static TEliteUser convert(EliteUser eliteUser){
        if(eliteUser == null)
            return null;
        TEliteUser tEliteUser = new TEliteUser();
        BeanUtil.copyProperties(eliteUser, tEliteUser, "bpId", "birthday", "nick", "firstLoginTime", "lastLoginTime");
        if(eliteUser.getBirthday() != null)
            tEliteUser.setBirthday(eliteUser.getBirthday().getTime());
        if(eliteUser.getFirstLoginTime() != null)
            tEliteUser.setFirstLoginTime(eliteUser.getFirstLoginTime().getTime());
        if(eliteUser.getLastLoginTime() != null)
            tEliteUser.setLastLoginTime(eliteUser.getLastLoginTime().getTime());
        tEliteUser.setBpId(eliteUser.getId());
        return tEliteUser;
    }

    public static List<TEliteUser> convertUserList(List<EliteUser> users){
        if(users == null)
            return null;
        List<TEliteUser> tEliteUsers = new ArrayList<>();
        for(EliteUser user : users){
            tEliteUsers.add(convert(user));
        }
        return tEliteUsers;
    }

    public static TEliteSubject convert(EliteSubject eliteSubject){
    	if(eliteSubject == null) return null;
    	TEliteSubject tEliteSubject = new TEliteSubject();
    	BeanUtil.copyProperties(eliteSubject, tEliteSubject, "createTime", "updateTime");
    	if(eliteSubject.getCreateTime() != null)
            tEliteSubject.setCreateTime(eliteSubject.getCreateTime().getTime());
        if(eliteSubject.getUpdateTime() != null)
        	tEliteSubject.setUpdateTime(eliteSubject.getUpdateTime().getTime());
    	return tEliteSubject;
    }
    
    public static TEliteTopic convert(EliteTopic eliteTopic)
    {
    	if(null == eliteTopic) return null;
    	TEliteTopic tEliteTopic = new TEliteTopic();
    	BeanUtil.copyProperties(eliteTopic, tEliteTopic, "createTime","updateTime","startTime","endTime");
    	if(eliteTopic.getCreateTime() != null)
    		tEliteTopic.setCreateTime(eliteTopic.getCreateTime().getTime());
    	if(eliteTopic.getUpdateTime() != null)
    		tEliteTopic.setUpdateTime(eliteTopic.getUpdateTime().getTime());
    	if(eliteTopic.getStartTime() != null)
    		tEliteTopic.setStartTime(eliteTopic.getStartTime().getTime());
    	if(eliteTopic.getEndTime() != null)
    		tEliteTopic.setEndTime(eliteTopic.getEndTime().getTime());
    	return tEliteTopic;
    	
    }
    
    public static TEliteMedia convert(EliteMedia eliteMedia)
    {
    	if(null == eliteMedia) return null;
    	TEliteMedia tEliteMedia = new TEliteMedia();
    	BeanUtil.copyProperties(eliteMedia, tEliteMedia, "type", "updateTime", "uploadTime", "status");
    	if(eliteMedia.getType() != null && eliteMedia.getType() > 0)
    		tEliteMedia.setType(TEliteMediaType.findByValue(eliteMedia.getType()));
    	if(eliteMedia.getStatus() != null && eliteMedia.getStatus() > 0)
    		tEliteMedia.setStatus(TEliteMediaStatus.findByValue(eliteMedia.getStatus()));
    	if(eliteMedia.getUpdateTime() != null)
    		tEliteMedia.setUpdateTime(eliteMedia.getUpdateTime().getTime());
    	if(eliteMedia.getUploadTime() != null)
    		tEliteMedia.setUploadTime(eliteMedia.getUploadTime().getTime());
    	
    	return tEliteMedia;
    }
    
    public static TEliteFragment convert(EliteFragment eliteFragment)
    {
    	if(null == eliteFragment) return null;
    	TEliteFragment tEliteFragment = new TEliteFragment();
    	BeanUtil.copyProperties(eliteFragment, tEliteFragment, "type","createTime","updateTime");
    	if(null != eliteFragment.getType() && eliteFragment.getType() > 0)
    		tEliteFragment.setType(TEliteFragmentType.findByValue(eliteFragment.getType()));
    	if(null != eliteFragment.getCreateTime())
    		tEliteFragment.setCreateTime(eliteFragment.getCreateTime().getTime());
    	if(null != eliteFragment.getUpdateTime())
    		tEliteFragment.setUpdateTime(eliteFragment.getUpdateTime().getTime());
    	return tEliteFragment;
    }
    
    public static TEliteFollow convert(EliteFollow eliteFollow)
    {
    	if(null == eliteFollow) return null;
    	TEliteFollow tEliteFollow = new TEliteFollow();
    	BeanUtil.copyProperties(eliteFollow, tEliteFollow, "type", "createTime", "updateTime");
    	if(null != eliteFollow.getType() && eliteFollow.getType() > 0)
    		tEliteFollow.setType(TEliteFollowType.findByValue(eliteFollow.getType()));
    	if(null != eliteFollow.getCreateTime() && eliteFollow.getCreateTime().getTime() > 0)
    		tEliteFollow.setCreateTime(eliteFollow.getCreateTime().getTime());
    	if(null != eliteFollow.getUpdateTime() && eliteFollow.getUpdateTime().getTime() > 0)
    		tEliteFollow.setUpdateTime(eliteFollow.getUpdateTime().getTime());
    	return tEliteFollow;
    }
    
    public static TEliteColumn convert(EliteColumn eliteColumn){
    	if(null == eliteColumn) return null;
    	TEliteColumn tEliteColumn = new TEliteColumn();
    	BeanUtil.copyProperties(eliteColumn, tEliteColumn, "createTime", "updateTime", "publishTime");
    	if(null != eliteColumn.getCreateTime())
    		tEliteColumn.setCreateTime(eliteColumn.getCreateTime().getTime());
    	if(null != eliteColumn.getUpdateTime())
    		tEliteColumn.setUpdateTime(eliteColumn.getUpdateTime().getTime());
    	if(null != eliteColumn.getPublishTime())
    		tEliteColumn.setPublishTime(eliteColumn.getPublishTime().getTime());
    	return tEliteColumn;
    }
    
    public static TEliteExpertTeam convert(EliteExpertTeam eliteExpertTeam) {
        if (null == eliteExpertTeam) return null;
        TEliteExpertTeam tEliteExpertTeam = new TEliteExpertTeam();
        BeanUtil.copyProperties(eliteExpertTeam, tEliteExpertTeam, "lastPushTime", "lastAnsweredTime", "id");
        tEliteExpertTeam.setBpId(eliteExpertTeam.getId());
        if (null != eliteExpertTeam.getLastAnsweredTime())
            tEliteExpertTeam.setLastAnsweredTime(eliteExpertTeam.getLastAnsweredTime().getTime());
        if (null != eliteExpertTeam.getLastPushTime())
            tEliteExpertTeam.setLastPushTime(eliteExpertTeam.getLastPushTime().getTime());
        return tEliteExpertTeam;
    }
    
    public static TEliteExpertTeamInfo convert(EliteExpertTeamInfo eliteExpertTeamInfo) {
        if (null == eliteExpertTeamInfo) return null;
        TEliteExpertTeamInfo tEliteExpertTeamInfo = new TEliteExpertTeamInfo();
        BeanUtil.copyProperties(eliteExpertTeamInfo, tEliteExpertTeamInfo);
        return tEliteExpertTeamInfo;
    }
    
    public static TEliteSquareItem convert(Long complexId) {
        TEliteSquareItem squareItem = new TEliteSquareItem();
        squareItem.setFeedType(SquareUtil.getFeedType(complexId).getValue());
        squareItem.setFeedId(SquareUtil.getFeedId(complexId));
        return squareItem;
    }
}
