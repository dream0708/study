package com.sohu.bp.elite.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.bean.ZombieKafkaBean;
import com.sohu.bp.elite.dao.EliteSquareDao;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.service.EliteAnswerService;
import com.sohu.bp.elite.service.EliteQuestionService;
import com.sohu.bp.elite.service.EliteSquareService;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.SquareUtil;
import com.sohu.bp.kafka.producer.BpKafkaProducer;
import com.sohu.bp.kafka.producer.BpKafkaProducerFactory;

import net.sf.json.JSONObject;

public class EliteSquareServiceImpl implements EliteSquareService {
    private static final Logger log = LoggerFactory.getLogger(EliteSquareServiceImpl.class);
    private static final BpKafkaProducer kafkaProducer = BpKafkaProducerFactory.getBpKafkaStringProducer();
    
    private EliteSquareDao squareDao;
    private EliteQuestionService questionService;
    private EliteAnswerService answerService;
    private String squareTopic;
    private String zombieTopic;
    
    public void setSquareDao(EliteSquareDao squareDao) {
        this.squareDao = squareDao;
    }
    
    public void setQuestionService(EliteQuestionService questionService) {
        this.questionService = questionService;
    }
    
    public void setAnswerService(EliteAnswerService answerService) {
        this.answerService = answerService;
    }
    
    public void setSquareTopic(String squareTopic) {
        this.squareTopic = squareTopic;
    }
    
    public void setZombieTopic(String zombieTopic) {
        this.zombieTopic = zombieTopic;
    }
    
    @Override
    public int getNewSquareNum(Long latestTime) {
        if (null == latestTime || latestTime <= 0) latestTime = new Date().getTime();
        return squareDao.getNewSquareNum(latestTime);
    }

    @Override
    public List<Long> getNewSquareList(Long latestTime) {
        if (null == latestTime || latestTime <= 0) latestTime = new Date().getTime();
        List<Long> complexIds = squareDao.getNewSquareList(latestTime);
        return complexIds;
    }

    @Override
    public List<Long> getBackward(Long feedId, FeedType feedType, int count) {
        return squareDao.getBackward(feedId, feedType, count);
    }
    
    @Override
    public List<Long> getBackward(int start, int count) {
       return squareDao.getBackward(start, count);
    }

    @Override
    public boolean insertSquare(Long feedId, FeedType feedType) {
        boolean result = false;
        if (null == feedId || feedId <= 0) return result;
        long complexId = SquareUtil.getComplexId(feedId, feedType);
        long time = 0;
        EliteQuestion question = null;
        EliteAnswer  answer = null;
        switch (feedType) {
        case QUESTION:
            question = questionService.getById(feedId);
            if (null != question) {
                time = question.getUpdateTime().getTime();
            }
            break;
        case ANSWER:
            answer = answerService.getById(feedId);
            if (null != answer) {
                time = answer.getUpdateTime().getTime();
            }
            break;
        }
        if (time > 0) {
            result = squareDao.insertSquare(complexId, time);
            if (result) {
                //发送信息到kafka队列，现有两个队列，广场新消息队列和僵尸队列
                sendKafkaMsg(feedId, FeedType.getBpTypeByFeedType(feedType));
                insertSelectedSquareFromNormal(question, answer);
            }
        }
        
        return result;
    }

    @Override
    public void flushSquare() {
        squareDao.flushSquare();
    }

    @Override
    public boolean removeSquareItem(Long feedId, FeedType feedType) {
        if (null == feedId || feedId <= 0) return false;
        long complexId = SquareUtil.getComplexId(feedId, feedType);
        return squareDao.removeSquareItem(complexId);
    }

    @Override
    public int getNewSelectedSquareNum(Long latestTime) {
        if (null == latestTime || latestTime <= 0) latestTime = new Date().getTime();
        return squareDao.getNewSelectedSquareNum(latestTime);
    }

    @Override
    public List<Long> getNewSelectedSquareList(Long latestTime) {
        if (null == latestTime || latestTime <= 0) latestTime = new Date().getTime();
        List<Long> complexIds = squareDao.getNewSelectedSquareList(latestTime);
        return complexIds;
    }

    @Override
    public List<Long> getSelectedSquareBackward(Long feedId, FeedType feedType, int count) {
        return squareDao.getSelectedSquareBackward(feedId, feedType, count);
    }
    
    @Override
    public List<Long> getSelectedSquareBackward(int start, int count) {
        return squareDao.getSelectedSquareBackward(start, count);
    }
    
    @Override
    public List<Long> getSelectedSquareBackward(long oldestTime, int count) {
        if (oldestTime <= 0) oldestTime = new Date().getTime();
        return squareDao.getSelectedSquareBackward(oldestTime, count);
    }

    @Override
    public List<Long> getSelectedSquareForward(long latestTime, int count) {
        if (latestTime <= 0) latestTime = new Date().getTime();
        return squareDao.getSelectedSquareForward(latestTime, count);
    }

    @Override
    public boolean insertSelectedSquare(Long feedId, FeedType feedType) {
        boolean result = false;
        if (null == feedId || feedId <= 0) return result;
        long complexId = SquareUtil.getComplexId(feedId, feedType);
        long time = 0;
        switch (feedType) {
        case QUESTION:
            EliteQuestion question = questionService.getById(feedId);
            if (null != question) {
                time = question.getUpdateTime().getTime();
            }
            break;
        case ANSWER:
            EliteAnswer answer = answerService.getById(feedId);
            if (null != answer) {
                time = answer.getUpdateTime().getTime();
            }
            break;
        }
        if (time > 0) {
            result = squareDao.insertSelectedSquare(complexId, time);
        }
        return result;
    }

    @Override
    public void flushSelectedSquare() {
        squareDao.flushSelectedSquare();
    }

    @Override
    public boolean removeSelectedSquareItem(Long feedId, FeedType feedType) {
        if (null == feedId || feedId <= 0) return false;
        long complexId = SquareUtil.getComplexId(feedId, feedType);
        return squareDao.removeSelectedSquareItem(complexId);
    }
    
    @Override
    public boolean isInSelectedSquare(Long feedId, FeedType feedType) {
        if (null == feedId || feedId <= 0) return false;
        Long complexId = SquareUtil.getComplexId(feedId, feedType);
        return squareDao.isInSelectedSquare(complexId);
    }
    
    //判断当前进入广场内容是否进入优选广场, 并进入广场
    //标准：
    //回答为内容数量超过200个字，照片数量超过3个
    //问题为问题标题加内容超过30个字
    private void insertSelectedSquareFromNormal(EliteQuestion question, EliteAnswer answer) {
        if (null == answer) {
//            if (ContentUtil.isContentValid(question) && squareDao.isSelectedValid(question.getId(), question.getBpId(), question.getUpdateTime().getTime())) {
//                Long complexId = SquareUtil.getComplexId(question.getId(), FeedType.QUESTION);
//                squareDao.insertSelectedSquare(complexId, question.getUpdateTime().getTime());
//                log.info("question id = {} is valid by content examine, insert from square to selected square.", new Object[]{question.getId()});
//            } else {
//                log.info("question id = {} is invalid by content examine, insert from square to selected square.", new Object[]{question.getId()});
//            }
          Long complexId = SquareUtil.getComplexId(question.getId(), FeedType.QUESTION);
          squareDao.insertSelectedSquare(complexId, question.getUpdateTime().getTime());
          log.info("question id = {} is valid by content examine, insert from square to selected square.", new Object[]{question.getId()});
        } else {
            if (ContentUtil.isContentValid(answer) && squareDao.isSelectedValid(answer.getQuestionId(), answer.getBpId(), answer.getUpdateTime().getTime())) {
                Long complexId = SquareUtil.getComplexId(answer.getId(), FeedType.ANSWER);
                squareDao.insertSelectedSquare(complexId, answer.getUpdateTime().getTime());
                log.info("answer id = {} is valid by content examine, insert from square to selected square.", new Object[]{answer.getId()});
            } else {
                log.info("answer id = {} is invalid by content examine, insert from square to selected square.", new Object[]{answer.getId()});
            }
        }
    }
    
    private void sendKafkaMsg(Long id, BpType type) {
        //发送信息到广场队列
        JSONObject data = new JSONObject();
        data.put("data", id);
        log.info("send kafka message to square topic, topic = {}, message = {}", new Object[]{squareTopic, data});
        kafkaProducer.send(squareTopic, data.toString());
        //发送信息到僵尸粉
        ZombieKafkaBean zombieKafkaBean = new ZombieKafkaBean();
        zombieKafkaBean.setGeneratorId(id);
        zombieKafkaBean.setGeneratorType(type.getValue());
        String content = JSONObject.fromObject(zombieKafkaBean).toString();
        log.info("send content to zombie producer, topic = {}, content = {}", new Object[]{zombieTopic, content});
        kafkaProducer.send(zombieTopic, content);
    }
}
