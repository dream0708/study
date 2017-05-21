package com.sohu.bp.elite.service.web;

import java.util.List;

import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TEliteSourceType;
/**
 * 问答广场
 * @author zhijungou
 * 2016年10月27日
 */
public interface SquareService {
	/**
	 * 获取最新更新的条数
	 * @param latestTime
	 * @return
	 */
	public Long getNewEliteNum(Long latestTime);
	/**
	 * 获取最初的Constants.SQUARE_INITIAL_ELITE_NUM条问答
	 * @param bpId
	 * @return
	 */
	public List<SimpleFeedItemBean> getInitialEliteList(Long bpId, TEliteSourceType source);
	/**
	 * 获取最新的回答，有Constants.SQUARE_MAX_NEW_ELITE_NUM条的限制
	 * @param latestTime
	 * @param bpId
	 * @return
	 */
	public List<SimpleFeedItemBean> getNewEliteList(Long latestTime, Long bpId, TEliteSourceType source);
	/**
	 * 将问答存入广场
	 * @param id
	 * @param feedType
	 */
	public void insertSquare(Long id, FeedType feedType);
	/**
	 * 清除广场的缓存
	 */	
	public void flushSquare();
	/**
	 * 将删除或者驳回的内容从队列中删除
	 * @param objectId
	 * @param feedType
	 */
	public void reduceObject(Long objectId, FeedType feedType);
	/**
	 * 获取老的广场feed流
	 * @param oldestTime
	 * @param bpId
	 * @param bpType
	 * @return
	 */
	public List<SimpleFeedItemBean> getBackward(Long oldestId, Integer feedType, Long bpId, TEliteSourceType source);
	/**
	 * 获取精选广场新增数量
	 * @param latestTime
	 * @return
	 */
	public Long getNewSelectedNum(Long latestTime);
	/**
	 * 获取精选广场backward
	 * @param oldestTime
	 * @param bpId
	 * @param count
	 * @param source
	 * @return
	 */
	public List<SimpleFeedItemBean> getSelectedBackward(Long oldestTime, int count, Long bpId, TEliteSourceType source);
	/**
	 * 获取精选广场forward
	 * @param latestTime
	 * @param count
	 * @param bpId
	 * @param source
	 * @return
	 */
	public List<SimpleFeedItemBean> getSelectedForward(Long latestTime, int count, Long bpId, TEliteSourceType source);
	/**
	 * 获取精选广场新增数量
	 * @param latestTime
	 * @param bpId
	 * @param source
	 * @return
	 */
	public List<SimpleFeedItemBean> getNewSelectedList(Long latestTime, Long bpId, TEliteSourceType source);
}
