package com.sohu.bp.elite.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteFeedAttitudeType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.service.EliteFeedService;
import com.sohu.bp.elite.util.SpringUtil;

//用于推送到timeline的任务类
public class EliteFeedTask implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(EliteFeedTask.class);
    private TimelineService timelineService;
    private ProduceActionType actionType;
    private EliteFeedAttitudeType attitudeType;
    private BpType feedType;
    private Long bpId;
    private Long objectId;
    private EliteFeedService feedService;
    
    public EliteFeedTask(BpType feedType, Long objectId, Long bpId, ProduceActionType actionType, EliteFeedAttitudeType attitudeType) {
        this.feedType = feedType;
        this.actionType = actionType;
        this.objectId = objectId;
        this.bpId = bpId;
        this.attitudeType = attitudeType;
        feedService = SpringUtil.getBean("eliteFeedService", EliteFeedService.class);
    }

    public ProduceActionType getActionType() {
        return actionType;
    }
    public void setActionType(ProduceActionType actionType) {
        this.actionType = actionType;
    }
    public BpType getFeedType() {
        return feedType;
    }
    public void setFeedType(BpType feedType) {
        this.feedType = feedType;
    }
    public Long getBpId() {
        return bpId;
    }
    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }
    public Long getObjectId() {
        return objectId;
    }
    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }
    public EliteFeedAttitudeType getAttitudeType() {
        return attitudeType;
    }
    public void setAttitudeType(EliteFeedAttitudeType attitudeType) {
        this.attitudeType = attitudeType;
    }

    @Override
    public void run() {
        Long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
        Long unitId = TimeLineUtil.getComplexId(objectId, feedType.getValue());
        
//        switch (attitudeType) {
//            case ADD :
//                timelineService.produce(accountId, unitId, actionType.getValue(), new Date());
//                break;
//            case REMOVE :
//                timelineService.reduce(accountId, unitId, actionType.getValue());
//                break;
//            default :
//                break;
//        }

        feedService.notify2Timeline(attitudeType, accountId, unitId, actionType);
        log.info("push action to timeline service : actionType = {}, attitude = {}, objectId = {}, feedType = {}, bpId = {}",
                new Object[]{actionType, attitudeType, objectId, feedType, bpId});
    }

}
