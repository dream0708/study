package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TColumnListResult;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.ColumnService;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;

public class ColumnServiceImpl implements ColumnService {
	
	private static final Logger log = LoggerFactory.getLogger(ColumnServiceImpl.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	private UserInfoService userInfoService;
	
	public void setRedisCacheManager(CacheManager redisCacheManager){
		this.redisCacheManager = redisCacheManager;
	}
	
	public void setUserInfoService(UserInfoService userInfoService){
		this.userInfoService = userInfoService;
	}
	
	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_FEATURE);
	}
	
	@Override
	public TEliteColumn getIndexColumn() {
		TEliteColumn column = (TEliteColumn)redisCache.get(Constants.ELITE_COLUMN_KEY);
		if(null == column) {
			List<TEliteColumn> columns = getColumns(0, 1).getColumns();
			if(null == columns || columns.size() <= 0) return new TEliteColumn();
			column = columns.get(0);
		}
		if(null != column) redisCache.put(Constants.ELITE_COLUMN_KEY, column);
		return column;
	}

	@Override
	public TEliteColumn getColumnById(Long columnId) {
		if(null == columnId || columnId <= 0 ) return null;
		TEliteColumn column = (TEliteColumn)redisCache.get(Constants.ELITE_COLUMN_KEY);
		if(null == column || column.getId() != columnId){
			try{
				return eliteAdapter.getEliteColumnById(columnId);
			} catch (Exception e){
				log.error("", e);
			}
		}
		return column;
	}

	@Override
	public TColumnListResult getColumns(int start, int count) {
		try{
			return eliteAdapter.getAllEliteColumnByStatus(start, count, EliteFeatureStatus.VALID.getValue());
		} catch (Exception e){
			log.error("", e);
		}
		return null;
	}

	@Override
	public List<SimpleFeedItemBean> getQuestionsByColumnId(Long columnId, int start, int count) {
		List<SimpleFeedItemBean> questionList = new ArrayList<>();
		try{
			TSearchQuestionCondition condition = new TSearchQuestionCondition();
			condition.setSpecialType(BpType.Elite_Column.getValue()).setSpecialId(columnId).setFrom(start).setCount(count).setSortField("updateTime").setSortType(SortType.DESC)
			.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
			TQuestionListResult listResult = eliteAdapter.searchQuestion(condition);
			List<TEliteQuestion> questions = listResult.getQuestions();
			questionList = ConvertUtil.convertQuestionList(questions, null, null);
		} catch (Exception e) {
			log.error("", e);
		}
		return questionList;
	}

	@Override
	public Long getQuestionNum(Long columnId) {
		Long num = 0L;
		try {
			TSearchQuestionCondition condition = new TSearchQuestionCondition();
			condition.setSpecialType(BpType.Elite_Column.getValue()).setSpecialId(columnId).setFrom(0).setCount(1).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));;
			TQuestionListResult listResult = eliteAdapter.searchQuestion(condition);
			num = listResult.getTotal();
		} catch (Exception e) {
			log.error("", e);
		}
		return num;
	}
}
