package com.sohu.bp.elite.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.bp.elite.enums.EliteFeedAttitudeType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.service.EliteFeedService;

public class EliteFeedServiceImpl implements EliteFeedService{
    private static final Logger log = LoggerFactory.getLogger(EliteFeedService.class);
    private static final TimelineService timelineService = AchelousTimeline.getService();
    
    @Override
    public void notify2Timeline(EliteFeedAttitudeType attitudeType, Long accountId, Long unitId,
            ProduceActionType actionType) {
        switch (attitudeType) {
        case ADD :
            timelineService.produce(accountId, unitId, actionType.getValue(), new Date());
            break;
        case REMOVE :
            timelineService.reduce(accountId, unitId, actionType.getValue());
            break;
        default :
            break;
        }
    }
    
    
}
