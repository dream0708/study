package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteMessageTargetType;
import com.sohu.bp.elite.enums.EliteSendWechatTemplate;
import com.sohu.bp.elite.enums.SendCloudTemplate;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessageFrequenceType;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.model.TEliteMessageTimePeriodType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;

/**
 * 用于消息（站内信，微信，短信）的工具类
 * @author zhijungou
 * 2017年3月28日
 */
public class EliteMessageUtil {
	private static final Logger log = LoggerFactory.getLogger(EliteMessageUtil.class);
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	public static String getMessageIdentity(Long bpId, Long questionId) {
		return bpId + "_" + questionId;
	}
	
	//用于QuestionAction和InviteAction中的对被邀请人发送信息
	public static void postInviteMessage(String name, Long invitedId, Long questionId) {
		try{
			TEliteMessageData messageData = new TEliteMessageData();
			TEliteQuestion question = eliteAdapter.getQuestionById(questionId);
			String title = question.getTitle();
			String inboxMessageContent = EliteMessageData.NEW_INVITE_BY_PERSON.getContent()
					.replace(Constants.MESSAGE_DATA_JUMPURL, "https://" + OverallDataFilter.askDomain + ToolUtil.getQuestionUri(IDUtil.encodeId(questionId)))
	                .replace(Constants.MESSAGE_DATA_NICKNAME, name)
	                .replace(Constants.MESSAGE_DATA_QUESTIONTITLE, title);
			Map<String, String> sendCloudVariables = new HashMap<String, String>();
			sendCloudVariables.put(SendCloudSmsUtil.PARAM_NICK_NAME, name);
			sendCloudVariables.put(SendCloudSmsUtil.PARAM_QUESTION_TITLE, title);
			sendCloudVariables.put(SendCloudSmsUtil.PARAM_QUESTION_URI, ToolUtil.getLoginQuestionUrl(IDUtil.encodeId(questionId), invitedId));
			messageData.setInboxMessageDataValue(EliteMessageData.NEW_INVITE_BY_PERSON.getValue()).setInboxMessageContent(inboxMessageContent)
			.setSendCloudTemplate(SendCloudTemplate.NEW_INVITE_BY_PERSON.getValue()).setSendCloudVariables(sendCloudVariables)
			.setWechatTemplateId(EliteSendWechatTemplate.NEW_INVITE_BY_PERSON.getValue()).setWechatUrl(WechatUtil.getWechatQuestionUrl(questionId)).setWechatData(WechatUtil.getInviteMessageWechatData(invitedId, name, question, null));
			TEliteMessageStrategy strategy = new TEliteMessageStrategy().setTimePeriodType(TEliteMessageTimePeriodType.DAY_TIME).setFrequenceType(TEliteMessageFrequenceType.HOUR)
					.setFrequenceValue(Constants.EXPERT_TEAM_DEFAULT_NUM_PER_HOUR);
			Future<Boolean> result = EliteAsyncTaskPool.submitTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.MEDIUM, messageData, strategy,
					EliteMessageTargetType.SINGLE, BpType.Elite_User, new ArrayList<Long>(){{add(invitedId);}}));
		} catch (Exception e){
			log.error("", e);
		}
	}
}
