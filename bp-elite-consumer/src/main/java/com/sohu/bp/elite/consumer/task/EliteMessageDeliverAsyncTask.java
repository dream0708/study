package com.sohu.bp.elite.consumer.task;

import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.consumer.enums.EliteMessageTargetType;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.model.*;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.model.ObjectUserRelation;
import com.sohu.bp.thallo.model.ObjectUserRelationListResult;
import com.sohu.bp.thallo.model.RelationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author nicholastang
 * 2016-10-17 19:20:03
 * TODO
 */
public class EliteMessageDeliverAsyncTask extends EliteAsyncTask
{
	private static final Logger logger = LoggerFactory.getLogger(EliteMessageDeliverAsyncTask.class);
	private static EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private static BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
	
	private TEliteMessagePushType messageType;
	private TEliteMessageData messageData;
	private TEliteMessageStrategy strategy;
	private EliteMessageTargetType messageTargetType;
	private BpType bpType;
	private List<Long> sourceIdList;

	public EliteMessageTargetType getMessageTargetType() {
		return messageTargetType;
	}
	public EliteMessageDeliverAsyncTask setMessageTargetType(EliteMessageTargetType messageTargetType) {
		this.messageTargetType = messageTargetType;
		return this;
	}
	public BpType getBpType() {
		return bpType;
	}
	public EliteMessageDeliverAsyncTask setBpType(BpType bpType) {
		this.bpType = bpType;
		return this;
	}
	public List<Long> getSourceIdList() {
		return sourceIdList;
	}
	public EliteMessageDeliverAsyncTask setSourceIdList(List<Long> sourceIdList) {
		this.sourceIdList = sourceIdList;
		return this;
	}
	public TEliteMessageData getMessageData() {
        return messageData;
    }
    public EliteMessageDeliverAsyncTask setMessageData(TEliteMessageData messageData) {
        this.messageData = messageData;
        return this;
    }
    public TEliteMessageStrategy getStrategy() {
        return strategy;
    }
    public EliteMessageDeliverAsyncTask setStrategy(TEliteMessageStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    public EliteMessageDeliverAsyncTask(TEliteMessagePushType messageType, TEliteMessageData messageData, TEliteMessageStrategy strategy, 
	        EliteMessageTargetType messageTargetType, BpType bpType, List<Long> sourceIdList) {
		
		this.messageType = messageType;
		this.messageData = messageData;
		this.strategy = strategy;
		this.messageTargetType = messageTargetType;
		this.bpType = bpType;
		this.sourceIdList = sourceIdList;
	}
	
	public long getSingleReceiver(BpType bpType, Long id) {
		long receiverId = -1L;
		if (null == bpType || null == id || id.longValue() <= 0)
			return receiverId;
		try {
			switch(bpType) {
			case Elite_User:
				receiverId = id;
				break;
			case Question:
				TEliteQuestion question = eliteAdapter.getQuestionById(id);
				if(question.getId() > 0)
					receiverId = question.getBpId();
				break;
			case Answer:
				TEliteAnswer answer = eliteAdapter.getAnswerById(id);
				if(answer.getId() > 0)
					receiverId = answer.getBpId();
				break;
			default:
				break;
			}
		} catch(Exception e) {
			logger.error("", e);
		}
		return receiverId;
	}
	
	public List<Long> getFanReceiver(BpType bpType, Long id) {
		List<Long> receiverIdList = new ArrayList<Long>();
		if (null == bpType || null == id || id.longValue() <= 0)
			return receiverIdList;
		try {
			switch(bpType) {
			case Elite_User:
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(id, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, -1);
				if (listResult != null && listResult.getTotal() > 0) {
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()) {
						receiverIdList.add(relation.getUserId());
					}
					logger.info("userId={} total fans size={}", new String[]{String.valueOf(id),String.valueOf(listResult.getTotal())});
				} else {
					logger.info("not found user's fans, userId="+id);
				}
				break;
			case Question:
				listResult = thalloServiceAdapter.getReactiveListByUserType(id, BpType.Question.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, -1);
				if(listResult != null && listResult.getTotal() > 0) {
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()) {
						receiverIdList.add(relation.getUserId());
					}
					logger.info("questionId={} total fans size={}", new String[]{String.valueOf(id),String.valueOf(listResult.getTotal())});
				} else {
					logger.info("not found question's fans, questionId="+id);
				}
				break;
			default:
				break;
			}
		} catch(Exception e) {
			logger.error("", e);
		}
		return receiverIdList;
	}
	
	@Override
	public void run() {
		if (null == messageType || null == messageTargetType || null == bpType || null == sourceIdList || sourceIdList.size() <= 0) {
			logger.error("parameters not enough to send message");
			return;
		}
		
		List<Long> receiverIdList = new ArrayList<Long>();
		switch(messageTargetType) {
		case SINGLE:
			long receiverId = getSingleReceiver(bpType, sourceIdList.get(0));
			if (receiverId > 0)
				receiverIdList.add(receiverId);
			break;
		case BATCH:
			for(Long sourceId : sourceIdList) {
				receiverId = getSingleReceiver(bpType, sourceId);
				if(receiverId > 0)
					receiverIdList.add(receiverId);
			}
			break;
		case FANS:
			for(Long sourceId : sourceIdList) {
				List<Long> fansIdList = getFanReceiver(bpType, sourceId);
				if(fansIdList.size() > 0)
					receiverIdList.addAll(fansIdList);
			}
			break;
		default:
			break;
		}
		
		if(receiverIdList.size() <= 0) {
			logger.info("get receiver id list failed.");
			return;
		}
		
		for (Long receiverId : receiverIdList) {
		    try {
		        if (eliteAdapter.postMessage(receiverId, messageType, messageData, strategy)) {
		            logger.info("message to reciverId = {}, messageType = {}, messageData = {}, strategy = {} succeed!", 
		                    new Object[]{receiverId, messageType, messageData, strategy});
		        } else {
		            logger.info("message to reciverId = {}, messageType = {}, messageData = {}, strategy = {} failed!",
		                    new Object[]{receiverId, messageType, messageData, strategy});
		        }
		    } catch (Exception e) {
		        logger.error("", e);
		    }
		}
	}
	
	//用于发送推送通知
    @Override
    public Boolean call() throws Exception {
        if (null == messageType || null == messageTargetType || null == bpType || null == sourceIdList
                || sourceIdList.size() <= 0) {
            logger.error("parameters not enough to send message");
            return false;
        }

        if (messageTargetType != EliteMessageTargetType.SINGLE)
            return false;
        Long receiverId = getSingleReceiver(bpType, sourceIdList.get(0));

        if (null == receiverId || receiverId <= 0) {
            logger.info("get receiver id list failed, receiverId = {}", new Object[] { receiverId });
            return false;
        }
        
        if (eliteAdapter.postMessage(receiverId, messageType, messageData, strategy)) {
            logger.info("message to reciverId = {}, messageType = {}, messageData = {}, strategy = {} succeed!", 
                    new Object[]{receiverId, messageType, messageData, strategy});
            return true;
        } else {
            logger.info("message to reciverId = {}, messageType = {}, messageData = {}, strategy = {} failed!",
                    new Object[]{receiverId, messageType, messageData, strategy});
            return false;
        }
    }
	
}