package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteMessageTargetType;
import com.sohu.bp.elite.enums.EliteSendWechatTemplate;
import com.sohu.bp.elite.enums.SendCloudTemplate;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessageFrequenceType;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.model.TEliteMessageTimePeriodType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.service.web.ExpertTeamService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.InviteUtil;
import com.sohu.bp.elite.util.SendCloudSmsUtil;
import com.sohu.bp.elite.util.ToolUtil;
import com.sohu.bp.elite.util.WechatUtil;

public class ExpertTeamServiceImpl implements ExpertTeamService {

    private static final Logger log = LoggerFactory.getLogger(ExpertTeamServiceImpl.class);
    
    private static final EliteThriftServiceAdapter eliteAdpater = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    
    private CacheManager redisCacheManager;
    private RedisCache redisCache;
    private RedisCache inviteRedisCache;
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_USER);
        inviteRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_INVITE);
    }
    
    @Override
    public boolean isExpert(Long bpId) {
        if (null == bpId || bpId <= 0) return false;
        return redisCache.zExist(CacheConstants.ELITE_EXPERT_SET, bpId.toString());
    }

    @Override
    public void addNewAnswered(Long bpId, Long questionId) {
        if (null == bpId || bpId <= 0 || null == questionId || questionId <= 0) return;
        try {
            eliteAdpater.addExpertNewAnswered(bpId, questionId);
        } catch (TException e) {
            log.error("", e);
        }
    }
    
	@Override
	public Future<Boolean> postMessage(String name, Long invitedId, TEliteQuestion question, String tags) {
		TEliteMessageData messageData = new TEliteMessageData();
		String title = question.getTitle();
		Long questionId = question.getId();
		String inboxMessageContent = EliteMessageData.NEW_INVITE_BY_PERSON.getContent()
				.replace(Constants.MESSAGE_DATA_JUMPURL, "https://" + OverallDataFilter.askDomain + ToolUtil.getQuestionUri(IDUtil.encodeId(questionId)))
                .replace(Constants.MESSAGE_DATA_NICKNAME, name)
                .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, title);
		Map<String, String> sendCloudVariables = new HashMap<String, String>();
		sendCloudVariables.put(SendCloudSmsUtil.PARAM_NICK_NAME, name);
		sendCloudVariables.put(SendCloudSmsUtil.PARAM_QUESTION_TITLE, title);
		sendCloudVariables.put(SendCloudSmsUtil.PARAM_QUESTION_URI, ToolUtil.getLoginQuestionUrl(IDUtil.encodeId(questionId), invitedId));
		messageData.setInboxMessageDataValue(EliteMessageData.NEW_INVITE_BY_PERSON.getValue()).setInboxMessageContent(inboxMessageContent)
		.setSendCloudTemplate(SendCloudTemplate.NEW_INVITE_BY_PERSON.getValue()).setSendCloudVariables(sendCloudVariables)
		.setWechatTemplateId(EliteSendWechatTemplate.NEW_INVITE_BY_PERSON.getValue()).setWechatUrl(WechatUtil.getWechatQuestionUrl(questionId)).setWechatData(WechatUtil.getInviteMessageWechatData(invitedId, name, question, tags));
		TEliteMessageStrategy strategy = new TEliteMessageStrategy().setTimePeriodType(TEliteMessageTimePeriodType.DAY_TIME).setFrequenceType(TEliteMessageFrequenceType.HOUR)
				.setFrequenceValue(Constants.EXPERT_TEAM_DEFAULT_NUM_PER_HOUR);
		Future<Boolean> result = EliteAsyncTaskPool.submitTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.MEDIUM, messageData, strategy,
				EliteMessageTargetType.SINGLE, BpType.Elite_User, new ArrayList<Long>(){{add(invitedId);}}));
		return result;
	} 

    @Override
    public boolean saveInviteStatusInCache(Long questionId, List<Long> invitedIds) {
        String cacheKey = InviteUtil.getSystemInviteCacheKey(questionId);
        boolean flag = true;
        if (null != invitedIds && invitedIds.size() > 0) {
            for (Long id : invitedIds) {
                if (!inviteRedisCache.zAdd(cacheKey, new Date().getTime(), id.toString())) flag = false;
            }
        }
        redisCache.expire(cacheKey, Constants.INVITE_EXPIRE_TIME_SECOND);
        return flag;
    }   
}
