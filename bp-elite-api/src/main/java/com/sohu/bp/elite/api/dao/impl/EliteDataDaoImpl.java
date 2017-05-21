package com.sohu.bp.elite.api.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.dao.EliteDataDao;

import net.sf.json.JSONObject;

public class EliteDataDaoImpl implements EliteDataDao{
    private static final Logger log = LoggerFactory.getLogger(EliteDataDaoImpl.class);
    
    private CacheManager redisCacheManager;
    private RedisCache redisCache;
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void init() {
        redisCache = (RedisCache)redisCacheManager.getCache(CacheConstants.CACHE_DATA);
    }
    
    @Override
    public JSONObject getAppFocus() {
        String dataString = (String)redisCache.get(CacheConstants.DATA_APP_FOCUS);
        JSONObject dataJSON = new JSONObject();
        if (StringUtils.isBlank(dataString)) return dataJSON;
        dataJSON = JSONObject.fromObject(dataString);
        return dataJSON;
    }

    @Override
    public void saveAppFoucs(JSONObject focusData) {
        redisCache.put(CacheConstants.DATA_APP_FOCUS, focusData.toString());
        log.info("save app focus in cache, data = {}", new Object[]{focusData});
    }

}
