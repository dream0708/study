package com.sohu.bp.elite.util;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtil;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.sohu.achelous.model.Feed;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.action.bean.answer.EliteAnswerDisplayBean;
import com.sohu.bp.elite.action.bean.company.CompanyItemBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureItemBean;
import com.sohu.bp.elite.action.bean.feedItem.ProduceBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.bean.ContentBean;
import com.sohu.bp.elite.bean.EliteOptionItemBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteVSAttitudeType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteMedia;
import com.sohu.bp.elite.model.TEliteMediaType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TEliteSquareItem;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteTopic;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.service.external.impl.UserInfoServiceImpl;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.ColumnService;
import com.sohu.bp.elite.task.EliteParallelPool;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import cn.focus.rec.model.ItemType;
import cn.focus.rec.model.RecItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
public class ConvertUtil {
	private static Logger log = LoggerFactory.getLogger(ConvertUtil.class);

	private static EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();

    private static BpUserService bpUserService;
    private static ColumnService columnService;
    private static UserInfoServiceImpl userInfoService;

    public void setBpUserService(BpUserService bpUserService) {
        this.bpUserService = bpUserService;
    }
    public void setColumnService(ColumnService columnService){
    	this.columnService = columnService;
    }
    public void setUserInfoService(UserInfoServiceImpl userInfoService) {
        this.userInfoService = userInfoService;
    }
    
    public static EliteQuestionDisplayBean convert(TEliteQuestion question, Long bpId, TEliteSourceType source) {
        EliteQuestionDisplayBean bean = new EliteQuestionDisplayBean();
        BeanUtil.copyProperties(question, bean, "id","bpId","detail");

        if(EliteStatusUtil.isInvalidStatus(question)){
            bean.setTitle("问题已关闭");
        }else {
            bean.setContent(question.getDetail());
        }

        ContentBean contentBean = ContentUtil.parseContent(question.getDetail(), source);
        bean.setPlainText(contentBean.getPlainText());
        bean.setImageList(contentBean.getImgList());
        if (Objects.equals(source, TEliteSourceType.WRITE_MOBILE) || Objects.equals(source, TEliteSourceType.WRITE_APP)) {
            bean.setContent(ContentUtil.changeContentImgs(question.getDetail()));
        }
        
//        bean.setUser(bpUserService.getUserDetailByBpId(question.getBpId(), bpId));

        bean.setUser(bpUserService.getUserSimpleByBpId(question.getBpId()));
        
        bean.setId(IDUtil.encodeId(question.getId()));
        bean.setBpId(IDUtil.encodeId(question.getBpId()));
        int[] nums = InteractionUtil.getInteractionNumsForQuestion(question.getId());
        bean.setFansNum(nums[0]);
        bean.setAnswerNum(nums[1]);
        
        bean.setTagList(convertToTagItemBeanList(question.getTagIds(), bpId, true, false));
        bean.setHasFollowed(InteractionUtil.hasFollowed(bpId, BpType.Elite_User.getValue(), question.getId(), BpType.Question.getValue()));
        
        int specialType = question.getSpecialType();
        bean.setSpecialType(specialType);
        bean.setSpecialId(question.getSpecialId());
        if (EliteSpecialUtil.isChooseType(question)) {
            List<EliteOptionItemBean> options = convertToOptionList(question);
            bean.setOptions(options);
            Integer num = options.stream().map(EliteOptionItemBean::getCount).reduce(Integer::sum).orElse(0);
            bean.setTotalChoosedNum(num);
        }
        
        if (EliteSpecialUtil.isVSType(question)) {
            int[] answerNums = InteractionUtil.getInteractionNumsForOption(question.getId());
            List<Integer> answerCounts = new ArrayList<Integer>();
            answerCounts.add(answerNums[0]);
            answerCounts.add(answerNums[1]);
            answerCounts.add(nums[1] - answerNums[0] - answerNums[1]);
            bean.setAnswerCounts(answerCounts);
            bean.setChoosedUsers(getChoosedUserList(question));
        }
        
        if(bpId != null && question.getBpId() == bpId){
           bean.setOwner(true);
        }

        List<TEliteMedia> medias = null;
        try {
            medias = eliteAdapter.getMediaListByQuestionIdAndType(question.getId(), TEliteMediaType.VIDEO);
        } catch (TException e) {
            log.error("", e);
        }
        if(medias != null && medias.size() > 0){
            bean.setVideoId(medias.get(0).getMediaGivenId());
        }else {
            bean.setVideoId(0);
        }
        if (source == TEliteSourceType.WRITE_APP) {
            //添加问题的关注人
            List<Long> ids = InteractionUtil.getFollowUserList(question.getId(), BpType.Question, 0, Constants.DEFAULT_QUESTION_FOLLOW_NUM);
            List<UserInfo> followUsers = ids.stream().map(userInfoService::getUserInfoByBpid).collect(Collectors.toList());
            bean.setFollowUsers(followUsers);
        }

        return bean;
    }
    
    /**
     * 获取基本的bean
     * @param question
     * @return
     */
    public static EliteQuestionDisplayBean convertSimple(TEliteQuestion question, TEliteSourceType source) {
        EliteQuestionDisplayBean bean = new EliteQuestionDisplayBean();
        BeanUtil.copyProperties(question, bean, "id","bpId","detail");
        bean.setId(IDUtil.encodeId(question.getId()));
        if(EliteStatusUtil.isInvalidStatus(question)){
            bean.setTitle("问题已关闭");
        }else {
            bean.setContent(question.getDetail());
        }
        
        ContentBean contentBean = ContentUtil.parseContent(question.getDetail(), null);
        bean.setPlainText(contentBean.getPlainText());
        
        int[] nums = InteractionUtil.getInteractionNumsForQuestion(question.getId());
        bean.setFansNum(nums[0]);
        bean.setAnswerNum(nums[1]);
        if (source == TEliteSourceType.WRITE_MOBILE) {
            bean.setTagList(convertToTagItemBeanList(question.getTagIds(), null, true, false));
        }
        return bean;
    }

    public static EliteAnswerDisplayBean convert(TEliteAnswer answer, Long bpId, TEliteSourceType source) {
        EliteAnswerDisplayBean bean = new EliteAnswerDisplayBean();

        BeanUtil.copyProperties(answer, bean, "id", "bpId", "questionId", "content");
        
        if (EliteStatusUtil.isInvalidStatus(answer)){
            bean.setContent("回答已删除");
        } else {
            bean.setContent(answer.getContent());
        }
        if (Objects.equals(source, TEliteSourceType.WRITE_MOBILE) || Objects.equals(source, TEliteSourceType.WRITE_APP)) {
            bean.setContent(ContentUtil.changeContentImgs(answer.getContent()));
        }
        UserDetailDisplayBean user = bpUserService.getUserSimpleByBpId(answer.getBpId());
        user.setHasFollowed(InteractionUtil.hasFollowed(bpId, BpType.Elite_User.getValue(), answer.getBpId(), BpType.Elite_User.getValue()));
        bean.setUser(user);
        bean.setId(IDUtil.encodeId(answer.getId()));
        bean.setBpId(IDUtil.encodeId(answer.getBpId()));
        bean.setQuestionId(IDUtil.encodeId(answer.getQuestionId()));
        bean.setHasLiked(InteractionUtil.hasLiked(bpId, answer.getId()));
        bean.setHasTreaded(InteractionUtil.hasTread(bpId, answer.getId()));
        bean.setHasFavorited(InteractionUtil.hasFavorited(bpId, answer.getId()));
        bean.setSpecialId(answer.getSpecialId());
        int[] nums = InteractionUtil.getInteractionNumsForAnswer(answer.getId());
        bean.setLikeNum(nums[0]);
        bean.setTreadNum(nums[1]);
        bean.setCommentNum(nums[2]);
        if(bpId != null && answer.getBpId() == bpId){
            bean.setOwner(true);
        }

        List<TEliteMedia> medias = null;
        try {
            medias = eliteAdapter.getMediaListByAnswerIdAndType(answer.getId(), TEliteMediaType.VIDEO);
        } catch (TException e) {
            log.error("", e);
        }
        if(medias != null && medias.size() > 0){
            bean.setVideoId(medias.get(0).getMediaGivenId());
        }else {
            bean.setVideoId(0);
        }

        TEliteQuestion question = null;
        try {
            question = eliteAdapter.getQuestionById(answer.getQuestionId());
        } catch (TException e) {
            log.error("", e);
        }
        if(EliteStatusUtil.isInvalidStatus(question)){
            bean.setQuestionTitle("问题已关闭");
        }else {
            bean.setQuestionTitle(question.getTitle());
        }

        return bean;
    }

    public static UserDetailDisplayBean convert(TEliteUser user, Long viewerId) {
        UserDetailDisplayBean userDetail = bpUserService.getUserDetailByBpId(user.getBpId(), viewerId);
        userDetail.setHighlightText(user.getHighlightText());
        userDetail.setHighlightWords(user.getHighlightWords());
//        if(EliteStatusUtil.isInvalidStatus(user)){
//            userDetail.setNick("用户已删除");
//        }
        return userDetail;
    }

    public static TagItemBean convertToTagItemBean(int tagId, Long bpId, boolean needStatistic){
        Tag tag = null;
        try {
            tag = decorationServiceAdapter.getTagById(tagId);
        } catch (TException e) {
            log.error("", e);
        }
        if(tag != null){
            return convertToTagItemBean(tag, bpId, needStatistic);
        }

        return null;
    }

    public static TagItemBean convertToTagItemBean(Tag tag, Long bpId, boolean needStatistic){
        TagItemBean bean = new TagItemBean();

        bean.setTagId(IDUtil.encodeId(tag.getId()));
        if(EliteStatusUtil.isInvalidStatus(tag)){
            //bean.setTagName("标签已删除");
            return null;
        }

        bean.setTagName(tag.getName());
        bean.setTagDesc(tag.getDescription());
        bean.setTagCover(tag.getPic());
        bean.setTagStatus(tag.getStatus().getValue());
        
        if (needStatistic) {
            int[] nums = InteractionUtil.getInteractionNumsForTag(tag.getId());
            bean.setFansNum(nums[0]);
            bean.setQuestionNum(nums[1]);
            bean.setAnswerNum(nums[2]);
        }
        bean.setHasFollowed(InteractionUtil.hasFollowed(bpId, BpType.Elite_User.getValue(), (long)tag.getId(), BpType.Tag.getValue()));

        return bean;
    }

    public static List<TagItemBean> convertToTagItemBeanList(List<Tag> tags, Long bpId, boolean needStatistic){
        List<TagItemBean> tagItemBeanList =  new ArrayList<>();
        if(tags != null && tags.size() > 0){
//            for(Tag tag : tags){
//                TagItemBean tagItemBean = convertToTagItemBean(tag, bpId);
//                if(null != tagItemBean)
//                    tagItemBeanList.add(tagItemBean);
//            }
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                    tags.parallelStream().map(tag -> {
                        return convertToTagItemBean(tag, bpId, needStatistic);
                    }).filter(Objects::nonNull).forEachOrdered(tagItemBeanList::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }

        return tagItemBeanList;
    }

    public static List<TagItemBean> convertToTagItemBeanList(String tagIds, Long bpId){
        return convertToTagItemBeanList(tagIds, bpId, true, true);
    }

    public static List<TagItemBean> convertToTagItemBeanList(String tagIds, Long bpId, boolean getAreaTag, boolean needStatistic) {
        List<Tag> tags = new ArrayList<>();
        if(StringUtils.isNotBlank(tagIds)) {
            String[] tagIdArray = tagIds.split(Constants.TAG_IDS_SEPARATOR);
            
            for (String tagId : tagIdArray) {
                if (StringUtils.isBlank(tagId)) {
                    continue;
                }
                try {
                    Tag tag = decorationServiceAdapter.getTagById(Integer.parseInt(tagId));
                    if (getAreaTag || (tag.getType() != TagType.ELITE_LOCATION_TAG)) {
                        tags.add(tag);
                    }
                } catch (TException e) {
                    log.error("", e);
                }
            }
        }

        return convertToTagItemBeanList(tags, bpId, needStatistic);
    }

    public static SimpleFeedItemBean convertToSimpleFeedItemBean(TEliteQuestion question, Map<Integer, List<Long>> summary, Long viewerId, TEliteSourceType source){
        SimpleFeedItemBean bean = new SimpleFeedItemBean();
        bean.setFeedType(FeedType.QUESTION.getValue());
        bean.setItemId(IDUtil.encodeId(question.getId()));
        ContentBean contentBean = ContentUtil.parseContent(question.getDetail(), source);
        if(EliteStatusUtil.isInvalidStatus(question)){
            bean.setTitle("问题已关闭");
        }else {
            bean.setTitle(HtmlUtils.htmlUnescape(question.getTitle()));
            bean.setContent(contentBean.getPlainText());
            bean.setFormatContent(contentBean.getFormatText());
            bean.setCoverImgList(contentBean.getImgList());
            bean.setSpecialType(question.getSpecialType());
            bean.setSpecialId(String.valueOf(question.getSpecialId()));
            if (question.getSpecialType() == BpType.Elite_Column.getValue()) {
                TEliteColumn column = columnService.getColumnById(question.getSpecialId());
                bean.setSpecialTitle(column.getName());
                bean.setSpecialId(IDUtil.encodeId(question.getSpecialId()));
            } 
            bean.setOptions(convertToOptionList(question));
        }

        bean.setPublishTime(new Date(question.getPublishTime()));
        bean.setUpdateTime(question.getUpdateTime());
        if (Objects.equals(TEliteSourceType.WRITE_APP, source)) {
            bean.setUser(bpUserService.getUserBaseByBpId(question.getBpId()));
            int[] nums = InteractionUtil.getInteractionNumsForQuestion(question.getId());
            bean.setFansNum(nums[0]);
            bean.setAnswerNum(nums[1]);
        } else {
            bean.setUser(bpUserService.getUserSimpleByBpId(question.getBpId()));
            int[] nums = InteractionUtil.getInteractionNumsForQuestion(question.getId());
            bean.setFansNum(nums[0]);
            bean.setAnswerNum(nums[1]);
            bean.setHasFollowed(InteractionUtil.hasFollowed(viewerId, BpType.Elite_User.getValue(), question.getId(), BpType.Question.getValue()));
        }
        if(null != viewerId && question.getBpId() == viewerId)  {
            bean.setOwner(true);
        }
        bean.setProduceBean(ProduceUtil.getProduceInfo(summary, viewerId));

        bean.setHighlightText(question.getHighlightText());
        bean.setHighlightWords(question.getHighlightWords());
        return bean;
    }
    
    public static SimpleFeedItemBean convertToSimpleFeedItemBean(TEliteAnswer answer, Map<Integer, List<Long>> summary, Long viewerId, TEliteSourceType source){
        SimpleFeedItemBean bean = new SimpleFeedItemBean();
        bean.setFeedType(FeedType.ANSWER.getValue());
        bean.setItemId(IDUtil.encodeId(answer.getId()));
        ContentBean contentBean = ContentUtil.parseContent(answer.getContent(), source);
        try {
            TEliteQuestion question = eliteAdapter.getQuestionById(answer.getQuestionId());
            if(EliteStatusUtil.isInvalidStatus(question)){
                bean.setTitle("问题已关闭");
            }else {
                bean.setTitle(HtmlUtils.htmlUnescape(question.getTitle()));
            }
            bean.setSpecialType(answer.getSpecialType());
            if (question.getSpecialType() == BpType.Elite_Column.getValue()) {
                bean.setSpecialId(IDUtil.encodeId(question.getSpecialId()));
                TEliteColumn column = columnService.getColumnById(question.getSpecialId());
                bean.setSpecialTitle(column.getName());
            } else if (question.getSpecialType() == BpType.Elite_VS.getValue()) {
                bean.setSpecialId(IDUtil.encodeId(answer.getSpecialId()));
                if (answer.getSpecialId() == EliteVSAttitudeType.POS.getValue()) {
                    bean.setSpecialLabel(Constants.VS_POS_ANSWER_LABEL);
                } else if (answer.getSpecialId() == EliteVSAttitudeType.NEG.getValue()) {
                    bean.setSpecialLabel(Constants.VS_NEG_ANSWER_LABEL);
                }
            } else if (question.getSpecialType() == BpType.Elite_Vote.getValue()) {
                bean.setSpecialId(IDUtil.encodeId(answer.getSpecialId()));
                bean.setSpecialLabel(String.valueOf(answer.getSpecialId()));
            }
            bean.setQuestionId(IDUtil.encodeId(question.getId()));
        } catch (TException e) {
            log.error("", e);
        }
        
        if (EliteStatusUtil.isInvalidStatus(answer)){
            bean.setContent("回答已删除");
            bean.setCoverImgListSmall(new ArrayList<String>());
        } else {
            bean.setContent(contentBean.getPlainText());
            bean.setCoverImgList(contentBean.getImgList());
            bean.setImgContent(contentBean.getImgText());
            bean.setFormatContent(contentBean.getFormatText());
        }
        if (Objects.equals(TEliteSourceType.WRITE_APP, source)) {
            bean.setUser(bpUserService.getUserBaseByBpId(answer.getBpId()));
            int[] nums = InteractionUtil.getInteractionNumsForAnswer(answer.getId());
            bean.setLikeNum(nums[0]);
            bean.setTreadNum(nums[1]);
            bean.setCommentNum(nums[2]);
        } else {
            bean.setUser(bpUserService.getUserSimpleByBpId(answer.getBpId()));
            int[] nums = InteractionUtil.getInteractionNumsForAnswer(answer.getId());
            bean.setLikeNum(nums[0]);
            bean.setTreadNum(nums[1]);
            bean.setCommentNum(nums[2]);
            bean.setHasLiked(InteractionUtil.hasLiked(viewerId, answer.getId()));
            bean.setHasFavorited(InteractionUtil.hasFavorited(viewerId, answer.getId()));
        }
        bean.setPublishTime(new Date(answer.getPublishTime()));
        bean.setUpdateTime(answer.getUpdateTime());
        bean.setProduceBean(ProduceUtil.getProduceInfo(summary, viewerId));
        bean.setFullContent(answer.getContent());
        bean.setHighlightText(answer.getHighlightText());
        bean.setHighlightWords(answer.getHighlightWords());
        try {
            List<TEliteMedia> medias = eliteAdapter.getMediaListByAnswerIdAndType(answer.getId(), TEliteMediaType.VIDEO);
            if(medias != null && medias.size() > 0){
                bean.setVideoId(medias.get(0).getMediaGivenId());
            }
        } catch (TException e) {
            log.error("", e);
        }

        if(null != viewerId && answer.getBpId() == viewerId) {
        	bean.setOwner(true);
        }
        return bean;
    }
    
    public static SimpleFeedItemBean convertToSimpleFeedItemBean(RecItem recItem, Long viewerId, TEliteSourceType source) {
        long feedId = recItem.getItemId();
        ItemType type = recItem.getItemType();
        FeedType feedType = FeedType.QUESTION;
        if (Objects.equals(type, ItemType.ANSWER)) {
            feedType = FeedType.ANSWER;
        }
        SimpleFeedItemBean bean = convertToSimpleFeedItemBean(feedId, feedType, viewerId, source);
        if (null != bean) {
            bean.setTraceId(recItem.getTraceId());
        }
        return bean;
    }
    
    public static SimpleFeedItemBean convertToSimpleFeedItemBean(TEliteSquareItem item, Long viewerId, TEliteSourceType source){
        long feedId = item.getFeedId();
        FeedType feedType = FeedType.getFeedTypeByValue(item.getFeedType());
        return convertToSimpleFeedItemBean(feedId, feedType, viewerId, source);
    }
    
    public static SimpleFeedItemBean convertToSimpleFeedItemBean(Long feedId, FeedType feedType, Long viewerId, TEliteSourceType source) {
        SimpleFeedItemBean bean = null;
        ProduceBean produceBean = new ProduceBean();
        ProduceActionType produceActionType = ProduceActionType.UPDATE;
        boolean isUpdate = false;
        try{
            switch (feedType) {
            case QUESTION :
                TEliteQuestion question = eliteAdapter.getQuestionById(feedId);
                if (null == question || question.getId() <= 0) break;
                bean = convertToSimpleFeedItemBean(question, null, viewerId, source);
                produceBean.setProduceAvatar(bean.getUser().getAvatar());
                produceBean.setProduceLink(bean.getUser().getHomeUrl());
                produceBean.setProduceIdentity(bean.getUser().getIdentity());
                produceBean.setProduceIdentityString(bean.getUser().getIdentityString());
                produceBean.setBpIdOrigin(bean.getUser().getBpIdOrigin());
                produceBean.setCompanyIdOrigin(bean.getUser().getCompanyIdOrigin());
                produceBean.setBpUserType(bean.getUser().getBpUserType());
                isUpdate = question.getUpdateTime() > question.getCreateTime();
                if (!isUpdate) {
                    if (question.getSpecialType() == BpType.Elite_Vote.getValue()) {
                        produceActionType = ProduceActionType.PUBLISH_VOTE;
                    } else if(question.getSpecialType() == BpType.Elite_VS.getValue()) {
                        produceActionType = ProduceActionType.PUBLISH_VS;
                    } else {
                        produceActionType = ProduceActionType.ASK;
                    }
                }
                produceBean.setProduceText(SquareUtil.getProduceText(bean.getUser().getNick(), produceActionType));
                bean.setProduceBean(produceBean);
                bean.setPublishTime(new Date(question.getUpdateTime()));
                break;
            case ANSWER :
                TEliteAnswer answer = eliteAdapter.getAnswerById(feedId);
                if (null == answer || answer.getId() <= 0) break;
                bean = convertToSimpleFeedItemBean(answer, null, viewerId, source);
                produceBean.setProduceAvatar(bean.getUser().getAvatar());
                produceBean.setProduceLink(bean.getUser().getHomeUrl());
                produceBean.setProduceIdentity(bean.getUser().getIdentity());
                produceBean.setProduceIdentityString(bean.getUser().getIdentityString());
                produceBean.setBpIdOrigin(bean.getUser().getBpIdOrigin());
                produceBean.setCompanyIdOrigin(bean.getUser().getCompanyIdOrigin());
                produceBean.setBpUserType(bean.getUser().getBpUserType());
                isUpdate = answer.getUpdateTime() > answer.getCreateTime();
                if (answer.getStatus() == EliteAnswerStatus.CHOOSE.getValue()) {
                    if (answer.getSpecialType() == BpType.Elite_Vote.getValue()) {
                        produceActionType = ProduceActionType.VOTE;
                    } else if (answer.getSpecialType() == BpType.Elite_VS.getValue()) {
                        produceActionType = ProduceActionType.VS;
                    }
                } else if (answer.getSpecialId() > 0) {
                    if (answer.getSpecialType() == BpType.Elite_Vote.getValue()) {
                        produceActionType = ProduceActionType.VOTE_AND_ANSWER;
                    } else if (answer.getSpecialType() == BpType.Elite_VS.getValue()) {
                        produceActionType = ProduceActionType.VS_AND_COMMENT;
                    } else {
                        if (!isUpdate) {
                            produceActionType = ProduceActionType.ANSWER;
                        }
                    }
                } else {
                    if (answer.getSpecialType() == BpType.Elite_Vote.getValue()) {
                        produceActionType = ProduceActionType.ANSWER;
                    } else if (answer.getSpecialType() == BpType.Elite_VS.getValue()) {
                        produceActionType = ProduceActionType.COMMENT;
                    } else {
                        if (!isUpdate) {
                            produceActionType = ProduceActionType.ANSWER;
                        }
                    }
                }

                produceBean.setProduceText(SquareUtil.getProduceText(bean.getUser().getNick(), produceActionType));
                bean.setProduceBean(produceBean);
                bean.setPublishTime(new Date(answer.getUpdateTime()));
                break;
                
            }
        } catch (Exception e){
            log.error("", e);
        }
            return bean;
    }
    
    public static List<SimpleFeedItemBean> convertSquareItemList(List<TEliteSquareItem> items, Long viewerId, TEliteSourceType source) {
        List<SimpleFeedItemBean> list = new ArrayList<>();
        if (null != items && items.size() > 0) {
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                    items.parallelStream().map(item -> {
                        return convertToSimpleFeedItemBean(item, viewerId, source);
                    }).filter(Objects::nonNull).forEachOrdered(list::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return list;
    }
    
    
    public static List<SimpleFeedItemBean> convertRecItemList(List<RecItem> items, Long viewerId, TEliteSourceType source) {
        List<SimpleFeedItemBean> list = new ArrayList<>();
        if (null != items && items.size() > 0) {
//            for (RecItem item : items) {
//                list.add(convertToSimpleFeedItemBean(item, viewerId, source));
//            }
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                    items.parallelStream().map(item -> {
                        return convertToSimpleFeedItemBean(item, viewerId, source);
                    }).filter(Objects::nonNull).forEachOrdered(list::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return list;
    }
    

    public static List<SimpleFeedItemBean> convertQuestionList(List<TEliteQuestion> questions, Long viewerId, TEliteSourceType source) {
        List<SimpleFeedItemBean> list = new ArrayList<>();
        if (questions != null && questions.size() > 0) {
//            for (TEliteQuestion question : questions) {
//                SimpleFeedItemBean bean = convertToSimpleFeedItemBean(question, null, viewerId, source);
//                list.add(bean);
//            }
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                    questions.parallelStream().map(question -> {
                        return convertToSimpleFeedItemBean(question, null, viewerId, source);
                    }).filter(Objects::nonNull).forEachOrdered(list::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return list;
    }
    
    public static List<SimpleFeedItemBean> convertQuestionListUser(List<TEliteQuestion> questions, Long viewerId, TEliteSourceType source) {
        List<SimpleFeedItemBean> list = new ArrayList<>();
        if (questions != null && questions.size() > 0) {
//            for (TEliteQuestion question : questions) {
//                SimpleFeedItemBean bean = convertToSimpleFeedItemBeanUser(question, null, viewerId, source);
//                list.add(bean);
//            }
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                   questions.parallelStream().map(question -> {
                       return convertToSimpleFeedItemBeanUser(question, null, viewerId, source);
                   }).forEachOrdered(list::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return list;
    }

    public static List<SimpleFeedItemBean> convertAnswerList(List<TEliteAnswer> answers, Long viewerId, TEliteSourceType source) {
        List<SimpleFeedItemBean> list = new ArrayList<>();
        if (answers != null && answers.size() > 0) {
//            for (TEliteAnswer answer : answers) {
//                SimpleFeedItemBean bean = convertToSimpleFeedItemBean(answer, null, viewerId, source);
//                list.add(bean);
//            }
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                    answers.parallelStream().map(answer -> {
                        return convertToSimpleFeedItemBean(answer, null, viewerId, source);
                    }).forEachOrdered(list::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return list;
    }
    
    public static List<SimpleFeedItemBean> convertAnswerListUser(List<TEliteAnswer> answers, Long viewerId, TEliteSourceType source) {
        List<SimpleFeedItemBean> list = new ArrayList<>();
        if (answers != null && answers.size() > 0) {
//            for (TEliteAnswer answer : answers) {
//                SimpleFeedItemBean bean = convertToSimpleFeedItemBeanUser(answer, null, viewerId, source);
//                list.add(bean);
//            }
            try {
                EliteParallelPool.getForkJoinPool().submit(() -> {
                    answers.parallelStream().map(answer -> {
                        return convertToSimpleFeedItemBean(answer, null, viewerId, source);
                    }).forEachOrdered(list::add);
                }).get();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return list;
    }
    

    public static List<SimpleFeedItemBean> convertFeedList(List<Feed> feeds, Long viewerId, TEliteSourceType source){
    	List<SimpleFeedItemBean> list = new ArrayList<>();
    	if(null != feeds && feeds.size() > 0){
    	    try {
        	    EliteParallelPool.getForkJoinPool().submit(() -> {
        	        feeds.parallelStream().map(feed -> {
        	            return convertFeedItem(feed, viewerId, source);
        	            }).filter(Objects::nonNull).forEachOrdered(list::add);
        	    }).get();
        	} catch (Exception e) {
        	    log.error("", e);
        	}
    	} 
    	return list;
    }
    
    private static SimpleFeedItemBean convertFeedItem(Feed feed, Long viewerId, TEliteSourceType source) {
        SimpleFeedItemBean bean = null;
        try {
            long unitId = feed.getUnitId();
            Map<Integer, List<Long>> summary = feed.getSummary();
            Date produceTime = feed.getTime();
            Map<String, Object> unitInfo = TimeLineUtil.getOriginInfo(unitId);
            if (null != unitInfo) {
                Integer objectType = (Integer)unitInfo.get(TimeLineUtil.OBJECT_TYPE);
                Long objectId = (Long)unitInfo.get(TimeLineUtil.OBJECT_ID);

                BpType bpType = (BpType) BpType.valueMap.get(objectType);
                if (null == bpType) return bean;
                switch(bpType)
                {
                case Question:
                    TEliteQuestion question = eliteAdapter.getQuestionById(objectId);
                    bean = convertToSimpleFeedItemBean(question, summary, viewerId, source);
                    bean.setProduceTime(produceTime.getTime());
                    break;
                case Answer:
                    TEliteAnswer answer = eliteAdapter.getAnswerById(objectId);
                    bean = convertToSimpleFeedItemBean(answer, summary, viewerId,source);
                    bean.setProduceTime(produceTime.getTime());
                    break;
                }
            }
        } catch(Exception e) {
            log.error("", e);
        }
        return bean;
    }
    
    public static CompanyItemBean convertCompany(DecorationCompany decorationCompany)
    {
    	CompanyItemBean company =  new CompanyItemBean();
    	if(null == decorationCompany || decorationCompany.getId() <= 0)
    		return company;
    	
    	company.setId(IDUtil.encodeIdOrigin(decorationCompany.getId()));
    	company.setName(decorationCompany.getName());
    	company.setAvatar(decorationCompany.getLogo());
    	company.setPhone(decorationCompany.getPhone());
    	company.setHomeUrl("/decoration/company/"+company.getId()+".html");
    	
    	return company;
    }
    
    public static SimpleFeedItemBean convertToSimpleFeedItemBeanUser(TEliteQuestion question, Map<Integer, List<Long>> summary, Long viewerId, TEliteSourceType source){
        SimpleFeedItemBean bean = new SimpleFeedItemBean();

        bean.setFeedType(FeedType.QUESTION.getValue());
        bean.setItemId(IDUtil.encodeId(question.getId()));
        ContentBean contentBean = ContentUtil.parseContent(question.getDetail());
        bean.setTitle(HtmlUtils.htmlUnescape(question.getTitle()));
        bean.setContent(contentBean.getPlainText());
        bean.setCoverImgList(contentBean.getImgList());
        bean.setPublishTime(new Date(question.getPublishTime()));
        bean.setUpdateTime(question.getUpdateTime());
        if (Objects.equals(TEliteSourceType.WRITE_APP, source)) {
            bean.setUser(bpUserService.getUserBaseByBpId(question.getBpId()));
        } else {
            bean.setUser(bpUserService.getUserSimpleByBpId(question.getBpId()));
            int[] nums = InteractionUtil.getInteractionNumsForQuestion(question.getId());
            bean.setFansNum(nums[0]);
            bean.setAnswerNum(nums[1]);
            bean.setHasFollowed(InteractionUtil.hasFollowed(viewerId, BpType.Elite_User.getValue(), question.getId(), BpType.Question.getValue()));
        }
        bean.setProduceBean(ProduceUtil.getProduceInfo(summary, viewerId));
        if(null != viewerId && question.getBpId() == viewerId) {
        	bean.setOwner(true);
        }
        bean.setSpecialType(question.getSpecialType());
        bean.setSpecialId(String.valueOf(question.getSpecialId()));
        if (question.getSpecialType() == BpType.Elite_Column.getValue()) {
            TEliteColumn column = columnService.getColumnById(question.getSpecialId());
            bean.setSpecialTitle(column.getName());
            bean.setSpecialId(IDUtil.encodeId(question.getSpecialId()));
        } 
        bean.setOptions(convertToOptionList(question));

        return bean;
    }
    
    public static SimpleFeedItemBean convertToSimpleFeedItemBeanUser(TEliteAnswer answer, Map<Integer, List<Long>> summary, Long viewerId, TEliteSourceType source){
        SimpleFeedItemBean bean = new SimpleFeedItemBean();

        bean.setFeedType(FeedType.ANSWER.getValue());
        bean.setItemId(IDUtil.encodeId(answer.getId()));
        ContentBean contentBean = ContentUtil.parseContent(answer.getContent());
        try {
            TEliteQuestion question = eliteAdapter.getQuestionById(answer.getQuestionId());
            bean.setTitle(HtmlUtils.htmlUnescape(question.getTitle()));
            bean.setQuestionId(IDUtil.encodeId(question.getId()));
            
            bean.setSpecialType(answer.getSpecialType());
            if (question.getSpecialType() == BpType.Elite_Column.getValue()) {
                bean.setSpecialId(IDUtil.encodeId(question.getSpecialId()));
                TEliteColumn column = columnService.getColumnById(question.getSpecialId());
                bean.setSpecialTitle(column.getName());
            } else {
                bean.setSpecialId(IDUtil.encodeId(answer.getSpecialId()));
                if (answer.getSpecialId() == EliteVSAttitudeType.POS.getValue()) {
                    bean.setSpecialLabel(Constants.VS_POS_ANSWER_LABEL);
                } else if (answer.getSpecialId() == EliteVSAttitudeType.NEG.getValue()) {
                    bean.setSpecialLabel(Constants.VS_NEG_ANSWER_LABEL);
                }
            }
        } catch (TException e) {
            log.error("", e);
        }
        bean.setContent(contentBean.getPlainText());
        bean.setCoverImgList(contentBean.getImgList());
        if (Objects.equals(TEliteSourceType.WRITE_APP, source)) {
            bean.setUser(bpUserService.getUserBaseByBpId(answer.getBpId()));
        } else {
            bean.setUser(bpUserService.getUserSimpleByBpId(answer.getBpId()));
            int[] nums = InteractionUtil.getInteractionNumsForAnswer(answer.getId());
            bean.setLikeNum(nums[0]);
            bean.setTreadNum(nums[1]);
            bean.setCommentNum(nums[2]);
            bean.setHasLiked(InteractionUtil.hasLiked(viewerId, answer.getId()));
            bean.setHasFavorited(InteractionUtil.hasFavorited(viewerId, answer.getId()));
        }
        bean.setPublishTime(new Date(answer.getPublishTime()));
        bean.setUpdateTime(answer.getUpdateTime());
        //bean.setProduceText(ProduceUtil.getProduceInfo(summary));
        bean.setProduceBean(ProduceUtil.getProduceInfo(summary, viewerId));
        bean.setFullContent(answer.getContent());

        try {
            List<TEliteMedia> medias = eliteAdapter.getMediaListByAnswerIdAndType(answer.getId(), TEliteMediaType.VIDEO);
            if(medias != null && medias.size() > 0){
                bean.setVideoId(medias.get(0).getMediaGivenId());
            }
        } catch (TException e) {
            log.error("", e);
        }

        if(null != viewerId && answer.getBpId() == viewerId) {
        	bean.setOwner(true);
        }
        
        return bean;
    }
    
   public static List<SimpleFeedItemBean> getWXCommentLists(Long answerId, Long viewerId){
	   List<SimpleFeedItemBean> commentLists = new ArrayList<>();
	   CodeMsgData codeMsgData = extendServiceAdapter.getBpInteractionByTargetInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, BpInteractionType.COMMENT, 0, Integer.MAX_VALUE);
	   if(codeMsgData.getCode() == ResponseConstants.OK){
		   JSONArray data = JSONArray.fromObject(codeMsgData.getData());
		   log.info("get weixin comments = {}", data);
		   if(null != data && data.size() > 0){
			 for(int i = 0; i < data.size(); i++){
				 Long interactionId = data.getLong(i);
				 codeMsgData = extendServiceAdapter.getBpInteraction(interactionId);
				 if(ResponseConstants.OK == codeMsgData.getCode()){
					 SimpleFeedItemBean comment = new SimpleFeedItemBean();
					 BpInteractionDetail detail = JSON.parseObject(codeMsgData.getData(), BpInteractionDetail.class);
					 comment.setPublishTime(new Date(detail.getCreateTime()));
					 comment.setUpdateTime(detail.getUpdateTime());
					 comment.setUser(bpUserService.getUserDetailByBpId(detail.getBpid(), viewerId));
					 comment.setContent(ContentUtil.parseContent(JSONObject.fromObject(detail.getExtra()).getString("data")).getPlainText());
					 commentLists.add(comment);
				 }
			 }
		   }
	   }
	   return commentLists;
   }
   
   public static EliteFeatureItemBean convertToFeatureItemBean(TEliteTopic topic) {
       if (null == topic) return null;
       EliteFeatureItemBean bean = new EliteFeatureItemBean();
       bean.setId(IDUtil.encodeId(topic.getId()))
           .setName(HtmlUtils.htmlUnescape(topic.getTitle()))
           .setBrief(HtmlUtils.htmlUnescape(topic.getBrief()))
           .setCover(topic.getCover())
           .setType(BpType.Elite_Topic.getValue())
           .setUpdateTime(topic.getUpdateTime())
           .setDate(DateUtil.format(topic.getUpdateTime(), DateUtil.sdfSimpleDot))
           .setQuestionId(IDUtil.encodeId(topic.getQuestionId()));
       try {
           TSearchAnswerCondition condition = new TSearchAnswerCondition();
           condition.setQuestionId(topic.getQuestionId()).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()))
           .setFrom(0).setCount(1);
           TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
           if (null != listResult) {
               bean.setNum((int)listResult.getTotal());
           }
       } catch (Exception e) {
           log.error("", e);
       }
       return bean;
   }
   
   public static EliteFeatureItemBean convertToFeatureItemBean(TEliteColumn column) {
       if (null == column) return null;
       EliteFeatureItemBean bean = new EliteFeatureItemBean();
       bean.setId(IDUtil.encodeId(column.getId()))
           .setName(HtmlUtils.htmlUnescape(column.getName()))
           .setCover(column.getCover())
           .setType(BpType.Elite_Column.getValue())
           .setUser(bpUserService.getUserBaseByBpId(JSONObject.fromObject(column.getUserInfo()).getLong("bpId")))
           .setBrief(HtmlUtils.htmlUnescape(column.getBrief()))
           .setUpdateTime(column.getUpdateTime())
           .setDate(DateUtil.format(column.getUpdateTime(), DateUtil.sdfSimpleDot));
       return bean;
   }
   
   public static EliteFeatureItemBean convertToFeatureItemBean(TEliteSubject subject) {
       if (null == subject) return null;
       EliteFeatureItemBean bean = new EliteFeatureItemBean();
       bean.setId(IDUtil.encodeId(subject.getId()))
           .setBrief(HtmlUtils.htmlUnescape(subject.getBrief()))
           .setCover(subject.getCover())
           .setType(BpType.Elite_Subject.getValue())
           .setName(HtmlUtils.htmlUnescape(subject.getName()))
           .setUpdateTime(subject.getUpdateTime())
           .setDate(DateUtil.format(subject.getUpdateTime(), DateUtil.sdfSimpleDot))
           .setNum(ContentUtil.getSubjectNum(subject.getDetail()));
       return bean;
   }
   
   public static List<EliteFeatureItemBean> convertSubjectList(List<TEliteSubject> subjects) {
       List<EliteFeatureItemBean> list = new ArrayList<>();
       if (subjects != null && subjects.size() > 0) {
           try {
               EliteParallelPool.getForkJoinPool().submit(() -> {
                   subjects.parallelStream().map(subject -> {
                       return convertToFeatureItemBean(subject);
                   }).filter(Objects::nonNull).forEachOrdered(list::add);
               }).get();
           } catch (Exception e) {
               log.error("", e);
           }
       }
       return list;
   }
   
   public static List<EliteFeatureItemBean> convertColumnList(List<TEliteColumn> columns) {
       List<EliteFeatureItemBean> list = new ArrayList<>();
       if (columns != null && columns.size() > 0) {
           try {
               EliteParallelPool.getForkJoinPool().submit(() -> {
                   columns.parallelStream().map(column -> {
                       return convertToFeatureItemBean(column);
                   }).filter(Objects::nonNull).forEachOrdered(list::add);
               }).get();
           } catch (Exception e) {
               log.error("", e);
           }
       }
       return list;
   }
   
   public static List<EliteFeatureItemBean> convertTopicList(List<TEliteTopic> topics) {
       List<EliteFeatureItemBean> list = new ArrayList<>();
       if (topics != null && topics.size() > 0) {
           try {
               EliteParallelPool.getForkJoinPool().submit(() -> {
                   topics.parallelStream().map(topic -> {
                       return convertToFeatureItemBean(topic);
                   }).filter(Objects::nonNull).forEachOrdered(list::add);
               }).get();
           } catch (Exception e) {
               log.error("", e);
           }
       }
       return list;
   }
   
   
   public static List<EliteOptionItemBean> convertToOptionList(TEliteQuestion question) {
       List<EliteOptionItemBean> options = new ArrayList<EliteOptionItemBean>();
       Map<Integer, Integer> counts = question.getCounts();
       if (EliteSpecialUtil.isVoteType(question)) {
	       String optionField = question.getOptions();
	       if (StringUtils.isNotBlank(optionField)) {
	           JSONArray array = JSONArray.fromObject(optionField);
	           for (int i = 0; i < array.size(); i++) {
	               EliteOptionItemBean optionItem = new EliteOptionItemBean();
	               JSONObject ele = array.getJSONObject(i);
	               String img = ele.containsKey("img") ? ele.getString("img") : "";
	               if (StringUtils.isNotBlank(img)) {
	                   optionItem.setImg(ImageUtil.removeImgProtocol(img)).setImgSmall(ImageUtil.getSmallImage(img, Constants.OPTION_IMG, null));
	               }
	               optionItem.setDescription(ele.containsKey("description") ? ele.getString("description") : "");
	               optionItem.setCount(counts.get(i + 1));
	               options.add(optionItem);
	           }
	       }
       } else if (EliteSpecialUtil.isVSType(question)) {
    	   EliteOptionItemBean pos = new EliteOptionItemBean().setCount(counts.get(1)).setImg(Constants.VS_POS_IMG)
    			   .setImgSmall(Constants.VS_POS_IMG).setDescription(Constants.VS_POS_TEXT);
    	   options.add(pos);
    	   EliteOptionItemBean neg = new EliteOptionItemBean().setCount(counts.get(2)).setImg(Constants.VS_NEG_IMG)
    	           .setImgSmall(Constants.VS_NEG_IMG).setDescription(Constants.VS_NEG_TEXT);
    	   options.add(neg);
       }
       return options;
   }
   
   public static List<List<UserInfo>> getChoosedUserList(TEliteQuestion question) {
       List<List<UserInfo>> result = new ArrayList<List<UserInfo>>();
       TSearchAnswerCondition answerCondition = new TSearchAnswerCondition();
       answerCondition.setQuestionId(question.getId()).setSpecialType(question.getSpecialType()).setSpecialId(1).setFrom(0).setCount(Constants.VS_DEFAULT_CHOOSED_USER_NUM);
       try {
           List<UserInfo> list = new ArrayList<UserInfo>();
           TAnswerListResult listResult = eliteAdapter.searchAnswer(answerCondition);
           if (null != listResult && listResult.getAnswers().size() > 0) {
               listResult.getAnswers().forEach(answer -> list.add(userInfoService.getUserInfoByBpid(answer.getBpId())));
           }
           result.add(list);
       } catch (Exception e) {
           log.error("", e);
       }
       answerCondition.setSpecialId(2);
       try {
           List<UserInfo> list = new ArrayList<UserInfo>();
           TAnswerListResult listResult = eliteAdapter.searchAnswer(answerCondition);
           if (null != listResult && listResult.getAnswers().size() > 0) {
               listResult.getAnswers().forEach(answer -> list.add(userInfoService.getUserInfoByBpid(answer.getBpId())));
           }
           result.add(list);
       } catch (Exception e) {
           log.error("", e);
       }
       return result;
   }
}
