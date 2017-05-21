package com.sohu.bp.elite.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangzhihao
 *         2016/8/24
 */
public class FeedAsyncTaskPool {

    private static final Logger log = LoggerFactory.getLogger(FeedAsyncTaskPool.class);

    private static final int BATCH_SIZE = 20;
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 0;

    private ExecutorService executor;
    private TaskEventQueue eventQueue;

    public void init() {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
        eventQueue = TaskEventCacheQueue.getInstance();
        log.info("init FeedAsyncTaskPool end, start to execute work");
        this.executeWork();
    }

    public void executeWork() {
        log.info("start to poll feed event");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<TaskEvent> events = eventQueue.poll(BATCH_SIZE);
                        if (events != null) {
                            log.info("poll feed events size=" + events.size());
                            executor.execute(new TaskEventDeal(events));
                        }
                    } catch (Exception e) {
                        log.error("deal feed event error," + e);
                    } finally {
                    	try {
							Random random = new Random();
							int randomValue = random.nextInt(20);
							int value = randomValue*100+1000;
							Thread.sleep(value);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                }
            }
        }).start();
    }
}
