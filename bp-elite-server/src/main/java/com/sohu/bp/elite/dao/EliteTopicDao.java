package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteTopic;


/**
 * 
 * @author zhijungou
 * 2016/8/15
 */
public interface EliteTopicDao {
	
	
	public Long insert(EliteTopic eliteTopic);
	public Boolean update(EliteTopic eliteTopic);
	public Long getTopicCount();
	public List<EliteTopic> getTopicHistory(Integer start, Integer count);
	public Long getTopicCountByStatus(Integer status);
	public List<EliteTopic> getTopicHistoryByStatus(Integer status, Integer start, Integer count);
	public EliteTopic getTopicById(Long id);
	public void removeCache();
	
	//设置互斥锁
	public boolean setMutexLock(String id);
	public void removeMutexLock(String id);
}
