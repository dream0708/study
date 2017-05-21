package com.sohu.bp.elite.api.task;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.util.SpringUtil;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * 抓取插入广场的任务队列
 * @author zhijungou
 * 2016年11月7日
 */
public class EliteCrawlSquareCacheQueue implements TaskQueue{
	
	private static final Logger log = LoggerFactory.getLogger(EliteCrawlSquareCacheQueue.class);
	
//	private static EliteCrawlSquareCacheQueue instance = new EliteCrawlSquareCacheQueue();
	private RedisCache redisCache;
	private EliteCrawlSquareCacheQueue(){
//		CacheManager ssdbCacheManager = (CacheManager) SpringUtil.getBean("ssdbCacheManager");
//		ssdbCache = (SsdbCache) ssdbCacheManager.getCache(CacheConstants.CACHE_CRAWL_SQUARE);
		CacheManager redisCacheManager = (CacheManager) SpringUtil.getBean("redisCacheManager", CacheManager.class);
		if(null == redisCacheManager) log.info("get redisCacheManager failed ");
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_CRAWL_SQUARE);
	}
	
    private static class EliteCrawlSquareCacheQueueHolder{
        private static EliteCrawlSquareCacheQueue instance = new EliteCrawlSquareCacheQueue();
    }
	public static EliteCrawlSquareCacheQueue getInstance(){
		return EliteCrawlSquareCacheQueueHolder.instance;
	}

	@Override
	public void push(EliteCrawlSquareAsycTask task) {
		if(null != task){
		redisCache.zAdd(CacheConstants.CRAWL_SQUARE_QUEUE, task.getUpdateTime(), JSONObject.fromObject(task).toString());
		log.info("insert task into crawl square queue : {}, time : {}",new Object[]{JSONObject.fromObject(task).toString(), new Date(task.getUpdateTime())});
		}
	}

	@Override
	public EliteCrawlSquareAsycTask poll() {
		Set<String> value = redisCache.zRange(CacheConstants.CRAWL_SQUARE_QUEUE, 0, 0);
		if(null != value && value.size() > 0){
			JSONObject taskJSON = JSONObject.fromObject(value.iterator().next());
			EliteCrawlSquareAsycTask task = (EliteCrawlSquareAsycTask)JSONObject.toBean(taskJSON, EliteCrawlSquareAsycTask.class);
			log.info("poll task from square queue : " + taskJSON.toString() + new Date(task.getUpdateTime()));
			return task;
		}
		return null;
	}

	@Override
	public void delete(EliteCrawlSquareAsycTask task) {
		String value = JSONObject.fromObject(task).toString();
		log.info("delete task form square queue : " + value);
		redisCache.zRem(CacheConstants.CRAWL_SQUARE_QUEUE, value);
	}

	@Override
	public long size() {
		return redisCache.zCount(CacheConstants.CRAWL_SQUARE_QUEUE);
	}

	@Override
	public boolean getMutexLock() {
		boolean result = redisCache.setnx(CacheConstants.CRWAL_QUEUE_MUTEX, Constants.CACHE_TTL, Constants.CRAWL_MUTEX_LOCK_FLAG);
		log.info("get crawl queue mutex lock result = " + result);
		return result;
	}

	@Override
	public void releaseMutexLock() {
		redisCache.remove(CacheConstants.CRWAL_QUEUE_MUTEX);
		log.info("release crawl queue mutex lock!");
	}
	
	
}
