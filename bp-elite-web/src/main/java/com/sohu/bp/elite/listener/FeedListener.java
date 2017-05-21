package com.sohu.bp.elite.listener;

import com.sohu.bp.elite.service.web.FeedService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Created by nicholastang on 2017/5/5.
 */
public class FeedListener extends KafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(FeedListener.class);
    private FeedService feedService;

    public FeedService getFeedService() {
        return feedService;
    }

    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }

    public void init() {
        this.setTaskHandler(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String msg) {
                try {
                    if (StringUtils.isBlank(msg))
                        return true;
                    logger.info("feed listener read msg=" + msg);
                    JSONObject msgJSON = JSONObject.fromObject(msg);
                    if (null != msgJSON && msgJSON.containsKey("accountId")) {
                        Long accountId = msgJSON.getLong("accountId");
                        if (null != accountId) {
                            logger.info("clear cache here");
                            feedService.removeCacheBackwardFeeds(accountId);
                        }
                    }
                } catch (Exception e) {
                    logger.info("", e);
                }
                return true;
            }
        });

        // 开始运行监听
        this.myRun();
    }
}
