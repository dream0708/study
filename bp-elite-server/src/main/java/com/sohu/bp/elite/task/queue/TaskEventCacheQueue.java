package com.sohu.bp.elite.task.queue;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.task.TaskEvent;
import com.sohu.bp.elite.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2016年6月18日 上午11:31:05
 * @auther dexingyang
 */
public class TaskEventCacheQueue implements TaskEventQueue {
	
	private static final Logger log = LoggerFactory.getLogger(TaskEventCacheQueue.class);

	private SsdbCache taskCacheQueue;
	private SsdbCache taskCacheBatchQueue;
	private String queuekey = "Elite_Task_Queue_Key";
	private String batchQueuekey = "Elite_Task_Batch_Queue_Key";

	private static class TaskEventCacheQueueHolder{
		private static TaskEventCacheQueue instance = new TaskEventCacheQueue();
	}
	
	private TaskEventCacheQueue(){
		taskCacheQueue = (SsdbCache) SpringUtil.getBean("ssdbCacheManager", CacheManager.class).getCache(SsdbConstants.ELITE_DEC_TASK_QUEUE);
		taskCacheBatchQueue = (SsdbCache) SpringUtil.getBean("ssdbCacheManager", CacheManager.class).getCache(SsdbConstants.ELITE_DEC_TASK_BATCH_QUEUE);
	}
	
	public static TaskEventCacheQueue getInstance(){
		return TaskEventCacheQueueHolder.instance;
	}

	@Override
	public boolean push(boolean isBatch, TaskEvent event){
		String queueName = (isBatch ? batchQueuekey : queuekey);
		try{
			String value = JSON.toJSONString(event);
			taskCacheQueue.qpush(queueName, value);
			log.info("Push "+queueName+" TaskEvent:"+value);
		}catch(Exception e){
			log.error("", e);
			return false;
		}
		return true;
	}

	@Override
	public TaskEvent poll(boolean isBatch){
		TaskEvent event = null;
		String queueName = (isBatch ? batchQueuekey : queuekey);
		try{
			List<String> list = taskCacheQueue.qpop(queueName, 1);
			if(list != null && list.size() > 0){
				event = JSON.parseObject(list.get(0), TaskEvent.class);
			}
		}catch(Exception e){
			log.error("", e);
		}
		return event;
	}

	@Override
	public List<TaskEvent> poll(boolean isBatch, int size){
		List<TaskEvent> eventList = new ArrayList<>(size);
		String queueName = (isBatch ? batchQueuekey : queuekey);
		TaskEvent event;
		try{
			List<String> list = taskCacheQueue.qpop(queueName, size);
			if(list != null && list.size() > 0){
				for(String item:list){
					event = JSON.parseObject(item, TaskEvent.class);
					eventList.add(event);
				}
			}
		}catch(Exception e){
			log.error("", e);
		}
		return eventList;
	}

	@Override
	public long size(boolean isBatch){
		if(isBatch)
			return taskCacheQueue.qsize(queuekey);
		else
			return taskCacheBatchQueue.qsize(batchQueuekey);
	}
}
