package com.sohu.bp.elite.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.focus.rec.collector.RecLogger;
import cn.focus.rec.log.BehaviorLog;
import cn.focus.rec.log.ItemLog;

/**
 * 用于推荐系统
 * @author zhijungou
 * 2017年4月18日
 */
public class EliteRecAsyncTask extends EliteAsyncTask{
    private Logger log = LoggerFactory.getLogger(EliteRecAsyncTask.class);
    private BehaviorLog behaviorLog;
    private ItemLog itemLog;
    
    public EliteRecAsyncTask(BehaviorLog behaviorLog) {
        this.behaviorLog = behaviorLog;
    }
    public EliteRecAsyncTask(ItemLog itemLog) {
        this.itemLog = itemLog;
    }
    
    @Override
    public void run() {
        if (null != itemLog) {
            RecLogger.log(itemLog);
            log.info("start rec task : itemLog = {}", new Object[]{itemLog});
        }
        if (null != behaviorLog) {
            RecLogger.log(behaviorLog);
            log.info("start rec task : behaviorLog = {}", new Object[]{behaviorLog});
        }
    }
}
