package com.sohu.bp.elite.api.task;

/**
 * 
 * @author zhijungou
 * 2016年11月7日
 */
public interface TaskQueue {
	public void push(EliteCrawlSquareAsycTask task);
	public EliteCrawlSquareAsycTask poll();
	public void delete(EliteCrawlSquareAsycTask task);
	public long size();
	public boolean getMutexLock();
	public void releaseMutexLock(); 
}
