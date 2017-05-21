package com.sohu.bp.elite.task;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.elite.service.web.EliteCacheService;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.util.SpringUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangzhihao
 *         2016/8/24
 */
public class TaskEventDeal implements Runnable{

    private static Logger log = LoggerFactory.getLogger(TaskEventDeal.class);

    private List<TaskEvent> events;
    private FeedService feedService;
    private EliteCacheService eliteCacheService;

    public TaskEventDeal(List<TaskEvent> event){
        this.events = event;
        feedService = SpringUtil.getBean("feedService", FeedService.class);
        eliteCacheService = SpringUtil.getBean("eliteCacheService", EliteCacheService.class);
    }

    @Override
    public void run() {
        for(TaskEvent event : events) {
            log.info("start to deal event <{}>", JSON.toJSONString(event));
            switch (event.getType()) {
                case SIMPLE_FEED :
                    long itemId = event.getQuestionId() == 0 ? event.getAnswerId() : event.getQuestionId();
                    feedService.publishSimpleFeed(event.getBpId(), itemId, event.getBpType(), event.getActionType());
                    break;
                case PUBLISH_QUESTION :
                    feedService.publishQuestion(event.getBpId(), event.getQuestionId(), event.getTagIds(), event.getActionType());
                    break;
                case PUBLISH_ANSWER :
                    feedService.publishAnswer(event.getBpId(), event.getAnswerId(), event.getQuestionId(), event.getActionType());
                    break;
                case PUBLISH_ANSWER_WITH_TAGS :
                    feedService.publishAnswerWithTags(event.getBpId(), event.getAnswerId(), event.getQuestionId(), event.getTagIds(), event.getActionType());
                    break;
                case UPDATE_QUESTION :
                    feedService.updateQuestion(event.getBpId(), event.getQuestionId(), event.getTagIds());
                    break;
                case DELETE_QUESTION :
                    feedService.deleteQuestion(event.getQuestionId());
                    break;
                case UPDATE_ANSWER :
                    feedService.updateAnswer(event.getBpId(), event.getAnswerId(), event.getQuestionId());
                    break;
                case DELETE_ANSWER:
                    feedService.deleteAnswer(event.getAnswerId());
                    break;

            }
            log.info("deal event finished");
        }
    }
}
