package com.sohu.bp.elite.service.web;

import java.util.List;

import com.sohu.achelous.model.Feed;
import com.sohu.bp.elite.action.bean.IndexDisplayBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.model.TEliteSourceType;

/**
 * @author zhangzhihao
 *         2016/8/22
 */
public interface FeedService {

    void fillFeeds(long bpId, IndexDisplayBean bean, TEliteSourceType source);

    List<SimpleFeedItemBean> getForwardFeeds(long latestTime, long bpId, TEliteSourceType source);

    List<SimpleFeedItemBean> getBackwardFeeds(long oldTime, long bpId, TEliteSourceType source);

    void ignoreFeed(long accountId, long unitId);
    
    int getNews(long bpId);
    
    void removeNews(long bpId);
    
    void publishSimpleFeed(long bpId, long itemId, BpType bpType, ProduceActionType actionType);
    
    void publishQuestion(long bpId, long questionId, String tagIds, ProduceActionType actionType);
    
    void publishAnswer(long bpId, long answerId, long questionId, ProduceActionType actionType);
    
    void publishAnswerWithTags(long bpId, long answerId, long questionId, String tagIds, ProduceActionType actionType);
    
    void updateQuestion(long bpId, long questionId, String tagIds);

    void deleteQuestion(long questionId);
    
    void updateAnswer(long bpId, long answerId, long questionId);

    void deleteAnswer(long answerId);

    List<Feed> getCacheBackwardFeeds(long accountId, long time, boolean reset);

    boolean checkAndBuildBackwardFeeds(long accountId);

    void removeCacheBackwardFeeds(long accountId);
}
