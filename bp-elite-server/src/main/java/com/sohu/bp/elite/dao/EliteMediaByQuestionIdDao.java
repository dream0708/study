package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteMediaByquestionid;

/**
 * 
 * @author nicholastang
 * 2016-08-16 10:15:00
 * TODO elite media 问题id 关联表
 */
public interface EliteMediaByQuestionIdDao
{
	public Long insert(EliteMediaByquestionid eliteMediaByquestionid);
	
	public boolean update(EliteMediaByquestionid eliteMediaByquestionid);
	
	public EliteMediaByquestionid getById(Long id);
	
	public EliteMediaByquestionid getByUnionId(Long questionId, Long mediaId);
	
	public List<Long> getMediaIdListByQuestionId(Long questionId);
	
	public List<Long> getMediaIdListByQuestionIdAndType(Long questionId, Integer type);
	
	public boolean delete(Long id);
}