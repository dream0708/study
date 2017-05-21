package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.feedItem.ProduceBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;


/**
 * 
 * @author nicholastang
 * 2016-08-23 10:49:48
 * TODO 
 */
public class ProduceUtil
{
	private static Logger logger = LoggerFactory.getLogger(ProduceUtil.class);
	
	private static BpDecorationServiceAdapter bpDecorationServiceAdapter;

	private static EliteThriftServiceAdapter eliteServiceAdapter;
	
	private static UserInfoService userInfoService;
	private static BpUserService bpUserService;
	
	private static final String ADD_TEXT = "新增了";
	
	static
	{
		bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
		eliteServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
		userInfoService = (UserInfoService)SpringUtil.getBean("userInfoService");
		bpUserService = (BpUserService)SpringUtil.getBean("bpUserService");
	}

	public static ProduceBean getProduceInfo(Map<Integer, List<Long>> summary, Long viewerId) {
		ProduceBean produceBean = new ProduceBean();
		if (null == summary || summary.size() == 0) {
			return produceBean;
		}
		if(null == viewerId) {
			viewerId = 0L;
		}
		try {
			StringBuilder sb = new StringBuilder("");
			for (Map.Entry<Integer, List<Long>> entry : summary.entrySet()) {
				Integer actionTypeVal = entry.getKey();
				List<Long> producerIdList = entry.getValue();
				if (null == actionTypeVal || null == producerIdList || producerIdList.size() == 0) {
					continue;
				}
				ProduceActionType produceActionType = (ProduceActionType) ProduceActionType.valueMap.get(actionTypeVal);
				if (null == produceActionType) {
					continue;
				}
				Long producerComplexId = producerIdList.get(0);
				Map<String, Object> producerMap = TimeLineUtil.getOriginInfo(producerComplexId);
				if (null == producerMap) {
					continue;
				}
				Integer producerType = (Integer) producerMap.get(TimeLineUtil.OBJECT_TYPE);
				Long producerId = (Long) producerMap.get(TimeLineUtil.OBJECT_ID);
				BpType bpType = (BpType) BpType.valueMap.get(producerType);
				if (null == bpType)
					continue;

				switch (bpType) {
					case Elite_User:
					    UserDetailDisplayBean userDetail = bpUserService.getUserSimpleByBpId(producerId);
						String nick = userDetail.getNick();
						if (producerId.longValue() == viewerId.longValue()) {
							nick = "我";
						}
						sb.append(nick);
						produceBean.setProduceAvatar(ImageUtil.getSmallImage(userDetail.getAvatar()));
						produceBean.setProduceLink(userDetail.getHomeUrl());
						produceBean.setProduceIdentity(userDetail.getIdentity());
						produceBean.setProduceIdentityString(userDetail.getIdentityString());
						produceBean.setBpUserType(userDetail.getBpUserType());
						break;
					case Question:
						sb.append("关注的问题").append(" ");
						produceBean.setProduceAvatar(Constants.DEFAULT_QUESTION_AVATAR);
						produceBean.setProduceLink(ToolUtil.getQuestionUri(IDUtil.encodeId(producerId)));
						break;
					case Tag:
						sb.append("关注的");
						Tag tag = bpDecorationServiceAdapter.getTagById(producerId.intValue());
						sb.append("#").append(tag.getName()).append("#").append(" ");
						produceBean.setProduceAvatar(Constants.DEFAULT_TAG_AVATAR);
						produceBean.setProduceLink(ToolUtil.getTagUri(IDUtil.encodeId(producerId)));
						break;
				}

				sb.append(produceActionType.getDesc());
				break;

			}
			produceBean.setProduceText(sb.toString());
		} catch (Exception e) {
			logger.error("", e);
		}
		return produceBean;
	}
	/**
	 * 将summary转化为文字输出
	 * @param summary
	 * @return
	 */
	/*
	public static String getProduceInfo(Map<Integer, List<Long>> summary)
	{
		StringBuilder sb = new StringBuilder("");
		if(null == summary || summary.size() == 0)
			return sb.toString();
		try{
			Map<Long, List<Integer>> summaryBro = reFormSummary(summary);
			
			//整理两个map,使之互补 summaryBro优先于summary
			List<Long> removeList = new ArrayList<>();
			for(Map.Entry<Long, List<Integer>> entry : summaryBro.entrySet()){
				Long producerId = entry.getKey();
				List<Integer> actionTypeList = entry.getValue();
				if(actionTypeList.size() <= 1){
					removeList.add(producerId);
					//summaryBro.remove(producerId);
				} else {
					for(Integer actionTypeVal : actionTypeList){
						summary.get(actionTypeVal).remove(producerId);
					}
				}
			}
			for(Long removeId : removeList)
				summaryBro.remove(removeId);
			
			for(Map.Entry<Integer, List<Long>> entry : summary.entrySet()){
				Integer actionType = entry.getKey();
				List<Long> producerList = entry.getValue();
				if(producerList.size() == 0)
					continue;
				
				ProduceActionType produceActionType = (ProduceActionType)ProduceActionType.valueMap.get(actionType);
				if(null == produceActionType)
					continue;
				
				int i = 0;
				for(Long producerComplexId : producerList)
				{
					Map<String, Object> producerMap = TimeLineUtil.getOriginInfo(producerComplexId);
					if(null != producerMap)
					{
						Integer objectType = (Integer)producerMap.get(TimeLineUtil.OBJECT_TYPE);
						Long objectId = (Long)producerMap.get(TimeLineUtil.OBJECT_ID);
						BpType bpType = (BpType) BpType.valueMap.get(objectType);
						if(null == bpType)
							continue;
						if(i++ > 0)
							sb.append("、");
						switch(bpType)
						{
						case Elite_User:
							UserInfo userInfo = userInfoService.getDecorateUserInfoByBpid(objectId);
							sb.append(userInfo.getNick()).append(" ");
							break;
						case Question:
							sb.append("关注的问题").append(" ");
							break;
						case Tag:
							sb.append("关注的");
							Tag tag = bpDecorationServiceAdapter.getTagById(objectId.intValue());
							sb.append("#").append(tag.getName()).append("#").append(" ");
						
						}
					}
				}
				
				sb.append(produceActionType.getDesc()+"，");
			}
			
			for(Map.Entry<Long, List<Integer>> entry : summaryBro.entrySet()){
				Long producerComplexId = entry.getKey();
				List<Integer> actionTypeList = entry.getValue();
				
				Map<String, Object> producerMap = TimeLineUtil.getOriginInfo(producerComplexId);
				if(null != producerMap)
				{
					Integer objectType = (Integer)producerMap.get(TimeLineUtil.OBJECT_TYPE);
					Long objectId = (Long)producerMap.get(TimeLineUtil.OBJECT_ID);
					BpType bpType = (BpType) BpType.valueMap.get(objectType);
					if(null == bpType)
						continue;
					switch(bpType)
					{
					case Elite_User:
						UserInfo userInfo = userInfoService.getDecorateUserInfoByBpid(objectId);
						sb.append(userInfo.getNick()).append(" ");
						break;
					case Question:
						sb.append("关注的问题").append(" ");
						break;
					case Tag:
						sb.append("关注的");
						Tag tag = bpDecorationServiceAdapter.getTagById(objectId.intValue());
						sb.append("#").append(tag.getName()).append("#").append(" ");
					
					}
					
					boolean showAnswer = actionTypeList.contains(ProduceActionType.ANSWER.getValue());
					
					//删除多余信息
					if(actionTypeList.contains((Integer)ProduceActionType.UPDATE.getValue())){
						actionTypeList.remove((Integer)ProduceActionType.ASK.getValue());
						actionTypeList.remove((Integer)ProduceActionType.ASK.getValue());
						actionTypeList.remove((Integer)ProduceActionType.ANSWER.getValue());
						actionTypeList.remove((Integer)ProduceActionType.COMMENT.getValue());
						actionTypeList.remove((Integer)ProduceActionType.LIKE.getValue());
					}
					if(actionTypeList.contains((Integer)ProduceActionType.ANSWER.getValue())){
						actionTypeList.remove((Integer)ProduceActionType.COMMENT.getValue());
						actionTypeList.remove((Integer)ProduceActionType.LIKE.getValue());
					}
					
					int actionLength = actionTypeList.size();
					for(int i=0;i<actionTypeList.size();i++){
						ProduceActionType produceActionType = (ProduceActionType)ProduceActionType.valueMap.get(actionTypeList.get(i));
						if(produceActionType == ProduceActionType.ANSWER){
							showAnswer = false;
						}
						if(null == produceActionType)
							continue;
						String actionText = produceActionType.getDesc();
						if(i < (actionLength-2)) {
							sb.append(actionText.substring(0, actionText.indexOf("了"))).append("、");
						}
						else if(i < (actionLength-1)) {
							sb.append(actionText.substring(0, actionText.indexOf("了"))).append("并");
						}
						else {
							sb.append(actionText);
							if(showAnswer)
								sb.append("回答");
						}
					}
				}
				sb.append("，");
				
			}
		}catch(Exception e){
			logger.error("", e);
		}
		String produceText = sb.toString();
		if(StringUtils.isBlank(produceText))
			return "";
		else
			return produceText.substring(0, produceText.length()-1);
	}
	*/
	
	//变换summary的形式
	public static Map<Long, List<Integer>> reFormSummary(Map<Integer, List<Long>> summary){
		Map<Long, List<Integer>> summaryBro = new HashMap<>();
		for(Map.Entry<Integer, List<Long>> entry : summary.entrySet()){
			Integer actionType = entry.getKey();
			List<Long> producerList = entry.getValue();
			for(Long producerId : producerList){
				if(summaryBro.containsKey(producerId)){
					List<Integer> actionTypeList = summaryBro.get(producerId);
					actionTypeList.add(actionType);
					summaryBro.put(producerId, actionTypeList);
				}
				else{
					List<Integer> actionTypeList = new ArrayList<Integer>();
					actionTypeList.add(actionType);
					summaryBro.put(producerId, actionTypeList);
				}
			}
		}
		return summaryBro;
	}
	
	//改变produceText的文本
	public static String tranProduceText(String produceText, BpType bpType){
		switch (bpType) {
		case Tag:
			produceText = ADD_TEXT;
			break;

		default:
			break;
		}
		return produceText;
	}
	
	public static void main(String[] args){
		Map<Integer, List<Long>> map = new HashMap<Integer, List<Long>>();
		List<Long> produerIdList = new ArrayList<Long>();
		produerIdList.add(75604040L);
		map.put(6, produerIdList);
		map.put(9, produerIdList);
		//String text = getProduceInfo(map);
    }
	
}