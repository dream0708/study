package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteSubject;



/**
 * 
 * @author zhijungou
 * 2016年8月9日
 */
public interface EliteSubjectService 
{
	public Long setEliteSubjectHistory(EliteSubject eliteSubject);
	public List<EliteSubject> getHistoryByStatus(Integer status, Integer start, Integer count);
	public List<EliteSubject> getAllHistory(Integer start, Integer count);
	public Long getAllHistoryCount();
	public Long getSubjectCountByStatus(Integer status);
	public EliteSubject getHistoryById(Long id);
}