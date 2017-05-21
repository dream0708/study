package com.sohu.bp.elite.db.partitioner;

import java.util.Map;

import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.persistence.AbstractEntity;

/**
 * 
 * @author nicholastang
 * 2016年3月21日
 */
public class TableNormalPartitioner implements TablePartitioner
{
	private String tbPartiCol;
	private String tbName;
	
	public String getTbPartiCol() {
		return tbPartiCol;
	}
	public void setTbPartiCol(String tbPartiCol) {
		this.tbPartiCol = tbPartiCol;
	}
	public String getTbName() {
		return tbName;
	}
	public void setTbName(String tbName) {
		this.tbName = tbName;
	}

	@Override
	public int caculateTableIndex(Map<String, Object> ctxMap) {
		// TODO Auto-generated method stub
		long id = (Long) ctxMap.get(tbPartiCol);
		
		int tableIndex = (int) (id % DbPartitionHelper.TABLE_COUNT);
		
		return tableIndex;
	}

	@Override
	public int caculateTableIndex(AbstractEntity entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int caculateTableIndex(Long num) {
		// TODO Auto-generated method stub
		return (int) (num % DbPartitionHelper.TABLE_COUNT);
	}

	@Override
	public int caculateTableIndex(Integer num) {
		// TODO Auto-generated method stub
		return num % DbPartitionHelper.TABLE_COUNT;
	}

	@Override
	public int caculateTableIndex(String str) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTableName(int tableIndex) {
		// TODO Auto-generated method stub
		return this.tbName+"_"+tableIndex;
	}
}