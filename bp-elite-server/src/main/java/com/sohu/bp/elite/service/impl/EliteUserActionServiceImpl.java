package com.sohu.bp.elite.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteFeedAttitudeType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.service.EliteAnswerService;
import com.sohu.bp.elite.service.EliteQuestionService;
import com.sohu.bp.elite.service.EliteUserActionService;
import com.sohu.bp.elite.service.UserInfoService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteFeedTask;
import com.sohu.bp.elite.task.EliteFeedTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.ToolUtil;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.enums.ActionType;
import com.sohu.bp.thallo.model.RelationAction;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 用户行为封装逻辑
 * @author zhijungou
 * 2017年3月28日
 */
public class EliteUserActionServiceImpl implements EliteUserActionService {
    private static final Logger log = LoggerFactory.getLogger(EliteUserActionServiceImpl.class);
    
    private static final BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
    private static final BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
    private UserInfoService userInfoService;
    private EliteQuestionService questionService;
    private EliteAnswerService answerService;
    private String askDomain;
    
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }
    
    public void setQuestionService(EliteQuestionService questionService) {
        this.questionService = questionService;
    }
    
    public void setAnswerService(EliteAnswerService answerService) {
        this.answerService = answerService;
    }
    
    public void setAskDomain(String askDomain) {
        this.askDomain = askDomain;
    }
    
    @Override
    public boolean likeAnswer(Long answerId, Long bpId, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag) {
        boolean flag = false;
        if (null == answerId || null == bpId || null == ip | null == port || answerId <= 0 || bpId <= 0 || ip < 0 || port < 0) return flag;
        if (null == time) time = new Date().getTime();
        EliteAnswer answer = answerService.getById(answerId);
        if (null == answer) return flag;
        BpInteractionDetail interactionDetail = new BpInteractionDetail();
        interactionDetail.setBpid(bpId).setType(BpInteractionType.LIKE).setTargetType(BpInteractionTargetType.ELITE_ANSWER).setTargetId(answerId)
        .setExtra(new JSONObject().toString()).setCreateTime(time).setUpdateTime(time).setCreateHost(ip).setCreatePort(port);
        CodeMsgData codeMsgData = extendServiceAdapter.addBpInteraction(interactionDetail);
        if (ResponseConstants.OK == codeMsgData.getCode()) flag = true;
        if (flag) {
            if (messageFlag) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                EliteQuestion question = questionService.getById(answer.getQuestionId());
                String inboxMessageContent = EliteMessageData.NEW_LIKE_BY_MYANSWER.getContent()
                        .replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                        .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                        .replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ askDomain + ToolUtil.getAnswerUri(IDUtil.encodeId(answerId)));
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, answer.getBpId(), 
                        new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_LIKE_BY_MYANSWER.getValue()).setInboxMessageContent(inboxMessageContent), null));
            }
            if (feedFlag) {
                EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Answer, answerId, bpId, ProduceActionType.LIKE, EliteFeedAttitudeType.ADD));
            }
        }
        log.info("[USER ACTION] bpId = {} like answer = {}, result = {}", new Object[]{bpId, answerId, flag});
        return flag;
    }

    @Override
    public boolean favoriteAnswer(Long answerId, Long bpId, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag) {
        boolean flag = false;
        if (null == answerId || null == bpId || null == ip || null == port || answerId <= 0 || bpId <= 0 || ip < 0 || time < 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        EliteAnswer answer = answerService.getById(answerId);
        if (null == answer) return flag;
        BpInteractionDetail interactionDetail = new BpInteractionDetail();
        interactionDetail.setBpid(bpId).setType(BpInteractionType.FAVORITE).setTargetType(BpInteractionTargetType.ELITE_ANSWER).setTargetId(answerId)
        .setExtra(new JSONObject().toString()).setCreateTime(time).setUpdateTime(time).setCreateHost(ip).setCreatePort(port);
        CodeMsgData codeMsgData = extendServiceAdapter.addBpInteraction(interactionDetail);
        if (ResponseConstants.OK == codeMsgData.getCode()) flag = true;
        if (flag) {
            if (messageFlag) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                EliteQuestion question = questionService.getById(answer.getQuestionId());
                String inboxMessageContent = EliteMessageData.NEW_FAVORITE_BY_MYANSWER.getContent()
                        .replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                        .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                        .replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ askDomain + ToolUtil.getAnswerUri(IDUtil.encodeId(answerId)));
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, answer.getBpId(), 
                        new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_FAVORITE_BY_MYANSWER.getValue()).setInboxMessageContent(inboxMessageContent), null));
            }
            if (feedFlag) {
                EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Answer, answerId, bpId, ProduceActionType.FAVORITE, EliteFeedAttitudeType.ADD));
            }
        }
        log.info("[USER ACTION] bpId = {} favorite answerId = {}, result = {}", new Object[]{bpId, answerId, flag});
        return flag;
    }
    
    @Override
    public boolean favoriteTarget(Long targetId, Integer targetType, Long bpId, Long ip, Integer port, Long time) {
        boolean flag = false;
        if (null == targetId || null == targetType || null == bpId || null == ip || null == port || targetId <= 0 || targetType <= 0 || bpId <= 0 || ip < 0 || time < 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        BpInteractionDetail interactionDetail = new BpInteractionDetail();
        interactionDetail.setBpid(bpId).setType(BpInteractionType.FAVORITE).setTargetType(BpInteractionTargetType.findByValue(targetType)).setTargetId(targetId)
        .setExtra(new JSONObject().toString()).setCreateTime(time).setUpdateTime(time).setCreateHost(ip).setCreatePort(port);
        CodeMsgData codeMsgData = extendServiceAdapter.addBpInteraction(interactionDetail);
        if (ResponseConstants.OK == codeMsgData.getCode()) flag = true;
        log.info("[USER ACTION] bpId = {} favorite targetType = {}, targetId = {}, result = {}", new Object[]{bpId, targetType, targetId, flag});
        return flag;
    }

    @Override
    public boolean followQuestion(Long questionId, Long bpId, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag) {
        boolean flag = false;
        if (null == questionId || null == bpId || null == ip || null == port || questionId <= 0 || bpId <= 0 || ip < 0 || port < 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        EliteQuestion question = questionService.getById(questionId);
        if (null == question) return flag;
        RelationAction relationAction = new RelationAction().setUserType(BpType.Elite_User.getValue()).setUserId(bpId).setType(ActionType.TYPE_FOLLOW.getValue())
                .setObjectType(BpType.Question.getValue()).setObjectId(questionId).setActTime(time).setActIp(ip).setActPort(port);
        try {
            flag = thalloServiceAdapter.doFollow(relationAction);
        } catch (Exception e) {
            log.error("", e);
            flag = false;
        }
        if (flag) {
            if (messageFlag) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                String inboxMessageContent = EliteMessageData.NEW_FOLLOW_BY_MYQUESTION.getContent()
                        .replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                        .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                        .replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ askDomain + ToolUtil.getQuestionUri(IDUtil.encodeId(questionId)));
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, question.getBpId(), 
                        new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_FOLLOW_BY_MYQUESTION.getValue()).setInboxMessageContent(inboxMessageContent), null));
            }
            if (feedFlag) {
                EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Question, questionId, bpId, ProduceActionType.FOLLOW, EliteFeedAttitudeType.ADD));
            }
        }
        log.info("[USER ACTION] bpId = {} follow questionId = {}, result = {}", new Object[]{bpId, questionId, flag});
        return flag;
    }

    @Override
    public boolean followPeople(Long followedId, Long followId, Long ip, Integer port, Long time, boolean messageFlag) {
        boolean flag = false;
        if (null == followedId || null == followId || null == ip || null == port || followedId <= 0 || followId <= 0 || ip < 0 || port < 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        RelationAction relationAction = new RelationAction().setType(ActionType.TYPE_FOLLOW.getValue()).setUserType(BpType.Elite_User.getValue()).setUserId(followId)
                .setObjectType(BpType.Elite_User.getValue()).setObjectId(followedId).setActTime(time).setActIp(ip).setActPort(port);
        try {
            flag = thalloServiceAdapter.doFollow(relationAction);
        } catch (Exception e) {
            log.error("", e);
            flag = false;
        }
        if (flag && messageFlag) {
            UserInfo userInfo = userInfoService.getUserInfoByBpid(followId);
            String inboxMessageContent = EliteMessageData.NEW_FOLLOW_BY_PERSON.getContent()
                    .replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                    .replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ askDomain + ToolUtil.getMyFansUri());
            EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, followedId, 
                    new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_FOLLOW_BY_PERSON.getValue()).setInboxMessageContent(inboxMessageContent), null));
        }
        log.info("[USER ACTION] bpId = {} follow bpId = {}, result = {}", new Object[]{followId, followedId, flag});
        return flag;
    }

    @Override
    public boolean unlikeAnswer(Long answerId, Long bpId) {
       boolean flag = false;
       if (null == answerId || null == bpId || answerId <= 0 || bpId <= 0) return flag;
       CodeMsgData responseData = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, bpId, BpInteractionType.LIKE);
       if (ResponseConstants.OK == responseData.getCode()) {
           JSONArray interactionIds = JSONArray.fromObject(responseData.getData());
           if(interactionIds != null && interactionIds.size() > 0) {
               long interactionId = interactionIds.getLong(0);
               responseData = extendServiceAdapter.deleteBpInteraction(interactionId);
               if (ResponseConstants.OK == responseData.getCode()) flag = true;
           }
       }
       if (flag) {
           EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Answer, answerId, bpId, ProduceActionType.LIKE, EliteFeedAttitudeType.REMOVE));
       }
       log.info("[USER ACTION] bpId = {} unlike answer = {}, result = {}", new Object[]{bpId, answerId, flag});
       return flag;
    }

    @Override
    public boolean unfavoriteAnswer(Long answerId, Long bpId) {
        boolean flag = false;
        if (null == answerId || null == bpId || answerId <= 0 || bpId <= 0) return flag;
        CodeMsgData codeMsgData = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, bpId, BpInteractionType.FAVORITE);
        if (ResponseConstants.OK == codeMsgData.getCode()) {
            JSONArray interactionIds = JSONArray.fromObject(codeMsgData.getData());
            if (interactionIds != null && interactionIds.size() > 0) {
                long interactionId;
                flag = true;
                for (int i = 0; i < interactionIds.size(); i++) {
                    interactionId = interactionIds.getLong(i);
                    codeMsgData = extendServiceAdapter.deleteBpInteraction(interactionId);
                    if (ResponseConstants.OK != codeMsgData.getCode()) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        if (flag) {
            EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Answer, answerId, bpId, ProduceActionType.FAVORITE, EliteFeedAttitudeType.REMOVE));
        }
        log.info("[USER ACTION] bpId = {} unfavorite answerId = {}, result = {}", new Object[]{bpId, answerId, flag});
        return flag;
    }
    
    @Override
    public boolean unfavoriteTarget(Long targetId, Integer targetType, Long bpId) {
        boolean flag = false;
        if (null == targetId || null == bpId || targetId <= 0 || bpId <= 0) return flag;
        CodeMsgData codeMsgData = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(targetId, BpInteractionTargetType.findByValue(targetType), bpId, BpInteractionType.FAVORITE);
        if (ResponseConstants.OK == codeMsgData.getCode()) {
            JSONArray interactionIds = JSONArray.fromObject(codeMsgData.getData());
            if (null != interactionIds && interactionIds.size() > 0) {
                long interactionId;
                flag = true;
                for (int i = 0; i < interactionIds.size(); i++) {
                    interactionId = interactionIds.getLong(i);
                    codeMsgData = extendServiceAdapter.deleteBpInteraction(interactionId);
                    if (ResponseConstants.OK != codeMsgData.getCode()) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        log.info("[USER ACTION] bpId = {} unfavorite targetType = {}, targetId = {}, result = {}", new Object[]{bpId, targetType, targetId,  flag});
        return flag;
    }

    @Override
    public boolean unfollowQuestion(Long questionId, Long bpId, Long ip, Integer port, Long time) {
        boolean flag = false;
        if (null == questionId || null == bpId || questionId <= 0 || bpId <= 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        RelationAction relationAction = new RelationAction().setType(ActionType.TYPE_UNFOLLOW.getValue()).setUserType(BpType.Elite_User.getValue()).setUserId(bpId)
                .setObjectType(BpType.Question.getValue()).setObjectId(questionId).setActTime(time).setActIp(ip).setActPort(port);
        try {
            flag = thalloServiceAdapter.doUnFollow(relationAction);
        } catch (Exception e) {
            log.error("", e);
            flag = false;
        }
        if (flag) {
            EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Question, questionId, bpId, ProduceActionType.FOLLOW, EliteFeedAttitudeType.REMOVE));
        }
        log.info("[USER ACTION] bpId = {} unfollow questionId = {}, result = {}", new Object[]{bpId, questionId, flag});
        return flag;
    }

    @Override
    public boolean unfollowPeople(Long followedId, Long followId, Long ip, Integer port, Long time) {
        boolean flag = false;
        if (null == followedId || null == followId || followedId <= 0 || followId <= 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        RelationAction relationAction = new RelationAction().setType(ActionType.TYPE_UNFOLLOW.getValue()).setUserType(BpType.Elite_User.getValue()).setUserId(followId)
                .setObjectType(BpType.Elite_User.getValue()).setObjectId(followedId).setActTime(time).setActIp(ip).setActPort(port);
        try {
            flag = thalloServiceAdapter.doUnFollow(relationAction);
        } catch (Exception e) {
            log.error("", e);
            flag = false;
        }
        log.info("[USER ACTION] bpId = {} unfollow bpId = {}, result = {}", new Object[]{followId, followedId, flag});
        return flag;
    }

    @Override
    public boolean commentAnswer(Long answerId, Long bpId, String content, Long ip, Integer port, Long time, boolean messageFlag, boolean feedFlag) {
        boolean flag = false;
        if (null == answerId || null == bpId || answerId <= 0 || bpId <= 0 || StringUtils.isBlank(content)) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        EliteAnswer answer = answerService.getById(answerId);
        if (null == answer) return flag;
        UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
        if (null == userInfo) return flag;
        JSONObject extra = new JSONObject();
        extra.put("data", content);
        BpInteractionDetail interactionDetail = new BpInteractionDetail().setType(BpInteractionType.COMMENT).setTargetType(BpInteractionTargetType.ELITE_ANSWER)
                .setTargetId(answerId).setBpid(bpId).setExtra(extra.toString()).setCreateHost(ip).setCreatePort(port).setCreateTime(time).setUpdateTime(time);
        CodeMsgData codeMsgData = extendServiceAdapter.addBpInteraction(interactionDetail);
        if (ResponseConstants.OK == codeMsgData.getCode()) flag = true;
        if (flag) {
            if (messageFlag) {
                EliteQuestion question = questionService.getById(answer.getQuestionId());
                String inboxMessageContent = EliteMessageData.NEW_COMMENT_BY_MYANSWER.getContent()
                        .replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                        .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                        .replace(Constants.MESSAGE_DATA_JUMPURL, "https://" + askDomain + ToolUtil.getAnswerUri(IDUtil.encodeId(answerId)));
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, answer.getBpId(),
                        new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_COMMENT_BY_MYANSWER.getValue()).setInboxMessageContent(inboxMessageContent), null));
            }
            if (feedFlag) {
                EliteFeedTaskPool.addTask(new EliteFeedTask(BpType.Answer, answerId, bpId, ProduceActionType.COMMENT, EliteFeedAttitudeType.ADD));
            }
        }
        log.info("[USER ACTION] comment answer : answerId = {}, bpId = {}, content = {}, result = {}", new Object[]{answerId, bpId, content, flag});
        return flag;
    }

    @Override
    public boolean treadAnswer(Long answerId, Long bpId, Long ip, Integer port, Long time) {
        boolean flag = false;
        if (null == answerId || null == bpId || null == ip || null == port || answerId <= 0 || bpId <= 0 || ip < 0 || time < 0) return flag;
        if (null == time || time <= 0) time = new Date().getTime();
        EliteAnswer answer = answerService.getById(answerId);
        if (null == answer) return flag;
        BpInteractionDetail interactionDetail = new BpInteractionDetail();
        interactionDetail.setBpid(bpId).setTargetId(answerId).setTargetType(BpInteractionTargetType.ELITE_ANSWER).setType(BpInteractionType.TREAD)
        .setExtra(new JSONObject().toString()).setCreateTime(time).setUpdateTime(time).setCreateHost(ip).setCreatePort(port);
        CodeMsgData codeMsgData = extendServiceAdapter.addBpInteraction(interactionDetail);
        if (ResponseConstants.OK == codeMsgData.getCode()) flag = true;
        log.info("[USER ACTION] tread answer : answerId = {}, bpId = {}", new Object[]{answerId, bpId});
        return flag;
    }

    @Override
    public boolean unTreadAnswer(Long answerId, Long bpId) {
        boolean flag = false;
        if (null == answerId || null == bpId || answerId <= 0 || bpId <= 0) return flag;
        CodeMsgData responseData = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, bpId, BpInteractionType.TREAD);
        if (ResponseConstants.OK == responseData.getCode()) {
            JSONArray interactionIds = JSONArray.fromObject(responseData.getData());
            if(interactionIds != null && interactionIds.size() > 0) {
                long interactionId = interactionIds.getLong(0);
                responseData = extendServiceAdapter.deleteBpInteraction(interactionId);
                if (ResponseConstants.OK == responseData.getCode()) flag = true;
            }
        }
        log.info("[USER ACTION] untread answer : answerId = {}, bpId = {}", new Object[]{answerId, bpId});
        return flag;
    }

}
