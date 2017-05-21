package com.sohu.bp.elite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author nicholastang
 * 2016-10-14 19:35:01
 * TODO elite通用异步线程池
 */
public class EliteAsyncTaskPool {
	private static final Logger logger = LoggerFactory.getLogger(EliteAsyncTaskPool.class);
	
	private static int CORE_POOL_SIZE = 8;
	private static int MAX_POOL_SIZE = 8;
	private static long KEEP_ALIVE_TIME = 0;
	
	private static LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	private static ThreadPoolExecutor executor = 
			new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.DiscardPolicy());
	
	public static void addTask(EliteAsyncTask asyncTask){
		 try{
			 executor.execute(asyncTask);
		 }catch (Exception e) {
			 logger.error("", e);
		}
	}
	
	public static Integer getQueueLength(){
		return queue.size();
	}
	
}