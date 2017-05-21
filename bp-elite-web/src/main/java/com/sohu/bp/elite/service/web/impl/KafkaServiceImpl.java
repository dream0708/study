package com.sohu.bp.elite.service.web.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.service.web.KafkaService;
import com.sohu.bp.elite.util.NetUtil;
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

public class KafkaServiceImpl implements KafkaService {

	private static final Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    private CacheManager redisCacheManager;
    private RedisCache redisCache;
    
    private static String zkConnect = "zk0.life.sohuno.com:2181,zk1.life.sohuno.com:2181,zk2.life.sohuno.com:2181,zk3.life.sohuno.com:2181,zk4.life.sohuno.com:2181";
    private static String zkNode = "/talent/bp/kafka";
    private static int threadNum = 4;
    private static String groupId = "bp-elite-kafka-consumer";
    private static String offsetReset = "largest";
    private static String sessionTimeOut = "40000";
    private static String syncTime = "20000";
    private static String autoCommitInterval = "1000";
    private String address;
    private String zombieTopic;
    
    public void setAddress(String address) {
    	this.address = address;
    }

    public void setZkConnect(String zkConnect) {
        this.zkConnect = zkConnect;
    }
    
    public void setZkNode(String zkNode) {
        this.zkNode = zkNode;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setOffsetReset(String offsetReset) {
        this.offsetReset = offsetReset;
    }
    
    public void setSessionTimeOut(String sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }

    public void setAutoCommitInterval(String autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
    	this.redisCacheManager = redisCacheManager;
    }

    public String getZombieTopic() {
        return zombieTopic;
    }

    public void setZombieTopic(String zombieTopic) {
        this.zombieTopic = zombieTopic;
    }

    public void init() {
    	redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_PUSH_SERVER);
    	while(!getMutexLock()){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.info("", e);
			}
    	}
    	Long serverNum = (Long) redisCache.get(CacheConstants.SERVER_NUM);
    	if (null == serverNum || serverNum < 0 ) serverNum = 0l;
    	redisCache.put(CacheConstants.SERVER_NUM, ++serverNum);
    	groupId = groupId + "-" + serverNum;
    	logger.info("set kafka group id : " + groupId);
    	releaseMutexLock();
    }
    
    public void destroy() {
		//only one server remove cache
		String serverIp = NetUtil.getServerIp();
		if (serverIp.equals(address)) {
			logger.info("ip = {}, remove elite kafka groupId cache", new Object[]{address});
	    	redisCache.remove(CacheConstants.SERVER_NUM);
		} else {
			logger.info("ip = {} doesn't equal {}, doesn't remove elite kafka groupId cache", new Object[]{serverIp, address});
		}
    }
    @Override
    public void consume(String topic, Function<String, Boolean> taskHandler) {
        this.consume(topic, groupId, threadNum, taskHandler);
    }
    
    @Override
    public void consume(String topic, String groupId, Function<String, Boolean> taskHandler) {
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
    public void produce (String topic, String content) {
    	kafkaProducer.send(topic, content);
    }

    @Override
    public void produceZombieMsg(String content) {
        logger.info("send msg to zombie, topic = {}, content = {}", new Object[]{zombieTopic, content});
        kafkaProducer.send(zombieTopic, content);
    }

    private boolean getMutexLock() {
    	return redisCache.setnx(CacheConstants.SERVER_LOCK, Constants.LOCK_FLAG);
    }
    
    private void releaseMutexLock() {
    	redisCache.remove(CacheConstants.SERVER_LOCK);
    }
    
}
