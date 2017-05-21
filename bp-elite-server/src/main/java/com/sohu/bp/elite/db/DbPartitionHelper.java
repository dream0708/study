package com.sohu.bp.elite.db;

import java.util.Map;

import com.sohu.bp.persistence.AbstractEntity;

/**
 * 数据分区分析器，通过此分析器可以分析表所在的具体的数据库与数据表名称
 * 
 * create time: 2015年11月5日 下午4:22:46
 * @auther dexingyang
 */ 
public class DbPartitionHelper {  
	
	/**
	 * data source prefix in map
	 */
	public static final String DS_PREFIX = "bp_decorate_";
	
	public static final int TABLE_COUNT = 64;
  
	/**
	 * 分为4个数据库,每个数据库16个表,总共64张表
	 * @param tableIndex
	 * @return
	 */
	public static int getDBIndex(int tableIndex){
		if(tableIndex < 16){
			return 0;
		}else if(tableIndex < 32){
			return 1;
		}else if(tableIndex < 48){
			return 2;
		}else{
			return 3;
		}
	}
	
    /** 
     * 根据Map参数进行分析，创建表的分区信息 
     *  
     * @param tableLogicName 
     *            逻辑表名称，也将会成为表的别名 
     * @param ctxMap 
     *            上下文信息存储,用来存储分区关键值 
     * @return 
     */  
    public static String getTableName(TablePartitioner partitioner,  
            Map<String, Object> ctxMap){
    	int tableIndex = partitioner.caculateTableIndex(ctxMap);
    	String tableName = partitioner.getTableName(tableIndex);
    	String dataSourceKey = DS_PREFIX + getDBIndex(tableIndex);
    	
    	DataSourceStatus.setCurrentDsKey(dataSourceKey);   //存储到threadLocal中
    	
    	return tableName;
    }
    
    /**
     * 根据AbstractEntity参数进行分析，创建表的分区信息 
     * @param partitioner
     * @param entity
     * @return
     */
    public static String getTableName(TablePartitioner partitioner,  
            AbstractEntity entity){
    	int tableIndex = partitioner.caculateTableIndex(entity);
    	String tableName = partitioner.getTableName(tableIndex);
    	String dataSourceKey = DS_PREFIX + getDBIndex(tableIndex);
    	
    	DataSourceStatus.setCurrentDsKey(dataSourceKey);   //存储到threadLocal中
    	
    	return tableName;
    }
    
    /**
     * 根据number参数进行分析，创建表的分区信息 
     * @param partitioner
     * @param num
     * @return
     */
    public static String getTableName(TablePartitioner partitioner,  
            Long num){
    	int tableIndex = partitioner.caculateTableIndex(num);
    	String tableName = partitioner.getTableName(tableIndex);
    	String dataSourceKey = DS_PREFIX + getDBIndex(tableIndex);
    	
    	DataSourceStatus.setCurrentDsKey(dataSourceKey);   //存储到threadLocal中
    	
    	return tableName;
    }
    
    /**
     * 根据number参数进行分析，创建表的分区信息 
     * @param partitioner
     * @param num
     * @return
     */
    public static String getTableName(TablePartitioner partitioner,  
            Integer num){
    	int tableIndex = partitioner.caculateTableIndex(num);
    	String tableName = partitioner.getTableName(tableIndex);
    	String dataSourceKey = DS_PREFIX + getDBIndex(tableIndex);
    	
    	DataSourceStatus.setCurrentDsKey(dataSourceKey);   //存储到threadLocal中
    	
    	return tableName;
    }
    
    /**
     * 根据字符串参数进行分析，创建表的分区信息 
     * @param partitioner
     * @param str
     * @return
     */
    public static String getTableName(TablePartitioner partitioner,  
            String str){
    	int tableIndex = partitioner.caculateTableIndex(str);
    	String tableName = partitioner.getTableName(tableIndex);
    	String dataSourceKey = DS_PREFIX + getDBIndex(tableIndex);
    	
    	DataSourceStatus.setCurrentDsKey(dataSourceKey);   //存储到threadLocal中
    	
    	return tableName;
    }
}  
