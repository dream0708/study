package com.sohu.bp.elite.service.impl;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.bean.InboxMessageBean;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteSendWechatTemplate;
import com.sohu.bp.elite.enums.SendCloudTemplate;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessageFrequenceType;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.elite.service.UserInfoService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.util.SendCloudSmsUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapter;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapterFactory;
import com.sohu.bp.wechat.model.BpWechatPlatform;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-27 21:02:35
 * TODO
 */
public class NotifyServiceImpl implements NotifyService {
	private static final Logger logger = LoggerFactory.getLogger(EliteAnswerServiceImpl.class);
	private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
	private static final BpExtendServiceAdapter bpExtendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static final BpWechatServiceAdapter wechatServiceAdapter = BpWechatServiceAdapterFactory.getWechatServiceAdapter();
	private static final BpServiceAdapter serviceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	private static final BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static final int WECHAT_PLATFORM_ID = 4;
	
	private static Set<String> whiteListSet = new HashSet(){{
	    add("13807940739");add("13979424222");add("18601028484");add("13901586316");add("15050528218");add("15145649936");add("18804642653");add("15114555751");
	    add("13177713332");add("13718759051");add("18611557450");add("18646495921");add("13946566425");add("18600650497");add("18202737927");add("13920653650");
	    add("18811427090");add("18061611941");add("18810720083");add("18511466227");add("15880055252");
//	    add("13810138235");
	}};

	private String topic;
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	private UserInfoService userInfoService;
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
	
	public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

	public void init() {
	    redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_ELITE_MSG_LIMIT);
	}
	
	@Override
	public boolean notify2Statistic(Long objectId, Integer objectType, Integer notifyType) {
		return notify2Statistic(objectId, objectType, notifyType, null);
	}

	@Override
	public boolean notify2Statistic(Long objectId, Integer objectType, Integer notifyType, Integer extra) {
		boolean retVal = false;
		if(StringUtils.isBlank(topic))
			return retVal;
		if(null == objectId || null == objectType || null == notifyType)
			return retVal;
		
		JSONObject msg = new JSONObject();
		msg.put("objectId", objectId);
		msg.put("objectType", objectType);
		msg.put("notifyType", notifyType);
		if(null != extra){
			msg.put("extra", extra);
		}
		logger.info("notify to statistic. message = " + msg.toString());
		try
		{
			kafkaProducer.send(topic, msg.toString());
			retVal = true;
		}catch(Exception e)
		{
			logger.error("", e);
		}
		
		return retVal;
	}

	@Override
	public boolean notify2Inbox(InboxMessageBean messageBean) {
		if(null == messageBean)
			return false;
		CodeMsgData codeMsgData = bpExtendServiceAdapter.addBpMessageDetail(messageBean.getBpMessageDetail());
		if (codeMsgData.getCode() == ResponseConstants.OK) {
		    logger.info("send inbox message to {} succeed", new Object[]{messageBean.getToId()});
			return true;
		} else {
		    logger.info("send inbox message to {} failed", new Object[]{messageBean.getToId()});
			return false;
		}
	}

	@Override
	public boolean notify2CellPhone(Integer templateId, String destnumber, Map<String, String> params) {
	    logger.info("send message to {}, params = {}", new Object[]{destnumber, params});
		if(whiteListSet.contains(destnumber)) {
			logger.info("number={} is in white list. so message won't be delivered", new String[]{destnumber});
			return true;
		}
		String resJSONStr = SendCloudSmsUtil.sendMessage(templateId, destnumber, params);
		if(StringUtils.isNotBlank(resJSONStr)) {
			JSONObject resJSON = JSONObject.fromObject(resJSONStr);
			if(null != resJSON && resJSON.getInt("code") == 0 && resJSON.getBoolean("ret")) {
				logger.info("send templale {} message to {} success", new String[]{String.valueOf(templateId), destnumber});
				return true;
			} else {
				logger.info("send templale {} message to {} failed", new String[]{String.valueOf(templateId), destnumber});
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean notify2Wechat(long bpId, int templateId, String url, String data) {
        try {
            logger.info("invoke notify2Wechat : bpId = {}, templateId = {}", new Object[]{bpId, templateId});
            String openId = "";
            CodeMsgData codeMsgData = serviceAdapter.getOpenIdentificationsByBpid(bpId);
            if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
                JSONArray msgDataJSONArray = JSONArray.fromObject(codeMsgData.getData());
                if (null == msgDataJSONArray || msgDataJSONArray.size() == 0) {
                    logger.error("get unionId for bpId={} failed.", new String[]{String.valueOf(bpId)});
                    return false;
                }
                String msgData = "";
                for(int i=0; i<msgDataJSONArray.size(); i++) {
                    msgData = msgDataJSONArray.getString(i);
                    if (!msgData.startsWith("WECHAT_")) {
                        msgData = "";
                        continue;
                    }
                    break;
                }
                String unionId = msgData.substring(("WECHAT_").length());
                if (StringUtils.isBlank(unionId)) {
                    logger.error("unionId of bpId={} is empty", new String[]{String.valueOf(bpId)});
                    return false;
                }
                codeMsgData = extendServiceAdapter.getBpWechatOpenIdByUnionId(unionId);
                if (null != codeMsgData &&  codeMsgData.getCode() == ResponseConstants.OK) {
                    JSONObject wechatInfoJSON = JSONObject.fromObject(codeMsgData.getData());
                    if (null == wechatInfoJSON || !wechatInfoJSON.containsKey("openId")) {
                        logger.error("get open id for bpId={} unionId={} failed.codeMsgData={}", new String[]{String.valueOf(bpId), unionId, codeMsgData.getMessage()});
                        return false;
                    }
                    openId = wechatInfoJSON.getString("openId");
                } else {
                    logger.error("get openId for bpId={} unionId={} failed", new String[]{String.valueOf(bpId), unionId});
                    return false;
                }
            } else {
                logger.error("get unio Id for bpId={} failed.", new String[]{String.valueOf(bpId)});
                return false;
            }
            if (StringUtils.isBlank(openId)) {
                logger.error("openId is blank which bpId={}", new String[]{String.valueOf(bpId)});
                return false;
            }
            
            BpWechatPlatform bpWechatPlatform = wechatServiceAdapter.getWechatPlatform(WECHAT_PLATFORM_ID);
            if (wechatServiceAdapter.sendWechatTemplateMessage(bpWechatPlatform, openId, templateId, url, data) > 0) {
               return true;
            }
        }catch (Exception e) {
            logger.error("", e);
        }

        return false;
    }
	
	public boolean notify2WechatCellphone() {
	    
	    return true;
	}
	
	private static String generateReplaceKey(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		StringBuilder builder = new StringBuilder("${").append(key).append("}");
		return builder.toString();
	}

    @Override
    public boolean postMessage(long bpId, TEliteMessagePushType messageType, TEliteMessageData messageData,
            TEliteMessageStrategy strategy) {
        logger.info("post message to bpId = {}, messageType = {}, messageData = {}, strategy = {}", new Object[]{bpId, messageType, messageData, strategy});
        boolean flag = false;
        if (bpId <= 0) return flag;
        String cacheKey = null == strategy ? null : strategy.getIdentity();
        if (StringUtils.isBlank(cacheKey)) cacheKey = getCacheKey(bpId);
        switch (messageType) {
        case INBOX :
            InboxMessageBean messageBean = conventMessageData2InboxMessageBean(bpId, messageData);
            flag = notify2Inbox(messageBean);
            break;
        case WECHAT:
            if (isTimeValid(strategy) && isNumValid(strategy, cacheKey)) {
                flag = notify2Wechat(bpId, messageData.getWechatTemplateId(), messageData.getWechatUrl(), messageData.getWechatData());
            }
            break;
        case CELL_MESSAGE:
            if (isTimeValid(strategy) && isNumValid(strategy, cacheKey)) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                String mobile = userInfo.getMobile();
                if (StringUtils.isBlank(mobile)) {
                    logger.info("user did not have cell phone number.which userId = " + bpId);
                    return false;
                }
                flag = notify2CellPhone(messageData.getSendCloudTemplate(), mobile, messageData.getSendCloudVariables());
            }
            break;
        case WEAK :
            messageBean = conventMessageData2InboxMessageBean(bpId, messageData);
            flag = notify2Inbox(messageBean);
            break;
        case MEDIUM :
            EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, bpId, messageData, null));
            if (isTimeValid(strategy) && isNumValid(strategy, cacheKey)) {
                flag = notify2Wechat(bpId, messageData.getWechatTemplateId(), messageData.getWechatUrl(), messageData.getWechatData());
                if (!flag) {
                    UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                    String mobile = userInfo.getMobile();
                    if (StringUtils.isBlank(mobile)) {
                        logger.info("user did not have cell phone number.which userId = " + bpId);
                        return false;
                    }
                    logger.info("send wechat message to bpId = {} failed, start to send cell message! cell num = {}", new Object[]{bpId, mobile});
                    flag = notify2CellPhone(messageData.getSendCloudTemplate(), mobile, messageData.getSendCloudVariables());
                }
            }
            break;
        case STRONG :
            EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, bpId, messageData, null));
            if (isTimeValid(strategy) && isNumValid(strategy, cacheKey)) {
                flag = notify2Wechat(bpId, messageData.getWechatTemplateId(), messageData.getWechatUrl(), messageData.getWechatData());
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                String mobile = userInfo.getMobile();
                if (StringUtils.isBlank(mobile)) {
                    logger.info("user did not have cell phone number.which userId = " + bpId);
                    return flag;
                }
                flag = flag || notify2CellPhone(messageData.getSendCloudTemplate(), mobile, messageData.getSendCloudVariables());
            }
            break;
        case CELL_AND_INBOX :
            EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, bpId, messageData, null));
            if (isTimeValid(strategy) && isNumValid(strategy, cacheKey)) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
                String mobile = userInfo.getMobile();
                if (StringUtils.isBlank(mobile)) {
                    logger.info("user did not have cell phone number.which userId = " + bpId);
                    return false;
                }
                flag = notify2CellPhone(messageData.getSendCloudTemplate(), mobile, messageData.getSendCloudVariables());
            }
            break;
       default:
           break;
        }
        //refreash cache key, if wechat message or cell message succeed
        if (flag && null !=strategy && strategy.getFrequenceType() != TEliteMessageFrequenceType.UNLIMIT && messageType != TEliteMessagePushType.INBOX && messageType != TEliteMessagePushType.WEAK) {
            int timePeriod = 0;
            switch (strategy.getFrequenceType()) {
            case DAY :
                timePeriod = 86400;
                break;
            case HOUR :
                timePeriod = 3600;
                break;
            default:
                break;
            }
            if (redisCache.exist(cacheKey)) {
                long ttl = redisCache.ttl(cacheKey);
                int num = (int) redisCache.get(cacheKey);
                redisCache.put(cacheKey, (int) ttl, --num);
                logger.info("bpId = {}, cache={} remaining = {}s, message restrict num = {}", new Object[]{bpId, cacheKey, ttl, num});
            } else {
                int num = strategy.getFrequenceValue() - 1;
                redisCache.put(cacheKey, timePeriod, num);
                logger.info("bpId = {}, cacheKey = {} doesn't exist, set in cache, timePeriod = {}, num = {}", new Object[]{bpId, cacheKey, timePeriod, num});
            }
        }
        logger.info("send message to bpId = {}, messageType = {}, result = {}", new Object[]{bpId, messageType, flag});
        return flag;
    }
    
    private String getCacheKey(long bpId) {
        return CacheConstants.CACHE_ELITE_MSG_LIMIT_PREFIX + String.valueOf(bpId);
    }
    
    private InboxMessageBean conventMessageData2InboxMessageBean(Long bpId, TEliteMessageData messageData) {
        InboxMessageBean inboxMessageBean = new InboxMessageBean();
        EliteMessageData eliteMessageData = EliteMessageData.findByValue(messageData.getInboxMessageDataValue());
        inboxMessageBean.setToId(bpId);
        inboxMessageBean.setTopic(eliteMessageData.getTopic());
        inboxMessageBean.setMessageType(eliteMessageData.getDetailType());
        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("content", messageData.getInboxMessageContent());
        switch (eliteMessageData.getDetailType()) {
        case INTERACTION:
            contentParams.put("interactionType", eliteMessageData.getInteractionType());
            break;
        case SYSTEM:
            contentParams.put("systemType", eliteMessageData.getSystemType());
            contentParams.put("systemStatus", eliteMessageData.getSystemStatus());
            break;
        default:
            break;
        }
        inboxMessageBean.setContentParams(contentParams);
        return inboxMessageBean;
    }
    
    private boolean isTimeValid(TEliteMessageStrategy strategy) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        switch (strategy.getTimePeriodType()) {
        case ALL_TIME:
            return true;
        case DAY_TIME:
            return hour < Constants.MESSAGE_STRATEGY_END_TIME && hour >= Constants.MESSAGE_STRATEGY_START_TIME;
        default:
            return false;
        }
    }
    
    private boolean isNumValid(TEliteMessageStrategy strategy, String cacheKey) {
        return strategy.getFrequenceType() == TEliteMessageFrequenceType.UNLIMIT || !redisCache.exist(cacheKey) || ((int) redisCache.get(cacheKey)) > 0;
    }
}