package com.sohu.bp.elite.task;

import com.sohu.bp.elite.enums.EliteSquareOperationType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.elite.util.SpringUtil;

public class EliteSquareAsyncTask extends EliteAsyncTask{
    private Long id;
    private FeedType feedType;
    private EliteSquareOperationType operationType;
    
    private SquareService squareService;
    
    public Long getId() {
        return id;
    }
    public EliteSquareAsyncTask setId(Long id) {
        this.id = id;
        return this;
    }
    public FeedType getFeedType() {
        return feedType;
    }
    public EliteSquareAsyncTask setFeedType(FeedType feedType) {
        this.feedType = feedType;
        return this;
    }
    public EliteSquareOperationType getOperationType() {
        return operationType;
    }
    public EliteSquareAsyncTask setOperationType(EliteSquareOperationType operationType) {
        this.operationType = operationType;
        return this;
    }    
    
    public EliteSquareAsyncTask(Long id, FeedType feedType, EliteSquareOperationType operationType) {
        this.id = id;
        this.feedType = feedType;
        this.operationType = operationType;
        this.squareService = SpringUtil.getBean("squareService", SquareService.class);
    }
    
    @Override
    public void run() {
        if (null != id && id > 0 && null != feedType) {
            if (null == squareService) {
                squareService = SpringUtil.getBean("squareService", SquareService.class);
            }
            switch (operationType) {
            case INSERT:
                squareService.insertSquare(id, feedType);
                break;
            case REMOVE:
                squareService.reduceObject(id, feedType);
                break;
            }
        }
    }
}
