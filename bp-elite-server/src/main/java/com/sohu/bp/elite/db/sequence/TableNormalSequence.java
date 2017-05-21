package com.sohu.bp.elite.db.sequence;

import com.sohu.bp.elite.db.Sequence;

/**
 * 
 * @author nicholastang
 * 2016年3月18日
 */
public class TableNormalSequence extends Sequence
{
	private String sequenceTableName;
	
	public String getSequenceTableName() {
		return sequenceTableName;
	}


	public void setSequenceTableName(String sequenceTableName) {
		this.sequenceTableName = sequenceTableName;
	}


	@Override
	public Long nextSequence() {
		// TODO Auto-generated method stub
		return super.nextSequence(sequenceTableName);
	}
	
}