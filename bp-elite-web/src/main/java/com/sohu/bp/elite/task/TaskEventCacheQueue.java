package com.sohu.bp.elite.task;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.util.SpringUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangzhihao
 *         2016/8/24
 */
public class TaskEventCacheQueue implements TaskEventQueue{

    private static Logger log = LoggerFactory.getLogger(TaskEventCacheQueue.class);

    private SsdbCache ssdbCache;

    private String queueKey = "FEED_EVENT_TASK_QUEUE";

    private static class TaskEventCacheQueueHolder{
        private static TaskEventCacheQueue instance = new TaskEventCacheQueue();
    }

    private TaskEventCacheQueue(){
        CacheManager cacheManager = SpringUtil.getBean("ssdbCacheManager", CacheManager.class);
        ssdbCache = (SsdbCache) cacheManager.getCache(SsdbConstants.CACHE_FEED_EVENT_TASK_QUEUE);
    }

    public static TaskEventCacheQueue getInstance(){
        return TaskEventCacheQueueHolder.instance;
    }

    @Override
    public void push(TaskEvent event) {
        String value = JSON.toJSONString(event);
        ssdbCache.qpush(queueKey, value);
        long size = ssdbCache.qsize(queueKey);
        log.info("push feed event into queue, event data=" + value + ", current ssdbCache size=" + size);
    }

    @Override
    public TaskEvent poll() {
        List<String> value = ssdbCache.qpop(queueKey, 1);
        if(value != null && value.size() > 0){
            log.info("poll feed event, event data=" + value.get(0));
            return JSON.parseObject(value.get(0), TaskEvent.class);
        }
        return null;
    }

    @Override
    public List<TaskEvent> poll(int size) {
        List<String> strings = ssdbCache.qpop(queueKey, size);
        if(strings != null && strings.size() > 0){
            List<TaskEvent> eventList = new ArrayList<>();
            for(String s : strings) {
                eventList.add(JSON.parseObject(s, TaskEvent.class));
            }
            return eventList;
        }
        return null;
    }

    @Override
    public long size() {
        return ssdbCache.qsize(queueKey);
    }
}
