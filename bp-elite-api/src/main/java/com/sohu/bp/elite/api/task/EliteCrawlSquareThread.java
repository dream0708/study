package com.sohu.bp.elite.api.task;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.enums.ProduceActionType;
import com.sohu.bp.elite.api.service.EliteUserService;
import com.sohu.bp.elite.api.util.SpringUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;


/**
 * 
 * @author zhijungou
 * 2016年11月7日
 */
public class EliteCrawlSquareThread {
	private Logger log = LoggerFactory.getLogger(EliteCrawlSquareThread.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private TimelineService timelineService = AchelousTimeline.getService();
	
	public static Thread squareThread;
	private EliteCrawlSquareCacheQueue queue;
	private EliteUserService eliteUserService;
	
	public void init(){
		eliteUserService = SpringUtil.getBean("eliteUserService", EliteUserService.class);
		queue = EliteCrawlSquareCacheQueue.getInstance();
		log.info("init EliteCrawlSquareCacheQueue succeed, start to execute work");
		this.executeWork();
	}
	
	public void executeWork(){
		squareThread = new Thread(){
			@Override
			public void run(){
				while(true){
					try{						
//						log.info("start to poll and insert crawl square queue");
						if(queue.size() <= 0 ){
							try {
								log.info("the crawl square queue size is 0, sleep for input");
								Thread.sleep(Constants.DAY_MILLISECONDS);
							} catch (InterruptedException e) {
								log.info("sleep is interrupted by input, start to do next step");
								continue;
							}
						}
						
						EliteCrawlSquareAsycTask task = queue.poll();
						Long time = task.getUpdateTime() - new Date().getTime();
						if(time >= 0 ){
							try{
								log.info("sleep for time {}", new Object[]{time});
								sleep(time);
							} catch (InterruptedException e){
								log.info("squareThread interrupted by early task");
								continue;
							} 
						}
						//利用互斥锁来解决同步问题
						if(queue.size() > 0 && queue.getMutexLock()){
							task = queue.poll();
							if(task.getUpdateTime() > new Date().getTime()) {
								queue.releaseMutexLock();
								continue;
							}
							queue.delete(task);
							queue.releaseMutexLock();
							insertAnswer(task);
						} else {
							try{
								Random rand = new Random();
								int randomValue = rand.nextInt(20);
								int value = randomValue * 100 + 1000;
								sleep(value);
							} catch (InterruptedException e){
								log.info("sleep is interrupted by input, start to do next step");
								continue;
							}
						}
//						log.info("delete task = {} from the crawl square queue, the size is {}", new Object[]{task.getContent(), queue.size()});
					} catch (Exception e){
						log.error("", e);
						try {
							sleep(Constants.DAY_MILLISECONDS);
						} catch (InterruptedException e1) {
							log.info("sleep is interrupted by input, start to do next step");
							continue;
						}
					}
				}
			}
		};
		squareThread.start();
	}
	
	public Long insertAnswer(EliteCrawlSquareAsycTask task){
		Long id;
		TEliteAnswer answer = new TEliteAnswer();
		answer.setQuestionId(task.getQuestionId()).setContent(task.getContent()).setBpId(task.getBpid()).setPublishTime(task.getUpdateTime()).setSource(TEliteSourceType.CRAWL.getValue()).setSourceUrl(task.getSourceUrl())
		.setUpdateTime(task.getUpdateTime()).setCreateTime(task.getCreateTime()).setCreateHost(Constants.DEFAULT_IP).setCreatePort(Constants.DEFAULT_PORT).setStatus(EliteAnswerStatus.PASSED.getValue());
		try {
			id = eliteAdapter.insertAnswerWithOptions(answer, false);
			if(id > 0){
				log.info("insert crawl square answer succeed. id = {}, content = {}", new Object[]{id, answer.getContent()});
				Long accountId = TimeLineUtil.getComplexId(answer.getBpId(), BpType.Elite_User.getValue());
				Long unitId = TimeLineUtil.getComplexId(id, BpType.Answer.getValue());
				timelineService.produce(accountId, unitId, ProduceActionType.ANSWER.getValue(), new Date(answer.getUpdateTime()));
				accountId = TimeLineUtil.getComplexId(answer.getQuestionId(), BpType.Question.getValue());
				timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), new Date(answer.getUpdateTime()));
				
				List<Integer> ids = task.getTagIds();
				if(null != ids && ids.size() > 0){
					for(Integer tagId : ids){
						accountId = TimeLineUtil.getComplexId(tagId.longValue(), BpType.Tag.getValue());
						timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), new Date(answer.getUpdateTime()));
					}
				}
				if(task.isSquareFlag()) {
				    eliteAdapter.insertSquare(id, FeedType.ANSWER.getValue());
				}
				//insert user in database
				eliteUserService.createUserIfNotExist(task.getBpid());
			} else {
				log.info("insert answer failed. answer content {} and time {}", new Object[]{answer.getContent(), answer.getUpdateTime()});
			}
		} catch (TException e) {
			log.info("", e);
			return null;
		}
		return id;
	}
	
}
