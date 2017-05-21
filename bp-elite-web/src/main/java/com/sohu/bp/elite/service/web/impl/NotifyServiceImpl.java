package com.sohu.bp.elite.service.web.impl;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.bean.InboxMessageBean;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.service.web.NotifyService;
import com.sohu.bp.elite.util.SendCloudSmsUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-27 21:02:35
 * TODO
 */
public class NotifyServiceImpl implements NotifyService
{
	private static Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);
	private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
	private static final BpExtendServiceAdapter bpExtendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static Set<String> whiteListSet = new HashSet(){{
		add("13807940739");add("13979424222");add("18601028484");add("13901586316");add("15050528218");add("15145649936");add("18804642653");add("15114555751");add("13177713332");add("13718759051");add("18611557450");add("18646495921");add("13946566425");add("18600650497");add("18202737927");add("13920653650");add("18811427090");add("18061611941");add("18810720083");add("18511466227");
	add("15880055252");add("13810138235");
	}};

	private CacheManager redisCacheManager;

	private RedisCache cellMsgLimitRedisCache;

	private String environment;

	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public void init(){
		cellMsgLimitRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_ELITE_CELL_MSG_LIMIT);
	}


	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.NotifyService#notify2Statistic(java.lang.Long, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public boolean notify2Statistic(Long objectId, Integer objectType, Integer notifyType) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 发送站内信
	 */
	@Override
	public boolean notify2Inbox(InboxMessageBean messageBean) {
		if(null == messageBean)
			return false;
		CodeMsgData codeMsgData = bpExtendServiceAdapter.addBpMessageDetail(messageBean.getBpMessageDetail());
        if (codeMsgData.getCode() == ResponseConstants.OK) {
            return true;
        } else {
            return false;
        }
	}

	/**
	 * 发送短信
	 */
	@Override
	public boolean notify2CellPhone(Integer templateId, String destnumber, Map<String, String> params) {
		//用于测试短信服务，暂时注释
//		if(StringUtils.isBlank(environment) || environment.equalsIgnoreCase("test"))
//		{
//			logger.info("test environment. so message won't be delivered");
//			return true;
//		}
		Integer pushedNum = (Integer)cellMsgLimitRedisCache.get(destnumber);
		if (null != pushedNum && pushedNum.intValue() >= 6) {
		    logger.info("pushNum is bigger than 6");
			return  false;
		}
		if (!isTimeRestrict()) {
		    logger.info("time is not in 8-22 hours");
		    return false;
		}
		if(whiteListSet.contains(destnumber))
		{
			logger.info("number={} is in white list. so message won't be delivered", new String[]{destnumber});
			return true;
		}
		String resJSONStr = SendCloudSmsUtil.sendMessage(templateId, destnumber, params);
		if(StringUtils.isNotBlank(resJSONStr))
		{
			JSONObject resJSON = JSONObject.fromObject(resJSONStr);
			if(null != resJSON && resJSON.getInt("code") == 0)
			{
				logger.info("send templale {} message to {} success", new String[]{String.valueOf(templateId), destnumber});
				if (null == pushedNum) {
					pushedNum = 0;
				}
				cellMsgLimitRedisCache.put(destnumber, ++pushedNum);
				return true;
			}
			else
			{
				logger.info("send templale {} message to {} failed", new String[]{String.valueOf(templateId), destnumber});
				return false;
				
			}
		}
		
		return false;
	}
	
	private boolean isTimeRestrict() {
	    Calendar calendar = Calendar.getInstance();
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    return hour >= 8 && hour <= 22;
	}	
}