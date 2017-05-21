package com.sohu.bp.elite.consumer.service;

import java.util.function.Function;

/**
 * Created by nicholastang on 2016/12/27.
 */
public interface KafkaService {
    /**
     * 简化的消费者
     * @param topic
     * @param taskHandler
     */
    public void consume(String topic, final Function<String, Boolean> taskHandler);

    /**
     *
     * @param topic
     * @param groupId
     * @param threadNum 线程数，最好适配运行的机器cpu
     * @param taskHandler
     */
    public void consume(String topic, String groupId, int threadNum, final Function<String, Boolean> taskHandler);

    public void produce(String topic, String content);
}
