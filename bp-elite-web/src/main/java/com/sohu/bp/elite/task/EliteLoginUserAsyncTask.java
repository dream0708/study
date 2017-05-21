package com.sohu.bp.elite.task;

import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAsyncTaskOper;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.service.web.WasherService;
import com.sohu.bp.elite.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicholastang on 2017/5/8.
 */
public class EliteLoginUserAsyncTask extends EliteAsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(EliteLoginUserAsyncTask.class);
    private static FeedService feedService;

    private Long bpId;

    public EliteLoginUserAsyncTask(Long bpId) {
        this.bpId = bpId;
    }
    @Override
    public void run() {
        if (null == bpId || bpId.longValue() <= 0) {
            return;
        }
        //建立feed
        if (null == feedService) {
            feedService = (FeedService)SpringUtil.getBean("feedService");
        }
        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        if(!feedService.checkAndBuildBackwardFeeds(accountId)) {
            logger.info("building feeds for accountId="+accountId);
        }
    }
}
