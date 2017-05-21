package com.sohu.bp.elite.db;

import java.util.Map;

import com.sohu.bp.persistence.AbstractEntity;

/**
 * create time: 2015年11月8日 下午2:47:40
 * @auther dexingyang
 */
public interface TablePartitioner {

	/**
	 * 根据参数计算分表的index
	 * @param ctxMap
	 * @return
	 */
	public int caculateTableIndex(Map<String, Object> ctxMap);
	
	/**
	 * 根据参数计算分表的index
	 * @param entity
	 * @return
	 */
	public int caculateTableIndex(AbstractEntity entity);
	
	/**
	 * 根据参数计算分表的index
	 * @param num
	 * @return
	 */
	public int caculateTableIndex(Long num);
	
	public int caculateTableIndex(Integer num); 
	
	/**
	 * 根据参数计算分表的index
	 * @param str
	 * @return
	 */
	public int caculateTableIndex(String str);
	
	/**
	 * 获取表名
	 * @param tableIndex
	 * @return
	 */
	public String getTableName(int tableIndex);
}
