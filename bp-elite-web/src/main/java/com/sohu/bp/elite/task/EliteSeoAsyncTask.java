package com.sohu.bp.elite.task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAsyncTaskOper;
import com.sohu.bp.elite.service.web.WasherService;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.SpringUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;

import net.sf.json.JSONObject;

/**
 * 推送sitemap的异步线程
 * @author nicholastang
 * 2016-11-07 15:01:49
 * TODO
 */
public class EliteSeoAsyncTask extends EliteAsyncTask
{
	private static final Logger logger = LoggerFactory.getLogger(EliteSeoAsyncTask.class);
	private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
	private Configuration configuration = (Configuration)SpringUtil.getBean("configuration");
	private String kafkaTopic = "SITEMAP_QUEUE_TEST";
	private Integer objectType;
	private String objectId;

	public Integer getObjectType() {
		return objectType;
	}


	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}


	public String getObjectId() {
		return objectId;
	}


	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	public EliteSeoAsyncTask(Integer objectType, Long objectId)
	{
		this.objectType = objectType;
		this.objectId = objectId.toString();
		
		this.kafkaTopic = configuration.get("sitemap.kafka.topic", kafkaTopic);
	}
	public EliteSeoAsyncTask(Integer objectType, String objectId) {
		this.objectType = objectType;
		this.objectId = objectId;
		
		this.kafkaTopic = configuration.get("sitemap.kafka.topic", kafkaTopic);
	}
	
	@Override
	public void run()
	{
//		if(StringUtils.isBlank(kafkaTopic) || 
//				null == objectType || objectType.intValue() <= 0 ||
//				null == objectId)
//			return;
//		JSONObject msgJSON = new JSONObject();
//		msgJSON.put("objectType", objectType);
//		msgJSON.put("encryptId", objectId);
//		msgJSON.put("objectId", IDUtil.decodeId(objectId));
//		
//		kafkaProducer.send(kafkaTopic, msgJSON.toString());
	}
	
	
}