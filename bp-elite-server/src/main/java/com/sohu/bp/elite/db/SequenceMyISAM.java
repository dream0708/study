package com.sohu.bp.elite.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * 表结构example：
 * CREATE TABLE `sequence_tb` (
 *  `id` BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
 *  `stub` char(1) NOT NULL,
 *  PRIMARY KEY (`id`),
 *  UNIQUE KEY `stub-index` (`stub`)
 * )ENGINE=MyISAM
 * 
 * SET auto_increment_increment=2;   控制列中的值的增量值，也就是步长
 * SET auto_increment_offset = 1;    确定AUTO_INCREMENT列值的起点，也就是初始值
 * 如果auto_increment_offset的值大于auto_increment_increment的值，
 * 则auto_increment_offset的值会被忽略
 * 
 * 可用如下语句
 * ALTER TABLE tableName auto_increment=number
 * 
 * replace into sequence_tb(stub) values('a');select LAST_INSERT_ID();
 * 
 * 需要使用'MyISAM'引擎，这样才能使用表级锁
 * 
 * create time: 2015年11月8日 下午5:26:15
 * @auther dexingyang
 */
public abstract class SequenceMyISAM {

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Long nextSequence(String tableName){
		Long id = null;
		Connection conn = null;
		Statement statment = null;
		ResultSet rs = null;
		try {
			String sql = "REPLACE INTO "+tableName+"(stub) VALUES('a');";
			String sql2 = "SELECT LAST_INSERT_ID();";
			
			conn = dataSource.getConnection();
			statment = conn.createStatement();
			statment.execute(sql);
			rs = statment.executeQuery(sql2);
			while(rs.next()){
				id = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try{
					rs.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
				rs = null;
			}
			if(statment != null){
				try{
					statment.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try{
					//将Connection连接对象还给数据库连接池
					conn.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return id;
	}
	
	public abstract Long nextSequence();
}
