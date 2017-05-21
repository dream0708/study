package com.sohu.bp.elite.dao;

import java.util.Map;

/**
 * 需要用户选择内容的service，包括投票，晒图等。
 * @author zhijungou
 * 2017年3月17日
 */
public interface EliteOptionsDao {
	/**
	 * 创建记录行为
	 * @param questionId
	 * @param optionNum
	 * @return
	 */
    public boolean createRecord(Long questionId, Integer optionNum);
    /**
     * 计数
     * @param questionId
     * @param optionId
     * @return
     */
    public boolean addOneVote(Long questionId, Long optionId);
    /**
     * 获取计数值
     * @param questionId
     * @return
     */
    public Map<Integer, Integer> getOptionRecord(Long questionId);
    /**
     * 删除对应缓存
     * @param questionId
     */
    public void removeCache(Long questionId);
}
