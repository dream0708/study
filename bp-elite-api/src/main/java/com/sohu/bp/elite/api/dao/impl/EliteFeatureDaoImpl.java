package com.sohu.bp.elite.api.dao.impl;





import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.sohu.bp.elite.model.TEliteFragmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.dao.EliteFeatureDao;
import com.sohu.bp.elite.api.util.CompositeIDUtil;
import com.sohu.bp.elite.enums.BpType;

public class EliteFeatureDaoImpl implements EliteFeatureDao {
	
    private Logger log = LoggerFactory.getLogger(EliteFeatureDaoImpl.class);
	private CacheManager redisCacheManager;
	private CacheManager ssdbCacheManager;
	private RedisCache redisCache;
	private SsdbCache ssdbCache;
	
	public void setRedisCacheManager(CacheManager redisCacheManager)
	{
		this.redisCacheManager = redisCacheManager;
	}
	public void setSsdbCacheManager(CacheManager ssdbCacheManager){
		this.ssdbCacheManager = ssdbCacheManager;
	}
	
	public void init()
	{
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_FEATURE);
		ssdbCache = (SsdbCache) ssdbCacheManager.getCache(CacheConstants.CACHE_FIRST_FOLLOW);
	}
	
	@Override
	public Boolean updateCache(String key) {
		Boolean retVal = false;
		if(null == key) return retVal;
		switch(key)
		{
		case Constants.ELITE_SUBJECT_KEY :
			redisCache.remove(Constants.ELITE_SUBJECT_KEY);
			redisCache.remove(Constants.ELITE_FOCUS_ORDER_KEY);
			break;
		case Constants.ELITE_TOPIC_KEY:
			redisCache.remove(Constants.ELITE_TOPIC_KEY);
			redisCache.remove(Constants.ELITE_FOCUS_ORDER_KEY);
			break;
		case Constants.ELITE_FRAGMENT_KEY:
			redisCache.remove(Constants.ELITE_FRAGMENT_KEY + Constants.DEFAULT_CACHE_SPLIT_CHAR + TEliteFragmentType.NAVLABEL.getValue());
			redisCache.remove(Constants.ELITE_FRAGMENT_KEY + Constants.DEFAULT_CACHE_SPLIT_CHAR + TEliteFragmentType.SLOGAN.getValue());
			break;
		case Constants.ELITE_TAGSQUARE_KEY:
			redisCache.remove(Constants.ELITE_TAGSQUARE_KEY);
			break;
		case Constants.ELITE_COLUMN_KEY:
			redisCache.remove(Constants.ELITE_COLUMN_KEY);
			redisCache.remove(Constants.ELITE_FOCUS_ORDER_KEY);
			break;
		default: return retVal;
		}
		retVal =true;
		return retVal;
	}

	@Override
	public String getAllUser() {
		Long num = ssdbCache.zCount(Constants.ELITE_FOLLOW_USER_LOGIN);
		List<String> ids = ssdbCache.zRange(Constants.ELITE_FOLLOW_USER_LOGIN, 0, num.intValue());
		String idString = "";
		for (String id : ids){
			idString += id + ";";
		}
		return idString;
	}
	@Override
	public List<Long> getInvitedList() {
		List<Long> ids = new ArrayList<Long>();
		Long totalNum = redisCache.zCount(CacheConstants.INVITED_USER);
		if(null != totalNum && totalNum > 0){
			Set<String> idSet = redisCache.zRevRange(CacheConstants.INVITED_USER, 0, totalNum - 1);
			idSet.forEach((id) -> {ids.add(Long.valueOf(id));});
		}
		return ids;
	}
	@Override
	public Boolean removeInvited(Long id) {
		if(null == id || id <= 0) return true;
		Boolean retVal = redisCache.zRem(CacheConstants.INVITED_USER, id.toString());
		return retVal;
	}
	@Override
	public Boolean addInvited(Long id) {
		if(null ==id || id <= 0) return true;
		Boolean retVal = redisCache.zAdd(CacheConstants.INVITED_USER, new Date().getTime(), id.toString());
		return retVal;
	}
	@Override
	public List<Long> getEditList() {
		List<Long> ids = new ArrayList<Long>();
		Long totalNum = redisCache.zCount(CacheConstants.EDIT_USER);
		if(null != totalNum && totalNum > 0){
			Set<String> idSet = redisCache.zRevRange(CacheConstants.EDIT_USER, 0, totalNum - 1);
			idSet.forEach((id) -> {ids.add(Long.valueOf(id));});
		}
		return ids;
	}
	@Override
	public Boolean addEditHistory(Long id) {
		if(null == id || id <= 0) return true;
		Boolean retVal = redisCache.zAdd(CacheConstants.EDIT_USER, new Date().getTime(), id.toString());
		return retVal;
	}
    @Override
    public Boolean saveFocusOrder(Long objectId, Integer bpType, Integer order) {
       boolean flag = false;
       Long compositeId = CompositeIDUtil.getCompositeId(objectId, BpType.valueMap.get(bpType));
       Set<String> sets = redisCache.zRangeByScore(Constants.ELITE_FOCUS_ORDER_SET_KEY, order.toString(), order.toString());
       if (null != sets && sets.size() > 0) return flag;
       if (order <= 0) {
           flag = redisCache.zRem(Constants.ELITE_FOCUS_ORDER_SET_KEY, compositeId.toString());
           log.info("remove bpType = {}, objectId = {} from focus order queue, result = {}", new Object[]{bpType, objectId, flag});
       } else {
           flag = redisCache.zAdd(Constants.ELITE_FOCUS_ORDER_SET_KEY, order, compositeId.toString());
           log.info("add bpTyp = {}, objectId = {} from focus order queue, result = {}", new Object[]{bpType, objectId, flag});
       }
       if (flag) {
           redisCache.remove(Constants.ELITE_FOCUS_ORDER_KEY);
           log.info("change focus order, remove elite focus cache!");
       }
       return flag;
    }
    @Override
    public List<Integer> getOrdersByIds(List<Long> objectIds, Integer bpType) {
        List<Integer> orders = new ArrayList<Integer>();
        for (Long objectId : objectIds) {
            Long compositeId = CompositeIDUtil.getCompositeId(objectId, BpType.valueMap.get(bpType));
            long order = redisCache.zScore(Constants.ELITE_FOCUS_ORDER_SET_KEY, compositeId.toString());
            orders.add((int)order);
        }
        log.info("get orders : BpType = {}, objectIds = {}, orders = {}", new Object[]{bpType, objectIds, orders});
        return orders;
    }

}
