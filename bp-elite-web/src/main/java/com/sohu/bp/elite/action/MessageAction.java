package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.elite.action.bean.message.MessageItemBean;
import com.sohu.bp.elite.action.bean.message.WechatMessageDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.util.EliteResponseJSON;
import com.sohu.bp.elite.util.HumanityUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.model.BpMessageDetail;
import com.sohu.bp.model.BpMessageDetailType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * 用于读取消息的接口
 * @author zhijungou
 * 2016年12月14日
 */
@Controller
public class MessageAction {
	private static final Logger log = LoggerFactory.getLogger(MessageAction.class);
	private static final BpExtendServiceAdapter bpExtendAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	private static final String messageContent = ">(.*)<";
	private static final String QAType = "bar.focus.cn/q/";
	private static final String FollowType = "bar.focus.cn/mf";
	private static final String objectId = "/(?:a|q)/(.*)";
	
	
	@ResponseBody
	@RequestMapping(value = "wx/message/noread", produces = "application/json;charset=utf-8")
	public String getNoreadMessageNum(HttpSession session){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId <= 0) return EliteResponseJSON.getNotLoginError().toString();
		int count = 0;
		CodeMsgData codeMsgData = bpExtendAdapter.getBpNoReadMessagesCountByType(bpId);
			if(ResponseConstants.OK == codeMsgData.getCode()){
				JSONObject data = JSONObject.fromObject(codeMsgData.getData());
				if(data.containsKey(String.valueOf(BpMessageDetailType.INTERACTION.getValue()))){
					count = data.getInt(String.valueOf(BpMessageDetailType.INTERACTION.getValue()));
			}
		}
			JSONObject data = new JSONObject();
			data.put("count", count);
			resJSON.put("data", data);
		return resJSON.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "wx/message/detail", produces = "application/json;charset=utf-8")
	public String getMessageDetail(HttpSession session, WechatMessageDisplayBean bean){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId <= 0) return EliteResponseJSON.getNotLoginError().toString();
		
		CodeMsgData codeMsgData = bpExtendAdapter.getBpMessageStatisticsByBpid(bpId);
		if(ResponseConstants.OK == codeMsgData.getCode()){
			JSONObject data = JSONObject.fromObject(codeMsgData.getData());
			int total = data.getInt(String.valueOf(BpMessageDetailType.INTERACTION.getValue()));
			bean.setTotal(total);
		}
		
		codeMsgData = bpExtendAdapter.getBpNoReadMessagesCountByType(bpId);
		if(ResponseConstants.OK == codeMsgData.getCode()){
			bean.setNoreadCount(JSONObject.fromObject(codeMsgData.getData()).getInt(String.valueOf(BpMessageDetailType.INTERACTION.getValue())));
		}
		
		int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
		int stop = bean.getCurrPageNo() * bean.getPageSize();
		codeMsgData = bpExtendAdapter.getBpMessageDetailsByToIdAndType(bpId, BpMessageDetailType.INTERACTION.getValue(), start, stop);
		if(ResponseConstants.OK == codeMsgData.getCode()){
			List<MessageItemBean> messageList = new ArrayList<>();
			JSONArray array = JSONArray.fromObject(codeMsgData.getData());
			for(int i = 0; i < array.size(); i++){
				try{
					long messageId = array.getLong(i);
					CodeMsgData getMessageDetailData = bpExtendAdapter.getBpMessageDetail(messageId);
					if(ResponseConstants.OK == getMessageDetailData.code){
						MessageItemBean message = new MessageItemBean();
						BpMessageDetail messageJSON = JSON.parseObject(getMessageDetailData.getData(), BpMessageDetail.class);
						message.setUpdateTime(HumanityUtil.humanityTime(messageJSON.getUpdateTime()));
						message.setTopic(messageJSON.getTopic());
						message.setMessageId(IDUtil.encodeId(messageJSON.getId()));
						JSONObject contentJSON = JSONObject.fromObject(messageJSON.getContent());
						if(contentJSON.getString("interactionType").equals("FOLLOW")){
							String content = contentJSON.getString("content");
							String[] contentParts = getMessageParts(content);
							message.setContent(contentParts[0]).setObjectId(contentParts[1]).setObjectType(contentParts[2]);
							if (Objects.equals(contentParts[2], "QA") && StringUtils.isNotBlank(contentParts[1])) {
							    Long questionId = Long.valueOf(IDUtil.decodeId(contentParts[1]));
							    TEliteQuestion question = eliteAdapter.getQuestionById(questionId);
							    message.setSpecialType(question.getSpecialType());
							}
							messageList.add(message);
						}
					}
				} catch (Exception e) {
					log.error("", e);
					continue;
				}
			}
			bean.setMessageList(messageList);
		}
		resJSON.put("data", bean);
		bpExtendAdapter.deleteAllBpNoReadMessages(bpId);
		return resJSON.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "wx/message/delete", produces = "application/json;charset=utf-8")
	public String deleteMessageDetail(@RequestParam(value = "messageId", required = true) String messageId){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long id = IDUtil.decodeId(messageId);
		if(null == id || id <= 0) return ResponseJSON.getErrorParamsJSON().toString();
		CodeMsgData codeMsgData = bpExtendAdapter.deleteBpMessageDetail(id);
		if(ResponseConstants.OK == codeMsgData.getCode()){
			return resJSON.toString();
		}
		return ResponseJSON.getErrorInternalJSON().toString();
	}
	
	private static String[] getMessageParts(String content){
		Pattern qaPattern = Pattern.compile(QAType);
		Pattern followPattern = Pattern.compile(FollowType);
		Pattern objectPattern = Pattern.compile(objectId);
		String[] parts = new String[3];
		
	    Document doc = Jsoup.parse(content);
	    Elements eles = doc.select("a");
	    Iterator<Element> iter = eles.iterator();
	    if (!iter.hasNext()) return new String[]{content};
	    Element aTag = iter.next();
	    parts[0] = aTag.text();
		String href = aTag.attr("href");
		String objectId = "";
		Matcher m = followPattern.matcher(href);
		if (m.find()) {
			parts[1] = "";
			parts[2] = "FOLLOW";
			return parts;
		}
		m = objectPattern.matcher(href);
		if(m.find()){
			objectId = m.group(1);
		}
		m = qaPattern.matcher(href);
		if(m.find()){
			parts[1] = objectId;
		} else {
			Long id = IDUtil.decodeId(objectId);
			if(null != id && id > 0){
				try{
					parts[1] = IDUtil.encodeId(eliteAdapter.getAnswerById(id).getQuestionId());
				} catch (Exception e){
					log.error("", e);
				}
			}
		}
		parts[2] = "QA";
		return parts;
	}
	public static void main(String[] args) {
	    Long bpId = IDUtil.decodeId("8214885");
	    WechatMessageDisplayBean bean = new WechatMessageDisplayBean();
	    bean.setCurrPageNo(4);
	    CodeMsgData codeMsgData = bpExtendAdapter.getBpMessageStatisticsByBpid(bpId);
        if(ResponseConstants.OK == codeMsgData.getCode()){
            JSONObject data = JSONObject.fromObject(codeMsgData.getData());
            int total = data.getInt(String.valueOf(BpMessageDetailType.INTERACTION.getValue()));
            bean.setTotal(total);
        }
        
        codeMsgData = bpExtendAdapter.getBpNoReadMessagesCountByType(bpId);
        if(ResponseConstants.OK == codeMsgData.getCode()){
            bean.setNoreadCount(JSONObject.fromObject(codeMsgData.getData()).getInt(String.valueOf(BpMessageDetailType.INTERACTION.getValue())));
        }
        
        int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
        int stop = bean.getCurrPageNo() * bean.getPageSize();
        codeMsgData = bpExtendAdapter.getBpMessageDetailsByToIdAndType(bpId, BpMessageDetailType.INTERACTION.getValue(), start, stop);
        if(ResponseConstants.OK == codeMsgData.getCode()){
            List<MessageItemBean> messageList = new ArrayList<>();
            JSONArray array = JSONArray.fromObject(codeMsgData.getData());
            for(int i = 0; i < array.size(); i++){
                try{
                    long messageId = array.getLong(i);
                    CodeMsgData getMessageDetailData = bpExtendAdapter.getBpMessageDetail(messageId);
                    if(ResponseConstants.OK == getMessageDetailData.code){
                        MessageItemBean message = new MessageItemBean();
                        BpMessageDetail messageJSON = JSON.parseObject(getMessageDetailData.getData(), BpMessageDetail.class);
                        message.setUpdateTime(HumanityUtil.humanityTime(messageJSON.getUpdateTime()));
                        message.setTopic(messageJSON.getTopic());
                        message.setMessageId(IDUtil.encodeId(messageJSON.getId()));
                        JSONObject contentJSON = JSONObject.fromObject(messageJSON.getContent());
                        if(contentJSON.getString("interactionType").equals("FOLLOW")){
                            String content = contentJSON.getString("content");
                            String[] contentParts = getMessageParts(content);
                            message.setContent(contentParts[0]).setObjectId(contentParts[1]).setObjectType(contentParts[2]);
                            messageList.add(message);
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                    continue;
                }
            }
            bean.setMessageList(messageList);
        }

	}
	
	
}
