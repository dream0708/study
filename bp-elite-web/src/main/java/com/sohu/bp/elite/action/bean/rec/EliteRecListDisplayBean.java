package com.sohu.bp.elite.action.bean.rec;

import com.sohu.bp.elite.action.bean.FeedListDisplayBean;
import com.sohu.bp.elite.constants.Constants;

/**
 * 用于推荐的流
 * @author zhijungou
 * 2017年4月25日
 */
public class EliteRecListDisplayBean extends FeedListDisplayBean{
    private Long feedId;
    private Integer feedType;
    private Long latestTime;
    private Long oldestTime;
    private boolean hasMore = false;
    
    public Long getLatestTime() {
        return latestTime;
    }

    public EliteRecListDisplayBean setLatestTime(Long latestTime) {
        this.latestTime = latestTime;
        return this;
    }

    public Long getOldestTime() {
        return oldestTime;
    }

    public EliteRecListDisplayBean setOldestTime(Long oldestTime) {
        this.oldestTime = oldestTime;
        return this;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public EliteRecListDisplayBean setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        return this;
    }

    public Long getFeedId() {
        return feedId;
    }

    public EliteRecListDisplayBean setFeedId(Long feedId) {
        this.feedId = feedId;
        return this;
    }

    public Integer getFeedType() {
        return feedType;
    }

    public EliteRecListDisplayBean setFeedType(Integer feedType) {
        this.feedType = feedType;
        return this;
    }
    
    
}
