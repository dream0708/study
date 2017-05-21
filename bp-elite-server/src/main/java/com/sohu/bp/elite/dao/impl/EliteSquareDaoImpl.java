package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.dao.EliteAnswerDao;
import com.sohu.bp.elite.dao.EliteQuestionDao;
import com.sohu.bp.elite.dao.EliteSquareDao;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.util.SquareUtil;


/**
 * 广场Dao层
 * 逻辑：
 * 1. 分为普通广场，长度为2000，优选广场，长度为1000.
 * 2. 普通广场的内容会根据一定逻辑自动进入优选广场，每个人和每个问题的回答在10条内不会出现多次。
 * 3. 广场满的话，截断处理， 取一半。
 * @author zhijungou
 * 2017年4月27日
 */
public class EliteSquareDaoImpl implements EliteSquareDao {
    private static final Logger log = LoggerFactory.getLogger(EliteSquareDaoImpl.class);
    
    private CacheManager redisCacheManager;
    private EliteQuestionDao questionDao;
    private EliteAnswerDao answerDao;
    
    private RedisCache redisCache;
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void setQuestionDao(EliteQuestionDao questionDao) {
        this.questionDao = questionDao;
    }
    
    public void setAnswerDao(EliteAnswerDao answerDao) {
        this.answerDao = answerDao;
    }
    
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_ELITE_SQUARE);
    }
    
    // 普通广场逻辑
    
    @Override
    public int getNewSquareNum(Long latestTime) {
        if (null == latestTime || latestTime <= 0) return 0;
        Set<String> set = redisCache.zRangeByScore(CacheConstants.SQUARE_SET_KEY, latestTime.toString(), String.valueOf(new Date().getTime()));
        return set.size();
    }

    @Override
    public List<Long> getNewSquareList(Long latestTime) {
        List<Long> complexIds = new ArrayList<Long>();
        if (null == latestTime || latestTime <= 0) return complexIds;
        int num = getNewSquareNum(latestTime);
        Set<String> set = null;
        if (num >= Constants.SQUARE_MAX_NEW_ELITE_NUM) {
            set = redisCache.zRevRange(CacheConstants.SQUARE_SET_KEY, 0, Constants.SQUARE_MAX_NEW_ELITE_NUM - 1);
        } else {
            set = redisCache.zRevRangeByScore(CacheConstants.SQUARE_SET_KEY, latestTime.toString(), String.valueOf(new Date().getTime()));
        }
        complexIds = set.stream().map(Long::valueOf).collect(Collectors.toList());
        return complexIds;
    }

    @Override
    public List<Long> getBackward(Long feedId, FeedType feedType, int count) {
        List<Long> complexIds = new ArrayList<Long>();
        long oldestTime = 0;
        Set<String> set = null;
        if (null == feedId || feedId <= 0) {
            set = redisCache.zRevRange(CacheConstants.SQUARE_SET_KEY, 0, count - 1);
            return set.stream().map(Long::valueOf).collect(Collectors.toList());
        }
        Long complexId = SquareUtil.getComplexId(feedId, feedType);
        long rank = redisCache.ZRevRank(CacheConstants.SQUARE_SET_KEY, complexId.toString());
        if (rank >= 0) {
            set = redisCache.zRevRange(CacheConstants.SQUARE_SET_KEY, rank + 1, rank + count);
            complexIds = set.stream().map(Long::valueOf).collect(Collectors.toList());
        } else {
            // 针对被删除广场的情况
            switch (feedType) {
            case QUESTION:
                EliteQuestion question = questionDao.get(feedId);
                oldestTime = question.getUpdateTime().getTime();
                break;
            case ANSWER:
                EliteAnswer answer = answerDao.getById(feedId);
                oldestTime = answer.getUpdateTime().getTime();
                break;
            default:
                break;
            }
            set = redisCache.zRevRangeByScore(CacheConstants.SQUARE_SET_KEY, String.valueOf(0), String.valueOf(oldestTime - 1));
            Iterator<String> iter = set.iterator();
            for (int i = 0; i < count; i++) {
                if (!iter.hasNext()) break;
                complexIds.add(Long.valueOf(iter.next()));
            }
        }
        return complexIds;
    }
    
    @Override
    public List<Long> getBackward(int start, int count) {
        Set<String> set = redisCache.zRevRange(CacheConstants.SQUARE_SET_KEY, start, start + count - 1);
        return set.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    @Override
    public boolean insertSquare(Long complexId, Long time) {
        boolean result = redisCache.zAdd(CacheConstants.SQUARE_SET_KEY, time, String.valueOf(complexId));
        log.info("insert into square, complexId = {}, time = {}, result = {}", new Object[]{complexId, new Date(time), result});
        resizeSquare();
        return result;
    }
    
    @Override
    public void flushSquare() {
        log.warn("flush square content!");
        redisCache.remove(CacheConstants.SQUARE_SET_KEY);
        flushSelectedSquare();
    }

    @Override
    public boolean removeSquareItem(Long complexId) {
        log.info("remove complexId = {} from square.", new Object[]{complexId});
        boolean result = redisCache.zRem(CacheConstants.SQUARE_SET_KEY, String.valueOf(complexId));
        if (result) {
            removeSelectedSquareItem(complexId);
        }
        return result;
    }
    
    private void resizeSquare() {
        if (redisCache.zCount(CacheConstants.SQUARE_SET_KEY) > Constants.SQUARE_SET_SIZE) {
            log.info("square size is bigger than {}, start to resize", new Object[]{Constants.SQUARE_SET_SIZE});
            int mid = Constants.SQUARE_SET_SIZE / 2;
            Set<String> ids = redisCache.zRange(CacheConstants.SQUARE_SET_KEY, mid, mid);
            long score = redisCache.zScore(CacheConstants.SQUARE_SET_KEY, ids.iterator().next());
            redisCache.zRemRangeByScore(CacheConstants.SQUARE_SET_KEY, String.valueOf(0), String.valueOf(score));
        }
    }

    // 精选广场逻辑
    @Override
    public int getNewSelectedSquareNum(Long latestTime) {
        if (null == latestTime || latestTime <= 0) return 0;
        Set<String> set = redisCache.zRangeByScore(CacheConstants.SQUARE_SELECTED_SET_KEY, latestTime.toString(), String.valueOf(new Date().getTime()));
        return set.size();
    }

    @Override
    public List<Long> getNewSelectedSquareList(Long latestTime) {
        List<Long> complexIds = new ArrayList<Long>();
        if (null == latestTime || latestTime <= 0) return complexIds;
        int num = getNewSelectedSquareNum(latestTime);
        Set<String> set = null;
        if (num >= Constants.SQUARE_MAX_NEW_ELITE_NUM) {
            set = redisCache.zRevRange(CacheConstants.SQUARE_SELECTED_SET_KEY, 0, Constants.SQUARE_MAX_NEW_ELITE_NUM - 1);
        } else {
            set = redisCache.zRevRangeByScore(CacheConstants.SQUARE_SELECTED_SET_KEY, latestTime.toString(), String.valueOf(new Date().getTime()));
        }
        complexIds = set.stream().map(Long::valueOf).collect(Collectors.toList());
        return complexIds;
    }

    @Override
    public List<Long> getSelectedSquareBackward(Long feedId, FeedType feedType, int count) {
        List<Long> complexIds = new ArrayList<Long>();
        Set<String> set = null;
        long oldestTime = 0;
        if (null == feedId || feedId <= 0) {
            set = redisCache.zRevRange(CacheConstants.SQUARE_SELECTED_SET_KEY, 0, count - 1);
            return set.stream().map(Long::valueOf).collect(Collectors.toList());
        }
        Long complexId = SquareUtil.getComplexId(feedId, feedType);
        long rank = redisCache.ZRevRank(CacheConstants.SQUARE_SELECTED_SET_KEY, complexId.toString());
        if (rank >= 0) {
            set = redisCache.zRevRange(CacheConstants.SQUARE_SELECTED_SET_KEY, rank + 1, rank + count);
            complexIds = set.stream().map(Long::valueOf).collect(Collectors.toList());
        } else {
            // 针对被删除广场的情况
            switch (feedType) {
            case QUESTION:
                EliteQuestion question = questionDao.get(feedId);
                oldestTime = question.getUpdateTime().getTime();
                break;
            case ANSWER:
                EliteAnswer answer = answerDao.getById(feedId);
                oldestTime = answer.getUpdateTime().getTime();
                break;
            default:
                break;
            }
            set = redisCache.zRevRangeByScore(CacheConstants.SQUARE_SELECTED_SET_KEY, String.valueOf(0), String.valueOf(oldestTime - 1));
            Iterator<String> iter = set.iterator();
            for (int i = 0; i < count; i++) {
                if (!iter.hasNext()) break;
                complexIds.add(Long.valueOf(iter.next()));
            }
        }
        return complexIds;
    }
    
    @Override
    public List<Long> getSelectedSquareBackward(long oldestTime, int count) {
        Set<String> ids = redisCache.zRevRangeByScore(CacheConstants.SQUARE_SELECTED_SET_KEY, String.valueOf(0), String.valueOf(oldestTime));
        return ids.stream().limit(count).map(Long::valueOf).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getSelectedSquareForward(long latestTime, int count) {
        Set<String> ids = redisCache.zRevRangeByScore(CacheConstants.SQUARE_SELECTED_SET_KEY, String.valueOf(latestTime), String.valueOf(new Date().getTime()));
        return ids.stream().limit(count).map(Long::valueOf).collect(Collectors.toList());
    }

    @Override
    public boolean insertSelectedSquare(Long complexId, Long time) {
        boolean result = redisCache.zAdd(CacheConstants.SQUARE_SELECTED_SET_KEY, time, String.valueOf(complexId));
        log.info("insert into selected square, complexId = {}, time = {}, result = {}", new Object[]{complexId, new Date(time), result});
        resizeSelectedSquare();
        return result;
    }

    @Override
    public void flushSelectedSquare() {
        log.warn("flush selected square content!");
        redisCache.remove(CacheConstants.SQUARE_SELECTED_SET_KEY);
    }

    @Override
    public boolean removeSelectedSquareItem(Long complexId) {
        log.info("remove complexId = {} from selected square.", new Object[]{complexId});
        return redisCache.zRem(CacheConstants.SQUARE_SELECTED_SET_KEY, String.valueOf(complexId));
    }
    
    private void resizeSelectedSquare() {
        if (redisCache.zCount(CacheConstants.SQUARE_SELECTED_SET_KEY) > Constants.SELECTED_SQUARE_SET_SZIE) {
            log.info("selected square size is bigger than {}, start to resize", new Object[]{Constants.SELECTED_SQUARE_SET_SZIE});
            int mid = Constants.SELECTED_SQUARE_SET_SZIE / 2;
            Set<String> ids = redisCache.zRange(CacheConstants.SQUARE_SELECTED_SET_KEY, mid, mid);
            long score = redisCache.zScore(CacheConstants.SQUARE_SELECTED_SET_KEY, ids.iterator().next());
            redisCache.zRemRangeByScore(CacheConstants.SQUARE_SELECTED_SET_KEY, String.valueOf(0), String.valueOf(score));
        }
    }

    @Override
    public boolean isSelectedValid(Long questionId, Long bpId, long time) {
        if (null == questionId || questionId <= 0 || null == bpId || bpId <= 0) return false;
        if (!redisCache.zExist(CacheConstants.SQUARE_RECENT_BPID_SET_KEY, bpId.toString()) && !redisCache.zExist(CacheConstants.SQUARE_RECENT_QUESTION_SET_KEY, questionId.toString())) {
            redisCache.zAdd(CacheConstants.SQUARE_RECENT_BPID_SET_KEY, time, bpId.toString());
            redisCache.zAdd(CacheConstants.SQUARE_RECENT_QUESTION_SET_KEY, time, questionId.toString());
            resizeCache(CacheConstants.SQUARE_RECENT_BPID_SET_KEY, Constants.SQUARE_RECENT_SET_SIZE);
            resizeCache(CacheConstants.SQUARE_RECENT_QUESTION_SET_KEY, Constants.SQUARE_RECENT_SET_SIZE);
            return true;
        }
        return false;
    }
    
    private void resizeCache(String cacheKey, int size) {
        if (redisCache.zCount(cacheKey) > size) {
            Set<String> set = redisCache.zRevRange(cacheKey, size, size);
            long score = redisCache.zScore(cacheKey, set.iterator().next());
            redisCache.zRemRangeByScore(cacheKey, String.valueOf(0), String.valueOf(score));
            log.info("resize cacheKey = {} to size = {} ", new Object[]{cacheKey, size});
        }
    }

    @Override
    public boolean isInSelectedSquare(Long complexId) {
        return redisCache.zExist(CacheConstants.SQUARE_SELECTED_SET_KEY, String.valueOf(complexId));
    }

    @Override
    public List<Long> getSelectedSquareBackward(int start, int count) {
        Set<String> set = redisCache.zRevRange(CacheConstants.SQUARE_SELECTED_SET_KEY, start, start + count - 1);
        return set.stream().map(Long::valueOf).collect(Collectors.toList());
    }

}
