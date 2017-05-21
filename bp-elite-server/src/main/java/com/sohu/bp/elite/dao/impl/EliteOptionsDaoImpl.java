package com.sohu.bp.elite.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.dao.EliteOptionsDao;
import com.sohu.bp.elite.dao.EliteQuestionDao;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.service.EliteSearchService;
import com.sohu.bp.elite.util.EliteFeatureUtil;

import net.sf.json.JSONArray;

public class EliteOptionsDaoImpl implements EliteOptionsDao{
    private static final Logger log = LoggerFactory.getLogger(EliteOptionsDaoImpl.class);
    private static final String CACHE_KEY_PREFIX = "option_";
    
    private CacheManager redisCacheManager;
    private EliteSearchService eliteSearchService;
    private EliteQuestionDao questionDao;
    private RedisCache optionRecordCache;
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void setEliteSearchService(EliteSearchService eliteSearchService) {
        this.eliteSearchService = eliteSearchService;
    }
    
    public void setQuestionDao(EliteQuestionDao questionDao) {
        this.questionDao = questionDao;
    }
    
    //TODO:增加redis的hincrement的接口
    public void init() {
        optionRecordCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_OPTION);
    }
    
    @Override
    public boolean createRecord(Long questionId, Integer optionNum) {
        if (null == questionId || questionId <= 0) return false;
        String key = getCacheKey(questionId);
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 1; i <= optionNum; i++) {
            map.put(String.valueOf(i), String.valueOf(0));
        }
        return optionRecordCache.hSet(key, map);
    }
    
    @Override
    public boolean addOneVote(Long questionId, Long optionId) {
        boolean flag = false;
        if (null == questionId || null == optionId) return flag;
        String key = getCacheKey(questionId);
        if (!optionRecordCache.exist(key)) {
            reloadCache(questionId);
        }
        //TODO:用hincrement来改进
        flag = optionRecordCache.hIncrBy(key, optionId.toString(), 1) > 0;
//        String value = optionRecordCache.hGet(key, optionId.toString());
//        if (StringUtils.isNotBlank(value)) {
//            Integer count = Integer.valueOf(value);
//            flag = optionRecordCache.hSet(key, optionId.toString(), (++count).toString());
//        }
        log.info("add one vote, questionId = {}, optionId = {}, result = {}", new Object[]{questionId, optionId, flag});
        return flag;
    }

    @Override
    public Map<Integer, Integer> getOptionRecord(Long questionId) {
       if (null == questionId || questionId <= 0) return null;
       Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
       String key = getCacheKey(questionId);
       if (!optionRecordCache.exist(key)) {
           reloadCache(questionId);
       }
       Map<String, String> map = optionRecordCache.hGetAll(key);
       map.forEach((optionId, count) -> counts.put(Integer.valueOf(optionId), Integer.valueOf(count)));
       return counts;
    }
    
	@Override
	public void removeCache(Long questionId) {
		String key = getCacheKey(questionId);
		optionRecordCache.remove(key);
	}
    
    private String getCacheKey(Long questionId) {
        return CACHE_KEY_PREFIX + questionId;
    }
    
    private void reloadCache(Long questionId) {
       TSearchAnswerCondition condition = new TSearchAnswerCondition();
       EliteQuestion question = questionDao.get(questionId);
       int optionNum = 0;
       if (EliteFeatureUtil.isVSQuestion(question)) {
           optionNum = 2;
       } else if (EliteFeatureUtil.isVoteQuestion(question)) {
           optionNum = JSONArray.fromObject(question.getOptions()).size();
       }
       String key = getCacheKey(questionId);
       for (int i = 1; i <= optionNum; i++) {
           condition.setQuestionId(questionId).setFrom(0).setCount(1).setSpecialId(i);
           ListResult listResult = eliteSearchService.searchAnswer(condition);
           if (null != listResult) {
               optionRecordCache.hSet(key, String.valueOf(i), String.valueOf(listResult.getTotal()));
           } else {
               optionRecordCache.hSet(key, String.valueOf(i), String.valueOf(0));
           }
       }
       log.info("reload questionId = {}, optionNum = {}", new Object[]{questionId, optionNum});
    }
}
