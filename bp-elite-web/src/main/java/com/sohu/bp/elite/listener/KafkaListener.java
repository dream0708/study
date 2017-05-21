package com.sohu.bp.elite.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Function;

import javax.sound.midi.MidiDevice.Info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.sohu.bp.elite.enums.EliteColumnType;
import com.sohu.bp.elite.enums.EliteConsumeType;
import com.sohu.bp.elite.service.web.KafkaService;
import com.sohu.bp.elite.util.NetUtil;
import com.sohu.bp.elite.util.SpringUtil;

public class KafkaListener implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(KafkaListener.class);
	private KafkaService kafkaService = null;
	private String address;
	private Integer consumeType;
	private String groupId;

	private Function taskHandler = null;
	private String topicName = "";

	public KafkaService getKafkaService() {
		return kafkaService;
	}

	public void setKafkaService(KafkaService kafkaService) {
		this.kafkaService = kafkaService;
	}

	public Function getTaskHandler() {
		return taskHandler;
	}

	public void setTaskHandler(Function taskHandler) {
		this.taskHandler = taskHandler;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
    public Integer getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Integer consumeType) {
        this.consumeType = consumeType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
	@Override
	public void run() {
		
		log.info("topic_name:" + this.topicName);
		if (kafkaService == null) {
			kafkaService = (KafkaService) SpringUtil.getBean("kafkaService");
		}

		try {
//			//only one server consumes square push msg
//			String serverIp = NetUtil.getServerIp();
//			if (serverIp.equals(address)) {
//				log.info("ip = {}, start to consume square push msg", new Object[]{address});
//				kafkaService.consume(this.topicName, this.taskHandler);
//			} else {
//				log.info("ip = {} doesn't equal {}, doesn't consume square push msg", new Object[]{serverIp, address});
//			}
			//all servers consume square push msg
		    
		    if (consumeType == EliteConsumeType.CONSUME_ALL.getValue()) kafkaService.consume(this.topicName, this.taskHandler);
		    if (consumeType == EliteConsumeType.CONSUME_ONCE.getValue()) kafkaService.consume(this.topicName, this.groupId, this.taskHandler);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public void myRun() {
		new Thread(this).start();
		log.info("+++++++++++++++++++++++++++++++" + this.topicName + "  " + this.consumeType +" start success!++++++++++++++++++++++++++++++");
	}
	
}
