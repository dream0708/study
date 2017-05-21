package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.achelous.model.Feed;
import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.action.bean.IndexDisplayBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.util.ConvertUtil;

import javax.annotation.Resource;

/**
 * @author zhangzhihao
 *         2016/8/22
 */
public class FeedServiceImpl implements FeedService {
    private Logger log = LoggerFactory.getLogger(FeedServiceImpl.class);

    private static final TimelineService timelineService = AchelousTimeline.getService();
    private static final EliteThriftServiceAdapter eliteServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();

    private CacheManager redisCacheManager;
    private String feedCacheTopic;

    private RedisCache personalInitFeedCache;

    public CacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public String getFeedCacheTopic() {
        return feedCacheTopic;
    }

    public void setFeedCacheTopic(String feedCacheTopic) {
        this.feedCacheTopic = feedCacheTopic;
    }

    public void init() {
        personalInitFeedCache = (RedisCache)redisCacheManager.getCache(CacheConstants.CACHE_PERSONAL_FEEDS);
    }

    @Override
    @Deprecated
    public void fillFeeds(long bpId, IndexDisplayBean bean, TEliteSourceType source) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        List<Feed> feeds = timelineService.forward(accountId, null, null);
        if (feeds != null && feeds.size() > 0) {
            bean.setFeedItemList(ConvertUtil.convertFeedList(feeds, bpId, source));
        } else {
            bean.setFeedItemList(new ArrayList<>());
        }
    }

    @Override
    public List<SimpleFeedItemBean> getForwardFeeds(long latestTime, long bpId, TEliteSourceType source) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        List<Feed> feeds = timelineService.forward(accountId, new Date(latestTime), null);
        if (feeds != null && feeds.size() > 0) {
            return ConvertUtil.convertFeedList(feeds, bpId, source);
        }
        return new ArrayList<>();
    }

    @Override
    public List<SimpleFeedItemBean> getBackwardFeeds(long oldTime, long bpId, TEliteSourceType source) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        Long start = System.currentTimeMillis();
        List<Feed> feeds = getCacheBackwardFeeds(accountId, oldTime, false);
        if (null == feeds || feeds.size() == 0) {
            feeds = timelineService.backward(accountId, new Date(oldTime - 1), null);
        } else {
            log.info("get feed from cache.accountId={}, oldTime={}", new String[]{String.valueOf(accountId), String.valueOf(bpId)});
        }
        Long end = System.currentTimeMillis();
        log.info("get backward time : " + (end - start));
        
        if (feeds != null && feeds.size() > 0) {
            return ConvertUtil.convertFeedList(feeds, bpId, source);
        }

        return new ArrayList<>();

    }

    @Override
    public void ignoreFeed(long accountId, long unitId) {
        timelineService.ignore(accountId, unitId);
        log.info("clear cache here");
        this.removeCacheBackwardFeeds(accountId);
    }

    @Override
    public int getNews(long bpId) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        int newsCount = timelineService.news(accountId);
        return newsCount;
    }

    @Override
    public void removeNews(long bpId) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long startTime = System.currentTimeMillis();
        if (timelineService.news(accountId) > 0) {
            timelineService.removeNews(accountId);
            log.info("clear cache here");
            this.removeCacheBackwardFeeds(accountId);
        }
        long endTime = System.currentTimeMillis();
        log.info("remove timeline news for bpId ={}, wastetime={} ", new String[]{String.valueOf(bpId), String.valueOf(endTime-startTime)});
    }

    @Override
    public void publishSimpleFeed(long bpId, long itemId, BpType bpType, ProduceActionType actionType) {
        Date nowDate = new Date();
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long unitId = TimeLineUtil.getComplexId(itemId, bpType.getValue());
        timelineService.produce(accountId, unitId, actionType.getValue(), nowDate);

    }

    @Override
    public void publishQuestion(long bpId, long questionId, String tagIds, ProduceActionType actionType) {
        Date nowDate = new Date();
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long unitId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
        timelineService.produce(accountId, unitId, actionType.getValue(), nowDate);

        if (StringUtils.isNotBlank(tagIds)) {
            for (String tagId : tagIds.split(Constants.TAG_IDS_SEPARATOR)) {
                accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
                timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), nowDate);
            }
        }
    }

    @Override
    public void publishAnswer(long bpId, long answerId, long questionId, ProduceActionType actionType) {
        Date nowDate = new Date();
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long unitId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
        timelineService.produce(accountId, unitId, actionType.getValue(), nowDate);

        accountId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
        timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), nowDate);
    }

    @Override
    public void publishAnswerWithTags(long bpId, long answerId, long questionId, String tagIds, ProduceActionType actionType) {
        Date nowDate = new Date();
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long unitId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
        timelineService.produce(accountId, unitId, actionType.getValue(), nowDate);

        accountId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
        timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), nowDate);

        if (StringUtils.isNotBlank(tagIds)) {
            for (String tagId : tagIds.split(Constants.TAG_IDS_SEPARATOR)) {
                accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
                timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), nowDate);
            }
        }

    }

    @Override
    public void updateQuestion(long bpId, long questionId, String tagIds) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long unitId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
        timelineService.produce(accountId, unitId, ProduceActionType.UPDATE.getValue(), new Date());

        if (StringUtils.isNotBlank(tagIds)) {
            for (String tagId : tagIds.split(Constants.TAG_IDS_SEPARATOR)) {
                accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
                timelineService.produce(accountId, unitId, ProduceActionType.UPDATE.getValue(), new Date());
            }
        }
    }

    @Override
    public void deleteQuestion(long questionId) {
        TEliteQuestion question = null;
        try {
            question = eliteServiceAdapter.getQuestionById(questionId);
        } catch (TException e) {
            log.error("", e);
        }
        if (question != null) {
            long bpId = question.getBpId();
            long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
            long unitId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
            timelineService.remove(accountId, unitId);
            log.info("clear cache here");
            this.removeCacheBackwardFeeds(accountId);

            if (StringUtils.isNotBlank(question.getTagIds())) {
                for (String tagId : question.getTagIds().split(Constants.TAG_IDS_SEPARATOR)) {
                    accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
                    timelineService.remove(accountId, unitId);
                }
            }
        }

    }

    @Override
    public void updateAnswer(long bpId, long answerId, long questionId) {
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        long unitId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
        timelineService.produce(accountId, unitId, ProduceActionType.UPDATE.getValue(), new Date());

        accountId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
        timelineService.produce(accountId, unitId, ProduceActionType.UPDATE.getValue(), new Date());

        try {
            TEliteQuestion question = eliteServiceAdapter.getQuestionById(questionId);
            if (question != null && StringUtils.isNotBlank(question.getTagIds())) {
                for (String tagId : question.getTagIds().split(Constants.TAG_IDS_SEPARATOR)) {
                    accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
                    timelineService.produce(accountId, unitId, ProduceActionType.UPDATE.getValue(), new Date());
                }
            }
        } catch (TException e) {
            log.error("", e);
        }
    }

    @Override
    public void deleteAnswer(long answerId) {
        TEliteAnswer answer = null;
        try {
            answer = eliteServiceAdapter.getAnswerById(answerId);
        } catch (TException e) {
            log.error("", e);
        }
        if (answer != null) {
            long bpId = answer.getBpId();
            long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
            long unitId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
            timelineService.remove(accountId, unitId);
            log.info("clear cache here");
            this.removeCacheBackwardFeeds(accountId);

            TEliteQuestion question = null;
            try {
                question = eliteServiceAdapter.getQuestionById(answer.getQuestionId());
            } catch (TException e) {
                log.error("", e);
            }
            if (question != null) {
                accountId = TimeLineUtil.getComplexId(question.getId(), BpType.Question.getValue());
                timelineService.remove(accountId, unitId);

                if (StringUtils.isNotBlank(question.getTagIds())) {
                    for (String tagId : question.getTagIds().split(Constants.TAG_IDS_SEPARATOR)) {
                        accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
                        timelineService.remove(accountId, unitId);
                    }
                }
            }
        }
    }

    @Override
    public List<Feed> getCacheBackwardFeeds(long accountId, long time, boolean reset) {
        if (0 >= accountId || 0 >= time) {
            return null;
        }
        if (time < (System.currentTimeMillis()-60000)) {
            return null;
        }
        List<Feed> feeds = null;
        if (reset) {
            personalInitFeedCache.remove(String.valueOf(accountId));
        }
        feeds = (List<Feed>)personalInitFeedCache.get(String.valueOf(accountId));
        if (null == feeds || feeds.size() == 0) {
            feeds = timelineService.backward(accountId, new Date(), null);
            if (null != feeds && feeds.size() > 0) {
                personalInitFeedCache.put(String.valueOf(accountId), feeds);
                log.info("build feed cache for accountId={} success.", new String[]{String.valueOf(accountId)});
            }
        }
        return feeds;
    }

    @Override
    public boolean checkAndBuildBackwardFeeds(long accountId) {
        if (!personalInitFeedCache.exist(String.valueOf(accountId))) {
            this.getCacheBackwardFeeds(accountId, System.currentTimeMillis(), false);
            return false;
        }
        return true;
    }

    @Override
    public void removeCacheBackwardFeeds(long accountId) {
        personalInitFeedCache.remove(String.valueOf(accountId));
        log.info("clear feed cache for accountId="+accountId);
    }

}
