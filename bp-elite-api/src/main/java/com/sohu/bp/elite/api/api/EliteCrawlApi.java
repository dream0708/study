package com.sohu.bp.elite.api.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.service.impl.TimelineServiceImpl;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.SearchTagCondition;
import com.sohu.bp.decoration.model.TagListResult;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.api.bean.CrawlQAndAData;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.enums.ProduceActionType;
import com.sohu.bp.elite.api.service.EliteUserService;
import com.sohu.bp.elite.api.service.InteractionService;
import com.sohu.bp.elite.api.service.UserInfoService;
import com.sohu.bp.elite.api.task.EliteCrawlSquareAsycTask;
import com.sohu.bp.elite.api.task.EliteCrawlSquareCacheQueue;
import com.sohu.bp.elite.api.task.EliteCrawlSquareThread;
import com.sohu.bp.elite.api.util.ContentUtil;
import com.sohu.bp.elite.api.util.IDUtil;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-08-27 11:00:24
 * TODO 抓取数据入库api
 */
@Controller
@RequestMapping("/innerapi/crawl")
public class EliteCrawlApi
{
	private Logger logger = LoggerFactory.getLogger(EliteCrawlApi.class);
	private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter(); 
	private BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
	private TimelineService timelineService = AchelousTimeline.getService();
	private String kafkaTopic;
	
	@Resource
	UserInfoService userInfoService;
	@Resource
	InteractionService interactionService;
	@Resource
	EliteUserService eliteUserService;
	@Resource
	Configuration configuration;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@PostConstruct
	public void init() {
	    String kafkaTopic = configuration.get("sitemap.kafka.topic");
	}
	
	@ResponseBody
	@RequestMapping(value = "insert-data", produces = "application/json;charset=utf-8", method=RequestMethod.POST)
	public String insertData(CrawlQAndAData data, HttpServletRequest request)
	{
		logger.info("start insert crawl data");
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		

		new Thread(() -> {
			try
			{
				Long ip = 0L;
				Integer port = 0;
				
				Long bpId = data.getBpId();
				if(null == bpId || bpId.longValue() <= 0)
					return;
//				if(null == bpId || bpId.longValue() <= 0)
//				{
//					//新建抓取账号
//					String mobile = dbPhoneGenerator.next();
//					if(StringUtils.isNotBlank(mobile))
//					{
//						String nick = data.getAuthorName();
//						if(StringUtils.isBlank(nick))
//							nick = "";
//						String avatar = data.getAuthorAvatar();
//						if(StringUtils.isBlank(avatar))
//							avatar = "";
//						
//						bpId = userInfoService.addBpUser(mobile, nick, avatar, ip.toString(), port);
//					}
//				}
				eliteUserService.createUserIfNotExist(bpId);
				TEliteQuestion tEliteQuestion = new TEliteQuestion();
				tEliteQuestion.setBpId(bpId);
				if(StringUtils.isNotBlank(data.getTitle()))
					tEliteQuestion.setTitle(StringEscapeUtils.unescapeHtml(data.getTitle()));
				if(StringUtils.isNotBlank(data.getContent()))
					tEliteQuestion.setDetail(ContentUtil.preDealContent(StringEscapeUtils.unescapeHtml(data.getContent())));
				
				//将tag名称转id

				Set<Integer> tagIdSet = new HashSet<Integer>();
				if(StringUtils.isNotBlank(data.getTags()))
				{
					String[] tagNames = data.getTags().split(" ");
					if(null != tagNames && tagNames.length > 0)
					{   
					    for (int i = 0; i < Constants.CRAWL_TAG_SIZE && i < tagNames.length; i++) {
							String tagName = tagNames[i];
							SearchTagCondition searchTagCondition = new SearchTagCondition();
							searchTagCondition.setKeywords(tagName);
							searchTagCondition.setTypes(new ArrayList<TagType>(){{
							  add(TagType.ELITE_TAG);
							  add(TagType.ELITE_LOCATION_TAG);
							}});
							searchTagCondition.setStatus(TagStatus.WORK);
							searchTagCondition.setFrom(0);
							searchTagCondition.setCount(1);
							TagListResult tagListResult = decorationServiceAdapter.searchTag(searchTagCondition);
							if(null != tagListResult && tagListResult.getTags() != null && tagListResult.getTags().size() > 0)
							{
								tagIdSet.add(tagListResult.getTags().get(0).getId());
							}
							
						}
					}
				}

				String givenTagIds = data.getTagIds();
				if(StringUtils.isNotBlank(givenTagIds)){
					String[] givenTagIdArray = givenTagIds.split(Constants.DEFAULT_SPLIT_CHAR);
					if(givenTagIdArray != null){
						for(String givenTagId : givenTagIdArray){
							if(StringUtils.isBlank(givenTagId))
								continue;
							try {
								Integer givenTagIdNum = Integer.parseInt(givenTagId);
								tagIdSet.add(givenTagIdNum);
							}catch(Exception e){
								logger.error("", e);
							}
						}
					}
				}

				List<Integer> tagIdList = new ArrayList<Integer>(tagIdSet);
				String tagIds = StringUtils.join(tagIdList.toArray(), Constants.DEFAULT_SPLIT_CHAR);
				tEliteQuestion.setTagIds(tagIds);
				
				if(data.getCityId() != null && data.getCityId().longValue() > 0)
					tEliteQuestion.setAreaCode(data.getCityId());
					tEliteQuestion.setPublishTime(new Date().getTime());
					tEliteQuestion.setUpdateTime(new Date().getTime());

				tEliteQuestion.setSource(TEliteSourceType.CRAWL.getValue());
				if(StringUtils.isNotBlank(data.getSourceUrl()))
					tEliteQuestion.setSourceUrl(data.getSourceUrl());
				
				tEliteQuestion.setCreateHost(ip);
				tEliteQuestion.setCreatePort(port);
				tEliteQuestion.setStatus(EliteQuestionStatus.PASSED.getValue());
				long questionId = eliteThriftServiceAdapter.insertQuestionWithOptions(tEliteQuestion, false);
				if(questionId > 0)
				{
					logger.info("insert crawl question succeed. id = {}, title = {}" , new Object[]{ String.valueOf(questionId), tEliteQuestion.getTitle()});
					
					//推送到timeline
					Date nowDate = new Date();
			        long accountId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
			        long unitId = TimeLineUtil.getComplexId(questionId, BpType.Question.getValue());
			        timelineService.produce(accountId, unitId, ProduceActionType.ASK.getValue(), nowDate);

			        if(null != tagIdList && tagIdList.size() > 0){
			            for(Integer tagId : tagIdList){
			                accountId = TimeLineUtil.getComplexId(tagId, BpType.Tag.getValue());
			                timelineService.produce(accountId, unitId, ProduceActionType.ADD.getValue(), nowDate);
			            }
			        }
			        
			        //推到广场
			        if(data.getSquareFlag()) {
			            try {
			                eliteThriftServiceAdapter.insertSquare(questionId, FeedType.QUESTION.getValue());
			            } catch (Exception e) {
			                logger.error("", e);
			            }
			        }
			        
			        //SEO,推送到kafka队列
			        seoMethod(questionId);
			        
					if(StringUtils.isNotBlank(data.getAnswerBody())){
						String deletedLine = data.getDeletedLine();
						Set<Integer> deletedAnswers = new HashSet<>();
						if (StringUtils.isNotBlank(deletedLine)) {
							 Arrays.asList(deletedLine.split(Constants.DELETED_ANSWER_SPLIT_CHAR)).forEach(index -> deletedAnswers.add(Integer.valueOf(index)));
						}
						String answerBody = data.getAnswerBody();
						answerBody = StringEscapeUtils.unescapeHtml(answerBody);
						JSONArray answerArray = JSONArray.fromObject(answerBody);
						if(null != answerArray && answerArray.size() > 0)
						{
							for(int i=0; i< answerArray.size(); i++)
							{	
								if (deletedAnswers.contains(i)) continue;
								JSONObject answerObject = answerArray.getJSONObject(i);
								
								//用于将抓取的回答推送到crawl square queue中
								EliteCrawlSquareAsycTask task = new EliteCrawlSquareAsycTask();
								task.setQuestionId(questionId);
								task.setSquareFlag(data.getSquareFlag());
							
								Long answerBpId = answerObject.getLong("cont_item_author_id");
								if(null == answerBpId || answerBpId.longValue() <= 0)
									continue;
								task.setBpid(answerBpId);
								String answerContent = answerObject.getString("cont_item_body");
								if(StringUtils.isNotBlank(answerContent))
									task.setContent(ContentUtil.preDealContent(answerContent));
								else continue;
								String answerTime = answerObject.getString("cont_item_time");
								if(StringUtils.isNotBlank(answerTime)) {
									task.setCreateTime(Long.valueOf(answerTime));
								} else {
									task.setCreateTime(new Date().getTime());
								}
								Long shiftTime = getTimeShift();
								task.setUpdateTime(shiftTime);
								task.setTagIds(tagIdList);
								if(StringUtils.isNotBlank(data.getSourceUrl())) task.setSourceUrl(data.getSourceUrl());
								EliteCrawlSquareCacheQueue.getInstance().push(task);
								EliteCrawlSquareThread.squareThread.interrupt();
							}
						}
					}
				}
				else
				{
					logger.warn("insert question failed.");
				}
			}catch(Exception e)
			{
				logger.error("", e);
			}
			
			logger.info("insert crawl data finished");
		}).start();

		return resJSON.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "test")
	public String test()
	{
		logger.info("ok");
		try
		{
			timelineService = TimelineServiceImpl.getInstance();
		}catch(Exception e)
		{
			logger.error("", e);
		}
		logger.info("ok");
		return "ok";
	}
	
	public Long getTimeShift(){
		Random rand = new Random();
		Long shift = (long)rand.nextInt((int)Constants.DAY_MILLISECONDS);
		Long time = new Date().getTime() + shift;
		return time;
	}
	
	public void seoMethod(Long id){
		Integer objectType = BpType.Question.getValue();
		String objectId = IDUtil.encodeId(id);
		JSONObject msgJSON = new JSONObject();
		msgJSON.put("objectType", objectType);
		msgJSON.put("encryptId", objectId);
		msgJSON.put("objectId", IDUtil.decodeId(objectId));
		logger.info("send infomation to kafka : " + msgJSON.toString());
		kafkaProducer.send(kafkaTopic, msgJSON.toString());
	}
}