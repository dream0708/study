package com.sohu.bp.elite.service.web.impl;

import com.sohu.achelous.model.Feed;
import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.tag.LabelPlazaDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.action.bean.tag.TagRelatedItemDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.auth.AuthenticationCenter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.web.EliteCacheService;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliteCacheServiceImpl implements EliteCacheService {

    private static final Logger log = LoggerFactory.getLogger(EliteCacheServiceImpl.class);
    private static final String LOGIN_TOKEN = "TOKEN";

    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
    private BpDecorationServiceAdapter bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private TimelineService timelineService = AchelousTimeline.getService();

    private CacheManager cacheManager;
    private RedisCache totalAnswerNumCache;
    private RedisCache webDataCache;
    private RedisCache loginTokenCache;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void init() {
        totalAnswerNumCache = (RedisCache) cacheManager.getCache(CacheConstants.CACHE_TOTAL_ANSWER_NUM);
        webDataCache = (RedisCache) cacheManager.getCache(CacheConstants.CACHE_ELITE_WEB_DATA);
        loginTokenCache = (RedisCache) cacheManager.getCache(CacheConstants.CACHE_LOGIN_TOKEN);
    }

    @Override
    public int getTotalAnswerNum() {
        return getTotalAnswerNum(true);
    }

    @Override
    public void clearTotalAnswerNumCache() {
        totalAnswerNumCache.remove(Constants.TOTAL_ANSWER_CACHE_KEY);
    }

    @Override
    public Object getTagIndex(int tagId, Long viewerId) {
        log.info("get tag index feed from cache");
        return getTagIndex(tagId, viewerId, true);
    }

    @Override
    public Object getTagQuestions(int tagId, Long viewerId) {
        log.info("get tag questions from cache");
        return getTagQuestions(tagId, viewerId, true);
    }

    @Override
    public Object getTagAnswers(int tagId, Long viewerId) {
        log.info("get tag answers from cache");
        return getTagAnswers(tagId, viewerId, true);
    }

    private String getUserLikeNumMergedKey(long bpId) {
        return Constants.TOTAL_LIKE_NUM_OF_USER_CACHE_KEY + "_" + bpId;
    }

    private int getTotalAnswerNum(boolean first) {
        if (!first && !totalAnswerNumCache.exist(Constants.TOTAL_ANSWER_CACHE_KEY)) {
            return 0;
        }

        if (!totalAnswerNumCache.exist(Constants.TOTAL_ANSWER_CACHE_KEY)) {
            reloadTotalAnswerNum();
            return getTotalAnswerNum(false);
        }

        Object obj = totalAnswerNumCache.get(Constants.TOTAL_ANSWER_CACHE_KEY);
        if (obj != null) {
            return Integer.parseInt(obj.toString());
        }

        return 0;
    }

    private void reloadTotalAnswerNum() {
        log.info("initializing total answer cache data");
        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        condition.setFrom(0)
                .setCount(1)
                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
        int totalAnswerNum = 0;
        try {
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            totalAnswerNum = (int) listResult.getTotal();
        } catch (TException e) {
            log.error("", e);
        }
        totalAnswerNumCache.put(Constants.TOTAL_ANSWER_CACHE_KEY, totalAnswerNum);
        log.info("initialize succeeded, total answer num=" + totalAnswerNum);
    }

    private Object getTagIndex(int tagId, Long viewerId, boolean first) {
        String key = Constants.TAG_INDEX_CACHE_KEY + "_" + tagId;
        if (!first && !webDataCache.exist(key)) {
            return null;
        }

        if (!webDataCache.exist(key)) {
            reloadTagIndex(tagId, viewerId);
            return getTagIndex(tagId, viewerId, false);
        }

        return webDataCache.get(key);
    }

    private Object getTagQuestions(int tagId, Long viewerId, boolean first) {
        String key = Constants.TAG_QUESTIONS_CACHE_KEY + "_" + tagId;
        if (!first && !webDataCache.exist(key)) {
            return null;
        }

        if (!webDataCache.exist(key)) {
            reloadTagQuestions(tagId, viewerId);
            return getTagQuestions(tagId, viewerId, false);
        }

        return webDataCache.get(key);
    }

    private Object getTagAnswers(int tagId, Long viewerId, boolean first) {
        String key = Constants.TAG_ANSWERS_CACHE_KEY + "_" + tagId;
        if (!first && !webDataCache.exist(key)) {
            return null;
        }

        if (!webDataCache.exist(key)) {
            reloadTagAnswers(tagId, viewerId);
            return getTagAnswers(tagId, viewerId, false);
        }

        return webDataCache.get(key);
    }

    public void reloadTagIndex(int tagId, Long viewerId) {
        log.info("reloading tag index feed, tagId={}, bpId={}", tagId, viewerId);
        LabelPlazaDisplayBean bean = new LabelPlazaDisplayBean();
        Tag tag = null;
        try {
            tag = bpDecorationServiceAdapter.getTagById(tagId);
        } catch (TException e) {
            log.error("", e);
        }
        if (null == tag || tag.getId() <= 0)
            return;

        TagItemBean tagItemBean = ConvertUtil.convertToTagItemBean(tag, viewerId, true);

        long latestTime = bean.getLatestTime();
        long oldestTime = bean.getOldestTime();
        if (latestTime <= 0)
            latestTime = System.currentTimeMillis();
        if (oldestTime <= 0)
            oldestTime = System.currentTimeMillis();

        long accountId = TimeLineUtil.getComplexId(tagId, BpType.Tag.getValue());
        List<Feed> feeds = timelineService.queue(accountId, new Date(oldestTime-1), null);
        List<SimpleFeedItemBean> feedItemList = ConvertUtil.convertFeedList(feeds, viewerId, null);
        long[] times = getLatestAndOldTime(feedItemList, latestTime, oldestTime);
        bean.setTag(tagItemBean);
        bean.setFeedItemList(feedItemList);
        bean.setLatestTime(times[0]);
        bean.setOldestTime(times[1]);

        log.info("reload index feed size=" + feedItemList.size());
        String key = Constants.TAG_INDEX_CACHE_KEY + "_" + tagId;
        webDataCache.put(key, bean);
    }


    public void reloadTagQuestions(int tagId, Long viewerId) {
        log.info("reload tag questions, tagId={}, bpId={}", tagId, viewerId);
        TagRelatedItemDisplayBean bean = new TagRelatedItemDisplayBean();

        TSearchQuestionCondition condition = new TSearchQuestionCondition();
        condition.setFrom((bean.getCurrPageNo() - 1) * bean.getPageSize())
                .setCount(bean.getPageSize())
                .setTagIds(String.valueOf(tagId))
                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

        TQuestionListResult listResult = null;
        try {
            listResult = eliteAdapter.searchQuestion(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if (listResult == null) {
            listResult = new TQuestionListResult(new ArrayList<>(), 0);
        }

        bean.setTotal(listResult.getTotal());
        bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
        bean.setTag(ConvertUtil.convertToTagItemBean(tagId, viewerId, true));

        log.info("tag questions size={}, total={}", bean.getFeedItemList().size(), listResult.getTotal());
        String key = Constants.TAG_QUESTIONS_CACHE_KEY + "_" + tagId;
        webDataCache.put(key, bean);
    }

    public void reloadTagAnswers(int tagId, Long viewerId) {
        log.info("reload tag answers, tagId={}, bpId={}", tagId, viewerId);
        TagRelatedItemDisplayBean bean = new TagRelatedItemDisplayBean();

        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        condition.setFrom((bean.getCurrPageNo() - 1) * bean.getPageSize())
                .setCount(bean.getPageSize())
                .setTagIds(String.valueOf(tagId))
                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

        TAnswerListResult listResult = null;

        try {
            listResult = eliteAdapter.searchAnswer(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if (listResult == null) {
            listResult = new TAnswerListResult(new ArrayList<>(), 0);
        }

        bean.setTotal(listResult.getTotal());
        bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
        bean.setTag(ConvertUtil.convertToTagItemBean(tagId, viewerId, true));

        log.info("tag answers size={}, total={}", bean.getFeedItemList().size(), listResult.getTotal());
        String key = Constants.TAG_ANSWERS_CACHE_KEY + "_" + tagId;
        webDataCache.put(key, bean);
    }

    @Override
    public void reloadTagFeeds(String tagIds, Long viewerId) {
        if(StringUtils.isNotBlank(tagIds)) {
            for(String tagId : tagIds.split(Constants.TAG_IDS_SEPARATOR)) {
                int id = Integer.parseInt(tagId);
                reloadTagIndex(id, viewerId);
                reloadTagQuestions(id, viewerId);
                reloadTagAnswers(id, viewerId);
            }
        }
    }

    @Override
    public String getLoginToken() {
        return getLoginToken(false);
    }

    @Override
    public String getLoginToken(boolean update) {
        String token = (String) loginTokenCache.get(LOGIN_TOKEN);
        if (StringUtils.isBlank(token)) {
           update = true;
        }
        if (update) {
            token = AuthenticationCenter.getRandomString(32);
            log.info("[LOGIN_TOKEN] update login token="+token);
        }
        loginTokenCache.put(LOGIN_TOKEN, token);
        return token;
    }

    private long[] getLatestAndOldTime(List<SimpleFeedItemBean> feedList, long latestTime, long oldTime) {
        long[] times = new long[2];
        for (SimpleFeedItemBean feedItemBean : feedList) {
            if (feedItemBean.getProduceTime() > latestTime)
                latestTime = feedItemBean.getProduceTime();
            if (feedItemBean.getProduceTime() < oldTime)
                oldTime = feedItemBean.getProduceTime();
        }
        times[0] = latestTime;
        times[1] = oldTime;
        return times;
    }
}
