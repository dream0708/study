package com.sohu.bp.elite.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 专门用于推送feed流的线程池
 * @author zhijungou
 * 2017年3月31日
 */
public class EliteFeedTaskPool {
    private static final Logger log = LoggerFactory.getLogger(EliteFeedTaskPool.class);
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final long KEEP_ALIVE_TIME = 0L;
    private static final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>();
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, blockingQueue, new ThreadPoolExecutor.DiscardPolicy());
    
    public static void addTask(EliteFeedTask task) {
        try {
            executor.execute(task);
        } catch (Exception e) {
            log.error("", e);
        }
    }
    
    public static Integer getQueueLength() {
        return blockingQueue.size();
    }
}
