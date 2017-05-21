package com.sohu.bp.elite.api.api;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.sohu.bp.elite.api.api.bean.FeedCacheNotifyBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.constants.Constants;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.enums.ProduceActionType;
import com.sohu.bp.elite.api.util.IDUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/9/1
 */
@Controller
@RequestMapping("/innerapi/timeline")
public class EliteTimeLineApi {

    private Logger log = LoggerFactory.getLogger(EliteTimeLineApi.class);

    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final TimelineService timelineService = AchelousTimeline.getService();
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    private String seoKafkaTopic;
    private String squareKafkaTopic;
	private String feedCacheTopic;
    
    @Resource
    private Configuration configuration;
    
    @PostConstruct
    private void init(){
    	 seoKafkaTopic = configuration.get("sitemap.kafka.topic");
    	 squareKafkaTopic = configuration.get("kafka.topic.square");
		feedCacheTopic = configuration.get("kafka.topic.feed.cache");
    }
    
	/**
	 * feedType(1:问题，2：回答)
	 * ids:问题或者回答的id列表
	 */
	@ResponseBody
	@RequestMapping(value = "auditPass", produces = "application/json;charset=utf-8")
	public String auditPass(int feedType, String ids){
		if(!(feedType == 1 || feedType == 2) || StringUtils.isBlank(ids))
			return ResponseJSON.getErrorParamsJSON().toString();

		log.info("calling audit pass api <feedType={}, ids={}>", feedType, ids);

		if(feedType == 2){
			String[] idArray = ids.split(Constants.DB_DEFAULT_SPLIT_CHAR);
			if(idArray.length > 0) {
				for (String idStr : idArray) {
					Long answerId = Long.parseLong(idStr);
					produceAnswer(answerId);
				}
			}
		}else {
			String[] idArray = ids.split(Constants.DB_DEFAULT_SPLIT_CHAR);
			if (idArray.length > 0) {
				for (String idStr : idArray) {
					Long questionId = Long.parseLong(idStr);
					seoMethod(questionId);
					produceQuestion(questionId);
				}
			}
		}

		return ResponseJSON.getSucJSON().toString();
	}

    /**
     * feedType(1:问题，2：回答)
     * ids:问题或者回答的id列表
     */
    @ResponseBody
    @RequestMapping(value = "ignoreFeed", produces = "application/json;charset=utf-8")
    public String ignoreFeed(int feedType, String ids){
        if(!(feedType == 1 || feedType == 2) || StringUtils.isBlank(ids))
            return ResponseJSON.getErrorParamsJSON().toString();

        log.info("calling ignore feed api <feedType={}, ids={}>", feedType, ids);
        
		if(feedType == 2){
			String[] idArray = ids.split(Constants.DB_DEFAULT_SPLIT_CHAR);
			if(idArray.length > 0) {
				for (String idStr : idArray) {
					Long answerId = Long.parseLong(idStr);
					reduceAnswer(answerId);
				}
			}
		}else {
			String[] idArray = ids.split(Constants.DB_DEFAULT_SPLIT_CHAR);
			if (idArray.length > 0) {
				for (String idStr : idArray) {
					Long questionId = Long.parseLong(idStr);
					reduceQuestion(questionId);
				}
			}
		}

        return ResponseJSON.getSucJSON().toString();
    }

	private void reduceQuestion(Long questionId){
		TEliteQuestion question = null;
		try {
			question = eliteAdapter.getQuestionById(questionId);
		} catch (TException e) {
			log.error("", e);
		}
		if(question != null){
			long bpId = question.getBpId();
			long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
			long unitId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
			timelineService.remove(accountId, unitId);
			log.info("clear feed cache here.topic={}, accountId={}", new String[]{feedCacheTopic, String.valueOf(accountId)});

			kafkaProducer.send(feedCacheTopic, JSONObject.fromObject(new FeedCacheNotifyBean(accountId)).toString());
			try {
			    eliteAdapter.removeSquareItem(questionId, FeedType.QUESTION.getValue());
			} catch (Exception e) {
			    log.error("", e);
			}
			if(StringUtils.isNotBlank(question.getTagIds())){
				for(String tagId : question.getTagIds().split(Constants.DB_DEFAULT_SPLIT_CHAR)){
					accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
					timelineService.remove(accountId, unitId);
				}
			}
		}
	}

	private void reduceAnswer(Long answerId){
		TEliteAnswer answer = null;
		try {
			answer = eliteAdapter.getAnswerById(answerId);
		} catch (TException e) {
			log.error("", e);
		}
		if(answer != null){
			long bpId = answer.getBpId();
			long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
			long unitId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
			timelineService.remove(accountId, unitId);
			log.info("clear feed cache here.topic={}, accountId={}", new String[]{feedCacheTopic, String.valueOf(accountId)});
			kafkaProducer.send(feedCacheTopic, JSONObject.fromObject(new FeedCacheNotifyBean(accountId)).toString());
			TEliteQuestion question = null;
			try {
			    eliteAdapter.removeSquareItem(answerId, FeedType.ANSWER.getValue());
				question = eliteAdapter.getQuestionById(answer.getQuestionId());
			} catch (TException e) {
				log.error("", e);
			}
			if(question != null) {
				accountId = TimeLineUtil.getComplexId(question.getId(), BpType.Question.getValue());
				timelineService.remove(accountId, unitId);

				if(StringUtils.isNotBlank(question.getTagIds())){
					for(String tagId : question.getTagIds().split(Constants.DB_DEFAULT_SPLIT_CHAR)){
						accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
						timelineService.remove(accountId, unitId);
					}
				}
			}
		}
	}

	private void produceQuestion(Long questionId){
		TEliteQuestion question = null;
		try {
			question = eliteAdapter.getQuestionById(questionId);
		} catch (TException e) {
			log.error("", e);
		}
		if(question != null && !(EliteQuestionStatus.PUBLISHED.getValue() == question.getStatus() || EliteQuestionStatus.PASSED.getValue() == question.getStatus())){
			long bpId = question.getBpId();
			long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
			long unitId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
			timelineService.produce(accountId, unitId, ProduceActionType.ASK.getValue(), new Date(question.getUpdateTime()));
			//timelineService.retreatReduceTemp(accountId, unitId, new Date(question.getUpdateTime()));
			try {
			    eliteAdapter.insertSquare(questionId, FeedType.QUESTION.getValue());
			} catch (Exception e) {
			    
			}
			if(StringUtils.isNotBlank(question.getTagIds())){
				for(String tagId : question.getTagIds().split(Constants.DB_DEFAULT_SPLIT_CHAR)){
					accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
					timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), new Date(question.getUpdateTime()));
					//timelineService.retreatReduceTemp(accountId, unitId, new Date(question.getUpdateTime()));
				}
			}
		}
	}

	private void produceAnswer(Long answerId){
		TEliteAnswer answer = null;
		try {
			answer = eliteAdapter.getAnswerById(answerId);
		} catch (TException e) {
			log.error("", e);
		}
		if(answer != null && !(EliteAnswerStatus.PUBLISHED.getValue() == answer.getStatus() || EliteAnswerStatus.PASSED.getValue() == answer.getStatus())){
			long bpId = answer.getBpId();
			long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
			long unitId = TimeLineUtil.getComplexId(answerId, BpType.Answer.getValue());
			timelineService.produce(accountId, unitId, ProduceActionType.ANSWER.getValue(), new Date(answer.getUpdateTime()));
			//timelineService.retreatReduceTemp(accountId, unitId, new Date(answer.getUpdateTime()));
			try {
			    eliteAdapter.insertSquare(answerId, FeedType.ANSWER.getValue());
			} catch (Exception e) {
			    log.error("", e);
			}
			accountId = TimeLineUtil.getComplexId(answer.getQuestionId(), BpType.Question.getValue());
			timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), new Date(answer.getUpdateTime()));
			//timelineService.retreatReduceTemp(accountId, unitId, new Date(answer.getUpdateTime()));
			
			try {
				TEliteQuestion question = eliteAdapter.getQuestionById(answer.getQuestionId());
				if(question != null && StringUtils.isNotBlank(question.getTagIds())){
					for(String tagId : question.getTagIds().split(Constants.DB_DEFAULT_SPLIT_CHAR)){
						accountId = TimeLineUtil.getComplexId(Long.parseLong(tagId), BpType.Tag.getValue());
						timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), new Date(answer.getUpdateTime()));
						//timelineService.retreatReduceTemp(accountId, unitId, new Date(answer.getUpdateTime()));
					}
				}
			} catch (TException e) {
				log.error("", e);
			}
		}
	}
	
	public void seoMethod(Long id){
		Integer objectType = BpType.Question.getValue();
		String objectId = IDUtil.encodeId(id);
		JSONObject msgJSON = new JSONObject();
		msgJSON.put("objectType", objectType);
		msgJSON.put("encryptId", objectId);
		msgJSON.put("objectId", IDUtil.decodeId(objectId));
		log.info("send infomation to kafka, topic = {}, msg = {}", new Object[]{seoKafkaTopic, msgJSON.toString()});
		kafkaProducer.send(seoKafkaTopic, msgJSON.toString());
	}
	
}
