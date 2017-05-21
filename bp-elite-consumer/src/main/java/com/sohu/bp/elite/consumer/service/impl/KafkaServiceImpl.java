package com.sohu.bp.elite.consumer.service.impl;

import com.sohu.bp.elite.consumer.service.KafkaService;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Created by nicholastang on 2016/12/27.
 */
public class KafkaServiceImpl implements KafkaService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();

    private static String zkConnect = "zk0.life.sohuno.com:2181,zk1.life.sohuno.com:2181,zk2.life.sohuno.com:2181,zk3.life.sohuno.com:2181,zk4.life.sohuno.com:2181";
    private static String zkNode = "/talent/bp/kafka";
    private static int threadNum = 4;
    private static String groupId = "bp-elite-kafka-consumer";
    private static String offsetReset = "largest";
    private static String sessionTimeOut = "40000";
    private static String syncTime = "20000";
    private static String autoCommitInterval = "1000";

    public String getZkConnect() {
        return zkConnect;
    }

    public void setZkConnect(String zkConnect) {
        this.zkConnect = zkConnect;
    }

    public String getZkNode() {
        return zkNode;
    }

    public void setZkNode(String zkNode) {
        this.zkNode = zkNode;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOffsetReset() {
        return offsetReset;
    }

    public void setOffsetReset(String offsetReset) {
        this.offsetReset = offsetReset;
    }

    public String getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(String sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }

    public String getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public void setAutoCommitInterval(String autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }

    @Override
    public void consume(String topic, Function<String, Boolean> taskHandler) {
        this.consume(topic, groupId, threadNum, taskHandler);
    }

    @Override
    public void consume(String topic, String groupId, int threadNum, Function<String, Boolean> taskHandler) {
        if(StringUtils.isBlank(topic) || StringUtils.isBlank(groupId) || threadNum <= 0 || null == taskHandler ){
            logger.error("call kafka consume method error.parameters empty");
            return;
        }
        String zkConnectStr = zkConnect + zkNode;

        Properties props = new Properties();
        props.setProperty("zk.connect", zkConnectStr);
        logger.info("**************************** groupId="+groupId+" ***********************");
        props.setProperty("groupid", groupId);
        //props.put("auto.offset.reset", offsetReset);
        props.put("zookeeper.session.timeout.ms", sessionTimeOut);
        props.put("zookeeper.sync.time.ms", syncTime);
        props.put("auto.commit.interval.ms", autoCommitInterval);
        ConsumerConfig consumerConfig = new ConsumerConfig(props);

        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        HashMap map = new HashMap();
        map.put(topic, Integer.valueOf(4));
        Map topicMessageStreams = consumerConnector.createMessageStreams(map);
        List streams = (List)topicMessageStreams.get(topic);
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        Iterator var11 = streams.iterator();

        while(var11.hasNext()) {
            final KafkaStream stream = (KafkaStream)var11.next();
            executor.submit(new Runnable() {
                public void run() {
                    ConsumerIterator var1 = stream.iterator();

                    while(var1.hasNext()) {
                        MessageAndMetadata msgAndMetadata = (MessageAndMetadata)var1.next();
                        String msg = (new StringDecoder()).toEvent((Message)msgAndMetadata.message());
                        taskHandler.apply(msg);
                        logger.info("get one message " + msg);
                    }

                }
            });
        }
    }

    @Override
    public void produce(String topic, String content) {
        kafkaProducer.send(topic, content);
    }
}
