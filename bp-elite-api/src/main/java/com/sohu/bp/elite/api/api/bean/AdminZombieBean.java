package com.sohu.bp.elite.api.api.bean;

public class AdminZombieBean{

	private int bpType;
	private long itemId;
	private long periodTime;
	private long createTime;
	
	public int getBpType() {
		return bpType;
	}
	public void setBpType(int bpType) {
		this.bpType = bpType;
	}
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public long getPeriodTime() {
		return periodTime;
	}
	public void setPeriodTime(long periodTime) {
		this.periodTime = periodTime;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
