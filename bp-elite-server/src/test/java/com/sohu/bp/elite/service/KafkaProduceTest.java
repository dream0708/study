package com.sohu.bp.elite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;

import net.sf.json.JSONObject;


/**
 * 用于测试与search之间的建索引
 * @author zhijungou
 * 2017年4月12日
 */
public class KafkaProduceTest {
    private static final Logger log = LoggerFactory.getLogger(KafkaProduceTest.class);
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    private static final String TOPIC_TEST = "elite-test";
    private static final String TOPIC_PRODUCTION = "elite";
    public static void main(String[] args) {
        Long id = 16103L;
        JSONObject msg = new JSONObject();
        msg.put("objectId", id);
        msg.put("objectType", BpType.Answer.getValue());
        msg.put("notifyType", EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
        log.info("notify to statistic. message = " + msg.toString());
        try {
            kafkaProducer.send(TOPIC_TEST, msg.toString());
        } catch(Exception e) {
            log.error("", e);
        }
    }
}
