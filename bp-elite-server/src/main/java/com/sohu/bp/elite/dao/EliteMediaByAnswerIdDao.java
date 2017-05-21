package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteMediaByanswerid;

/**
 * 
 * @author nicholastang
 * 2016-08-16 10:46:42
 * TODO
 */
public interface EliteMediaByAnswerIdDao
{
	public Long insert(EliteMediaByanswerid eliteMediaByanswerid);
	
	public boolean update(EliteMediaByanswerid eliteMediaByanswerid);
	
	public EliteMediaByanswerid getById(Long id);
	
	public EliteMediaByanswerid getByUnionId(Long answerId, Long mediaId);
	
	public List<Long> getMediaIdListByAnswerId(Long answerId);
	
	public List<Long> getMediaIdListByAnswerIdAndType(Long answerId, Integer type);
	
	public boolean delete(Long id);
}