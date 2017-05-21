package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteMedia;

/**
 * 
 * @author nicholastang
 * 2016-08-17 14:36:17
 * TODO elite media service interface
 */
public interface EliteMediaService
{
	/**
	 * 创建一个多媒体内容
	 * @param eliteMedia
	 * @return
	 */
	public Long create(EliteMedia eliteMedia);
	
	/**
	 * 更新一个多媒体内容
	 * @param eliteMedia
	 * @return
	 */
	public boolean update(EliteMedia eliteMedia);
	
	/**
	 * 删除一个多媒体内容
	 * @param eliteMedia
	 * @return
	 */
	public boolean remove(EliteMedia eliteMedia);
	
	/**
	 * 通过id 获取多媒体内容
	 * @param id
	 * @return
	 */
	public EliteMedia getById(Long id);
	
	/**
	 * 获取问题的有效多媒体内容
	 * @param questionId
	 * @return
	 */
	public List<EliteMedia> getMediaListByQuestionId(Long questionId);
	
	/**
	 * 获取问题特定种类的多媒体内容
	 * @param questionId
	 * @param type
	 * @return
	 */
	public List<EliteMedia> getMediaListByQuestionIdAndType(Long questionId, Integer type);
	
	/**
	 * 获取回答的有效多媒体内容
	 * @param answerId
	 * @return
	 */
	public List<EliteMedia> getMediaListByAnswerId(Long answerId);
	
	/**
	 * 获取回答的特定种类多媒体内容
	 * @param answerId
	 * @param type
	 * @return
	 */
	public List<EliteMedia> getMediaListByAnswerIdAndType(Long answerId, Integer type);
	
	
}