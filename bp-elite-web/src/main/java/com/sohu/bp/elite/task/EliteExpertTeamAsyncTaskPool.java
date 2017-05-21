package com.sohu.bp.elite.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 用于专家团推送信息的线程池
 * @author zhijungou
 * 2017年2月24日
 */
public class EliteExpertTeamAsyncTaskPool {
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamAsyncTaskPool.class);
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final ExecutorService executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardPolicy());
   
    public static void addTask(EliteExpertTeamAsyncTask task) {
        try{
            executor.execute(task);
            log.info("add task = {} to thread pool." + task.toString());
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
