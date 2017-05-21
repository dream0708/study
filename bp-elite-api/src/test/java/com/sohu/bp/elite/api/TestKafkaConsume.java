package com.sohu.bp.elite.api;

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.api.service.KafkaService;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * Created by nicholastang on 2017/1/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*.xml","classpath:applicationContext.xml"})
public class TestKafkaConsume {
    private Logger logger = LoggerFactory.getLogger(TestKafkaConsume.class);

    @Resource
    private Configuration configuration;
    @Resource
    private KafkaService kafkaService;

    @Test
    public void testConsume(){
        String topic = configuration.get("kafka.topic.zombie");
        logger.info("KAFKA topic:" + topic);
        kafkaService.produce(topic, "abc");
        try {
            Thread.sleep(20000);
        }catch(Exception e){
            logger.error("", e);
        }

//        kafkaService.produce(topic, ""+ System.currentTimeMillis());
    }
}
