package com.sohu.bp.elite.consumer.listener;

import com.sohu.bp.elite.consumer.service.KafkaService;
import com.sohu.bp.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Created by nicholastang on 2016/12/27.
 */
public class KafkaListener implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(KafkaListener.class);

    private KafkaService kafkaService = null;

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

    @Override
    public void run() {
        log.info("topic_name:" + this.topicName);
        if(kafkaService == null){
            kafkaService = (KafkaService) SpringUtil.getBean("kafkaService");
        }

        try {
            kafkaService.consume(this.topicName, this.taskHandler);
        }catch(Exception e){
            log.error("", e);
        }

    }

    public void myRun()
    {
        new Thread(this).start();
        log.info("+++++++++++++++++++++++++++++++" + this.topicName + " start success!++++++++++++++++++++++++++++++");
    }
}
