package com.sohu.bp.elite.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 表结构example：
 * CREATE TABLE `tb_sequence` (
 * `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'sequence name',
 * `id` bigint(20) DEFAULT '0' COMMENT '当前的最大id',
 * `desc` varchar(255) DEFAULT '' COMMENT '描述',
 * PRIMARY KEY (`name`),
 * ) ENGINE=InnoDB
 * 
 * create time: 2015年11月8日 下午5:26:15
 * @auther dexingyang
 */
public abstract class Sequence {

	private static final Logger logger = LoggerFactory.getLogger(Sequence.class);
	
	private String SEQ_TABLE_NAME = "tb_sequence";
	private int DEFAULT_STEP = 100;
	private DataSource dataSource;

	private AtomicLong currentValue;
	private long maxValue;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public Long nextSequence(String sequenceName){
		return nextSequence(sequenceName, DEFAULT_STEP);
	}
	
	public Long nextSequence(String sequenceName, int step){
		//需要从数据库重新获得id段
		if(currentValue == null || currentValue.get() == 0 
		  || currentValue.compareAndSet(maxValue, 0)){
			synchronized(this){
				//再次check,因为有可能在阻塞时其它线程已创建
				if(currentValue == null || currentValue.get() == 0 
				  || currentValue.compareAndSet(maxValue, 0)){
					long id = 0L;
					while(id == 0){
						id = updateId(sequenceName,step);   
						if(id == 0){
							try{
								Thread.sleep(10);  //如果获取失败,则sleep后重新获取
							}catch(Exception e){}
						}
					}
					currentValue = new AtomicLong(id-step); //-1保证(id-step)值能用,因为后面是先increment
					maxValue = id;
				}
			}
		}
		
		return new Long(currentValue.incrementAndGet());
	}
	
	/**
	 * 从数据库中重新获取id段
	 * @param sequenceName
	 * @param step
	 */
	private long updateId(String sequenceName, int step){
		Connection conn = null;
		PreparedStatement selectStatment = null;
		PreparedStatement updateStatment = null;
		ResultSet rs = null;
		long id = 0L;
		try {
			String updateSql = "UPDATE "+SEQ_TABLE_NAME+" SET id=last_insert_id(id+?) WHERE name=?";
			String selectSql = "SELECT last_insert_id() AS id from tb_sequence";
			
			conn = dataSource.getConnection();
			
			updateStatment = conn.prepareStatement(updateSql);
			updateStatment.setInt(1, step);
			updateStatment.setString(2, sequenceName);
			
			selectStatment = conn.prepareStatement(selectSql);
			
			int count = updateStatment.executeUpdate();
			if(count > 0){
				rs = selectStatment.executeQuery();
				if(rs != null){
					rs.next();
					id = rs.getLong(1);
					System.out.println(System.nanoTime()+" thread-id:"+Thread.currentThread().getId()+",id="+id);
					logger.info("get sequence=["+sequenceName+"] section success, maxId="+id+", step="+step);
				}
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
			if(selectStatment != null){
				try{
					selectStatment.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(updateStatment != null){
				try{
					updateStatment.close();
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
