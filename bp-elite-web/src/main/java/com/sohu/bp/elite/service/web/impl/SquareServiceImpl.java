package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TEliteSquareItem;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.elite.task.EliteParallelPool;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.SquareUtil;

import redis.clients.jedis.Tuple;

public class SquareServiceImpl implements SquareService{
	
	private static final Logger log = LoggerFactory.getLogger(SquareServiceImpl.class);
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
				
	@Override
	public Long getNewEliteNum(Long latestTime) {
		if (null == latestTime || latestTime <= 0) return -1l;
		long num = 0;
		try {
		    num = eliteAdapter.getNewSquareNum(latestTime);
		} catch (Exception e) {
		    log.error("", e);
		}
		return num;
	}

	@Override
	public List<SimpleFeedItemBean> getInitialEliteList(Long bpId, TEliteSourceType source) {
		List<SimpleFeedItemBean> itemList = new ArrayList<>();
	    try {
	        List<TEliteSquareItem> items = eliteAdapter.getSquareBackward(0, FeedType.QUESTION.getValue(), Constants.SQUARE_INITIAL_ELITE_NUM);
	        log.info("get initial elite square list num : {}", new Object[]{items});
	        itemList = ConvertUtil.convertSquareItemList(items, bpId, source);
        } catch (Exception e) {
            log.error("", e);
        } 
		return itemList;
	}

	@Override
	public List<SimpleFeedItemBean> getNewEliteList(Long latestTime, Long bpId, TEliteSourceType source) {
		if (null == latestTime || latestTime <= 0 ) return null;
		List<SimpleFeedItemBean> itemList = new ArrayList<>();
		try {
		    List<TEliteSquareItem> items = eliteAdapter.getNewSquareList(latestTime);
		    itemList = ConvertUtil.convertSquareItemList(items, bpId, source);
		} catch (Exception e) {
		    log.error("", e);
		}
		return itemList;
	}
	
	@Override
	public List<SimpleFeedItemBean> getBackward(Long oldestId, Integer feedType, Long bpId, TEliteSourceType source) {
		if (null == oldestId || oldestId <= 0) return null;
		if (null == feedType || feedType < 0) feedType = FeedType.QUESTION.getValue();
		List<SimpleFeedItemBean> itemList = null;
		try {
		    List<TEliteSquareItem> items = eliteAdapter.getSquareBackward(oldestId, feedType, Constants.SQUARE_PAGE_SIZE);
		    itemList = ConvertUtil.convertSquareItemList(items, bpId, source);
		} catch (Exception e) {
		    log.error("", e);
		}
		return itemList;
	}

	@Override
	public void insertSquare(Long id, FeedType feedType) {
	    boolean result = false;
		try {
		    result = eliteAdapter.insertSquare(id, feedType.getValue());
		} catch (Exception e) {
		    log.error("", e);
		}
		log.info("insert itemId = {}, itemType = {} into square, result = {}", new Object[]{id, feedType, result});
	}
	
	@Override
	public void flushSquare() {
		log.info("caution! start to flush square content");
		try {
		    eliteAdapter.flushSquare();
		} catch (Exception e) {
		    log.error("", e);
		}
	}
	

	@Override
	public void reduceObject(Long objectId, FeedType feedType) {
	    boolean result = false;
	    try {
	        result = eliteAdapter.removeSquareItem(objectId, feedType.getValue());
	    } catch (Exception e) {
	        log.error("");
	    }
	    log.info("remove item from square, itemId = {}, itemType = {}, result = {}", new Object[]{objectId, feedType, result});
	}

    @Override
    public Long getNewSelectedNum(Long latestTime) {
        if (null == latestTime || latestTime <= 0) return -1l;
        long num = 0;
        try {
            num = eliteAdapter.getNewSelectedSquareNum(latestTime);
        } catch (Exception e) {
            log.error("", e);
        }
        return num;
    }

    @Override
    public List<SimpleFeedItemBean> getSelectedBackward(Long oldestTime, int count, Long bpId, TEliteSourceType source) {
        List<SimpleFeedItemBean> itemList = new ArrayList<SimpleFeedItemBean>();
        try {
            List<TEliteSquareItem> items = eliteAdapter.getSelectedSquareBackwardByOldestTime(oldestTime, count);
            itemList = ConvertUtil.convertSquareItemList(items, bpId, source);
        } catch (Exception e) {
            log.error("", e);
        }
        return itemList;
    }
    
    @Override
    public List<SimpleFeedItemBean> getSelectedForward(Long latestTime, int count, Long bpId, TEliteSourceType source) {
        List<SimpleFeedItemBean> itemList = new ArrayList<SimpleFeedItemBean>();
        try {
            List<TEliteSquareItem> items = eliteAdapter.getSelectedSquareForwardByLatestTime(latestTime, count);
            itemList = ConvertUtil.convertSquareItemList(items, bpId, source);
        } catch (Exception e) {
            log.error("", e);
        }
        return itemList;
    }

    @Override
    public List<SimpleFeedItemBean> getNewSelectedList(Long latestTime, Long bpId, TEliteSourceType source) {
        if (null == latestTime || latestTime <= 0 ) return null;
        List<SimpleFeedItemBean> itemList = new ArrayList<>();
        try {
            List<TEliteSquareItem> items = eliteAdapter.getNewSelectedSquareList(latestTime);
            itemList = ConvertUtil.convertSquareItemList(items, bpId, source);
        } catch (Exception e) {
            log.error("", e);
        }
        return itemList;
    }

}
