package com.sohu.bp.elite.dao;

import com.sohu.bp.elite.persistence.EliteMedia;

/**
 * 
 * @author nicholastang
 * 2016-08-15 17:38:33
 * TODO media dao interface
 */
public interface EliteMediaDao
{
	/**
	 * 新增
	 * @param eliteMedia
	 * @return
	 */
	public Long save(EliteMedia eliteMedia);
	
	/**
	 * 更新
	 * @param eliteMedia
	 * @return
	 */
	public boolean update(EliteMedia eliteMedia);
	
	/**
	 * 获取
	 * @param id
	 * @return
	 */
	public EliteMedia getById(Long id);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public boolean delete(Long id);
}